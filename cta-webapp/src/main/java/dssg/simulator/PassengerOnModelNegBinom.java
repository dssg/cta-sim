package dssg.simulator;

import umontreal.iro.lecuyer.probdist.NegativeBinomialDist;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

public class PassengerOnModelNegBinom implements PassengerOnModel {
  private final RandomStream rand;

  public PassengerOnModelNegBinom() {
    this.rand = new MRG32k3a();
  }

  @Override
  public int sample(BusState bus, StopState stop) {
    double n = 1;
    double p = 1;
    double u = this.rand.nextDouble();
    int s = NegativeBinomialDist.inverseF(n,p,u);
    return s; 
  }

}
