package dssg.simulator;

import org.joda.time.DateMidnight;

public interface PassengerOffModel {
  public int sample(String busStopId, DateMidnight day, int arrivalTime, int arrivingLoad);
}
