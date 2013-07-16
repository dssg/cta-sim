// Main page CTA portal code
// Called from webapp.java

package dssg.client;

// Imported libraries
import java.util.ArrayList;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.event.ChartEvent;
import com.extjs.gxt.charts.client.event.ChartListener;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.FilledBarChart;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.extjs.gxt.ui.client.widget.Slider;

// Main class containing all elements
public class GwtPortalContainer extends Viewport {

	// Global variables

	private FormData formData;
	private VerticalPanel vp;
	private ContentPanel panel;
	private Resizable r;
	private Command updateChart1Cmd;
	private Command updateChart2Cmd;
	private Command updateChart3Cmd;
	private Command updateSliderCmd;
	private Command updateSlider2Cmd;
	private Command createCenterPanelCmd;
	private Integer time = 0;
	private Integer route = null;
	private Boolean bothDir = true;
	private Boolean north = true;
	private Integer startT = 0;
	private Integer stopT = 23;
	private Integer numStops = 80;
	private Integer stop = 0;
	private S3CommunicationServiceAsync s3ComunicationService;
	// Main portal container (main window) is a portal container which is
	// divided into North, South, East and West regions.

	public GwtPortalContainer(SimulationServiceAsync simulationService, S3CommunicationServiceAsync s3ComunicationService) {
		this.s3ComunicationService = s3ComunicationService;
	}

	// Elements to add upon rendering
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		Viewport view = new Viewport();
		view.setScrollMode(Scroll.AUTOY);
		view.setMonitorWindowResize(true);
		view.setStyleAttribute("backgroundcolor", "#000033");

