package dssg.server;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Channels;
import java.net.URL;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

import dssg.client.MyParameters;
import dssg.client.SimulationService;
import dssg.shared.FieldVerifier;
import dssg.simulator.SimulationInstance;

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

  static public class SimulationRunnable implements Runnable {

    SimulationInstance simInst;

    public SimulationRunnable(SimulationInstance simInst) {
      this.simInst = simInst;
    }

    @Override
    public void run() {
      while (simInst.step()) {
        /*
         * TODO FIXME compute stats or put them in whatever format is needed.
         */
      }

    }

  }

  @Autowired
	public BlockIndexService bis;

  @Autowired
  public BlockLocationService bls;

  @Autowired
  public BlockCalendarService bcs;

  @Autowired
  public TransitGraphDao tgd;
  
  
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

  private static final ExecutorService executor = Executors
      .newFixedThreadPool(THREAD_COUNT);

  private static final int NUM_SIMULATIONS = 100;
  
  private Map<String, SimulationInstance> simulations = Maps.newHashMap();

  public String submitSimulation(String route, Date date, long startTime,
			long endTime) throws IllegalArgumentException {

		/*
		 * Get simulation parameters from S3 model output file
		 */
		List<MyParameters> parameters = new ArrayList<MyParameters>();
		S3CommunicationServiceImpl s3ComunicationService = new S3CommunicationServiceImpl();
		parameters = s3ComunicationService.downloadParameters();
		System.out.println("Number of parameters: "
				+ parameters.toArray().length);

		SimulationInstance simInst = createSimulation(route, date, startTime, endTime, parameters);

    for (int i = 0; i < NUM_SIMULATIONS; i++) {
      executor.execute(new SimulationRunnable(simInst));
    }

		return simInst.getSimulationId();
	}

	private SimulationInstance createSimulation(String route, Date date,
    long startTime, long endTime, List<MyParameters> parameters) {
	  String simId = route + date + startTime + endTime;
	  
    SimulationInstance simulation = simulations.get(simId);
    if (simulation == null) {
      simulation = new SimulationInstance(this, simId, route, date, startTime, endTime, parameters);
      simulations.put(simId, simulation);
    }
    
    return simulation;
    
  }

	/**
	 * Return simulation results to client side for display.
	 */
	public List<Number> getResults(String simId){
	  
    SimulationInstance simulation = simulations.get(simId);
    
    if (simulation != null) {
      // TODO implement!  
    }
	  
		int[] dummyData = {30,9,10,12,10,10,11,3,8,11,
				16,25,29,35,55,54,48,49,48,36,
				33,41,43,42,49,44,49,50,51,52,
				53,50,51,52,50,55,53,50,48,52,
				48,46,44,43,35,33,31};
		List<Number> data = new ArrayList<Number>();
		Random generator = new Random();
		for (double n = 1; n <= 100; n = n + .5) {
//			data.add(Math.floor(Math.abs(150
//					* Math.sin(n * Math.PI / 20 - Math.PI * 4 / 24)
//					+ generator.nextDouble() * 80)));
			data.add(dummyData[(int) (n*2)]);
		}
		return data;
	}
}
