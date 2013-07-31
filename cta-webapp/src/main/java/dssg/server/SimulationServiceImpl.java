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

	private S3CommunicationService s3ComunicationService = new S3CommunicationServiceImpl();

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

	public S3CommunicationService getS3ComunicationService() {
		return s3ComunicationService;
	}

	public void setS3ComunicationService(
			S3CommunicationService s3ComunicationService) {
		this.s3ComunicationService = s3ComunicationService;
	}

	public String submitSimulation(String route, Date date, long startTime,
			long endTime) throws IllegalArgumentException {

		/*
		 * Get simulation parameters from S3 model output file
		 */
		List<MyParameters> parameters = new ArrayList<MyParameters>();
		parameters = s3ComunicationService.downloadParameters();
		System.out.println("Number of parameters: "
				+ parameters.toArray().length);

		String batchId = route + date + startTime + endTime;

		SimulationBatch simBatch = simulations.get(batchId);
		if (simBatch == null) {
			simBatch = new SimulationBatch(this, batchId, route, date,
					startTime, endTime, parameters);
			simulations.put(batchId, simBatch);
		}

		return simBatch.getBatchId();
	}

	/**
	 * Return simulation results to client side for display.
	 */
	public List<Number> getResults(String batchId) {

		SimulationBatch simulation = simulations.get(batchId);

		if (simulation != null) {
			// TODO implement!
		}

		int[] dummyData = { 30, 9, 10, 12, 10, 10, 11, 3, 8, 11, 16, 25, 29,
				35, 55, 54, 48, 49, 48, 36, 33, 41, 43, 42, 49, 44, 49, 50, 51,
				52, 53, 50, 51, 52, 50, 55, 53, 50, 48, 52, 48, 46, 44, 43, 35,
				33, 31 };
		List<Number> data = new ArrayList<Number>();
		for (int n = 0; n <= 46; n++) {
			data.add(dummyData[n]);
		}
		return data;
	}
}
