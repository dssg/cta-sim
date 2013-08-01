package dssg.simulator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TimeZone;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.transit_data_federation.services.blocks.BlockCalendarService;
import org.onebusaway.transit_data_federation.services.blocks.BlockIndexService;
import org.onebusaway.transit_data_federation.services.blocks.BlockInstance;
import org.onebusaway.transit_data_federation.services.realtime.BlockLocationService;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockConfigurationEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockStopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.RouteEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.StopEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.StopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.TransitGraphDao;
import org.onebusaway.transit_data_federation.services.transit_graph.TripEntry;

import dssg.client.MyParameters;
import dssg.server.SimulationServiceImpl;

/**
 * This class holds the state/result of a single simulation.
 * 
 * @author bwillard
 * 
 */
public class SimulationRun implements Runnable {

  private final static TimeZone TIMEZONE = TimeZone.getTimeZone("America/Chicago");
  
  /*
   * Simulation static properties
   */
  final private BlockIndexService bis;
  final private BlockLocationService bls;
  final private BlockCalendarService bcs;
  final private TransitGraphDao tgd;
  final private PassengerOnModel boardModel;
  final private PassengerOffModel alightModel;

  final int runId;
  final RouteEntry route;
  final String routeId;
  final Date startTime;
  final Date endTime;

  final PriorityQueue<BlockStopTimeEntry> stopTimes;
  final List<StopEvent> events;
  final Map<String,BusState> buses; // Map GTFS block id to bus state object
  final Map<String,StopState> stops; // Map GTFS stop id to stop state object
  

  public SimulationRun(SimulationBatch simBatch, int runId,
    String routeId, Date startTime, Date endTime) {
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
    
    this.events = new ArrayList<StopEvent>();
    this.buses = new HashMap<String,BusState>();
    this.stops = new HashMap<String,StopState>();
    
    AgencyAndId routeAgencyAndId = AgencyAndId.convertFromString("Chicago Transit Authority_" + routeId);
    this.route = tgd.getRouteForId(routeAgencyAndId);
    List<BlockInstance> blocks = bcs.getActiveBlocksForRouteInTimeRange(routeAgencyAndId, startTime.getTime(), endTime.getTime());

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
    StopEvent event = makeStopEvent(bste, bus, stop);
    this.events.add(event);
    return true;
  }
  
  public StopEvent makeStopEvent(BlockStopTimeEntry bste, BusState bus, StopState stop) {
    String taroute = bus.getCurrentRouteId();
    String tageoid = stop.getStopId();
    String busStopId = taroute + "," + tageoid;

    int lastDepartureTime = stop.getTimeOfLastBus(taroute);
    int actualDepartureTime = bste.getStopTime().getDepartureTime(); // TODO: replace with service model
    int arrivingLoad = bus.getLoad();

    int alight = this.alightModel.sample(busStopId, arrivingLoad);
    int prevLeftBehind = stop.getLeftBehind(routeId);
    // TODO: add day field, switch to Joda time
    Calendar day = Calendar.getInstance(TIMEZONE);
    day.setTime(startTime);
    int attemptBoard = this.boardModel.sample(busStopId, day, lastDepartureTime, actualDepartureTime) + prevLeftBehind;
    int actualBoard = bus.update(alight, attemptBoard);
    int leftBehind = attemptBoard - actualBoard;
    stop.update(routeId, actualDepartureTime, leftBehind);
    int departingLoad = bus.getLoad();
    
    BlockStopTimeEntry nextStopTime = bus.depart();
    if(nextStopTime != null)
      this.stopTimes.add(nextStopTime);
    
    System.out.println(attemptBoard + " attempting to board at stop " + tageoid);

    return new StopEvent(bste, alight, actualBoard, leftBehind, departingLoad);
  }
  
  @Override
  public void run() {
    while (step()) {
      /*
       * TODO FIXME compute stats or put them in whatever format is needed.
       */
    }
  }

  public int getRunId() {
    return runId;
  }

  public RouteEntry getRoute() {
    return route;
  }

  public String getRouteId() {
    return routeId;
  }

  public Date getStartTime() {
    return startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

}
