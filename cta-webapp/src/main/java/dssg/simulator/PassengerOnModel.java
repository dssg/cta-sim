package dssg.simulator;

import org.joda.time.DateMidnight;

import umontreal.iro.lecuyer.rng.RandomStream;

public interface PassengerOnModel {
  public int sample(String busStopId, DateMidnight day, int lastDepart, int thisDepart, RandomStream rng);
}
