package dssg.simulator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.joda.time.DateMidnight;
import org.onebusaway.transit_data_federation.services.blocks.BlockInstance;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockConfigurationEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockStopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockTripEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.StopEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.StopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.TripEntry;

import dssg.shared.Config;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

/**
 * This class holds the state/result of a single simulation.
 * 
 * @author jtbates
 * 
 */
public class SimulationRun implements Runnable {
  
  /*
   * Simulation static properties
   */
  final private SimulationBatch batch;
  final private RandomStream rng;
  final private BusServiceModel serviceModel;
  final private PassengerOnModel boardModel;
  final private PassengerOffModel alightModel;

  final private int runId;
  final private List<BlockInstance> blocks;
  final private Set<String> routeAndIds;
  final private DateMidnight day;

  final private PriorityQueue<BusState> stopTimes;
  final private List<StopEvent> events;
  final private Map<String,BusState> buses; // Map GTFS block id to bus state object
  final private Map<String,StopState> stops; // Map GTFS stop id to stop state object
  
  final private boolean keepEventObjs;

  final private Map<BlockTripEntry,LogStopEvent> tripToLastEvent;

  public SimulationRun(SimulationBatch simBatch, int runId,
      Set<String> routeAndDirs, List<BlockInstance> blocks, DateMidnight day) {
    this(simBatch, runId, routeAndDirs, blocks, day, false);
  }

