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
   * Create a remote service proxy to talk to the server-side simulation service.
   */
  private final SimulationServiceAsync simulationService = GWT.create(SimulationService.class);

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    RootPanel rootPanel = RootPanel.get();
    //rootPanel.add(new LoadingWindow());
    rootPanel.add(new GwtPortalContainer());
    DOM.removeChild(RootPanel.getBodyElement(), DOM.getElementById("loading"));
  }
}						