		// Layout preferences for the entire page
		final BorderLayout layout = new BorderLayout();
		setLayout(layout);
		// --Layout data for all regions--
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH,
				80);
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

		// Regions added to the main function
		// FIXME
		// protected void onWindowResize() {
		// @Override
		//
		//
		// }
		add(getNorth(), northData);
		add(getWest(), westData);
		add(getCenter(), centerData);
	}

	// --- North region information (top region of the webapp) ---
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

	// --- West region information (region to the right of the webapp) ---
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

	// --- Center region information (center region of the webapp) ---
	private ContentPanel getCenter() {
		final ContentPanel center = new ContentPanel();
		// Layout preferences
		center.setHeadingHtml("Charts");
		// FIXME scroll is only working on the edges
		center.setScrollMode(Scroll.AUTOY);
		r = new Resizable(center);
		r.setDynamic(true);
		// Local variables
		Portlet portlet;

		// Portal columns created for portlet items (sub-regions)
		final Portal portal = new Portal(2);
		// Layout preferences
		portal.setBorders(true);
		portal.setStyleAttribute("backgroundColor", "white");
		portal.setColumnWidth(0, .55);
		portal.setColumnWidth(1, .45);
		portal.setScrollMode(Scroll.AUTOY);

		// Box Layout for the charts and sliders
		final VBoxLayout portletLayout = new VBoxLayout();
		portletLayout.setPadding(new Padding(15));
		portletLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);

		final Portlet centerPortlet = new Portlet();
		// Layout preferences
		centerPortlet.setHeadingHtml("Load");
		configPanel(centerPortlet);
		centerPortlet.setHeight(750);
		centerPortlet.setLayout(portletLayout);
		r = new Resizable(centerPortlet);
		r.setDynamic(true);
		final VBoxLayoutData vBoxData = new VBoxLayoutData(10, 15, 10, 30);
		final VBoxLayoutData vBoxData2 = new VBoxLayoutData(10, 10, 10, 25);
		centerPortlet.add(getCrowdingChart());
		centerPortlet.add(getHourControls(), vBoxData);
		centerPortlet.add(getCrowdingChart2());
		centerPortlet.add(getHourControls2(), vBoxData2);

		// Portlet for the Load @ stop, time period
		final Portlet stopPortlet = new Portlet();
		// Layout preferences
		stopPortlet.setHeadingHtml("Load @ stop.");
		configPanel(stopPortlet);
		stopPortlet.setHeight(370);
		r = new Resizable(stopPortlet);
		r.setDynamic(true);
		stopPortlet.add(getCrowdingChart3());

		// Portlet for the Information Grid
		final Portlet gridPortlet = new Portlet();
		gridPortlet.setHeadingHtml("Stat. Information");

		configPanel(gridPortlet);
		gridPortlet.setHeight(370);
		gridPortlet.setLayout(new FitLayout());
		gridPortlet.add(createGrid());
		r = new Resizable(gridPortlet);
		r.setDynamic(true);

		// Portlet for the Crowding charts
		createCenterPanelCmd = new Command() {
			@Override
			public void execute() {
				portal.add(centerPortlet, 0);
				portal.add(stopPortlet, 1);
				portal.add(gridPortlet, 1);
			}
		};
		// Portal added to the center region
		center.add(portal);
		return center;
	}

	// -- Methods to create forms --
	// Form to display data visualization options
	private void createGenInfWest() {

		// Local variables
		final TextField<String> text;
		RadioGroup rGroup = new RadioGroup();

		// Initial form panel
		final FormPanel simple = new FormPanel();
		// Layout preferences
		simple.setFrame(false);
		simple.setHeaderVisible(false);

		// Add radio button for North South
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
		ListStore<MyRoutes> routes = new ListStore<MyRoutes>();
		routes.add(Data.getRoutes());

		final ComboBox<MyRoutes> routeCmb = new ComboBox<MyRoutes>();
		routeCmb.setFieldLabel("Route");
		routeCmb.setEmptyText("Select a route");
		routeCmb.setAllowBlank(false);
		routeCmb.setDisplayField("name");
		routeCmb.setStore(routes);
		routeCmb.setTypeAhead(true);
		simple.add(routeCmb, formData);

		// Date field
		DateField date = new DateField();
		date.setFieldLabel("Date");
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
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				route = routeCmb.getValue().getId();
				north = northB.getValue();
				bothDir = bothB.getValue();

				if (routeCmb.getValue() == null || timeS.getValue() == null
						|| timeF.getValue() == null) {
					MessageBox.alert("Carefull", "Null values not allowed",
							null);
				} else if (timeS.getDateValue().getHours() > timeF
						.getDateValue().getHours()) {
					MessageBox.alert("Carefull", "Time window is incorrect.",
							null);
				} else {
					callSimulationServices();
					callLoading();
					startT = timeS.getDateValue().getHours();
					stopT = timeF.getDateValue().getHours();
					updateChart1Cmd.execute();
					updateChart2Cmd.execute();
					updateChart3Cmd.execute();
					updateSliderCmd.execute();
					updateSlider2Cmd.execute();
					createCenterPanelCmd.execute();
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
		CheckBox check1 = new CheckBox();
		check1.setBoxLabel("Load/Flow");
		simple.add(check1);
		check1 = new CheckBox();
		check1.setBoxLabel("Delta Time");
		simple.add(check1);
		check1 = new CheckBox();
		check1.setBoxLabel("Crowding ");
		simple.add(check1);
		check1 = new CheckBox();
		check1.setBoxLabel("Bunching");
		simple.add(check1);

		// Submit and Cancel buttons
		Button b = new Button("Submit");
		simple.add(b);
		simple.add(new Button("Cancel"));
		simple.setWidth(235);

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
		gtsfFile.setAllowBlank(true);
		gtsfFile.setEmptyText("GTSF file");
		gtsfFile.setData("text", "Choose GTSF file");
		simple.add(gtsfFile);
		final FileUploadField schedFile = new FileUploadField();
		schedFile.setAllowBlank(true);
		schedFile.setEmptyText("Schedule file");
		schedFile.setData("text", "Choose Schedule file");
		simple.add(schedFile);

		// Submit Button
		Button submitBtn = new Button("Submit");
		submitBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				callS3(gtsfFile.getValue());			}
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

	// -- GET Center Objects --
	// Crowding Chart
	private ContentPanel getCrowdingChart() {
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
				chart.setChartModel(getLoad24());
			}
		};
		updateChart1Cmd.execute();

		return panel;
	}

	// Crowding Chart 2
	private ContentPanel getCrowdingChart2() {

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
				chart.setChartModel(getLoadAtTime());
				panel.expand();
			}
		};
		updateChart2Cmd.execute();
		return panel;
	}

	// Crowding Chart 3
	private ContentPanel getCrowdingChart3() {

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
				chart.setChartModel(getLoadAtStop(stop));
			}
		};
		updateChart3Cmd.execute();
		chart.setHeight(320);
		panel.add(chart);

		return panel;

	}

	// Center Slider
	private SliderField getHourControls() {
		// 24 hrs slider
		final Slider slider = new Slider();
		slider.setIncrement(1);
		// FIXME slider.setStyleName("project-Slider");
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

		slider.addListener(Events.Change, new Listener<SliderEvent>() {
			@Override
			public void handleEvent(SliderEvent be) {
				slider.setMessage(((slider.getValue())) * .5 + " hrs");
				time = be.getNewValue() / 2;
				updateChart2Cmd.execute();
			}
		});

		return sf;
	}

	// Bottom Slider
	private SliderField getHourControls2() {
		// 24 hrs slider
		final Slider slider = new Slider();
		slider.setIncrement(1);
		slider.addStyleName("sliderStyle");
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

		slider.addListener(Events.Change, new Listener<SliderEvent>() {
			@Override
			public void handleEvent(SliderEvent be) {
				slider.setMessage("Stop:" + slider.getValue());
				stop = be.getNewValue();
				updateChart3Cmd.execute();
			}
		});

		return sf;
	}

	// Stat. Information Grid
	private ContentPanel createGrid() {
		final Grid<MyStats> grid;
		ArrayList<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig stopCol = new ColumnConfig();
		stopCol.setId("stop");
		stopCol.setHeaderHtml("Stop");
		stopCol.setWidth(120);
		TextField<String> text = new TextField<String>();
		text.setAllowBlank(false);
		stopCol.setEditor(new CellEditor(text));
		configs.add(stopCol);

		ColumnConfig minCol = new ColumnConfig();
		minCol.setId("min");
		minCol.setHeaderHtml("Max Load");
		minCol.setWidth(120);
		text = new TextField<String>();
		text.setAllowBlank(false);
		minCol.setEditor(new CellEditor(text));
		configs.add(minCol);

		ColumnConfig meanCol = new ColumnConfig();
		meanCol.setId("mean");
		meanCol.setHeaderHtml("Current Headway");
		meanCol.setWidth(120);
		text = new TextField<String>();
		text.setAllowBlank(false);
		meanCol.setEditor(new CellEditor(text));
		configs.add(meanCol);

		ColumnConfig thCol = new ColumnConfig();
		thCol.setId("th");
		thCol.setHeaderHtml("Recom Headway");
		thCol.setWidth(120);
		text = new TextField<String>();
		text.setAllowBlank(false);
		thCol.setEditor(new CellEditor(text));
		configs.add(thCol);

		ColumnConfig maxCol = new ColumnConfig();
		maxCol.setId("max");
		maxCol.setHeaderHtml("Running Time");
		maxCol.setWidth(120);
		text = new TextField<String>();
		text.setAllowBlank(false);
		maxCol.setEditor(new CellEditor(text));
		configs.add(maxCol);

		final ListStore<MyStats> store = new ListStore<MyStats>();
		store.add(Data.getStats());

		ColumnModel cm = new ColumnModel(configs);

		panel = new ContentPanel();
		panel.setFrame(false);
		panel.setHeight(280);
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());

		GridSelectionModel<MyStats> select = new GridSelectionModel<MyStats>();
		grid = new Grid<MyStats>(store, cm);
		grid.setBorders(true);
		grid.setSelectionModel(select);
		select.select(stop, false);
		panel.add(grid);

		return panel;
	}

	// -- CHARTS --
	// Area chart for Load 24hrs
	private ChartModel getLoad24() {
		// Create a ChartModel with the Chart Title and some style attributes
		ChartModel cm = new ChartModel("Route: " + route
				+ "   Max load per hour, all stops.",
				"font-size: 14px; font-family:      Verdana; text-align: center;");
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
				xa.addLabels(Double.toString(i));
			} else {
				xa.addLabels("");
			}
		}
		cm.setXAxis(xa);

		// Create the Y axis
		YAxis ya = new YAxis();
		// Add the labels to the Y axis
		ya.setRange(0, 300, 50);
		cm.setYAxis(ya);

		// Create a Area Chart object NORTH
		LineChart lchart = new LineChart();
		lchart.setAnimateOnShow(true);
		lchart.setColour("#00aa00");
		lchart.setTooltip("#val#");
		lchart.setText("North");
		for (double n = startT; n <= stopT; n = n + .5) {
			lchart.addValues(Math.floor(Math.abs(150
					* Math.sin(n * Math.PI / 20 - Math.PI * 4 / 24)
					+ Random.nextDouble() * 80)));
		}
		if ((north == true || bothDir == true) && route != null) {
			cm.addChartConfig(lchart);
		}

		// Create a Area Chart object SOUTH
		lchart = new LineChart();
		lchart.setAnimateOnShow(true);
		lchart.setColour("#ff0000");
		lchart.setTooltip("#val#");
		lchart.setText("South");
		for (double n = startT; n <= stopT; n = n + .5) {
			lchart.addValues(Math.floor(Math.abs(180
					* Math.sin(n * Math.PI / 20 - Math.PI * 4 / 24)
					+ Math.random() * 80)));
		}
		if ((north == false || bothDir == true) && route != null) {
			cm.addChartConfig(lchart);
		}

		// Creates the line for max sugested load
		lchart = new LineChart();
		lchart.setColour("#0099FF");
		lchart.setText("Suggested Max Load");
		for (double n = startT; n <= stopT; n = n + .5) {
			lchart.addValues(220);
		}
		cm.addChartConfig(lchart);

		// Returns the Chart Model
		return cm;
	}

	// Area chart for Load per stop @ TIME
	public ChartModel getLoadAtTime() {
		// Create a ChartModel with the Chart Title and some style attributes
		ChartModel cm = new ChartModel("Time: " + time
				+ "hrs.   Max load per stop ",
				"font-size: 14px; font-family:      Verdana; text-align: center;");
		// Code to add legends and paddings
		Legend lg = new Legend(Position.TOP, true);
		lg.setPadding(5);
		lg.setShadow(false);
		cm.setLegend(lg);
		// Create the X axis
		XAxis xa = new XAxis();
		xa.setOffset(true);
		// set the labels for the axis
		for (int i = 0; i <= numStops; i++) {
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
		ya.setRange(0, 300, 50);
		cm.setYAxis(ya);

		// Create a Line Chart object NORTH
		LineChart lchart = new LineChart();
		lchart.setAnimateOnShow(true);
		lchart.addChartListener(listener); // FIXME use this listener to refresh
											// other plot
		lchart.setColour("#00aa00");
		lchart.setTooltip("#val#");
		lchart.setText("North");
		for (int n = 0; n <= numStops; n++) {
			lchart.addValues(Math.floor(Math.abs(Math.sin(time * (Math.PI / 24)
					+ Math.PI / 24))
					* Math.abs(Math.cos(Random.nextDouble()) * 220
							* Math.sin(n * Math.PI / 70)) + 20));
		}
		if ((north == true || bothDir == true) && route != null) {
			cm.addChartConfig(lchart);
		}

		// Create a Line Chart object SOUTH
		lchart = new LineChart();
		lchart.setAnimateOnShow(true);
		lchart.addChartListener(listener); // FIXME use this listener to refresh
											// other plot
		lchart.setColour("#ff0000");
		lchart.setTooltip("#val#");
		lchart.setText("South");
		for (int n = 0; n <= numStops; n++) {
			lchart.addValues(Math.abs(Math.sin(time * (Math.PI / 24) + Math.PI
					/ 24))
					* Math.abs(Math.floor(Math.cos(Random.nextDouble()) * 220
							* Math.sin(n * Math.PI / 70)) + 20));
		}
		if ((north == false || bothDir == true) && route != null) {
			cm.addChartConfig(lchart);
		}

		// Creates the line for max sugested load
		lchart = new LineChart();
		lchart.setColour("#0099FF");
		lchart.setText("Suggested Max Load");
		for (double n = 0; n <= numStops; n++) {
			lchart.addValues(220);
		}
		cm.addChartConfig(lchart);

		// Returns the Chart Model
		return cm;
	}

	// Chart for Load for a stop
	private ChartModel getLoadAtStop(int s) {
		// Chart model initialization
		ChartModel cm = new ChartModel("Stop: " + s + "  Load per hour.",
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
				xa.addLabels(Double.toString(i));
			} else {
				xa.addLabels("");
			}
		}
		cm.setXAxis(xa);
		// Create the Y axis
		YAxis ya = new YAxis();
		// Add the labels to the Y axis
		ya.setRange(0, 300, 50);
		cm.setYAxis(ya);

		Double[] dataN = new Double[(int) (stopT - startT)*2];
		Double[] dataNM = new Double[(int) (stopT - startT)*2];
		Double[] dataS = new Double[(int) (stopT - startT)*2];
		Double[] dataSM = new Double[(int) (stopT - startT)*2];
		for (int n = 0; n < (stopT - startT) * 2; n++) {
			dataN[n] = Math.floor(Math.abs(150
					* Math.sin(n * Math.PI / 24 - Math.PI * 4 / 24)
					+ Random.nextDouble() * 80));
			dataNM[n] = dataN[n] * 3 / 4;
			dataS[n] = Math.floor(Math.abs(150
					* Math.sin(n * Math.PI / 24 - Math.PI * 4 / 24)
					+ Random.nextDouble() * 80));
			dataSM[n] = dataS[n] * 3 / 4;
		}

		// Creation of the bar chart NORTH
		FilledBarChart bchartN = new FilledBarChart();
		// Layout preferences
		bchartN.setTooltip("#val#");
		bchartN.setText("North");
		bchartN.setColour("#00aa00");
		bchartN.setAnimateOnShow(true);
		bchartN.addValues(dataN);

		LineChart lchartN = new LineChart();
		// Layout preferences
		lchartN.setTooltip("#val#");
		lchartN.setText("North Mean");
		lchartN.setColour("#006600");
		lchartN.setAnimateOnShow(true);
		lchartN.addValues(dataNM);

		if ((north == true || bothDir == true) && route != null) {
			cm.addChartConfig(bchartN);
			cm.addChartConfig(lchartN);
		}

		// Creation of the bar chart SOUTH
		FilledBarChart bchartS = new FilledBarChart();
		// Layout preferences
		bchartS.setTooltip("#val#");
		bchartS.setText("South");
		bchartS.setColour("#ff0000");
		bchartS.setAnimateOnShow(true);
		bchartS.addValues(dataS);

		LineChart lchartS = new LineChart();
		// Layout preferences
		lchartS.setTooltip("#val#");
		lchartS.setText("South Mean");
		lchartS.setColour("#cc0000");
		lchartS.setAnimateOnShow(true);
		lchartS.addValues(dataSM);

		if ((north == false || bothDir == true) && route != null) {
			cm.addChartConfig(bchartS);
			cm.addChartConfig(lchartS);
		}

		// Creates the line for max sugested load
		LineChart lchart = new LineChart();
		lchart.setColour("#0099FF");
		lchart.setText("Suggested Max Load");
		for (double n = startT; n <= stopT; n = n + .5) {
			lchart.addValues(220);
		}
		cm.addChartConfig(lchart);

		// Returns the Chart Model
		return cm;

	}

	// Chart listener for the area charts
	private ChartListener listener = new ChartListener() {

		@Override
		public void chartClick(ChartEvent ce) {
			Info.display("Chart Clicked", "You selected {0}.",
					"" + ce.getValue());
		}
	};

	// -- SIMULATION --
	private void callSimulationServices() {
		Info.display("Calling simulation services", "");
	}

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

	// -- AWS S3 --
	private void callS3(final String file) {
		this.s3ComunicationService.uploadFile(file, new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer arg0) {
				Info.display("Sucess", Integer.toString(arg0));
				
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				Info.display("Failure", file);
				
			}
		});
	}

	// Panel configuration method for all center portlets
	private void configPanel(final ContentPanel panel) {
		// Layout configuration
		panel.setCollapsible(true);
		panel.setAnimCollapse(false);
		panel.setScrollMode(Scroll.AUTOY);
		// panel.getHeader().addTool(new ToolButton("x-tool-gear")); //Deleted
		// the gear button
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
