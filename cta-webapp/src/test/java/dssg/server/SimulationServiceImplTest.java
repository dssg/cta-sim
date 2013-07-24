package dssg.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

import dssg.client.MyParameters;
import dssg.client.S3CommunicationService;
import dssg.simulator.SimulationBatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:dssg/application-context-webapp.xml")
public class SimulationServiceImplTest {

  @Test
  public void test() {
    SimulationServiceImpl simService = new SimulationServiceImpl();
    S3CommunicationService mockService = mock(S3CommunicationServiceImpl.class);
    when(mockService.downloadParameters()).thenReturn(Lists.newArrayList(
        new MyParameters(1, 1.8d)));
    simService.setS3ComunicationService(mockService);
    Calendar testCal = GregorianCalendar.getInstance();
    testCal.set(2013, Calendar.FEBRUARY,11);
    Date testDate = testCal.getTime();
    String batchId = simService.submitSimulation("6", testDate, 3*60*60, 27*60*60);
    
    SimulationBatch simBatch = simService.getSimulation(batchId);
    try {
      while(!simBatch.awaitTermination(1, TimeUnit.SECONDS)) {
        // check again
      };
    } catch (InterruptedException e) {
      System.err.println("Simulation batch interrupted:");
      e.printStackTrace();
    }

    fail("Not yet implemented");
  }

}
