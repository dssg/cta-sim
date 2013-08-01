package dssg.simulator;

import org.joda.time.DateMidnight;

public interface PassengerOnModel {
  public int sample(String busStopId, DateMidnight day, int lastDepart, int thisDepart);
}
