package dssg.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

import dssg.client.S3CommunicationService;
import dssg.simulator.SimulationBatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:dssg/application-context-webapp.xml")
public class SimulationServiceImplTest {

  @Test
  public void test() {
    SimulationServiceImpl simService = new SimulationServiceImpl();

    Calendar testCal = GregorianCalendar.getInstance(TimeZone.getTimeZone(SimulationBatch.AGENCY_TIMEZONE));
    testCal.clear(Calendar.MILLISECOND);
    testCal.set(2013, Calendar.FEBRUARY,11,0,0,0);
    Date day = testCal.getTime();

    Date startTime = new Date(day.getTime() + 3*60*60*1000);
    Date endTime = new Date(day.getTime() + 27*60*60*1000);
    
    String batchId;
    try {
      batchId = simService.submitSimulation("6", startTime, endTime);
      SimulationBatch simBatch = simService.getSimulation(batchId);
      try {
        while(!simBatch.awaitTermination(1, TimeUnit.SECONDS)) {
          // check again
        };
      } catch (InterruptedException e) {
        System.err.println("Simulation batch interrupted:");
        e.printStackTrace();
      }
    } catch (IllegalArgumentException e1) {
      e1.printStackTrace();
      fail("Illegal argument exception on submitting simulation");
    }

    fail("Not yet implemented");
  }

}
