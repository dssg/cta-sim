package dssg.simulator;

import cern.jet.random.Poisson;
import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;

public class PassengerOnModelPoisson implements PassengerOnModel {
  private final RandomEngine engine;
  private final Poisson poisson;

  public PassengerOnModelPoisson() {
    this.engine = new DRand();
    this.poisson = new Poisson(1,this.engine);
  }

  @Override
  public int sample(BusState bus, StopState stop) {
    return 1; 
  }

}
