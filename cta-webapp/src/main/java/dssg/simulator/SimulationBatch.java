package dssg.simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.transit_data_federation.services.blocks.BlockInstance;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockConfigurationEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockStopTimeEntry;
import org.onebusaway.transit_data_federation.services.transit_graph.BlockTripEntry;

import com.google.common.base.Preconditions;

import dssg.server.SimulationServiceImpl;
import dssg.shared.Config;

/**
 * This class holds the state/results of a batch of simulations.
 * 
 * @author jtbates
 * If you get a nullpointer exception for line 115 or 116 clean the proyect and try again.
 * 
 */

public class SimulationBatch {
  private static final DateTimeZone TIMEZONE = DateTimeZone.forID(Config.AGENCY_TIMEZONE);

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

  private final int NUM_RUNS = 30;

  private final ExecutorService executor;
  private final Semaphore runsFinished;
  protected final StatProbesBatch probes;
  protected final boolean computeStats;
  protected final LogBatch logger;
  protected final boolean saveLogs;

  protected final SimulationServiceImpl simService;
  protected final String batchId;
  
  protected final BusServiceModel serviceModel;
  protected final PassengerOnModel boardModel;
  protected final PassengerOffModel alightModel;

  public SimulationBatch(SimulationServiceImpl simService, String batchId,
      Set<String> routeAndDirs, Date startTime, Date endTime) throws FileNotFoundException {
    this(simService,batchId,routeAndDirs,startTime,endTime,true,false,
         new BufferedReader(new FileReader(Config.MODEL_FIT_BOARD)),
         new BufferedReader(new FileReader(Config.MODEL_FIT_ALIGHT)),
         null);
  }

