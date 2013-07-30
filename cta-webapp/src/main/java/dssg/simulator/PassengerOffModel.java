package dssg.simulator;

import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;

public class AlightingModel {
  private final RandomEngine engine;
  
  public AlightingModel() {
    this.engine = new DRand();
  }
  public int getNumberAlighting(BusState bus, StopState stop) {
    return 1;
  }

}
