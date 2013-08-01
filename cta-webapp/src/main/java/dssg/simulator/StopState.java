package dssg.simulator;

import java.util.HashMap;
import java.util.Map;

import org.onebusaway.transit_data_federation.services.transit_graph.StopEntry;

public class StopState {
	private final String stopId;
	
	/**
	 * The time (in seconds since midnight) that the last bus for a given route 
	 * passed this stop.
	 */
	private Map<String,Integer> timeOfLastBusByRoute;
	/**
	 * The number of passengers who were left behind by the last bus on a given
	 * route because it was full.
	 */
	private Map<String,Integer> leftBehindByRoute;

	public StopState(StopEntry stop) {
	  this.stopId = stop.getId().getId();
	  this.timeOfLastBusByRoute = new HashMap<String,Integer>();
	  this.leftBehindByRoute = new HashMap<String,Integer>();
	}
	
	public String getStopId() {
	  return stopId;
	}
	
	public int getTimeOfLastBus(String routeId) {
	  return this.timeOfLastBusByRoute.get(routeId);
	}
	
	public int getLeftBehind(String routeId) {
	  if(this.leftBehindByRoute.containsKey(routeId)) {
  	  return this.leftBehindByRoute.get(routeId);
	  }
	  else {
	    return 0;
	  }
	}
	
	public void update(String routeId, int departureTime, int leftBehind) {
	  this.timeOfLastBusByRoute.put(routeId, departureTime);
	  this.leftBehindByRoute.put(routeId, leftBehind);
	}
	
}