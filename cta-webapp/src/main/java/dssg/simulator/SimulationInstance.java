package dssg.simulator;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;

/**
 * This class holds the state/result of a single simulation.
 * @author bwillard
 *
 */
public class SimulationInstance {

  final GtfsDaoImpl store;

  final protected String simulationId;
  final protected String operatorId;
  final protected String busId;

  protected String currentRouteId;
  protected String currentTripId;
  protected String currentBlockId;

  double currentDistanceAlongBlock;
  
  

  public SimulationInstance(GtfsDaoImpl store, String simulationId, String operatorId,
    String busId) {

    this.store = store;
    this.simulationId = simulationId;
    this.operatorId = operatorId;
    this.busId = busId;
  }

  public void step() {
    // TODO finish this...
    
  }
}
