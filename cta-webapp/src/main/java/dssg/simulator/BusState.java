package dssg.simulator;

import org.onebusaway.transit_data_federation.services.transit_graph.BlockEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockStopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockTripEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.RouteEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.TripEntry;

import dssg.shared.ProjectConstants;

public class BusState {
  final private String blockId;
	final private int maxCapacity;

	private int passengersIn;
	private LogStopEvent prevStopEvent;
  private BlockStopTimeEntry nextStop;
  private String currentRouteId;
  private String currentDirectionId;
	
	public BusState(BlockStopTimeEntry bste, String vehicleType) {
	  BlockTripEntry bte = bste.getTrip();
    TripEntry tripEntry = bte.getTrip();
    RouteEntry routeEntry = tripEntry.getRoute();
    BlockEntry blockEntry = tripEntry.getBlock();

	  this.blockId = blockEntry.getId().getId();
	  this.currentRouteId = routeEntry.getId().getId();
    this.currentDirectionId = tripEntry.getDirectionId();
	  this.nextStop = bste;

		this.maxCapacity = ProjectConstants.BUS_PRACTICAL_MAX_CAPACITY.get(vehicleType);
		this.passengersIn = 0;
		this.prevStopEvent = null;
	}

  public int getLoad() {
    return this.passengersIn;
  }

  public String getBlockId() {
    return this.blockId;
  }

  public String getCurrentRouteId() {
    return this.currentRouteId;
  }

  public String getCurrentDirectionId() {
    return this.currentDirectionId;
  }
  
  public BlockStopTimeEntry getNextStop() {
    return this.nextStop;
  }

  /**
   * Updates the bus state after making a stop.
   * @param alighting the number of passengers who get off the bus
   * @param boarding  the number of passengers who want to board the bus
   * @return          the number of passengers who actually board the bus
   */
  public int update(int alighting, int boarding) {

    if(alighting > this.passengersIn) {
      // TODO: should this be an exception?
      System.err.println("Warning: attempting to alight more passengers than in bus");
      alighting = this.passengersIn;
    }
    this.passengersIn -= alighting;
    
    this.passengersIn += boarding;
    int left_behind = 0;
    int actual_board = boarding;
    if(this.passengersIn > this.maxCapacity) {
      left_behind = this.passengersIn - this.maxCapacity;
      actual_board = boarding - left_behind;
      this.passengersIn = this.maxCapacity;
    }

    return actual_board;
  }

  public BlockStopTimeEntry depart(LogStopEvent prevStopEvent) {
    this.prevStopEvent = prevStopEvent;
    if(this.nextStop.hasNextStop()) {
      BlockStopTimeEntry prevStop = this.nextStop;
      BlockStopTimeEntry stop = prevStop.getNextStop();
      TripEntry trip = stop.getTrip().getTrip();

      this.currentRouteId = trip.getRoute().getId().getId();
      this.currentDirectionId = trip.getDirectionId();
      this.nextStop = stop;

      return stop;
    }
    else return null;
  }

  public LogStopEvent getPrevStopEvent() {
    return this.prevStopEvent;
  }
}
