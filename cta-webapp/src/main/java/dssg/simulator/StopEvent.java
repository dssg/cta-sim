package dssg.simulator;

import org.onebusaway.transit_data_federation.services.transit_graph.BlockStopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.StopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.TripEntry;

public class StopEvent {
  final StopTimeEntry ste;
  final String tripId;
  final String routeId;
  final String directionId;
  final String blockId;
  
  final int alight;
  final int board;
  final int leftBehind;
  final int departingLoad;

  public StopEvent(BlockStopTimeEntry bste, int alight,
      int board, int leftBehind, int departingLoad) {
    this.ste = bste.getStopTime();
    TripEntry trip = this.ste.getTrip();
    this.tripId = trip.getId().getId();
    this.routeId = trip.getRoute().getId().getId();
    this.directionId = trip.getDirectionId();
    this.blockId = trip.getBlock().getId().getId();

    this.alight = alight;
    this.board = board;
    this.leftBehind = leftBehind;
    this.departingLoad = departingLoad;
  }

  public int getActualArrivalTime() {
    return ste.getArrivalTime();
  }

  public int getActualDepartureTime() {
    return ste.getDepartureTime();
  }

  public int getScheduledArrivalTime() {
    return ste.getArrivalTime();
  }

  public int getScheduledDepartureTime() {
    return ste.getDepartureTime();
  }
  
}
