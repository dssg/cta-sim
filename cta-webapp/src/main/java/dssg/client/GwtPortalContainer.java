/*
 * Main Class CTA portal code
 * Called from webapp.java
 */

package dssg.client;

import java.util.Map;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;

/*
 *  Main class containing all elements
 *  
 *  Main portal container (main window) is a portal container which is
 *  divided into North, South, East and West regions.
 */
public class GwtPortalContainer extends Viewport {

  // Global variables
  private FormData formData;
  private VerticalPanel vp;
  private ContentPanel panel;
  private Resizable r;
  private Command createCenterPanelCmd;
  private String route;
  private Integer startT;
  private Integer stopT;
  private S3CommunicationServiceAsync s3ComunicationService;
  private SimulationServiceAsync simulationService;
  private String direction;
  private CheckBox checkLoad;
  private CheckBox checkFlow;
  private Map<String, Integer[]> simulation_data;
  private DateField date;

  /*
   * Constructor
   */
  public GwtPortalContainer(SimulationServiceAsync simulationService,
      S3CommunicationServiceAsync s3ComunicationService) {
    // Services to connect with server side
    this.s3ComunicationService = s3ComunicationService;
    this.simulationService = simulationService;
    // Global variable initialization
    direction = "";
    route = null;
    startT = 0;
    stopT = 23;
    checkLoad = new CheckBox();
    checkFlow = new CheckBox();
  }

