package dssg.simulator;

import java.util.Calendar;

public interface PassengerOnModel {
  public int sample(String busStopId, Calendar day, int lastDepart, int thisDepart);
}
