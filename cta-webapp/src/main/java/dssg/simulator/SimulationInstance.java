package dssg.simulator;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.transit_data_federation.services.blocks.BlockCalendarService;
import org.onebusaway.transit_data_federation.services.blocks.BlockIndexService;
import org.onebusaway.transit_data_federation.services.blocks.BlockInstance;
import org.onebusaway.transit_data_federation.services.realtime.BlockLocationService;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockStopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.RouteEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.StopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.TransitGraphDao;

import dssg.server.SimulationServiceImpl;

/**
 * This class holds the state/result of a single simulation.
 * 
 * @author bwillard
 * 
 */
public class SimulationInstance {

  /*
   * Simulation static properties
   */
  final BlockIndexService bis;
  final BlockLocationService bls;
  final BlockCalendarService bcs;
  final TransitGraphDao tgd;
  final protected String simulationId;
  final RouteEntry route;
  final protected String routeId;
  final protected Date startDate;
  final protected long startTime;
  final protected long endTime;


  /*
   * Simulation's running properties
   */
  StopTime currentStopTime;
  double currentDistanceAlongBlock;

  public SimulationInstance(SimulationServiceImpl simService, String simulationId,
    String routeId, Date startDate, long startTime, long endTime) {
    this.bis = simService.bis;
    this.bls = simService.bls;
    this.bcs = simService.bcs;
    this.tgd = simService.tgd;
    this.simulationId = simulationId;
    this.routeId = routeId;
    this.startDate = startDate;
    this.startTime = startTime;
    this.endTime = endTime;

    AgencyAndId routeAgencyAndId = AgencyAndId.convertFromString("Chicago Transit Authority_" + routeId);
    this.route = tgd.getRouteForId(routeAgencyAndId);
    long dateTime = startDate.getTime();
    List<BlockInstance> blocks = bcs.getActiveBlocksForRouteInTimeRange(routeAgencyAndId, dateTime+startTime, dateTime+endTime);

    PriorityQueue<StopTimeEntry> stopTimeEntryQueue = new PriorityQueue<StopTimeEntry>(1000,
	    new Comparator<StopTimeEntry>() {
	        @Override
	        public int compare(StopTimeEntry st1, StopTimeEntry st2) {
	            return st1.getDepartureTime() - st2.getDepartureTime();
	        }
	    });

    for(BlockInstance blockInst : blocks)
        for(BlockStopTimeEntry bstEntry : blockInst.getBlock().getStopTimes())
            stopTimeEntryQueue.add(bstEntry.getStopTime());

    for(StopTimeEntry stEntry : stopTimeEntryQueue) {
        System.out.println(stEntry.getTrip().getBlock().getId());
        System.out.println(stEntry.getTrip().getId());
        System.out.println(stEntry.getStop().getId());
        System.out.println(stEntry.getSequence());
        System.out.println(stEntry.getArrivalTime());
        System.out.println(stEntry.getDepartureTime());
        System.out.println();
    }
    
    // FIXME get the first stop in the route from the GTFS DAO
    this.currentStopTime = null;
    
  }

  /**
   * Increment the simulation in time, or stop sequence for the route...
   * FIXME TODO which one?
   */
  public void step() {

  }
}
