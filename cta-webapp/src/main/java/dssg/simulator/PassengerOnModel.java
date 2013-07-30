package dssg.simulator;

import cern.jet.random.Poisson;
import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;

public class BoardingModel {
  private final RandomEngine engine;
  private final Poisson poisson;
  
  public BoardingModel() {
    this.engine = new DRand();
    this.poisson = new Poisson(1,this.engine);
  }
  public int getNumberBoarding(BusState bus, StopState stop) {
    return 1; 
  }
}
