package dssg.simulator;

import java.util.Date;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;

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
  final GtfsDaoImpl store;
  final protected String simulationId;
  final Route route;
  final protected String routeId;
  final protected Date startDate;
  final protected long startTime;
  final protected long endTime;


  /*
   * Simulation's running properties
   */
  StopTime currentStopTime;
  double currentDistanceAlongBlock;

  public SimulationInstance(GtfsDaoImpl store, String simulationId,
    String routeId, Date startDate, long startTime, long endTime) {
    this.store = store;
    this.simulationId = simulationId;
    this.routeId = routeId;
    this.startDate = startDate;
    this.startTime = startTime;
    this.endTime = endTime;
    
    this.route = store.getRouteForId(AgencyAndId.convertFromString(routeId));
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
