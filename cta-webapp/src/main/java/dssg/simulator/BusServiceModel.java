package dssg.simulator;

import umontreal.iro.lecuyer.rng.RandomStream;

public interface BusServiceModel {
  public int sample(String routeAndDir, int prevDelta, int schedInterval, RandomStream rng);
}
