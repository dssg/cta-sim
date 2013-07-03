package dssg.server;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimulationServiceImplTest {

  @Test
  public void test() {
    SimulationServiceImpl simService = new SimulationServiceImpl();
    simService.submitSimulation(null, null, 0, 0);

    fail("Not yet implemented");
  }

}
