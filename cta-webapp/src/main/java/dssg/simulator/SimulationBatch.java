package dssg.simulator;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import dssg.client.MyParameters;
import dssg.server.SimulationServiceImpl;

/**
 * This class holds the state/results of a batch of simulations.
 * 
 * @author jtbates
 * 
 */

public class SimulationBatch {

  final protected SimulationServiceImpl simService;
  final protected String batchId;

  static public final int THREAD_COUNT;

  static {
    final int numProcessors =
        Runtime.getRuntime().availableProcessors();

    if (numProcessors <= 2) {
      THREAD_COUNT = numProcessors;
    } else {
      THREAD_COUNT = numProcessors - 1;
    }
  }

  private final ExecutorService executor = Executors
      .newFixedThreadPool(THREAD_COUNT);

  private final int NUM_RUNS = 1;
  
  public SimulationBatch(SimulationServiceImpl simService, String batchId,
      String routeId, Date startDate, long startTime, long endTime, List<MyParameters> parameters) {
    this.simService = simService;
    this.batchId = batchId;
    
    for (int i = 0; i < NUM_RUNS; i++) {
      this.executor.execute(new SimulationRun(this, i, routeId, startDate, startTime, endTime, parameters));
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
