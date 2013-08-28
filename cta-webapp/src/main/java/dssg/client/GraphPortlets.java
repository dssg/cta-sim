package dssg.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.FilledBarChart;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.google.gwt.user.client.Command;

/**
 * Class to create the three panels for the center region of the webapp
 * Depending on the type of information given the panels are created slightly
 * different
 * 
 */
public class GraphPortlets extends Portlet {
  // Global variables
  private ContentPanel panel;
  private Command updateChart1Cmd;
  private Command updateChart2Cmd;
  private Command updateChart3Cmd;
  private Command updateSliderCmd;
  private Command updateSlider2Cmd;
  private Command updateGrid;
  private String dataType;
  private Integer timeSliderPosition;
  private String route;
  private String direction;
  private Integer startT;
  private Integer stopT;
  private Integer numStops;
  private Integer stopTageoid;
  Map<String, Integer[]> sim_data;
  private Integer[][] sim_dataTimeWindow;
  private Integer[][] sim_dataTimeStopN;
  private Integer[][] sim_dataTimeStopS;
  private Resizable r;
  private String title;
  private Date date;
  private final int NUMHOURSEG = 48;

  /**
   * Constructor for LOAD and FLOW
   * 
   * @param dataType
   * @param route
   * @param direction
   * @param startT
   * @param stopT
   * @param date
   * @param sim_data
   */
  public GraphPortlets(String dataType, String route, String direction,
      Integer startT, Integer stopT, Date date, Map<String, Integer[]> sim_data) {

    // Global variable initializers
    this.sim_data = sim_data;
    timeSliderPosition = startT * 2;
    stopTageoid = 0;
    this.dataType = dataType;
    this.route = route;
    if (direction.equals(""))
      MessageBox.alert("Error with direction", "Direction does not exist.",
          null);
    else
      this.direction = direction;

    this.startT = startT;
    this.stopT = stopT;
    this.date = date;

    // Create the simulated data arrays
    sim_dataTimeWindow = new Integer[2][NUMHOURSEG];
    // arrays for LOAD
    if (dataType.equals("Load")) {
      if (direction.equals("N") || direction.equals("B"))
        numStops = sim_data.get("load_timestop_N_hour1").length;
      else
        numStops = sim_data.get("load_timestop_S_hour1").length;

      sim_dataTimeWindow[0] = sim_data.get("max_load_N");
      sim_dataTimeWindow[1] = sim_data.get("max_load_S");
      sim_dataTimeStopN = new Integer[NUMHOURSEG][numStops];
      sim_dataTimeStopS = new Integer[NUMHOURSEG][numStops];
      if (direction.equals("N") || direction.equals("B")) {
        for (int i = 0; i < NUMHOURSEG; i++)
          sim_dataTimeStopN[i] = sim_data.get("load_timestop_N_hour" + i);
      }
      if (direction.equals("S")) {
        for (int i = 0; i < NUMHOURSEG; i++)
          sim_dataTimeStopS[i] = sim_data.get("load_timestop_S_hour" + i);
      }
      if (direction.equals("B")) {
        Integer[][] temp_reverse = new Integer[NUMHOURSEG][numStops];
        for (int i = 0; i < NUMHOURSEG; i++) {
          temp_reverse[i] = sim_data.get("load_timestop_S_hour" + i);
          for (int j = 0; j < numStops; j++)
            sim_dataTimeStopS[i][j] = temp_reverse[i][numStops - (1 + j)];
        }
      }
    }
    // arrays for FLOW
    if (dataType.equals("Flow")) {
      if (direction.equals("N") || direction.equals("B"))
        numStops = sim_data.get("flow_timestop_N_hour1").length;
      else
        numStops = sim_data.get("flow_timestop_S_hour1").length;

      sim_dataTimeWindow[0] = sim_data.get("max_flow_N");
      sim_dataTimeWindow[1] = sim_data.get("max_flow_S");
      sim_dataTimeStopN = new Integer[NUMHOURSEG][numStops];
      sim_dataTimeStopS = new Integer[NUMHOURSEG][numStops];
      if (direction.equals("N") || direction.equals("B")) {
        for (int i = 0; i < NUMHOURSEG; i++)
          sim_dataTimeStopN[i] = sim_data.get("flow_timestop_N_hour" + i);
      }
      if (direction.equals("S")) {
        for (int i = 0; i < NUMHOURSEG; i++)
          sim_dataTimeStopS[i] = sim_data.get("flow_timestop_S_hour" + i);
      }
      if (direction.equals("B")) {
        Integer[][] temp_reverse = new Integer[NUMHOURSEG][numStops];
        for (int i = 0; i < NUMHOURSEG; i++) {
          temp_reverse[i] = sim_data.get("flow_timestop_S_hour" + i);
          for (int j = 0; j < numStops; j++)
            sim_dataTimeStopS[i][j] = temp_reverse[i][numStops - (1 + j)];
        }
      }

    }

    // Box Layout for the charts and sliders
    final VBoxLayout portletLayout = new VBoxLayout();
    portletLayout.setPadding(new Padding(15));
    portletLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);

