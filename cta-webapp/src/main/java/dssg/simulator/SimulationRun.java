package dssg.simulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.transit_data_federation.services.blocks.BlockCalendarService;
import org.onebusaway.transit_data_federation.services.blocks.BlockIndexService;
import org.onebusaway.transit_data_federation.services.blocks.BlockInstance;
import org.onebusaway.transit_data_federation.services.realtime.BlockLocationService;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockConfigurationEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockStopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.RouteEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.StopEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.StopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.TransitGraphDao;

import dssg.shared.ProjectConstants;

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
  final private BlockIndexService bis;
  final private BlockLocationService bls;
  final private BlockCalendarService bcs;
  final private TransitGraphDao tgd;
  final private PassengerOnModel boardModel;
  final private PassengerOffModel alightModel;

  final private int runId;
  final private RouteEntry route;
  final private String routeId;
  final private DateTime startTime;
  final private DateTime endTime;
  final private DateMidnight day;

  final private PriorityQueue<BlockStopTimeEntry> stopTimes;
  final private List<StopEvent> events;
  final private Map<String,BusState> buses; // Map GTFS block id to bus state object
  final private Map<String,StopState> stops; // Map GTFS stop id to stop state object
  
  final private boolean logEvents;
  final private boolean keepEventObjs;


  public SimulationRun(SimulationBatch simBatch, int runId, String routeId,
      DateTime startTime, DateTime endTime) {
    this(simBatch, runId, routeId, startTime, endTime, true, false);
  }

  public SimulationRun(SimulationBatch simBatch, int runId, String routeId,
      DateTime startTime, DateTime endTime, boolean logEvents,
      boolean keepEventObjs) {
    this.batch = simBatch;
    this.bis = simBatch.simService.bis;
    this.bls = simBatch.simService.bls;
    this.bcs = simBatch.simService.bcs;
    this.tgd = simBatch.simService.tgd;
    this.boardModel = simBatch.boardModel;
    this.alightModel = simBatch.alightModel;

    this.runId = runId;
    this.routeId = routeId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.day = startTime.toDateMidnight();


    this.buses = new HashMap<String,BusState>();
    this.stops = new HashMap<String,StopState>();
    
    this.logEvents = logEvents;

    this.keepEventObjs = keepEventObjs;
    if(keepEventObjs) this.events = new ArrayList<StopEvent>();
    else this.events = null;

    AgencyAndId routeAgencyAndId = AgencyAndId.convertFromString(ProjectConstants.AGENCY_NAME + "_" + routeId);
    this.route = tgd.getRouteForId(routeAgencyAndId);
    List<BlockInstance> blocks = bcs.getActiveBlocksForRouteInTimeRange(routeAgencyAndId, startTime.getMillis(), endTime.getMillis());

    stopTimes = new PriorityQueue<BlockStopTimeEntry>(1000,
	    new Comparator<BlockStopTimeEntry>() {
	      @Override
	      public int compare(BlockStopTimeEntry st1, BlockStopTimeEntry st2) {
	        return st1.getStopTime().getDepartureTime() - st2.getStopTime().getDepartureTime();
	      }
	    });

    for(BlockInstance blockInst : blocks)
        stopTimes.add(blockInst.getBlock().getStopTimes().get(0));
    
  }

  /**
   * Increment the simulation in time
   * @return false for finished, true for in-progress
   */
  public boolean step() {
    if(this.stopTimes.isEmpty()) {
      return false;
    }
    BlockStopTimeEntry bste = this.stopTimes.remove();

    BlockConfigurationEntry bce = bste.getTrip().getBlockConfiguration();
    String blockId = bce.getBlock().getId().getId();
    StopTimeEntry stopTimeEntry = bste.getStopTime();
    BusState bus = this.buses.get(blockId);
    if(bus == null) {
      bus = new BusState(bste);
      this.buses.put(blockId, bus);
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
    String tageoid = stop.getStopId();
    String busStopId = taroute + "," + tageoid;

    int scheduledArrivalTime = bste.getStopTime().getArrivalTime();
    int scheduledDepartureTime = bste.getStopTime().getDepartureTime();
    // for CTA GTFS, the scheduled departure time is always equal to the scheduled arrival time
    int actualArrivalTime = scheduledArrivalTime; // TODO: replace with service model
    int actualDepartureTime = scheduledDepartureTime; // TODO: replace with service model
    Integer lastDepartureTime = stop.getTimeOfLastBus(taroute);
    // TODO: better way of handling first bus of day?
    if(lastDepartureTime == null) {
      lastDepartureTime = actualDepartureTime - 60*15; // passengers arriving 15min ahead
    }
    int arrivingLoad = bus.getLoad();

    int alight = this.alightModel.sample(busStopId, this.day, actualArrivalTime, arrivingLoad);
    int prevLeftBehind = stop.getLeftBehind(routeId);

    int attemptBoard = this.boardModel.sample(busStopId, this.day, lastDepartureTime, actualDepartureTime) + prevLeftBehind;
    int actualBoard = bus.update(alight, attemptBoard);
    int leftBehind = attemptBoard - actualBoard;
    stop.update(routeId, actualDepartureTime, leftBehind);
    int departingLoad = bus.getLoad();
    
    BlockStopTimeEntry nextStopTime = bus.depart();
    if(nextStopTime != null)
      this.stopTimes.add(nextStopTime);
    
    System.out.println(tageoid + "," + scheduledArrivalTime + "," + actualArrivalTime + "," + actualDepartureTime + "," + actualBoard + "," + alight + "," + departingLoad);

    if(this.logEvents) {
      LogStopEvent eventLog = new LogStopEvent(this.runId,tageoid,
          scheduledArrivalTime, actualArrivalTime,actualDepartureTime,
          actualBoard,alight,departingLoad);
      this.batch.logger.logEvent(eventLog);
    }
    if(this.keepEventObjs) {
      // TODO: This is currently dead code - figure out if we would need to
      // keep around StopEvent objects linked to their BlockStopTimeEntries.
      // If not, the prune it.
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
