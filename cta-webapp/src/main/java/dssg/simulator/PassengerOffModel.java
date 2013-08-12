package dssg.simulator;

import org.joda.time.DateMidnight;

import umontreal.iro.lecuyer.rng.RandomStream;

public interface PassengerOffModel {
  public int sample(String busStopId, DateMidnight day, int arrivalTime, int arrivingLoad, RandomStream rng);
}
