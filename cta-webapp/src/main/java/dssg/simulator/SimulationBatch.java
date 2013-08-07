package dssg.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import dssg.server.SimulationServiceImpl;
import dssg.shared.ProjectConstants;

/**
 * This class holds the state/results of a batch of simulations.
 * 
 * @author jtbates
 * 
 */

public class SimulationBatch {
  private static final DateTimeZone TIMEZONE = DateTimeZone.forID(ProjectConstants.AGENCY_TIMEZONE);
  private static final String REL_PARAM_PATH = "params";
  private static final File PARAM_PATH = new File(ProjectConstants.RESOURCES_PATH,REL_PARAM_PATH);
  private static final String REL_BOARD_PARAM_PATH = "boardParams.json";
  private static final String REL_ALIGHT_PARAM_PATH = "alightParams.json";

  private static final int THREAD_COUNT;
  static {
    final int numProcessors =
        Runtime.getRuntime().availableProcessors();

    if (numProcessors <= 2) {
      THREAD_COUNT = numProcessors;
    } else {
      THREAD_COUNT = numProcessors - 1;
    }
  }

  private final int NUM_RUNS = 1;

  private final ExecutorService executor;
  private final Semaphore runsFinished;
  protected final StatProbesBatch probes;
  protected final boolean computeStats;
  protected final LogBatch logger;
  protected final boolean saveLogs;

  protected final SimulationServiceImpl simService;
  protected final String batchId;
  
  protected final PassengerOnModel boardModel;
  protected final PassengerOffModel alightModel;
  
  public SimulationBatch(SimulationServiceImpl simService, String batchId,
      String routeId, Date startTime, Date endTime) throws FileNotFoundException {
    this(simService,batchId,routeId,startTime,endTime,PARAM_PATH, true, false);
  }
  
  public SimulationBatch(SimulationServiceImpl simService, String batchId,
      String routeId, Date startTime, Date endTime, File paramPath,
      final boolean computeStats, final boolean saveLogs) throws FileNotFoundException {
    this.executor = Executors.newFixedThreadPool(THREAD_COUNT);
    this.runsFinished = new Semaphore(0);
    this.computeStats = computeStats;
    if(computeStats) this.probes = new StatProbesBatch(NUM_RUNS);
    this.saveLogs = saveLogs;
    if(saveLogs) this.logger = new LogBatch(batchId);

    this.simService = simService;
    this.batchId = batchId;

    BufferedReader boardParamReader = new BufferedReader(new FileReader(new File(paramPath,REL_BOARD_PARAM_PATH)));
    BufferedReader alightParamReader = new BufferedReader(new FileReader(new File(paramPath,REL_ALIGHT_PARAM_PATH)));
    this.boardModel = new PassengerOnModelNegBinom(boardParamReader); 
    this.alightModel = new PassengerOffModelBinom(alightParamReader);
    
    // switch to Joda time internally
    DateTime jStartTime = new DateTime(startTime.getTime(),TIMEZONE);
    DateTime jEndTime = new DateTime(endTime.getTime(),TIMEZONE);
    
    for (int i = 0; i < NUM_RUNS; i++) {
      this.executor.execute(new SimulationRun(this, i, routeId, jStartTime, jEndTime));
    }
    this.executor.execute(this.logger);

    this.executor.execute(new Runnable() {
      @Override public void run() {
        try {
          runsFinished.acquire(NUM_RUNS);
          //Code to execute when all runs have completed
          if(computeStats) probes.finish();
          if(saveLogs) logger.finish();
        } catch (InterruptedException e) {
          if(computeStats) probes.cancel();
          if(saveLogs) logger.cancel();
        }
      }});

    this.executor.shutdown();
  }
  
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return this.executor.awaitTermination(timeout, unit);
  }

  public void handleEvent(LogStopEvent event) {
    if(this.saveLogs) this.logger.process(event);
    if(this.computeStats) this.probes.queue(event);
  }
  public void runCallback(SimulationRun run) {
    this.runsFinished.release();
  }

  public int getNumRuns() {
    return NUM_RUNS;
  }
  
  public String getBatchId() {
    return this.batchId;
  }
}
