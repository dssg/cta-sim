package dssg.simulator;

import java.util.Calendar;

import umontreal.iro.lecuyer.probdist.PoissonDist;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

public class PassengerOnModelPoisson implements PassengerOnModel {
  private final RandomStream rand;

  public PassengerOnModelPoisson() {
    this.rand = new MRG32k3a();
  }

  @Override
  public int sample(String busStopId, Calendar day, int lastDepart, int thisDepart) {
    double lambda = 1;
    double u = this.rand.nextDouble();
    int s = PoissonDist.inverseF(lambda,u); 
    return s;
  }

}
