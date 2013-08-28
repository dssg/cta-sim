package dssg.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Class that returns data for combo box, grid, and graphs.
 * 
 * Used by GWTPortalContainer and GraphPortlets
 */

public class GetData {

  public List<DataStats> stats = new ArrayList<DataStats>();

  /**
   * Method to create route objects to populate combo box and be able to
   * simulate
   * 
   * @return
   */
  public static List<DataRoutes> getRoutes() {
    List<DataRoutes> routes = new ArrayList<DataRoutes>();

    routes.add(new DataRoutes(6, "6.Jack-Park-Exp"));
    // TODO: as new routes are added to the bundle uncomment next lines.
    // routes.add(new DataRoutes(2, "2.Hyde-Park-Exp"));
    // routes.add(new DataRoutes(3, "3.King Drive"));
    // routes.add(new DataRoutes(7, "7.Harrison"));
    // routes.add(new DataRoutes(9, "9.Ashland"));
    // Add more routes here
    return routes;

  }

  /**
   * Method to create statistics to populate the Summary Grid
   * 
   * @param dataType
   * @param sim_data
   * @param direction
   * @return
   */
  public static List<DataStats> getStats(String dataType,
      Map<String, Integer[]> sim_data, String direction) {
    List<DataStats> stats = new ArrayList<DataStats>();
    // Load Statistics
    if (dataType.equals("Load")) {
      for (int i = 0; i < 48; i++) {
        if (direction.equals("N") || direction.equals("B"))
          stats.add(new DataStats((double) i / 2, sim_data
              .get("load_timestop_N_hour" + i)[1], sim_data
              .get("load_timestop_N_hour" + i)[20], sim_data
              .get("load_timestop_N_hour" + i)[40], sim_data
              .get("load_timestop_N_hour" + i)[59]));
        if (direction.equals("S") || direction.equals("B"))
          stats.add(new DataStats((double) i / 2, sim_data
              .get("load_timestop_S_hour" + i)[1], sim_data
              .get("load_timestop_S_hour" + i)[20], sim_data
              .get("load_timestop_S_hour" + i)[40], sim_data
              .get("load_timestop_S_hour" + i)[59]));
      }
    }
    // Flow Statistics
    if (dataType.equals("Flow")) {
      for (int i = 0; i < 48; i++) {
        if (direction.equals("N") || direction.equals("B"))
          stats.add(new DataStats((double) i / 2, sim_data
              .get("flow_timestop_N_hour" + i)[1], sim_data
              .get("flow_timestop_N_hour" + i)[20], sim_data
              .get("load_timestop_N_hour" + i)[40], sim_data
              .get("flow_timestop_N_hour" + i)[59]));
        if (direction.equals("S") || direction.equals("B"))
          stats.add(new DataStats((double) i / 2, sim_data
              .get("flow_timestop_S_hour" + i)[1], sim_data
              .get("flow_timestop_S_hour" + i)[20], sim_data
              .get("load_timestop_S_hour" + i)[40], sim_data
              .get("flow_timestop_S_hour" + i)[59]));
      }
    }
    // Return argument
    return stats;
  }

  /**
   * Method to initialize simulation and receive simulated data Executes
   * updateCharts after simulation is complete
   * 
   * @param simulationService
   * @param gwtPortalContainer
   * @param route
   * @param direction
   * @param date
   * @param startT
   * @param stopT
   */
  public static void runSim(SimulationServiceAsync simulationService,
      final GwtPortalContainer gwtPortalContainer, String route,
      String direction, Date date, Integer startT, Integer stopT) {
    simulationService.runSimulation(route, direction, date, startT, stopT,
        new AsyncCallback<Map<String, Integer[]>>() {
          @Override
          public void onSuccess(Map<String, Integer[]> output) {
            Info.display("Simulation Complete", "Sending data to Portal.");
            /*
             * If you need to look at what data points you are getting from the
             * server use: output.get("max_load_N").length));
             */
            // Update charts in Portal container
            gwtPortalContainer.updateCharts(output);
          }

          @Override
          public void onFailure(Throwable e) {
            Info.display("Failure in getting data",
                "There is a problem with the server or the connection.");
          }
        });

  }

  /**
   * UNUSED Optional method to communicate with Amazon S3 services
   * 
   * @param s3ComunicationService
   */
  public static void testS3(S3CommunicationServiceAsync s3ComunicationService) {
    s3ComunicationService.downloadParameters(new AsyncCallback<List<String>>() {
      @Override
      public void onSuccess(List<String> output) {
        Info.display(
            "TEST S3\nSucess in getting data @DATA.",
            "Number of data points:"
                + Integer.toString(output.toArray().length));
        ;

      }

      @Override
      public void onFailure(Throwable e) {
        Info.display("TEST S3\nFailure in getting data", "");
      }
    });
  }

}
