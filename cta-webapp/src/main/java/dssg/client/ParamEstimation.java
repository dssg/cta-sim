package dssg.client;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Class that communicates client side with server side to start parameter
 * estimation for simulation.
 */
public class ParamEstimation {
  /**
   * Method to communicate to server side for the execution of a script to
   * estimate new parameter
   * 
   * @param simulationService
   * @param route
   */
  public static void estParameters(SimulationServiceAsync simulationService,
      String route) {
    simulationService.estimateParameters(route, new AsyncCallback<String>() {
      @Override
      public void onSuccess(String output) {
        Info.display("Update", "Parameters successfully updated.");
      }

      @Override
      public void onFailure(Throwable e) {
        Info.display("Failure in updating parameters",
            "There is a problem with the server or the connection.");
      }
    });

  }

}
