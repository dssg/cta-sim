package dssg.server;

import static org.junit.Assert.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dssg.shared.Config;
import dssg.simulator.SimulationBatch;
import dssg.simulator.StatProbesBatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:dssg/application-context-webapp.xml")
public class SimulationServiceImplTest {

  /**
   * Test Class for simulations
   * This class lets you test the simulation without having the webapp on
   */
  @Test
  public void test() {
    SimulationServiceImpl simService = new SimulationServiceImpl();
    
    // Create date for simulation
    Calendar testCal = Calendar.getInstance(TimeZone.getTimeZone(Config.AGENCY_TIMEZONE));
    testCal.clear(Calendar.MILLISECOND);
    testCal.set(2013, Calendar.FEBRUARY,11,0,0,0);
    Date day = testCal.getTime();
    
    // Create time for simulation
    Date startTime = new Date(day.getTime() + 3*60*60*1000);
    Date endTime = new Date(startTime.getTime() + 24*60*60*1000);
    
    // Arrays for output data from simulation
    Integer[][] dataLoadByTimeStopN;
    Integer[][] dataFlowByTimeStopN;
    Map<String, Integer[]> results = new HashMap<String, Integer[]>();
    
    String batchId;
    try {
      Set<String> routeAndDirs = new HashSet<String>();
      routeAndDirs.add("6,0");
      // Run Simulation
      batchId = simService.submitSimulation(routeAndDirs, startTime, endTime);
      SimulationBatch simBatch = simService.getSimulation(batchId);
      // Return value probe for statistics
      StatProbesBatch statProbe = simBatch.getProbes();
      try {
        while(!simBatch.awaitTermination(1, TimeUnit.SECONDS)) {
          // check again
        };
        // When simulation complete probe for statistics and include the in array
        int stops = statProbe.getQ3LoadByTimeByStop("6,0")[0].length;
        dataLoadByTimeStopN = new Integer[48][stops];
        dataFlowByTimeStopN = new Integer[48][stops];
        for (int i = 0; i < 48; i++) {
          for (int j = 0; j < stops; j++) {
            dataLoadByTimeStopN[i][j] = (int) statProbe
                .getQ3LoadByTimeByStop("6,0")[i][j];
            dataFlowByTimeStopN[i][j] = (int) statProbe
                .getQ3FlowByTimeByStop("6,0")[i][j];
          }
          results.put("load_timestop_N_hour" + i, dataLoadByTimeStopN[i]);
          System.out.println(dataLoadByTimeStopN[i][1]);
          System.out.println(dataFlowByTimeStopN[i][1]);
        }
        
      } catch (InterruptedException e) {
        System.err.println("Simulation batch interrupted:");
        e.printStackTrace();
      }
    } catch (IllegalArgumentException e1) {
      e1.printStackTrace();
      fail("Illegal argument exception on submitting simulation");
    }
    //fail("Not yet implemented");
  }

}
