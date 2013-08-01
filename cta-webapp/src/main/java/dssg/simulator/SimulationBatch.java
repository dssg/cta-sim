package dssg.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import dssg.client.MyParameters;
import dssg.server.SimulationServiceImpl;
import dssg.shared.S3Communication;

/**
 * This class holds the state/results of a batch of simulations.
 * 
 * @author jtbates
 * 
 */

public class SimulationBatch {
  static private final int THREAD_COUNT;
  static {
    final int numProcessors =
        Runtime.getRuntime().availableProcessors();

    if (numProcessors <= 2) {
      THREAD_COUNT = numProcessors;
    } else {
      THREAD_COUNT = numProcessors - 1;
    }
  }

  static private final String REL_PARAM_PATH = "src/main/resources/params/";
  static private final File PARAM_PATH = new File(System.getProperty("user.dir"),REL_PARAM_PATH);
  static private final String REL_BOARD_PARAM_PATH = "boardParams.json";

  private final ExecutorService executor = Executors
      .newFixedThreadPool(THREAD_COUNT);
  private final int NUM_RUNS = 1;

  protected final SimulationServiceImpl simService;
  protected final String batchId;
  
  protected final PassengerOnModel boardModel;
  protected final PassengerOffModel alightModel;
  
  public SimulationBatch(SimulationServiceImpl simService, String batchId,
      String routeId, Date startTime, Date endTime) throws FileNotFoundException {
    this(simService,batchId,routeId,startTime,endTime,PARAM_PATH);
  }
  
  public SimulationBatch(SimulationServiceImpl simService, String batchId,
      String routeId, Date startTime, Date endTime, File paramPath) throws FileNotFoundException {
    this.simService = simService;
    this.batchId = batchId;

    BufferedReader boardParamReader = new BufferedReader(new FileReader(new File(paramPath,REL_BOARD_PARAM_PATH)));
    this.boardModel = new PassengerOnModelNegBinom(boardParamReader); 
    this.alightModel = null;
    
    
    for (int i = 0; i < NUM_RUNS; i++) {
      this.executor.execute(new SimulationRun(this, i, routeId, startTime, endTime));
    }
    this.executor.shutdown();
  }
  
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return this.executor.awaitTermination(timeout, unit);
  }

  public static int getThreadCount() {
    return THREAD_COUNT;
  }

  public int getNumRuns() {
    return NUM_RUNS;
  }
  
  public String getBatchId() {
    return batchId;
  }
}
