package dssg.simulator;

import umontreal.iro.lecuyer.probdist.PoissonDist;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

public class PassengerOnModelPoisson implements PassengerOnModel {
  private final RandomStream rand;

  public PassengerOnModelPoisson() {
    this.rand = new MRG32k3a();
  }

  @Override
  public int sample(BusState bus, StopState stop) {
    double lambda = 1;
    double u = this.rand.nextDouble();
    int s = PoissonDist.inverseF(lambda,u); 
    return s;
  }

}
