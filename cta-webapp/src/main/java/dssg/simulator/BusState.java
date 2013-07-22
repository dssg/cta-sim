package dssg.simulator;

import org.onebusaway.transit_data_federation.services.transit_graph.BlockEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.RouteEntry;

public class BusState {
  final RouteEntry route;
  final String routeId;
  final BlockEntry block;
  final String blockId;
	final int max_capacity;
	private int passengers_in;
	
	public BusState(RouteEntry route, BlockEntry block) {
	  this.route = route;
	  this.routeId = route.getId().getId();
	  this.block = block;
	  this.blockId = block.getId().getId();
		this.max_capacity = 80; 
		this.passengers_in = 0;
	}

  public int getLoad() {
    return this.passengers_in;
  }
  
  public String getRouteId() {
    return this.routeId;
  }
  
  /**
   * Updates the bus state after making a stop.
   * @param alighting the number of passengers who get off the bus
   * @param boarding  the number of passengers who want to board the bus
   * @return          the number of passengers who actually board the bus
   */
  public int update(int alighting, int boarding) {

    if(alighting > this.passengers_in) {
      // TODO: should this be an exception?
      System.err.println("Warning: attempting to alight more passengers than in bus");
      alighting = this.passengers_in;
    }
    this.passengers_in -= alighting;
    
    this.passengers_in += boarding;
    int left_behind = 0;
    int actual_board = boarding;
    if(this.passengers_in > this.max_capacity) {
      left_behind = this.passengers_in - this.max_capacity;
      actual_board = boarding - left_behind;
      this.passengers_in = this.max_capacity;
    }

    return actual_board;
  }
}
