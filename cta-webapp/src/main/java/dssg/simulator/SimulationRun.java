package dssg.simulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.transit_data_federation.services.blocks.BlockCalendarService;
import org.onebusaway.transit_data_federation.services.blocks.BlockIndexService;
import org.onebusaway.transit_data_federation.services.blocks.BlockInstance;
import org.onebusaway.transit_data_federation.services.realtime.BlockLocationService;
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

  /*
   * Simulation static properties
   */
  final BlockIndexService bis;
  final BlockLocationService bls;
  final BlockCalendarService bcs;
  final TransitGraphDao tgd;
  final protected int runId;
  final RouteEntry route;
  final protected String routeId;
  final protected Date startDate;
  final protected long startTime;
  final protected long endTime;
  final protected List<MyParameters> parameters;
  
  final protected PriorityQueue<StopTimeEntry> stopTimes;
  final protected List<StopEvent> events;
  final protected Map<String,BusState> buses; // Map GTFS block id to bus state object
  final protected Map<String,StopState> stops; // Map GTFS stop id to stop state object
  
  final protected BoardingModel boardingModel;
  final protected AlightingModel alightingModel;

  public SimulationRun(SimulationBatch simBatch, int runId,
    String routeId, Date startDate, long startTime, long endTime, List<MyParameters> parameters) {
    this.bis = simBatch.simService.bis;
    this.bls = simBatch.simService.bls;
    this.bcs = simBatch.simService.bcs;
    this.tgd = simBatch.simService.tgd;
    this.runId = runId;
    this.routeId = routeId;
    this.startDate = startDate;
    this.startTime = startTime;
    this.endTime = endTime;
    this.parameters = parameters;
    
    this.events = new ArrayList<StopEvent>();
    this.buses = new HashMap<String,BusState>();
    this.stops = new HashMap<String,StopState>();
    
    this.boardingModel = new BoardingModel();
    this.alightingModel = new AlightingModel();

    AgencyAndId routeAgencyAndId = AgencyAndId.convertFromString("Chicago Transit Authority_" + routeId);
    this.route = tgd.getRouteForId(routeAgencyAndId);
    long dateTime = startDate.getTime();
    List<BlockInstance> blocks = bcs.getActiveBlocksForRouteInTimeRange(routeAgencyAndId, dateTime+startTime, dateTime+endTime);

    stopTimes = new PriorityQueue<StopTimeEntry>(1000,
	    new Comparator<StopTimeEntry>() {
	      @Override
	      public int compare(StopTimeEntry st1, StopTimeEntry st2) {
	        return st1.getDepartureTime() - st2.getDepartureTime();
	      }
	    });

    for(BlockInstance blockInst : blocks)
      for(BlockStopTimeEntry bstEntry : blockInst.getBlock().getStopTimes())
        stopTimes.add(bstEntry.getStopTime());
    
  }

  /**
   * Increment the simulation in time
   * @return false for finished, true for in-progress
   */
  public boolean step() {
    if(this.stopTimes.isEmpty()) {
      return false;
    }
    StopTimeEntry stopTimeEntry = this.stopTimes.remove();
    TripEntry tripEntry = stopTimeEntry.getTrip();
    RouteEntry routeEntry = tripEntry.getRoute();
    BlockEntry blockEntry = tripEntry.getBlock();
    String blockId = blockEntry.getId().getId();
    BusState bus = this.buses.get(blockId);
    if(bus == null) {
      bus = new BusState(routeEntry,blockEntry);
      this.buses.put(blockId, bus);
    }
    StopEntry stopEntry = stopTimeEntry.getStop();
    String stopId = stopEntry.getId().getId();
    StopState stop = this.stops.get(stopId);
    if(stop == null) {
      stop = new StopState(stopEntry);
      this.stops.put(stopId, stop);
    }
    StopEvent event = makeStopEvent(stopTimeEntry, bus, stop);
    this.events.add(event);
    return true;
  }
  
  public StopEvent makeStopEvent(StopTimeEntry ste, BusState bus, StopState stop) {
    String routeId = bus.getRouteId();
    int alight = this.alightingModel.getNumberAlighting(bus, stop);
    int prevLeftBehind = stop.getLeftBehind(routeId);
    int attemptBoard = this.boardingModel.getNumberBoarding(bus, stop) + prevLeftBehind;
    int actualBoard = bus.update(alight, attemptBoard);
    int leftBehind = attemptBoard - actualBoard;
    stop.update(routeId, ste.getDepartureTime(), leftBehind);
    int load = bus.getLoad();
    
    return new StopEvent(ste, bus, stop, alight, actualBoard, leftBehind, load);
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

  public Date getStartDate() {
    return startDate;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

}
