// Main page CTA portal code
// Called from webapp.java

package dssg.client;

// Imported libraries
import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.event.ChartEvent;
import com.extjs.gxt.charts.client.event.ChartListener;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.AreaChart;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.DragListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.Slider;

import dssg.simulator.SimulationInstance;

// Main class containing all elements
public class GwtPortalContainer extends LayoutContainer {

	// Global variables

	private FormData formData;
	private VerticalPanel vp;
	private Image image;
	private ContentPanel panel;
	private Command updateCmd;
	private Command updateChart1Cmd;
	private Integer time = 0;
	private Resizable r;
	private String route = "0";

	// Main portal container (main window) is a portal container which is
	// divided into North, South, East and West regions.
	public GwtPortalContainer() {
	}

	// Elements to add upon rendering
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
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
		westData.setSplit(true);
		westData.setMargins(new Margins(0, 5, 0, 0));
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0));
		// BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST,
		// 200);
		// eastData.setSplit(true);
		// eastData.setCollapsible(true);
		// eastData.setMargins(new Margins(0, 0, 0, 5));

		// Regions added to the main function //
		add(getNorth(), northData);
		add(getWest(), westData);
		add(getCenter(), centerData);
		// add(getEast(), eastData);
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
		image = new Image();
		image.setUrl(GWT.getModuleBaseURL() + "000033.png");
		north.add(image, new RowData(.03, 1, new Margins(4)));
		// CTA logo
		image = new Image();
		image.setUrl(GWT.getModuleBaseURL() + "cta-logo2.gif");
		image.setSize("415px", "70px");
		panel = new ContentPanel();
		panel.setFrame(false);
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		panel.setBodyStyle("background: #000033");
		panel.add(image);
		north.add(panel, new RowData(.27, 1, new Margins(6, 0, 0, 0)));
		// blue image
		image = new Image();
		image.setUrl(GWT.getModuleBaseURL() + "000033.png");
		north.add(image, new RowData(.62, 1, new Margins(4)));
		// DSSG logo round
		image = new Image();
		image.setUrl(GWT.getModuleBaseURL() + "dssg-logo.png");
		image.setSize("65px", "65px");
		panel = new ContentPanel();
		panel.setFrame(false);
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		panel.setBodyStyle("background: #000033");
		panel.add(image);
		north.add(panel, new RowData(.05, 1, new Margins(10, 0, 0, 0)));
		// Blue image
		image = new Image();
		image.setUrl(GWT.getModuleBaseURL() + "000033.png");
		north.add(image, new RowData(.03, 1, new Margins()));
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
		west.setHeading("Information Tools");

		// Content panel for "Information Type"
		panel = new ContentPanel();
		// Layout preferences
		panel.setHeading("General Information");
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
		panel.setHeading("Chart Options");
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
		panel.setHeading("Upload Files");
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

	// --- East region information (region to the right of the webapp) ---
	private ContentPanel getEast() {
		ContentPanel east = new ContentPanel();
		// Layout preferences
		east.setBorders(true);
		east.setBodyBorder(true);
		east.setAutoHeight(true);
		east.setLayout(new AccordionLayout());
		east.setHeading("Additional Info");
		east.setHeight(200);

		// Content panel for statistics
		panel = new ContentPanel();
		// Layout preferences
		panel.setHeading("Statistical Summary");
		panel.setBorders(false);
		panel.setCollapsible(true);
		formData = new FormData("-20");

		// Vertical panel to display form 4
		vp = new VerticalPanel();
		vp.setSpacing(15);
		createFormEast();
		panel.add(vp);
		east.add(panel);

		// Content panel for additional information
		panel = new ContentPanel();
		// Layout preferences
		panel.setHeading("Event Information");
		panel.setBorders(false);
		panel.setCollapsible(true);
		panel.setBodyStyle("fontSize: 12px; padding: 10px");
		panel.addText("More event information here");
		panel.collapse();
		east.add(panel);

		return east;
	}

	// --- Center region information (center region of the webapp) ---
	private ContentPanel getCenter() {
		ContentPanel center = new ContentPanel();
		// Layout preferences
		center.setHeading("Charts");
		center.setScrollMode(Scroll.AUTOX);

		// Local variables
		Portlet portlet;

		// Portal columns created for portlet items (sub-regions)
		Portal portal = new Portal(2);
		// Layout preferences
		portal.setBorders(true);
		portal.setStyleAttribute("backgroundColor", "white");
		portal.setColumnWidth(0, .75);
		portal.setColumnWidth(1, .25);

		/*
		 * Portlet Layout preferences FIXME the separation between items should
		 * be greater
		 */
		// Box Layout for the charts and sliders
		VBoxLayout portletLayout = new VBoxLayout();
		portletLayout.setPadding(new Padding(15));
		portletLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);

		// Portlet for the Crowding charts
		portlet = new Portlet();
		// Layout preferences
		portlet.setHeading("Crowding");
		configPanel(portlet);
		portlet.setHeight(700);
		portlet.setLayout(portletLayout);
		r = new Resizable(portlet);
		r.setDynamic(true);
		VBoxLayoutData vBoxData = new VBoxLayoutData(10, 25, 10, 40);
		portlet.add(getCrowdingChart());
		portlet.add(getHourControls(), vBoxData);
		portlet.add(getCrowdingChart2());

		portal.add(portlet, 0); // Portlet added to the first column of the
								// portal

		// Portlet for the Information Grid
		portlet = new Portlet();
		portlet.setHeading("Information Grid 2");
		configPanel(portlet);
		portlet.setLayout(new FitLayout());
		/* FIXME create some grid data to populate this. */
		/* portlet.add(createGrid()); Grid data to be created. */
		portlet.setHeight(250);
		r = new Resizable(portlet);
		r.setDynamic(true);

		portal.add(portlet, 1); // Portlet added to the second column of the
								// portal

		// Portlet for the Delay Graph
		portlet = new Portlet();
		// Layout preferences
		portlet.setHeading("Delay by stop");
		configPanel(portlet);
		r = new Resizable(portlet);
		r.setDynamic(true);
		portlet.add(getPieChart());
		portal.add(portlet, 1);

		// Portal added to the center region
		center.add(portal);

		return center;
	}

	// -- Methods to get Center Charts
	private ContentPanel getCrowdingChart() {
		// Static chart
		// FIXME add refresh listener for route id submit button
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
		updateChart1Cmd = new Command() {
			public void execute() {
				chart.setChartModel(getLoad24());
			}
		};
		updateChart1Cmd.execute();

		return panel;
	}

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
		updateCmd = new Command() {
			public void execute() {
				chart.setChartModel(getLoadAtTime());
			}
		};
		updateCmd.execute();

		return panel;
	}

	private ContentPanel getPieChart() {

		String url;
		Chart chart;
		Resizable r; // Charts can be resized

		// Content panel for chart
		panel = new ContentPanel();
		panel.setFrame(false);
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);

		// Chart info
		url = "chart/open-flash-chart.swf";
		chart = new Chart(url);
		chart.setBorders(true);
		chart.setChartModel(getPieChartData());

		panel.add(chart);

		return panel;

	}

	// -- Methods to get Center Controls
	private SliderField getHourControls() {

		// 24 hrs slider
		Slider slider = new Slider();
		slider.setWidth(200);
		slider.setIncrement(1);
		slider.setMaxValue(23);
		slider.setMinValue(0);
		slider.setTitle("Hour select");
		slider.setMessage("{0} hrs");
		final SliderField sf = new SliderField(slider);
		sf.setFieldLabel("Time:");

		slider.addListener(Events.Change, new Listener<SliderEvent>() {
			public void handleEvent(SliderEvent be) {
				time = be.getNewValue();
				updateCmd.execute();
			}
		});

		return sf;
	}

	// -- Methods to create forms --
	// Form to display data visualization options
	private void createGenInfWest() {

		// Local variables
		final TextField<String> text;

		// Initial form panel
		FormPanel simple = new FormPanel();
		// Layout preferences
		simple.setFrame(false);
		simple.setHeaderVisible(false);
		simple.setButtonAlign(HorizontalAlignment.CENTER);

		// Text fields for route number
		text = new TextField<String>();
		text.setFieldLabel("Route");
		text.setAllowBlank(false);
		text.setEmptyText("Route number");
		simple.add(text, formData);

		// Date field
		DateField date = new DateField();
		date.setFieldLabel("Date");
		simple.add(date, formData);
		// Time field
		TimeField time = new TimeField();
		time.setFieldLabel("Time");
		simple.add(time, formData);
		// Submit and Cancel buttons
		Button b = new Button("Submit");
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				route = text.getValue();
				callSimulationServices();
				updateChart1Cmd.execute();
				updateCmd.execute();
			}
		});
		simple.add(b);
		simple.add(new Button("Cancel"));

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
		FileUploadField file = new FileUploadField();
		file.setAllowBlank(true);
		file.setEmptyText("GTSF file");
		file.setData("text", "Choose GTSF file");
		simple.add(file);
		file = new FileUploadField();
		file.setAllowBlank(true);
		file.setEmptyText("Schedule file");
		file.setData("text", "Choose Schedule file");
		simple.add(file);

		// Reset Button
		Button btn = new Button("Reset");
		/* FIXME figure out what is the equivalent for GWT 3 */
		// btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
		// @Override
		// public void componentSelected(ButtonEvent ce) {
		// simple.reset();
		// }
		// });
		simple.add(btn);
		// Submit Button
		btn = new Button("Submit");
		/* FIXME figure out what is the equivalent for GWT 3 */
		// btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
		// @Override
		// public void componentSelected(ButtonEvent ce) {
		// if (!simple.isValid()) {
		// return;
		// }
		// // normally would submit the form but for example no server set up to
		// // handle the post
		// // panel.submit();
		// MessageBox.info("Action", "You file was uploaded", null);
		// }
		// });
		simple.add(btn);

		vp.add(simple);
	}

	// Form to display statistical information
	private void createFormEast() {
		// Initial panel
		final FormPanel simple = new FormPanel();
		// Layout preferences
		simple.setLayout(new FillLayout());
		simple.setFrame(false);
		simple.setHeaderVisible(false);

		// Panel informations
		simple.addText("Mean: 40");
		simple.addText("Median: 30");
		simple.addText("Max: 30");
		simple.addText("Min: 30");
		simple.addText("75%: 30");
		simple.addText(" ");

		vp.add(simple);
	}

	// Panel configuration method for all center portlets
	private void configPanel(final ContentPanel panel) {
		// Layout configuration
		panel.setCollapsible(true);
		panel.setAnimCollapse(false);
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

	// -- Methods to get charts --
	// Area chart for Load 24hrs
	public ChartModel getLoad24() {
		// Create a ChartModel with the Chart Title and some style attributes
		ChartModel cm = new ChartModel("Max load per hour. Route: " + route,
				"font-size: 14px; font-family:      Verdana; text-align: center;");

		// Create the X axis
		XAxis xa = new XAxis();
		xa.setOffset(true);
		// set the labels for the axis
		for (int i = 0; i < 23; i++) {
			xa.addLabels(Integer.toString(i));
		}

		cm.setXAxis(xa);

		// Create the Y axis
		YAxis ya = new YAxis();
		// Add the labels to the Y axis
		ya.setRange(0, 300, 50);
		cm.setYAxis(ya);

		// Create a Area Chart object and add points to the object
		LineChart lchart = new LineChart();
		lchart.setColour("#00aa00");
		lchart.setTooltip("#val#");
		for (int n = 0; n < 24; n++) {
			lchart.addValues(Math.abs( 150
					* Math.sin(n * Math.PI / 20 - Math.PI * 4 / 24)
					+ Random.nextDouble() * 80 ));
		}
		cm.addChartConfig(lchart);

		// Create a Area Chart object and add points to the object
		lchart = new LineChart();
		lchart.setColour("#ff0000");
		lchart.setTooltip("#val#");
		for (int n = 0; n < 24; n++) {
			lchart.addValues(Math.abs( 180
					* Math.sin(n * Math.PI / 20 - Math.PI * 4 / 24)
					+ Math.random() * 80 ));
		}
		cm.addChartConfig(lchart);

		// Returns the Chart Model
		return cm;
	}

	// Area chart for Load per stop @ TIME
	public ChartModel getLoadAtTime() {
		// Create a ChartModel with the Chart Title and some style attributes
		ChartModel cm = new ChartModel("Max load @ " + time,
				"font-size: 14px; font-family:      Verdana; text-align: center;");

		// Create the X axis
		XAxis xa = new XAxis();
		xa.setOffset(true);
		// set the labels for the axis
		for (int i = 0; i < 80; i++) {
			xa.addLabels(Integer.toString(i));
		}

		cm.setXAxis(xa);

		// Create the Y axis
		YAxis ya = new YAxis();
		// Add the labels to the Y axis
		ya.setRange(0, 220, 50);
		cm.setYAxis(ya);

		// Create a Area Chart object and add points to the object
		LineChart lchart = new LineChart();
		lchart.setColour("#00aa00");
		lchart.setTooltip("#val#");
		for (int n = 0; n < 80; n++) {
			lchart.addValues(Math.sin(time * (Math.PI / 24) + Math.PI / 24)
					* Math.abs(Math.cos(Random.nextDouble()) * 180
							* Math.sin(n* Math.PI / 70)) + 20);
		}
		cm.addChartConfig(lchart);

		// Create a Area Chart object and add points to the object
		lchart = new LineChart();
		lchart.setColour("#ff0000");
		lchart.setTooltip("#val#");
		for (int n = 0; n < 80; n++) {
			lchart.addValues(Math.sin(time * (Math.PI / 24) + Math.PI / 24)
					* Math.abs(Math.cos(Random.nextDouble()) * 180
							* Math.sin(n* Math.PI / 70)) + 20);
		}
		cm.addChartConfig(lchart);

		// Returns the Chart Model
		return cm;
	}

	// Area chart for Delta Times
	private ChartModel getPieChartData() {
		// Chart model initialization
		ChartModel cm = new ChartModel("",
				"font-size: 10px; font-family: Verdana; text-align: center;");
		cm.setBackgroundColour("#fffff5");
		// Code to add legends and paddings
		// Legend lg = new Legend(Position.RIGHT, true);
		// lg.setPadding(10);
		// cm.setLegend(lg);

		// Creation of the pie chart
		PieChart pie = new PieChart();
		// Layout preferences
		pie.setAlpha(0.5f);
		pie.setNoLabels(true);
		pie.setTooltip("#label# #val#<br>#percent#");
		pie.setColours("#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff");
		// Data
		pie.addSlices(new PieChart.Slice(5, "Mich/Lad", ""));
		pie.addSlices(new PieChart.Slice(6.5, "Mich/Ost", ""));
		pie.addSlices(new PieChart.Slice(2.7, "Mich/Hyd", ""));
		pie.addSlices(new PieChart.Slice(0.5, "Mich/Ren", ""));
		pie.addSlices(new PieChart.Slice(1.2, "Mich/Tor", ""));
		pie.addChartListener(listener);

		cm.addChartConfig(pie);
		// Returns the Chart Model
		return cm;

	}

	// Cart listener for the area charts
	private ChartListener listener = new ChartListener() {

		public void chartClick(ChartEvent ce) {
			Info.display("Chart Clicked", "You selected {0}.",
					"" + ce.getValue());
		}
	};

	private void callSimulationServices() {
		Info.display("Calling simulation services", "");
	}

}
