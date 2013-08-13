package dssg.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Channels;
import java.net.URL;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.transit_data_federation.services.blocks.BlockCalendarService;
import org.onebusaway.transit_data_federation.services.blocks.BlockIndexService;
import org.onebusaway.transit_data_federation.services.blocks.BlockInstance;
import org.onebusaway.transit_data_federation.services.realtime.BlockLocationService;
import org.onebusaway.transit_data_federation.services.transit_graph.AgencyEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.TransitGraphDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.context.ServletContextAware;

import dssg.client.S3CommunicationService;
import dssg.client.SimulationService;
import dssg.shared.FieldVerifier;
import dssg.shared.ProjectConstants;
import dssg.simulator.SimulationBatch;
import dssg.simulator.SimulationBatch;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@Configurable
@SuppressWarnings("serial")
public class SimulationServiceImpl extends RemoteServiceServlet implements
    SimulationService {

  public SimulationServiceImpl() {
    super();
  }

  @Autowired
  public BlockIndexService bis;

  @Autowired
  public BlockLocationService bls;

  @Autowired
  public BlockCalendarService bcs;

  @Autowired
  public TransitGraphDao tgd;

  private Map<String, SimulationBatch> simulations = Maps.newHashMap();

  public BlockIndexService getBis() {
    return bis;
  }

  public BlockLocationService getBls() {
    return bls;
  }

  public BlockCalendarService getBcs() {
    return bcs;
  }

  public TransitGraphDao getTgd() {
    return tgd;
  }

  public Map<String, SimulationBatch> getSimulations() {
    return simulations;
  }

  public SimulationBatch getSimulation(String batchId) {
    return simulations.get(batchId);
  }

  public String submitSimulation(Set<String> routeAndDirs, Date startTime,
      Date endTime) throws IllegalArgumentException {

    String batchId = "";
    for (String routeAndDir : routeAndDirs)
      batchId += routeAndDir + "_";
    batchId += "From_" + startTime + "_To_" + endTime;

    SimulationBatch simBatch = simulations.get(batchId);
    if (simBatch == null) {
      try {
        simBatch = new SimulationBatch(this, batchId, routeAndDirs, startTime,
            endTime);
        simulations.put(batchId, simBatch);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        return null;
      }
    }
    return simBatch.getBatchId();
  }

  /**
   * Return simulation results to client side for display.
   */
  public Map<String, Integer[]> getResults(String route, String direction,
      Date date, Integer startT, Integer endT) {

    System.out
        .println("\nSimulationsServiceImpl.getResults() accessed with params:\n"
            + route + "," + direction + "," + date + "," + startT + "," + endT);

    Integer[] dataMaxLoadN = new Integer[48];
    Integer[] dataMaxFlowN = new Integer[48];
    Integer[] dataMaxLoadS = new Integer[48];
    Integer[] dataMaxFlowS = new Integer[48];

    for (int i = 0; i < 48; i++) {
      dataMaxLoadN[i] = 0;
      dataMaxFlowN[i] = 0;
      dataMaxLoadS[i] = 0;
      dataMaxFlowS[i] = 0;
    }

    Map<String, Integer[]> results = new HashMap<String, Integer[]>();

    // Simulation Service Object
    SimulationServiceImpl simService = new SimulationServiceImpl();
    // Substract 12 hourse from the date
    Date day = new Date(date.getTime() - 12 * 60 * 60 * 1000);
    Date startTimeH = new Date(day.getTime() + startT * 60 * 60 * 1000);
    Date endTimeH = new Date(startTimeH.getTime() + (endT - startT) * 60 * 60
        * 1000);

    Set<String> routeAndDirs = new HashSet<String>();
    if (direction.equals("N"))
      routeAndDirs.add(route + ",1");
    if (direction.equals("S"))
      routeAndDirs.add(route + ",0");
    if (direction.equals("B")) {
      routeAndDirs.add(route + ",1");
      routeAndDirs.add(route + ",0");
    }

    String batchId;
    try {
      batchId = simService.submitSimulation(routeAndDirs, startTimeH, endTimeH);
      SimulationBatch simBatch = simService.getSimulation(batchId);
      try {
        while (!simBatch.awaitTermination(1, TimeUnit.SECONDS)) {
          // check again
        }
        ;

        // North direction results
        if (direction.equals("N") || direction.equals("B")) {

          // Check if the length of the result is correct
          if (simBatch.getProbes().getMaxLoadByTime(route + ",1").length != 48
              || simBatch.getProbes().getMaxFlowByTime(route + ",1").length != 48)
            System.out.println(" Problem with the length of the output:");

          // Max Load results
          System.out
              .println("LOAD, length of simulation result: "
                  + (int) simBatch.getProbes().getMaxLoadByTime(route + ",1").length);
          for (int i = 0; i < 48; i++)
            dataMaxLoadN[i] = (int) simBatch.getProbes().getMaxLoadByTime(
                route + ",1")[i];

          // Max Flow results
          System.out
              .println("FLOW, length of simulation result: "
                  + (int) simBatch.getProbes().getMaxFlowByTime(route + ",1").length);
          for (int i = 0; i < 48; i++)
            dataMaxFlowN[i] = (int) simBatch.getProbes().getMaxFlowByTime(
                route + ",1")[i];
        }

        // South direction results
        if (direction.equals("S") || direction.equals("B")) {

          // Check if the length of the result is correct
          if (simBatch.getProbes().getMaxLoadByTime(route + ",1").length != 48
              || simBatch.getProbes().getMaxFlowByTime(route + ",1").length != 48)
            System.out.println(" Problem with the length of the output:");

          // Max Load results
          System.out
              .println("LOAD, length of simulation result: "
                  + (int) simBatch.getProbes().getMaxLoadByTime(route + ",0").length);
          for (int i = 0; i < 48; i++)
            dataMaxLoadS[i] = (int) simBatch.getProbes().getMaxLoadByTime(
                route + ",0")[i];

          // Max Flow results
          System.out
              .println("FLOW, length of simulation result: "
                  + (int) simBatch.getProbes().getMaxFlowByTime(route + ",0").length);
          for (int i = 0; i < 48; i++)
            dataMaxFlowS[i] = (int) simBatch.getProbes().getMaxFlowByTime(
                route + ",0")[i];
        }

      } catch (InterruptedException e) {
        System.err.println("Simulation batch interrupted:");
        e.printStackTrace();
      }
    } catch (IllegalArgumentException e1) {
      e1.printStackTrace();
      System.err.println("Illegal argument exception on submitting simulation");
    }

    results.put("max_load_N", dataMaxLoadN);
    results.put("max_flow_N", dataMaxFlowN);
    results.put("max_load_S", dataMaxLoadS);
    results.put("max_flow_S", dataMaxFlowS);

    return results;
  }

}