  /*
   * Elements to add upon rendering
   */
  @Override
  protected void onRender(Element parent, int index) {
    super.onRender(parent, index);
    // Viewport allows window resize adjustments
    Viewport view = new Viewport();
    view.setScrollMode(Scroll.AUTOY);
    view.setMonitorWindowResize(true);
    view.setStyleAttribute("backgroundcolor", "#000033");

    // Layout preferences for the entire page
    final BorderLayout layout = new BorderLayout();
    setLayout(layout);
    // --Layout data for all regions--
    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 80);
    northData.setCollapsible(false);
    northData.setHideCollapseTool(true);
    northData.setMargins(new Margins(0, 0, 5, 0));
    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 260);
    westData.setCollapsible(true);
    westData.setSplit(false);
    westData.setMargins(new Margins(0, 5, 0, 0));
    final BorderLayoutData centerData = new BorderLayoutData(
        LayoutRegion.CENTER);
    centerData.setMargins(new Margins(0));

    // Call to methods that create the panels.
    add(getNorth(), northData);
    add(getWest(), westData);
    add(getCenter(), centerData);
  }

  /*
   * --- North region information (top region of the webapp) ---
   */
  private LayoutContainer getNorth() {
    LayoutContainer north = new LayoutContainer();
    // Layout preferences
    north.setBorders(false);
    north.setLayout(new RowLayout(Orientation.HORIZONTAL));
    north.setStyleAttribute("background-color", "#000033");

    // Image loading for the north region

    // Blue image
    Image blueImage = new Image();
    blueImage.setUrl(GWT.getModuleBaseURL() + "000033.png");
    north.add(blueImage, new RowData(.03, 1, new Margins(4)));
    // CTA logo
    Image ctaImage = new Image();
    ctaImage.setUrl(GWT.getModuleBaseURL() + "cta-logo2.gif");
    ctaImage.setSize("415px", "70px");
    panel = new ContentPanel();
    panel.setFrame(false);
    panel.setBodyBorder(false);
    panel.setHeaderVisible(false);
    panel.setBodyStyle("background: #000033");
    panel.add(ctaImage);
    north.add(panel, new RowData(-1, 1, new Margins(6, 0, 0, 0)));
    // Blue image
    north.add(blueImage, new RowData(.94, 1, new Margins(4)));
    // DSSG logo round
    Image dssgImage = new Image();
    dssgImage.setUrl(GWT.getModuleBaseURL() + "dssg-logo.png");
    dssgImage.setSize("65px", "65px");
    panel = new ContentPanel();
    panel.setFrame(false);
    panel.setBodyBorder(false);
    panel.setHeaderVisible(false);
    panel.setBodyStyle("background: #000033");
    panel.add(dssgImage);
    north.add(panel, new RowData(-1, 1, new Margins(10, 0, 0, 0)));
    // Blue image
    north.add(blueImage, new RowData(.03, 1, new Margins()));
    return north;
  }

  /*
   * --- West region information (region to the left of the webapp) ---
   */
  private ContentPanel getWest() {
    ContentPanel west = new ContentPanel();
    // Layout preferences
    west.setBorders(true);
    west.setBodyBorder(true);
    west.setLayout(new FillLayout());
    west.setButtonAlign(HorizontalAlignment.CENTER);
    west.setHeadingHtml("Information Tools");
    west.setBodyStyle("background: #000033");
    west.setBorders(false);

    // Content panel for "Information Type"
    panel = new ContentPanel();
    // Layout preferences
    panel.setHeadingHtml("General Information");
    panel.setBorders(false);
    panel.setCollapsible(true);
    formData = new FormData("-20");
    // Vertical panel for form data
    vp = new VerticalPanel();
    vp.setSpacing(10);
    createGenInfWest();
    panel.add(vp);
    west.add(panel);

    // Content panel for "Chart Options"
    panel = new ContentPanel();
    // Layout preferences
    panel.setHeadingHtml("Chart Options");
    panel.setBorders(false);
    panel.setCollapsible(true);
    formData = new FormData("-20");
    // Vertical panel for for data
    vp = new VerticalPanel();
    vp.setSpacing(10);
    createCharOptWest();
    panel.add(vp);
    panel.collapse();
    west.add(panel);

    // Content panel for "General Settings"
    panel = new ContentPanel();
    // Layout preferences
    panel.setHeadingHtml("Upload Files");
    panel.setBorders(false);
    panel.setCollapsible(true);
    formData = new FormData("-20");
    // Vertical panel for form data
    vp = new VerticalPanel();
    vp.setSpacing(10);
    createUpldFileWest();
    panel.add(vp);
    panel.collapse();
    west.add(panel);

    return west;
  }

  /*
   * --- Center region information (center region of the webapp) ---
   */
  private ContentPanel getCenter() {
    final ContentPanel center = new ContentPanel();
    // Layout preferences
    center.setHeadingHtml("Charts");
    // FIXME scroll is only working on the edges
    center.setScrollMode(Scroll.AUTOY);
    r = new Resizable(center);
    r.setDynamic(true);
    // Local variables

    // Portal columns created for portlet items (sub-regions)
    final Portal portal = new Portal(2);
    // Layout preferences
    portal.setBorders(true);
    portal.setStyleAttribute("backgroundColor", "white");
    portal.setColumnWidth(0, .55);
    portal.setColumnWidth(1, .45);
    portal.setScrollMode(Scroll.AUTOY);

    // EXECUTED AFTER SIMULATION FINISHES
    // Portlet for Charts executed when updating charts.
    createCenterPanelCmd = new Command() {
      @Override
      public void execute() {
        if (checkLoad.getValue()) {
          GraphPortlets graphPortlets = new GraphPortlets("Load", route,
              direction, startT, stopT,date.getDatePicker().getValue(), simulation_data);
          portal.add(graphPortlets, 0);
          portal.add(graphPortlets.getPortletOneStop(), 1);
          portal.add(graphPortlets.getGrid(), 1);
        }
        if (checkFlow.getValue()) {
          GraphPortlets graphPortlets = new GraphPortlets("Flow", route,
              direction, startT, stopT, date.getDatePicker().getValue(), simulation_data);
          portal.add(graphPortlets, 0);
          portal.add(graphPortlets.getPortletOneStop(), 1);
          portal.add(graphPortlets.getGrid(), 1);
        }
      }
    };
    // Portal added to the center region
    center.add(portal);
    return center;
  }

  /*
   * -- Methods to create forms --
   */
  // Form to display data visualization options
  private void createGenInfWest() {

    // Local variables
    RadioGroup rGroup = new RadioGroup();

    // Initial form panel
    final FormPanel simple = new FormPanel();
    // Layout preferences
    simple.setFrame(false);
    simple.setHeaderVisible(false);

    // Add radio button for North South Both
    final Radio northB = new Radio();
    northB.setBoxLabel("N/E");
    final Radio southB = new Radio();
    southB.setBoxLabel("S/W");
    final Radio bothB = new Radio();
    bothB.setBoxLabel("Both");
    northB.setValue(true);
    rGroup.setFieldLabel("Direction:");
    rGroup.setSpacing(1);
    rGroup.add(northB);
    rGroup.add(southB);
    rGroup.add(bothB);
    simple.add(rGroup);

    // Text fields for route number
    ListStore<DataRoutes> routes = new ListStore<DataRoutes>();
    routes.add(GetData.getRoutes());

    final ComboBox<DataRoutes> routeCmb = new ComboBox<DataRoutes>();
    routeCmb.setFieldLabel("Route");
    routeCmb.setEmptyText("Select a route");
    routeCmb.setAllowBlank(false);
    routeCmb.setDisplayField("name");
    routeCmb.setStore(routes);
    routeCmb.setTypeAhead(true);
    simple.add(routeCmb, formData);

    // Date field
    date = new DateField();
    date.setFieldLabel("Date");
    date.setAllowBlank(false);
    simple.add(date, formData);
    // Time field
    final TimeField timeS = new TimeField();
    timeS.setFieldLabel("Start Time");
    timeS.setIncrement(60);
    timeS.setAllowBlank(false);
    simple.add(timeS, formData);
    final TimeField timeF = new TimeField();
    timeF.setFieldLabel("End Time");
    timeF.setIncrement(60);
    timeF.setAllowBlank(false);
    simple.add(timeF, formData);
    // Submit button
    Button b = new Button("Simulate");
    // Workaround for simulation
    final GwtPortalContainer portalContainer = this;
    b.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        route = routeCmb.getValue().getId().toString();
        if (northB.getValue() == true)
          direction = "N";
        if (southB.getValue() == true)
          direction = "S";
        if (bothB.getValue() == true)
          direction = "B";
        // Check that all parameters are correctly inputed
        if (routeCmb.getValue().equals(null) || timeS.getValue() == null
            || timeF.getValue() == null) {
          MessageBox.alert("Carefull", "Null values not allowed", null);
        } else if (timeS.getDateValue().getHours() > timeF.getDateValue()
            .getHours()) {
          MessageBox.alert("Carefull", "Time window is incorrect.", null);
        } else {
          // Data.testS3(s3ComunicationService);
          // callLoading();
          startT = timeS.getDateValue().getHours();
          stopT = timeF.getDateValue().getHours();
          // RUN SIMULATION
          GetData.runSim(simulationService, portalContainer, route, direction,
              date.getDatePicker().getValue(), startT, stopT);
          Info.display("Starting Simulation.","The results will be displayed at the bottom of the page.");
        }
      }
    });
    simple.add(b);
    // Cancel button
    b = new Button("Reset");
    b.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        simple.reset();
      }
    });
    simple.add(b);
    simple.setWidth(235);

    vp.add(simple);
  }

  // Form to display Chart Option
  private void createCharOptWest() {

    // Initial Layout
    FormPanel simple = new FormPanel();
    // Layout preferences
    simple.setFrame(false);
    simple.setHeaderVisible(false);
    simple.setHideLabels(true);

    // Type of information buttons

    checkLoad.setBoxLabel("Load");
    checkLoad.setValue(true);
    simple.add(checkLoad);
    checkFlow.setBoxLabel("Flow");
    checkFlow.setValue(true);
    simple.add(checkFlow);

    vp.add(simple);
  }

  // Form to input schedule or gtfs
  private void createUpldFileWest() {
    // Initial panel
    final FormPanel simple = new FormPanel();
    // Layout preferences
    simple.setFrame(false);
    simple.setHeaderVisible(false);
    simple.setHideLabels(true);

    // Upload Fields
    final FileUploadField gtsfFile = new FileUploadField();
    gtsfFile.setName("GTSF");
    simple.add(gtsfFile);
    final FileUploadField schedFile = new FileUploadField();
    schedFile.setAllowBlank(true);
    schedFile.setEmptyText("Schedule file");
    simple.add(schedFile);

    // Submit Button
    Button submitBtn = new Button("Submit");
    submitBtn.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // callS3Upload(gtsfFile.getRawValue());
      }
    });
    simple.add(submitBtn);

    // Reset Button
    Button resetBtn = new Button("Reset");
    resetBtn.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        simple.reset();
      }
    });
    simple.add(resetBtn);
    simple.setWidth(235);

    vp.add(simple);
  }

  /*
   * -- Charts -- EXECUTED AFTER SIMULATION FINISHES Create new charts for the
   * ouput of the simulation.
   */
  // Update all charts
  public void updateCharts(Map<String, Integer[]> data) {
    // Info.display("Sucess in getting data @portal.",
    // "The results are shown in a new pannel at the bottom of your screen.");
    simulation_data = data;
    createCenterPanelCmd.execute();

  }

  /*
   * -- Other Functions --
   */
  // -- LOADING MESSAGE --
  private void callLoading() {
    final MessageBox box = MessageBox.progress("Please wait",
        "Loading simulation...", "Initializing...");
    final ProgressBar bar = box.getProgressBar();
    final Timer t = new Timer() {
      float i;

      @Override
      public void run() {
        bar.updateProgress(i / 100, (int) i + "% Complete");
        i += 5;
        if (i > 105) {
          cancel();
          box.close();
          Info.display("Simulation", "DONE", "");
        }
      }
    };
    t.scheduleRepeating(150);
  }

  // -- AWS S3 File Upload--
  // FIXME get this method to work
  // private void callS3Upload(final String file) {
  // System.out.println("File path: " + file);
  //
  // this.s3ComunicationService.uploadFile(file,
  // new AsyncCallback<List<DataStats>>() {
  // @Override
  // public void onSuccess(List<DataStats> output) {
  // Info.display("Sucess", "");
  // }
  //
  // @Override
  // public void onFailure(Throwable e) {
  // Info.display("Failure", "");
  //
  // }
  // });
  // }
}
