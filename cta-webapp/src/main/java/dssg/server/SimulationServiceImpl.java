package dssg.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.onebusaway.transit_data_federation.services.blocks.BlockCalendarService;
import org.onebusaway.transit_data_federation.services.blocks.BlockIndexService;
import org.onebusaway.transit_data_federation.services.realtime.BlockLocationService;
import org.onebusaway.transit_data_federation.services.transit_graph.TransitGraphDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import dssg.client.SimulationService;
import dssg.simulator.SimulationBatch;
import dssg.simulator.StatProbesBatch;

import com.google.common.collect.Maps;
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
  
  /**
   * Method that executes bash script for parameter estimation
   * 
   * @param route
   * @return String
   */
  @Override
  public String estimateParameters(String route) {
    System.out.println("\n[WA INFO] Attempt to run script for estimation process for parameters on route: "+ route);
 // TODO correct path to R script
    String cmd = "/var/lib/ctasim/testScript.sh";
    
    // Building process to execute
    ProcessBuilder pb = new ProcessBuilder("/bin/sh", cmd, route);
    try {
      Process p = pb.start();
      p.waitFor();
      // Read lines outputted from the script execution
      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line = reader.readLine();        
      while (line != null) {
        System.out.println("[WA SCRIPT INFO] "+line);
        line = reader.readLine();
      }
    } catch (IOException e) {
      System.out.println("[WA FAIL] Problem with the execution of the script.");
      e.printStackTrace();
    } catch (InterruptedException e) {
      System.out.println("[WA FAIL] Problem with the execution of the script.");
      e.printStackTrace();
    }
    
    return "Done";
  }

  /**
   * Method to submit a simulation based on input parameters
   * 
   * @param routeAndDirs
   * @param startTime
   * @param endTime
   * @return String
   */
  @Override
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
   * Run simulation and return simulation results to client side for display.
   * 
   * @param route
   * @param direction
   * @param date
   * @param startT
   * @param endT
   */
  @Override
  public Map<String, Integer[]> runSimulation(String route, String direction,
      Date date, Integer startT, Integer endT) {

    System.out
        .println("\n[WA INFO] SimulationsServiceImpl.getResults() accessed with parameters: Route "
            + route
            + ", Direction "
            + direction
            + ", Date "
            + date
            + ", Start Time " + startT + ", End Time " + endT);

    Integer[] dataMaxLoadN = new Integer[48];
    Integer[] dataMaxFlowN = new Integer[48];
    Integer[] dataMaxLoadS = new Integer[48];
    Integer[] dataMaxFlowS = new Integer[48];

    Integer[][] dataLoadByTimeStopN;
    Integer[][] dataFlowByTimeStopN;
    Integer[][] dataLoadByTimeStopS;
    Integer[][] dataFlowByTimeStopS;

    for (int i = 0; i < 48; i++) {
      dataMaxLoadN[i] = 0;
      dataMaxFlowN[i] = 0;
      dataMaxLoadS[i] = 0;
      dataMaxFlowS[i] = 0;
    }

    // Simulation Service Object
    SimulationServiceImpl simService = new SimulationServiceImpl();
    Date day = new Date(date.getTime()-(6*60*60*1000));
    Date startTimeH = new Date(day.getTime() + startT * 60 * 60 * 1000);
    Date endTimeH = new Date(startTimeH.getTime() + (endT - startT) * 60 * 60
        * 1000);
    // Return variable
    Map<String, Integer[]> results = new HashMap<String, Integer[]>();

    // Adding routes and directions for the simmulation.
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
      System.out.println("[WA INFO] Starting Simulation with parameters:" +
      		" routeAndDirs " + routeAndDirs + ", startTimeH " + startTimeH +", endTimeH "+ endTimeH);
      batchId = simService.submitSimulation(routeAndDirs, startTimeH, endTimeH);
      SimulationBatch simBatch = simService.getSimulation(batchId);
      StatProbesBatch statProbe = simBatch.getProbes();
      try {
        while (!simBatch.awaitTermination(1, TimeUnit.SECONDS)) {
          // check again
        }
        ;

        System.out.println("[WA INFO] Simulation Finished.");

        // North direction results
        if (direction.equals("N") || direction.equals("B")) {
          int stops;
          System.out.println("[WA INFO] North results.");

          // Check if the length of the result is correct
          if (statProbe.getMaxLoadByTime(route + ",1").length != 48
              || statProbe.getMaxFlowByTime(route + ",1").length != 48
              || statProbe.getQ3LoadByTimeByStop(route + ",1").length != 48)
            System.out
                .println("[WA FAIL] Problem with the length of the output:");

          // LOAD results

          // Max Load
          System.out
              .println("[WA INFO] Max Load, length of simulation result: "
                  + statProbe.getMaxLoadByTime(route + ",1").length);
          for (int i = 0; i < 48; i++)
            dataMaxLoadN[i] = (int) statProbe.getMaxLoadByTime(route + ",1")[i];

          // Load by time and stop
          System.out
              .println("[WA INFO] Load by Time Stop, length of simulation result: "
                  + statProbe.getQ3LoadByTimeByStop(route + ",1").length+ " x "
                      + statProbe.getQ3LoadByTimeByStop(route + ",1")[0].length);
          stops = statProbe.getQ3LoadByTimeByStop(route + ",1")[0].length;
          dataLoadByTimeStopN = new Integer[48][stops];
          for (int i = 0; i < 48; i++) {
            for (int j = 0; j < stops; j++) {
              dataLoadByTimeStopN[i][j] = (int) statProbe
                  .getQ3LoadByTimeByStop(route + ",1")[i][j];
            }
            results.put("load_timestop_N_hour" + i, dataLoadByTimeStopN[i]);
          }

          // FLOW results

          // Max Flow results
          System.out
              .println("[WA INFO] Max Flow, length of simulation result: "
                  + statProbe.getMaxFlowByTime(route + ",1").length);
          for (int i = 0; i < 48; i++)
            dataMaxFlowN[i] = (int) statProbe.getMaxFlowByTime(route + ",1")[i];

          // Flow by time and stop
          System.out
              .println("[WA INFO] Flow by Time Stop, length of simulation result: "
                  + statProbe.getQ3FlowByTimeByStop(route + ",1").length+ " x "
                      + statProbe.getQ3FlowByTimeByStop(route + ",1")[0].length);
          stops = statProbe.getQ3FlowByTimeByStop(route + ",1")[0].length;
          dataFlowByTimeStopN = new Integer[48][stops];
          for (int i = 0; i < 48; i++) {
            for (int j = 0; j < stops; j++) {
              dataFlowByTimeStopN[i][j] = (int) statProbe
                  .getQ3FlowByTimeByStop(route + ",1")[i][j];
            }
            results.put("flow_timestop_N_hour" + i, dataFlowByTimeStopN[i]);
          }
        }

        // South direction results
        if (direction.equals("S") || direction.equals("B")) {
          int stops;
          System.out.println("[WA INFO] South results.");

          // Check if the length of the result is correct
          if (statProbe.getMaxLoadByTime(route + ",0").length != 48
              || statProbe.getMaxFlowByTime(route + ",0").length != 48
              || statProbe.getQ3LoadByTimeByStop(route + ",0").length != 48)
            System.out.println("[FAIL] Problem with the length of the output:");

          // LOAD results
          
          // Max Load
          System.out
              .println("[WA INFO] Max Load, length of simulation result: "
                  + statProbe.getMaxLoadByTime(route + ",0").length);
          for (int i = 0; i < 48; i++)
            dataMaxLoadS[i] = (int) statProbe.getMaxLoadByTime(route + ",0")[i];
          
          // Load by time and stop
          System.out
              .println("[WA INFO] Load by Time Stop, length of simulation result: "
                  + statProbe.getQ3LoadByTimeByStop(route + ",0").length+ " x "
                      + statProbe.getQ3LoadByTimeByStop(route + ",0")[0].length);
          stops = statProbe.getQ3LoadByTimeByStop(route + ",0")[0].length;
          dataLoadByTimeStopS = new Integer[48][stops];
          for (int i = 0; i < 48; i++) {
            for (int j = 0; j < stops; j++) {
              dataLoadByTimeStopS[i][j] = (int) statProbe
                  .getQ3LoadByTimeByStop(route + ",0")[i][j];
            }
            results.put("load_timestop_S_hour" + i, dataLoadByTimeStopS[i]);
          }

          // FLOW results
          // Max Flow
          System.out
              .println("[WA INFO] Max Flow, length of simulation result: "
                  + statProbe.getMaxFlowByTime(route + ",0").length);
          for (int i = 0; i < 48; i++)
            dataMaxFlowS[i] = (int) statProbe.getMaxFlowByTime(route + ",0")[i];
          
          // Flow by time and stop
          System.out
              .println("[WA INFO] Flow by Time Stop, length of simulation result: "
                  + statProbe.getQ3FlowByTimeByStop(route + ",0").length + " x "
                  + statProbe.getQ3FlowByTimeByStop(route + ",0")[0].length);
          stops = statProbe.getQ3FlowByTimeByStop(route + ",0")[0].length;
          dataFlowByTimeStopS = new Integer[48][stops];
          for (int i = 0; i < 48; i++) {
            for (int j = 0; j < stops; j++) {
              dataFlowByTimeStopS[i][j] = (int) statProbe
                  .getQ3FlowByTimeByStop(route + ",0")[i][j];
            }
            results.put("flow_timestop_S_hour" + i, dataFlowByTimeStopS[i]);
          }
        }

      } catch (InterruptedException e) {
        System.err.println("[WA FAIL] Simulation batch interrupted:");
        e.printStackTrace();
      }
    } catch (IllegalArgumentException e1) {
      e1.printStackTrace();
      System.err
          .println("[WA FAIL] Illegal argument exception on submitting simulation");
    }

    // Simulation results added to return variable (Map)

    System.out.println("[WA INFO] Putting data into results to return.");
    results.put("max_load_N", dataMaxLoadN);
    results.put("max_flow_N", dataMaxFlowN);
    results.put("max_load_S", dataMaxLoadS);
    results.put("max_flow_S", dataMaxFlowS);

    // Return results to client side
    System.out.println("[WA INFO] Returning results to client side.");
    return results;
  }

}
