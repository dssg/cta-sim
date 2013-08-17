package dssg.client;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ParamEstimation {
  
  public static void estParameters(SimulationServiceAsync simulationService, String route) {
    simulationService.estimateParameters(route,
        new AsyncCallback<String>() {
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
