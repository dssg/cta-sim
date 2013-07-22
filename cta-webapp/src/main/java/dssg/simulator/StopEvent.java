package dssg.simulator;

import org.onebusaway.transit_data_federation.services.transit_graph.StopTimeEntry;

public class StopEvent {
  final StopTimeEntry ste;
  final BusState bus;
  final StopState stop;
  
  final int alight;
  final int board;
  final int leftBehind;
  final int departingLoad;

  public StopEvent(StopTimeEntry ste, BusState bus, StopState stop, int alight,
      int board, int leftBehind, int departingLoad) {
    this.ste = ste;
    this.bus = bus;
    this.stop = stop;
    this.alight = alight;
    this.board = board;
    this.leftBehind = leftBehind;
    this.departingLoad = departingLoad;
    
    System.out.print("Bus running block " + bus.getRouteId());
    System.out.print(" stops at stop " + stop.getStopId());
    System.out.print(", drops off " + alight);
    System.out.print(" passengers, picks up " + board);
    System.out.print(" passengers, and leaves behind " + leftBehind);
    System.out.println(" with a departing load of " + departingLoad);
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