  public SimulationRun(SimulationBatch simBatch, int runId,
      Set<String> routeAndIds, List<BlockInstance> blocks, DateMidnight day,
      boolean keepEventObjs) {
    this.batch = simBatch;
    this.runId = runId;
    this.routeAndIds = routeAndIds;
    this.blocks = blocks;
    this.day = day;

    this.rng = new MRG32k3a();
    this.serviceModel = simBatch.serviceModel;
    this.boardModel = simBatch.boardModel;
    this.alightModel = simBatch.alightModel;

    this.buses = new HashMap<String,BusState>();
    this.stops = new HashMap<String,StopState>();

    this.keepEventObjs = keepEventObjs;
    if(keepEventObjs) this.events = new ArrayList<StopEvent>();
    else this.events = null;

    this.tripToLastEvent = new HashMap<BlockTripEntry,LogStopEvent>();

    this.stopTimes = new PriorityQueue<BusState>(1000,
	    new Comparator<BusState>() {
	      @Override
	      public int compare(BusState b1, BusState b2) {
	        return b1.getNextStop().getStopTime().getDepartureTime() - b2.getNextStop().getStopTime().getDepartureTime();
	      }
	    });

    Connection db = null;
    try {
      db = Config.getDatabaseConnection();
      PreparedStatement vehTypeStmt = db.prepareStatement("SELECT veh_type FROM dn_bt_veh_type WHERE bt_ver=? AND blockno=?");
      for(BlockInstance blockInst : blocks) {
        BlockConfigurationEntry bce = blockInst.getBlock();
        BlockStopTimeEntry bste = bce.getStopTimes().get(0);
        String btVerAndPatternId = bste.getTrip().getTrip().getShapeId().getId();
        String btVer = btVerAndPatternId.substring(0,3);
        String blockno = bce.getBlock().getId().getId();
        vehTypeStmt.setString(1, btVer);
        vehTypeStmt.setString(2, blockno);
        ResultSet rs = vehTypeStmt.executeQuery();
        rs.next();
        String vehType = rs.getString(1);
        BusState bus = new BusState(bste,vehType);
        stopTimes.add(bus);
      }
    }
    catch(SQLException e) {
      // FIXME handle this exception properly
      e.printStackTrace();
    }
    finally {
      if(db != null)
        try {
          db.close();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
    }
  }

  /**
   * Increment the simulation in time
   * @return false for finished, true for in-progress
   */
  public boolean step() {
    if(this.stopTimes.isEmpty()) {
      return false;
    }
    BusState bus = this.stopTimes.remove();
    BlockStopTimeEntry bste = bus.getNextStop();
    TripEntry trip = bste.getTrip().getTrip();
    String taroute = trip.getRoute().getId().getId();
    String direction = trip.getDirectionId();
    String routeAndDir = taroute + "," + direction;

    BlockConfigurationEntry bce = bste.getTrip().getBlockConfiguration();
    String blockId = bce.getBlock().getId().getId();
    StopTimeEntry stopTimeEntry = bste.getStopTime();

    if(!this.routeAndIds.contains(routeAndDir)) {
      BlockStopTimeEntry nextStop = bus.depart(null);
      if(nextStop != null) this.stopTimes.add(bus);
      return true;
    }

    StopEntry stopEntry = stopTimeEntry.getStop();
    String stopId = stopEntry.getId().getId();
    StopState stop = this.stops.get(stopId);
    if(stop == null) {
      stop = new StopState(stopEntry);
      this.stops.put(stopId, stop);
    }
    makeStopEvent(bste, bus, stop);
    return true;
  }
  
  public void makeStopEvent(BlockStopTimeEntry bste, BusState bus, StopState stop) {
    String taroute = bus.getCurrentRouteId();
    String dir_group = bus.getCurrentDirectionId();
    String tageoid = stop.getStopId();
    String busStopId = taroute + "," + tageoid;
    String routeAndDir= taroute + "," + dir_group;

    // for CTA GTFS, the scheduled departure time is always equal to the
    // scheduled arrival time, but this may change in the future
    int scheduledArrivalTime = bste.getStopTime().getArrivalTime();
    int scheduledDepartureTime = bste.getStopTime().getDepartureTime();

    LogStopEvent prevStopEvent = bus.getPrevStopEvent();
    // TODO: better way to handle first stop in trip?
    int prevDelta = 0;
    int schedInterval = 10*60;
    if(prevStopEvent != null) {
      int prevDepartureTime = prevStopEvent.getTime_actual_depart();
      int prevScheduledTime = prevStopEvent.getTime_scheduled();
      prevDelta = prevDepartureTime - prevScheduledTime;
      schedInterval = scheduledDepartureTime - prevScheduledTime;
    }
    int delta = this.serviceModel.sample(routeAndDir,prevDelta,schedInterval,this.rng);
    int actualArrivalTime = scheduledDepartureTime + delta;
    int actualDepartureTime = actualArrivalTime; // TODO: model dwell time?

    Integer lastDepartureTime = stop.getTimeOfLastBus(taroute);
    // TODO: better way of handling first bus of day?
    if(lastDepartureTime == null) {
      lastDepartureTime = actualDepartureTime - 60*15; // passengers arriving 15min ahead
    }
    int arrivingLoad = bus.getLoad();

    int alight = this.alightModel.sample(busStopId, this.day, actualArrivalTime, arrivingLoad, this.rng);
    int prevLeftBehind = stop.getLeftBehind(taroute);

    int attemptBoard = this.boardModel.sample(busStopId, this.day, lastDepartureTime, actualDepartureTime, this.rng) + prevLeftBehind;
    int actualBoard = bus.update(alight, attemptBoard);
    int leftBehind = attemptBoard - actualBoard;
    stop.update(taroute, actualDepartureTime, leftBehind);
    int departingLoad = bus.getLoad();
    
    BlockTripEntry trip = bste.getTrip();
    LogStopEvent lastEvent = this.tripToLastEvent.get(trip);
    BlockStopTimeEntry nextStopTime = bus.depart(lastEvent);

    LogStopEvent eventLog = new LogStopEvent(this.runId,taroute,dir_group,tageoid,
        scheduledArrivalTime, actualArrivalTime,actualDepartureTime,
        actualBoard,alight,departingLoad,lastEvent);
    this.batch.handleEvent(eventLog);

    if(nextStopTime != null) {
      this.stopTimes.add(bus);
      this.tripToLastEvent.put(trip,eventLog);
    }
    else {
      this.tripToLastEvent.remove(trip);
    }

    if(this.keepEventObjs) {
      // TODO: This is currently dead code - figure out if we would need to
      // keep around StopEvent objects linked to their BlockStopTimeEntries.
      // If not, the prune it. If we do need it, merge with LogStopEvent?
      StopEvent event = new StopEvent(bste, alight, actualBoard, leftBehind, departingLoad);
      this.events.add(event);
    }
  }

  public List<StopEvent> getStopEvents() {
    return this.events;
  }
  
  @Override
  public void run() {
    try {
      while (step()) {
        /*
         * TODO FIXME compute stats on the fly?
         * Possibly use StatProbe objects from SSJ?
         */
      }
    }
    finally {
      this.batch.runCallback(this);
    }
  }

}
