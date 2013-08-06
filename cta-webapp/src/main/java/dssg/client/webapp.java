package dssg.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class webapp implements EntryPoint {

  /**
   * Remote service proxy to talk to the server-side simulation
   * service.
   */
  private final SimulationServiceAsync simulationService = GWT
      .create(SimulationService.class);
  private final S3CommunicationServiceAsync s3ComunicationService = GWT
	      .create(S3CommunicationService.class);

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	RootPanel rootPanel = RootPanel.get();
    rootPanel.setStyleName("requires-min-width");
    rootPanel.setStyleName("root");
    rootPanel.add(new GwtPortalContainer(simulationService, s3ComunicationService));
    
    DOM.removeChild(RootPanel.getBodyElement(),
        DOM.getElementById("loading"));
  }
}