  public SimulationBatch(SimulationServiceImpl simService, String batchId,
      Set<String> routeAndDirs, Date startTime, Date endTime,
      final boolean computeStats, final boolean saveLogs, Reader boardParamReader,
      Reader alightParamReader, Reader serviceParamReader) throws FileNotFoundException {
    this.executor = Executors.newFixedThreadPool(THREAD_COUNT);
    this.runsFinished = new Semaphore(0);

    this.saveLogs = saveLogs;
    if(saveLogs) this.logger = new LogBatch(batchId);
    else this.logger = null;

    this.simService = simService;
    this.batchId = batchId;

    this.boardModel = new PassengerOnModelNegBinom(boardParamReader); 
    this.alightModel = new PassengerOffModelBinom(alightParamReader);
    this.serviceModel = new BusServiceModelNormal(serviceParamReader);
    
    // switch to Joda time internally
    DateTime jStartTime = new DateTime(startTime.getTime(),TIMEZONE);
    DateTime jEndTime = new DateTime(endTime.getTime(),TIMEZONE);
    DateMidnight day = jStartTime.toDateMidnight();

    Map<String,List<String>> routeToDirs = new HashMap<String,List<String>>();
    List<BlockInstance> blocks = new ArrayList<BlockInstance>();
    for(String routeAndDir : routeAndDirs) {
      String[] split = routeAndDir.split(",",2);
      String taroute = split[0];
      String dir_group = split[1];
      AgencyAndId routeAgencyAndId = AgencyAndId.convertFromString(Config.AGENCY_NAME + "_" + taroute);
      List<BlockInstance> routeBlocks = simService.bcs.getActiveBlocksForRouteInTimeRange(routeAgencyAndId,
              jStartTime.getMillis(), jEndTime.getMillis());
      Preconditions.checkState(!routeBlocks.isEmpty());
      blocks.addAll(routeBlocks);
    }
    
    // final canonical pattern (longest) for each routeAndDir
    // pattern is encoded with bt_ver + patternid in the shapeId
    // FIXME: Why can't I use the interfaces in these declarations?
    HashMap<String,ArrayList<String>> routeAndDirToPatterns;
    routeAndDirToPatterns = new HashMap<String,ArrayList<String>>();
    HashMap<String,ArrayList<String>> routeAndDirToStops;
    routeAndDirToStops = new HashMap<String,ArrayList<String>>();
    Map<String,ArrayList<String>> patternToStopList = new HashMap<String,ArrayList<String>>();
    for(BlockInstance b : blocks) {
      for(BlockTripEntry bte : b.getBlock().getTrips()) {
        String taroute = bte.getTrip().getRoute().getId().getId();
        String direction = bte.getTrip().getDirectionId();
        String routeAndDir = taroute + "," + direction;
        if(!routeAndDirs.contains(routeAndDir)) continue;
        if(!routeAndDirToPatterns.containsKey(routeAndDir)) {
          routeAndDirToPatterns.put(routeAndDir, new ArrayList<String>());
          routeAndDirToStops.put(routeAndDir, new ArrayList<String>());
        }
        ArrayList<String> patterns = routeAndDirToPatterns.get(routeAndDir);
        String pattern = bte.getTrip().getShapeId().getId();
        if (patterns.contains(pattern)) continue;
        ArrayList<String> stopList = new ArrayList<String>();
        for(BlockStopTimeEntry bste : bte.getStopTimes()) {
          String tageoid = bste.getStopTime().getStop().getId().getId();
          stopList.add(tageoid);
        }
        int patternLength = stopList.size();
        patternToStopList.put(pattern, stopList);
        int i = 0;
        for(; i < patterns.size(); i++) {
          String p2 = patterns.get(i);
          if(patternLength > patternToStopList.get(p2).size())
            break;
        }
        patterns.add(i,pattern);
        if(i == 0)
          routeAndDirToStops.put(routeAndDir, stopList);
      }
    }

    // build tageoid -> canonical pattern stop number for each route and direction
    HashMap<String,LinkedHashMap<String,Integer>> routeAndDirToStopIdToNum;
    routeAndDirToStopIdToNum = new HashMap<String,LinkedHashMap<String,Integer>>();
    for(String routeAndDir : routeAndDirToStops.keySet()) {
      LinkedHashMap<String,Integer> stopIdToNum = new LinkedHashMap<String,Integer>();
      ArrayList<String> stopList = routeAndDirToStops.get(routeAndDir);
      for(int i = 0; i < stopList.size(); i++) {
        stopIdToNum.put(stopList.get(i), i);
      }
      routeAndDirToStopIdToNum.put(routeAndDir, stopIdToNum);
    }

    // find the vehicle type for all blocks
    Set<String> btVers = new HashSet<String>();
    Set<String> blocknos = new HashSet<String>();
    Connection db = null;
    for(BlockInstance blockInst : blocks) {
      BlockConfigurationEntry bce = blockInst.getBlock();
      BlockStopTimeEntry bste = bce.getStopTimes().get(0);
      String btVerAndPatternId = bste.getTrip().getTrip().getShapeId().getId();
      String btVer = btVerAndPatternId.substring(0,3);
      String blockno = bce.getBlock().getId().getId();
      btVers.add(btVer);
      blocknos.add(blockno);
    }
    Map<String,String> btVerAndBlocknoToVehType = new HashMap<String,String>();
    try {
      db = Config.getDatabaseConnection();
      String stmt = "SELECT bt_ver,blockno,veh_type FROM dn_bt_veh_type WHERE bt_ver IN (";
      for(String btVer : btVers) {
        stmt += btVer + ",";
      }
      stmt = stmt.substring(0,stmt.length() - 1);
      stmt += ") AND blockno IN (";
      for(String blockno : blocknos) {
        stmt += blockno + ",";
      }
      stmt = stmt.substring(0,stmt.length() - 1);
      stmt += ");";
      PreparedStatement vehTypesStmt = db.prepareStatement(stmt);
      ResultSet rs = vehTypesStmt.executeQuery();
      while(rs.next()) {
        String btVer = rs.getString(1);
        String blockno = rs.getString(2);
        String vehType = rs.getString(3);
        String btVerAndBlockno = btVer + "," + blockno;
        btVerAndBlocknoToVehType.put(btVerAndBlockno, vehType);
      }
    }
    catch(SQLException e) {
      // FIXME handle this exception properly
      e.printStackTrace();
    }
    finally {
      if(db != null)
        try {
          db.close();
        } catch (SQLException e) {
          // FIXME handle this exception properly
          e.printStackTrace();
        }
    }

    // Create list of statistical probes
    this.computeStats = computeStats;
    // FIXME: This will fail - replaces with list of all stops for route
    if(computeStats) this.probes = new StatProbesBatch(NUM_RUNS, routeAndDirToStopIdToNum);
    else this.probes = null;

    for (int i = 0; i < NUM_RUNS; i++) {
      this.executor.execute(new SimulationRun(this, i, routeAndDirs, blocks, btVerAndBlocknoToVehType, day));
    }
    if(saveLogs)
      this.executor.execute(this.logger);
    if(computeStats)
      this.executor.execute(this.probes);

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

  public StatProbesBatch getProbes() {
    return probes;
  }
  
}
