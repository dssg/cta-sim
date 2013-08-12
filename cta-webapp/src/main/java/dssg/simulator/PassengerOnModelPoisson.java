package dssg.simulator;

import org.joda.time.DateMidnight;

import umontreal.iro.lecuyer.probdist.PoissonDist;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

public class PassengerOnModelPoisson implements PassengerOnModel {

  public PassengerOnModelPoisson() {
    // load params
  }

  @Override
  public int sample(String busStopId, DateMidnight day, int lastDepart, int thisDepart, RandomStream rng) {
    double lambda = 1;
    double u = rng.nextDouble();
    int s = PoissonDist.inverseF(lambda,u); 
    return s;
  }

}
