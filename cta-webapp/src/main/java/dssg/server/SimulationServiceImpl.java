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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import dssg.client.S3CommunicationService;
import dssg.client.SimulationService;
import dssg.shared.FieldVerifier;
import dssg.simulator.SimulationBatch;
import dssg.simulator.SimulationBatch;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@Configurable
@SuppressWarnings("serial")
public class SimulationServiceImpl extends RemoteServiceServlet implements
    SimulationService {

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

  public String submitSimulation(Set<String> routeAndDirs, Date startTime, Date endTime)
      throws IllegalArgumentException {

    String batchId = "";
    for(String routeAndDir : routeAndDirs)
      batchId += routeAndDir + "_";
    batchId += "From_" + startTime + "_To_" + endTime;

    SimulationBatch simBatch = simulations.get(batchId);
    if (simBatch == null) {
      try {
        simBatch = new SimulationBatch(this, batchId, routeAndDirs, startTime, endTime);
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
  public Map<String, Integer[]> getResults(String batchId) {

    SimulationBatch simulation = simulations.get(batchId);

    if (simulation != null) {
      // TODO implement!
    }
    Map<String, Integer[]> results = new HashMap<String, Integer[]>();
    Integer[] dummyData = { 30, 9, 10, 12, 10, 10, 11, 3, 8, 11, 16, 25, 29,
        35, 55, 54, 48, 49, 48, 36, 33, 41, 43, 42, 49, 44, 49, 50, 51, 52, 53,
        50, 51, 52, 50, 55, 53, 50, 48, 52, 48, 46, 44, 43, 35, 33, 31 };

    Integer[] dummyData2 = { 15, 9, 11, 10, 8, 6, 3, 8, 12, 17, 27, 30, 36, 59,
        56, 54, 50, 48, 50, 48, 48, 38, 43, 42, 49, 40, 40, 49, 35, 41, 38, 36,
        37, 36, 44, 42, 33, 36, 41, 42, 48, 47, 26, 25, 24, 25, 10 };

    Integer[] dummyData3 = { 5315, 17705, 5317, 5318, 5320, 1510, 1511, 1512,
        1513, 1514, 1515, 1517, 1518, 1519, 1520, 1521, 1522, 1523, 1524, 5037,
        7137, 5038, 5039, 5040, 14483, 4883, 4884, 14485, 76, 15241, 2188,
        1119, 1120, 1121, 3953, 3954, 14222 };

    results.put("max_load_N", dummyData);
    results.put("max_load_S", dummyData2);
    results.put("stops", dummyData3);

    return results;
  }
}