    // Layout preferences
    this.setHeadingHtml("Route " + route + ": " + dataType + "  (" + date + ")");
    configPanel(this);
    this.setHeight(750);
    this.setLayout(portletLayout);
    r = new Resizable(this);
    r.setDynamic(true);
    final VBoxLayoutData vBoxData = new VBoxLayoutData(10, 15, 10, 25);
    final VBoxLayoutData vBoxData2 = new VBoxLayoutData(10, 10, 10, 25);

    // Elements added
    this.add(panelChartTimeWindow());
    this.add(hourControls(), vBoxData);
    this.add(panelChartAllStops());
    this.add(stopControls(), vBoxData2);

  }

  /**
   * Create All Elements Left Potlet (Double Chart)
   * 
   * @return ContentPanel
   */
  private ContentPanel panelChartTimeWindow() {
    // Local variables
    String url;
    final Chart chart;
    // Content panel for chart
    panel = new ContentPanel();
    panel.setFrame(false);
    panel.setBodyBorder(false);
    panel.setHeaderVisible(false);
    // Chart info
    url = "chart/open-flash-chart.swf";
    chart = new Chart(url);
    chart.setBorders(true);
    chart.setHeight(310);
    panel.add(chart);
    updateChart1Cmd = new Command() {
      @Override
      public void execute() {
        chart.setChartModel(createChartTimeWindow());
      }
    };
    updateChart1Cmd.execute();

    return panel;
  }

  /**
   * Line chart Time Window
   * 
   * @return ChartModel
   */
  private ChartModel createChartTimeWindow() {
    // Choose title
    if (dataType.equals("Load"))
      title = "   Max Load by half hour, all stops.";
    if (dataType.equals("Flow"))
      title = "   Max Flow by half hour, all stops.";

    // Create a ChartModel with the Chart Title and some style attributes
    ChartModel cm = new ChartModel(title,
        "font-size: 14px; font-family:      Verdana; text-align: center;");
    // Code to add legends and paddings
    Legend lg = new Legend(Position.TOP, true);
    lg.setPadding(5);
    lg.setShadow(false);
    cm.setLegend(lg);
    cm.setBackgroundColour("#fffff0");
    // Create the X axis
    XAxis xa = new XAxis();
    // set the labels for the axis
    for (double i = startT; i <= stopT; i = i + 0.5) {
      if (i % 1 == 0) {
        xa.addLabels(Integer.toString((int) i));
      } else {
        xa.addLabels("");
      }
    }
    cm.setXAxis(xa);

    // Create the Y axis
    YAxis ya = new YAxis();
    // Add the labels to the Y axis
    int maxY = Math.max(getMax(sim_dataTimeWindow[0]),
        getMax(sim_dataTimeWindow[1]));
    ya.setRange(0, 1.1 * maxY + 1, 10);
    cm.setYAxis(ya);

    // Creates the line for max Suggested load
    LineChart lchartD = new LineChart();
    lchartD.setColour("#0099FF");
    lchartD.setText("Max 30ft bus");
    for (double n = startT; n <= stopT; n = n + .5) {
      lchartD.addValues(40);
    }
    if (dataType.equals("Load") && maxY >= 40)
      cm.addChartConfig(lchartD);

    LineChart lchartM = new LineChart();
    lchartM.setColour("#0066FF");
    lchartM.setText("Max 40ft bus");
    for (double n = startT; n <= stopT; n = n + .5) {
      lchartM.addValues(70);
    }
    if (dataType.equals("Load") && maxY >= 70)
      cm.addChartConfig(lchartM);

    LineChart lchartU = new LineChart();
    lchartU.setColour("#0033FF");
    lchartU.setText("Max 60ft bus");
    for (double n = startT; n <= stopT; n = n + .5) {
      lchartU.addValues(95);
    }
    if (dataType.equals("Load") && maxY >= 95)
      cm.addChartConfig(lchartU);

    // Create a Line Chart object NORTH DATA
    LineChart lchart = new LineChart();
    lchart.setAnimateOnShow(true);
    lchart.setColour("#00aa00");
    lchart.setTooltip("#val#");
    lchart.setText("North");
    int i = 0;
    for (double n = startT; n <= stopT; n = n + .5) {
      lchart.addValues(sim_dataTimeWindow[0][i]);
      i++;
    }

    if ((direction.equals("N") || direction.equals("B")) && route != null) {
      cm.addChartConfig(lchart);
    }

    // Create a Line Chart object SOUTH DATA
    lchart = new LineChart();
    lchart.setAnimateOnShow(true);
    lchart.setColour("#ff0000");
    lchart.setTooltip("#val#");
    lchart.setText("South");
    i = 0;
    for (double n = startT; n <= stopT; n = n + .5) {
      lchart.addValues(sim_dataTimeWindow[1][i]);
      i++;
    }
    if ((direction.equals("S") || direction.equals("B")) && route != null) {
      cm.addChartConfig(lchart);
    }

    // Returns the Chart Model
    return cm;
  }

  /**
   * Center Slider
   * 
   * @return SliderField
   */
  private SliderField hourControls() {
    // Time Window slider
    final Slider slider = new Slider();
    slider.setIncrement(1);
    // Update Execution
    updateSliderCmd = new Command() {
      @Override
      public void execute() {
        slider.setMaxValue(stopT * 2);
        slider.setMinValue(startT * 2);
      }
    };
    updateSliderCmd.execute();

    final SliderField sf = new SliderField(slider);
    sf.setFieldLabel("Time:");

    // Executed when slider position changes
    slider.addListener(Events.Change, new Listener<SliderEvent>() {
      @Override
      public void handleEvent(SliderEvent be) {
        slider.setMessage(((slider.getValue())) * .5 + " hrs");
        timeSliderPosition = (int) be.getNewValue();
        updateChart2Cmd.execute();
      }
    });

    return sf;
  }

  /**
   * Panel for All Stops chart
   * 
   * @return ContentPanel
   */
  private ContentPanel panelChartAllStops() {
    // Local variables
    String url;
    final Chart chart;
    // Content panel for chart
    panel = new ContentPanel();
    panel.setFrame(false);
    panel.setBodyBorder(false);
    panel.setHeaderVisible(false);
    // Chart info
    url = "chart/open-flash-chart.swf";
    chart = new Chart(url);
    chart.setBorders(true);
    chart.setHeight(300);
    panel.add(chart);
    updateChart2Cmd = new Command() {
      @Override
      public void execute() {
        chart.setChartModel(createChartAllStops());
        panel.expand();
      }
    };
    updateChart2Cmd.execute();
    return panel;
  }

  /**
   * Line chart for All Stops
   * 
   * @return ChartModel
   */
  private ChartModel createChartAllStops() {
    // Choose title
    if (dataType.equals("Load"))
      title = " hrs.   Load by stop.";
    if (dataType.equals("Flow"))
      title = " hrs.   Flow by stop.";

    // Create a ChartModel with the Chart Title and some style attributes
    ChartModel cm = new ChartModel("Time: " + (double) timeSliderPosition / 2
        + title,
        "font-size: 14px; font-family:      Verdana; text-align: center;");
    // Code to add legends and paddings
    Legend lg = new Legend(Position.TOP, true);
    lg.setPadding(5);
    lg.setShadow(false);
    cm.setLegend(lg);
    cm.setBackgroundColour("#fffff0");
    // Create the X axis
    XAxis xa = new XAxis();
    xa.setOffset(true);
    // set the labels for the axis
    for (int i = 0; i < numStops; i++) {
      if (i % 5 == 0) {
        xa.addLabels(Integer.toString(i));
      } else {
        xa.addLabels("");
      }

    }
    cm.setXAxis(xa);
    // Create the Y axis
    YAxis ya = new YAxis();
    // Add the labels to the Y axis
    int maxY = 1;
    if (direction.equals("N"))
      maxY = getMax(sim_dataTimeStopN[timeSliderPosition]);
    if (direction.equals("S"))
      maxY = getMax(sim_dataTimeStopS[timeSliderPosition]);
    if (direction.equals("B"))
      maxY = Math.max(getMax(sim_dataTimeStopN[timeSliderPosition]),
          getMax(sim_dataTimeStopS[timeSliderPosition]));

    ya.setRange(0, 1.1 * maxY + 1, 10);
    cm.setYAxis(ya);

    // Creates the line for max Suggested load
    LineChart lchartD = new LineChart();
    lchartD.setColour("#0099FF");
    lchartD.setText("Max 30ft bus");
    for (int n = 0; n <= numStops; n++) {
      lchartD.addValues(40);
    }
    if (dataType.equals("Load") && maxY >= 40)
      cm.addChartConfig(lchartD);

    LineChart lchartM = new LineChart();
    lchartM.setColour("#0066FF");
    lchartM.setText("Max 40ft bus");
    for (int n = 0; n <= numStops; n++) {
      lchartM.addValues(70);
    }
    if (dataType.equals("Load") && maxY >= 70)
      cm.addChartConfig(lchartM);

    LineChart lchartU = new LineChart();
    lchartU.setColour("#0033FF");
    lchartU.setText("Max 60ft bus");
    for (int n = 0; n <= numStops; n++) {
      lchartU.addValues(95);
    }
    if (dataType.equals("Load") && maxY >= 95)
      cm.addChartConfig(lchartU);

    // Create a Line Chart object NORTH DATA
    LineChart lchart = new LineChart();
    lchart.setAnimateOnShow(true);
    lchart.setColour("#00aa00");
    lchart.setTooltip("#val#");
    lchart.setText("North");

    if ((direction.equals("N") || direction.equals("B")) && route != null) {
      for (int n = 0; n < numStops; n++) {
        lchart.addValues(sim_dataTimeStopN[timeSliderPosition][n]);
      }
      cm.addChartConfig(lchart);
    }

    // Create a Line Chart object SOUTH DATA
    lchart = new LineChart();
    lchart.setAnimateOnShow(true);
    lchart.setColour("#ff0000");
    lchart.setTooltip("#val#");
    lchart.setText("South");

    if ((direction.equals("S") || direction.equals("B")) && route != null) {
      for (int n = 0; n < numStops; n++) {
        lchart.addValues(sim_dataTimeStopS[timeSliderPosition][n]);
      }
      cm.addChartConfig(lchart);
    }

    // Returns the Chart Model
    return cm;
  }

  /**
   * Bottom Slider
   * 
   * @return SliderField
   */
  private SliderField stopControls() {
    // Stop slider
    final Slider slider = new Slider();
    slider.setIncrement(1);
    slider.addStyleName("sliderStyle");
    // Update Execution
    updateSlider2Cmd = new Command() {
      @Override
      public void execute() {
        slider.setMinValue(0);
        slider.setMaxValue(numStops);
      }
    };
    updateSlider2Cmd.execute();

    final SliderField sf = new SliderField(slider);
    sf.setFieldLabel("Time:");
    // Executed when slider position changes
    slider.addListener(Events.Change, new Listener<SliderEvent>() {
      @Override
      public void handleEvent(SliderEvent be) {
        slider.setMessage("Stop:" + slider.getValue());
        stopTageoid = be.getNewValue();
        updateChart3Cmd.execute();
        updateGrid.execute();
      }
    });

    return sf;
  }

  /**
   * Create All Elements Right Top Portlet (One Stop Chart)
   * 
   * @return Portlet
   */
  public Portlet getPortletOneStop() {
    // Portlet One stop, time period
    final Portlet stopPortlet = new Portlet();
    // Layout preferences
    stopPortlet.setHeadingHtml("Route: " + route + "\t" + dataType
        + " by half hour for one stop. (" + date + ")");
    configPanel(stopPortlet);
    stopPortlet.setHeight(370);
    r = new Resizable(stopPortlet);
    r.setDynamic(true);
    stopPortlet.add(pannelChartOneStop());

    return stopPortlet;
  }

  /**
   * Pannel for One Stop chart
   * 
   * @return ContentPanel
   */
  private ContentPanel pannelChartOneStop() {

    String url;
    final Chart chart;

    // Content panel for chart
    panel = new ContentPanel();
    panel.setFrame(false);
    panel.setBodyBorder(false);
    panel.setHeaderVisible(false);

    // Chart info
    url = "chart/open-flash-chart.swf";
    chart = new Chart(url);
    chart.setBorders(true);
    updateChart3Cmd = new Command() {
      @Override
      public void execute() {
        chart.setChartModel(createChartOneStop(stopTageoid));
      }
    };
    updateChart3Cmd.execute();
    chart.setHeight(320);
    panel.add(chart);

    return panel;

  }

  /**
   * Line and bar chart for One Stop
   * 
   * @param s
   *          (stop id)
   * @return ChartModel
   */
  private ChartModel createChartOneStop(int s) {
    // Chart model initialization
    ChartModel cm = new ChartModel("Stop: " + s + "\t" + dataType
        + " by half hour.",
        "font-size: 14px; font-family: Verdana; text-align: center;");
    cm.setBackgroundColour("#fffff5");
    // Code to add legends and paddings
    Legend lg = new Legend(Position.TOP, true);
    lg.setPadding(5);
    lg.setShadow(false);
    cm.setLegend(lg);

    // Create the X axis
    XAxis xa = new XAxis();
    // set the labels for the axis
    for (double i = startT; i <= stopT; i = i + 0.5) {
      if (i % 1 == 0) {
        xa.addLabels(Integer.toString((int) i));
      } else {
        xa.addLabels("");
      }
    }
    cm.setXAxis(xa);

    Integer[] dataN = new Integer[(stopT - startT) * 2];
    Integer[] dataS = new Integer[(stopT - startT) * 2];

    if ((direction.equals("N") || direction.equals("B")) && route != null) {
      for (int n = 0; n < (stopT - startT) * 2; n++) {
        dataN[n] = sim_dataTimeStopN[n][stopTageoid];
      }
    }
    if ((direction.equals("S") || direction.equals("B")) && route != null) {
      for (int n = 0; n < (stopT - startT) * 2; n++) {
        dataS[n] = sim_dataTimeStopS[n][stopTageoid];
      }
    }

    // Create the Y axis
    YAxis ya = new YAxis();
    // Add the labels to the Y axis
    int maxY = 1;
    if (direction.equals("N"))
      maxY = getMax(dataN);
    if (direction.equals("S"))
      maxY = getMax(dataS);
    if (direction.equals("B")) {
      maxY = Math.max(getMax(dataN), getMax(dataS));
    }
    ya.setRange(0, 1.1 * maxY + 1, 10);
    cm.setYAxis(ya);

    // Creates the line for max Suggested load
    LineChart lchartD = new LineChart();
    lchartD.setColour("#0099FF");
    lchartD.setText("Max 30ft bus");
    for (double n = startT; n <= stopT; n = n + .5) {
      lchartD.addValues(40);
    }
    if (dataType.equals("Load") && maxY >= 40)
      cm.addChartConfig(lchartD);

    LineChart lchartM = new LineChart();
    lchartM.setColour("#0066FF");
    lchartM.setText("Max 40ft bus");
    for (double n = startT; n <= stopT; n = n + .5) {
      lchartM.addValues(70);
    }
    if (dataType.equals("Load") && maxY >= 70)
      cm.addChartConfig(lchartM);

    LineChart lchartU = new LineChart();
    lchartU.setColour("#0033FF");
    lchartU.setText("Max 60ft bus");
    for (double n = startT; n <= stopT; n = n + .5) {
      lchartU.addValues(95);
    }
    if (dataType.equals("Load") && maxY >= 95)
      cm.addChartConfig(lchartU);

    // Creation of the bar chart NORTH DATA
    FilledBarChart bchartN = new FilledBarChart();
    // Layout preferences
    bchartN.setTooltip("#val#");
    bchartN.setText("North");
    bchartN.setColour("#00aa00");
    bchartN.setAnimateOnShow(true);

    if ((direction.equals("N") || direction.equals("B")) && route != null) {
      bchartN.addValues(dataN);
      cm.addChartConfig(bchartN);
    }

    // Creation of the bar chart SOUTH DATA
    FilledBarChart bchartS = new FilledBarChart();
    // Layout preferences
    bchartS.setTooltip("#val#");
    bchartS.setText("South");
    bchartS.setColour("#ff0000");
    bchartS.setAnimateOnShow(true);

    if ((direction.equals("S") || direction.equals("B")) && route != null) {
      bchartS.addValues(dataS);
      cm.addChartConfig(bchartS);
    }

    // Returns the Chart Model
    return cm;

  }

  /**
   * Create All Elements Right Bottom Portlet (Grid) Statistic Information Grid
   * portlet
   * 
   * @return Portlet
   */
  public Portlet getGrid() {
    // Portlet for the Information Grid
    final Portlet gridPortlet = new Portlet();
    gridPortlet.setHeadingHtml("Route: " + route + " " + dataType
        + " Summary (" + date + ")");

    configPanel(gridPortlet);
    gridPortlet.setHeight(370);
    gridPortlet.setLayout(new FitLayout());
    gridPortlet.add(createGrid());

    r = new Resizable(gridPortlet);
    r.setDynamic(true);

    return gridPortlet;
  }

  /**
   * Statistic Information Grid panel
   * 
   * @return ContentPanel
   */
  private ContentPanel createGrid() {
    final Grid<DataStats> grid;
    ArrayList<ColumnConfig> configs = new ArrayList<ColumnConfig>();

    // Columns for statistic types
    ColumnConfig stopCol = new ColumnConfig();
    stopCol.setId("hour");
    stopCol.setHeaderHtml("Hour");
    stopCol.setWidth(50);
    TextField<String> text = new TextField<String>();
    text.setAllowBlank(false);
    stopCol.setEditor(new CellEditor(text));
    configs.add(stopCol);

    ColumnConfig loadBeg = new ColumnConfig();
    loadBeg.setId("load_at_beg");
    loadBeg.setHeaderHtml(dataType + " at stop 1");
    loadBeg.setWidth(90);
    text = new TextField<String>();
    text.setAllowBlank(false);
    loadBeg.setEditor(new CellEditor(text));
    configs.add(loadBeg);

    ColumnConfig loadMid = new ColumnConfig();
    loadMid.setId("load_at_mid");
    loadMid.setHeaderHtml(dataType + " at stop 20");
    loadMid.setWidth(90);
    text = new TextField<String>();
    text.setAllowBlank(false);
    loadMid.setEditor(new CellEditor(text));
    configs.add(loadMid);

    ColumnConfig loadMid2 = new ColumnConfig();
    loadMid2.setId("load_at_mid2");
    loadMid2.setHeaderHtml(dataType + " at stop 40");
    loadMid2.setWidth(90);
    text = new TextField<String>();
    text.setAllowBlank(false);
    loadMid2.setEditor(new CellEditor(text));
    configs.add(loadMid2);

    ColumnConfig loadFin = new ColumnConfig();
    loadFin.setId("load_at_fin");
    loadFin.setHeaderHtml(dataType + " at stop 59");
    loadFin.setWidth(90);
    text = new TextField<String>();
    text.setAllowBlank(false);
    loadFin.setEditor(new CellEditor(text));
    configs.add(loadFin);

    // Information to populate grid
    final ListStore<DataStats> store = new ListStore<DataStats>();
    store.add(GetData.getStats(dataType, sim_data, direction));

    ColumnModel cm = new ColumnModel(configs);
    // Layout preferences
    panel = new ContentPanel();
    panel.setFrame(false);
    panel.setHeight(280);
    panel.setHeaderVisible(false);
    panel.setLayout(new FitLayout());

    // TODO: make the grid highlight the sime of day selected in the One Stop
    // Chart
    GridSelectionModel<DataStats> selectModel = new GridSelectionModel<DataStats>();
    selectModel.select(stopTageoid, true);

    grid = new Grid<DataStats>(store, cm);
    grid.setBorders(true);
    grid.setSelectionModel(selectModel);

    panel.add(grid);

    return panel;
  }

  /*
   * Function: Get the maximum value of an array
   */
  public int getMax(Integer[] list) {
    int max = Integer.MIN_VALUE;
    for (int i = 0; i < list.length; i++) {
      if (list[i] > max) {
        max = list[i];
      }
    }
    return max;
  }

  /**
   * Panel configuration method for portlets
   * 
   * @param panel
   */
  private void configPanel(final ContentPanel panel) {
    // Layout configuration
    panel.setCollapsible(true);
    panel.setAnimCollapse(false);
    panel.setScrollMode(Scroll.AUTOY);
    // Close button
    panel.getHeader().addTool(
        new ToolButton("x-tool-close",
            new SelectionListener<IconButtonEvent>() {

              @Override
              public void componentSelected(IconButtonEvent ce) {
                panel.removeFromParent();
              }

            }));
  }
}