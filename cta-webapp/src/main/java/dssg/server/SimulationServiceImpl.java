package dssg.server;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.serialization.GtfsReader;

import dssg.client.SimulationService;
import dssg.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SimulationServiceImpl extends RemoteServiceServlet
    implements SimulationService {

  public String simulationServer(String route, Date date,
    long startTime, long endTime) throws IllegalArgumentException {
    /*
     * FIXME Verify that the input is valid.
     */
    //    if (!FieldVerifier.isValidName(input)) {
    //      // If the input is not valid, throw an IllegalArgumentException back to
    //      // the client.
    //      throw new IllegalArgumentException(
    //          "Name must be at least 4 characters long");
    //    }

    /*
     * We need to read a GTFS file for the simulation.
     * FIXME use the arguments to find/produce the GTFS files
     * If it fails, then no-go.
     */
    String gtfsInputFile = "";
    String simName;
    try {
      GtfsDaoImpl store = getGtfs(gtfsInputFile);

      simName = createSimulation(store);

    } catch (IOException e) {
      e.printStackTrace();
      simName = null;
    }

    return simName;
  }

  /**
   * Create and queue simulations (also check if we're already running the same
   * one).
   * 
   * @param store
   * @return
   */
  private String createSimulation(GtfsDaoImpl store) {
    /*
     * FIXME: run a simulation!
     * create a simulation instance, spawn a thread to step through the simulation, 
     * add the thread's future object to some queue/list of * running/run simulations, etc.
     */
    return null;
  }

  /**
   * Retrieve/create/cache the GTFS DAO object needed to run through a schedule.
   * 
   * @param gtfsFileName
   * @return
   * @throws IOException
   */
  private GtfsDaoImpl getGtfs(String gtfsFileName) throws IOException {

    GtfsReader reader = new GtfsReader();
    reader.setInputLocation(new File(gtfsFileName));

    GtfsDaoImpl store = new GtfsDaoImpl();
    reader.setEntityStore(store);

    reader.run();

    return store;
  }

  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   * 
   * @param html
   *          the html string to escape
   * @return the escaped string
   */
  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;");
  }
}
