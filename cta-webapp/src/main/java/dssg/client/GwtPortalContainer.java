package dssg.client; 

  
import java.util.ArrayList;  
import java.util.Collections;
import java.util.List;  
import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.event.ChartEvent;
import com.extjs.gxt.charts.client.event.ChartListener;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.ToolTip.MouseStyle;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.AreaChart;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.HorizontalBarChart;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;  
import com.extjs.gxt.ui.client.Style.LayoutRegion;  
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;  
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;  
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.store.ListStore;  
import com.extjs.gxt.ui.client.util.Margins;  
import com.extjs.gxt.ui.client.widget.ContentPanel;  
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;  
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;  
import com.extjs.gxt.ui.client.widget.custom.Portal;  
import com.extjs.gxt.ui.client.widget.custom.Portlet;  
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;  
import com.extjs.gxt.ui.client.widget.grid.ColumnData;  
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;  
import com.extjs.gxt.ui.client.widget.grid.Grid;  
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;  
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;  
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;  
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;  
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;  
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.i18n.client.NumberFormat;  
import com.google.gwt.user.client.Element;  
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.layout.VerticalLayout;

import dssg.simulator.SimulationInstance;
import com.extjs.gxt.ui.client.widget.Slider;
  
public class GwtPortalContainer extends LayoutContainer implements RequiresResize, ProvidesResize{
	FormData formData;
    VerticalPanel vp;
    
	public GwtPortalContainer() {
	}  
  
  @Override  
  protected void onRender(Element parent, int index) {  
    super.onRender(parent, index);  
    final BorderLayout layout = new BorderLayout();  
    setLayout(layout); 
    
    
    Slider slider = new Slider();
  
    LayoutContainer north = new LayoutContainer();
    ContentPanel west = new ContentPanel(); 
    ContentPanel center = new ContentPanel();
    ContentPanel east = new ContentPanel();  

// North  information
    north.setBorders(false);
    north.setLayout(new RowLayout(Orientation.HORIZONTAL));
    north.setStyleAttribute("background-color", "#000033");
    
    
    Image image = new Image();
    image.setUrl("http://www.colorhexa.com/000033.png");
    north.add(image, new RowData(.03, 1, new Margins(4)));
    image = new Image();
    image.setUrl("http://www.transitchicago.com/images/global/hdr-cta.gif");
    north.add(image, new RowData(.32, 1, new Margins(4)));
    image = new Image();
    image.setUrl("http://www.colorhexa.com/000033.png");
    north.add(image, new RowData(.54, 1, new Margins(4)));
    image = new Image();
    image.setUrl("http://dssg.io/img/logo.png");
    north.add(image, new RowData(.08, 1, new Margins(4)));
    image = new Image();
    image.setUrl("http://www.colorhexa.com/000033.png");
    north.add(image, new RowData(.03, 1, new Margins(4)));
    
 // East information
    east.setBorders(true);
    east.setBodyBorder(true); 
    east.setHeight(400);
    east.setLayout(new AccordionLayout());
    east.setHeading("Additional Info"); 
    	ContentPanel stats = new ContentPanel();  
    	stats.setHeading("Statistical Summary");
    	stats.setBorders(false);  ;
    	stats.setCollapsible(true);
    		formData = new FormData("-20");
    		vp = new VerticalPanel();
    		vp.setSpacing(15); 
    		createForm4();
    	stats.add(vp);
	east.add(stats);
	
		stats = new ContentPanel();  
		stats.setHeading("Event Information");
		stats.setBorders(false);  
		stats.setCollapsible(true);
		stats.setBodyStyle("fontSize: 12px; padding: 10px"); 
		stats.addText("More event information here");
			Button button = new Button("Reset");
			button.setSize("60px","20px");
		stats.add(button);
		stats.collapse();
	east.add(stats);
	
// West  information
   
    west.setBorders(true);
    west.setBodyBorder(true); 
    west.setLayout(new FillLayout());
    west.setButtonAlign(HorizontalAlignment.CENTER);
    west.setHeading("Information Tools");
    
    	ContentPanel nav = new ContentPanel();
    	nav.setHeading("Information Type");  
    	nav.setBorders(false);  
    	nav.setCollapsible(true);
    		formData = new FormData("-20");
    		vp = new VerticalPanel();
    		vp.setSpacing(10); 
    		createForm1();
    	nav.add(vp);
    west.add(nav); 
    
    	ContentPanel nav1 = new ContentPanel();  
    	nav1.setHeading("Chart Options");  
    	nav1.setBorders(false);  
    	nav1.setCollapsible(true);
    		formData = new FormData("-20");
    		vp = new VerticalPanel();
    		vp.setSpacing(10); 
    		createForm2();
    	nav1.add(vp);
    	nav1.collapse();
	  
    west.add(nav1);  
    
  
    	ContentPanel settings = new ContentPanel();  
    	settings.setHeading("General Settings");  
    	settings.setBorders(false);
    	settings.setCollapsible(true);
    		formData = new FormData("-20");
    		vp = new VerticalPanel();
    		vp.setSpacing(10);
    		createForm3();
    	settings.add(vp);
    	settings.collapse();
    	
    west.add(settings);
    

// Center information
    center.setHeading("Charts");
    center.setScrollMode(Scroll.AUTOX);
    
    Portal portal = new Portal(2);  
    portal.setBorders(true);  
    portal.setStyleAttribute("backgroundColor", "white");  
    portal.setColumnWidth(0, .75);  
    portal.setColumnWidth(1, .25);
      
    	Resizable r;
    	Portlet portlet;
    	String url;
    	Chart chart;
    	
    	portlet = new Portlet();  
    	portlet.setHeading("Time window (24hr)") ;
    	portlet.setLayout(new FitLayout());
    	slider = new Slider();
    	slider.setTitle("Time window (24hr)");
    	slider.setIncrement(1);
    	slider.setMaxValue(24);
    	slider.setMinValue(0);
    	slider.setValue(12);
    	slider.setData("text", "Choose the time Window");
    	portlet.add(slider);
    	r = new Resizable(portlet);  
    	r.setDynamic(true);
    portal.add(portlet, 0); 
    	
    
    	portlet = new Portlet();  
    	portlet.setHeading("Boardings/Alightings");  
    	configPanel(portlet);
    	portlet.setHeight(450);
        r = new Resizable(portlet);  
        r.setDynamic(true);
    	url = "chart/open-flash-chart.swf";
    	chart = new Chart(url);  
    	chart.setBorders(true);  
    	chart.setHeight(400);
    	r = new Resizable(chart);  
        r.setDynamic(true);
    	chart.setChartModel(getVerticalAreaChartModel()); 
    	portlet.add(chart);
    portal.add(portlet, 0); 
    
    	portlet = new Portlet();  
    	portlet.setHeading("Information Grid 2");  
    	configPanel(portlet);  
    	portlet.setLayout(new FitLayout());  
    	//portlet.add(createGrid());  
    	portlet.setHeight(250);
    	r = new Resizable(portlet);  
    	r.setDynamic(true);
    portal.add(portlet, 1);
    
    	portlet = new Portlet();  
    	portlet.setHeading("Delay by stop");  
    	configPanel(portlet);    
    	url = "chart/open-flash-chart.swf";
    	chart = new Chart(url);  
    	chart.setBorders(true);  
    	chart.setChartModel(getPieChartData()); 
    	r = new Resizable(portlet);  
    	r.setDynamic(true);
    	portlet.add(chart);
    portal.add(portlet, 1); 
    
    
    
    
    
    center.add(portal);
    
    
    
     
     
// Layout data  
    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 80);  
    northData.setCollapsible(false);
    northData.setHideCollapseTool(true);     
    northData.setMargins(new Margins(0, 0, 5, 0)); 
  
    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 260);  
    westData.setCollapsible(true); 
    westData.setSplit(true);
    westData.setMargins(new Margins(0,5,0,0));  
  
    BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
    centerData.setMargins(new Margins(0));  
  
    BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST,220);  
    eastData.setSplit(true);  
    eastData.setCollapsible(true);  
    eastData.setMargins(new Margins(0,0,0,5));    
  
    add(north, northData);  
    add(west, westData);  
    add(center, centerData);  
    add(east, eastData); 
  }  
  
  private void createForm1() {  
	    FormPanel simple = new FormPanel(); 
	    simple.setFrame(false); 
	    simple.setHeaderVisible(false);
	    
	    Radio radio = new Radio();  
	    radio.setBoxLabel("Route");  
	    radio.setValue(true);  
	  
	    Radio radio2 = new Radio();  
	    radio2.setBoxLabel("Pattern");  
	  
	    RadioGroup radioGroup = new RadioGroup();  
	    radioGroup.setFieldLabel("Info");  
	    radioGroup.add(radio);  
	    radioGroup.add(radio2);  
	    simple.add(radioGroup, formData); 
	  
	    TextField<String> Route = new TextField<String>();  
	    Route.setFieldLabel("Route");  
	    Route.setAllowBlank(false);
	    Route.setEmptyText("Route number");
	    simple.add(Route, formData);  
	  
	    TextField<String> Pattern = new TextField<String>();  
	    Pattern.setFieldLabel("Pattern");  
	    Pattern.setAllowBlank(false);  
	    Pattern.setEmptyText("Pattern ID");
	    simple.add(Pattern, formData);  
	    
	    DateField date = new DateField();  
	    date.setFieldLabel("Date");  
	    simple.add(date, formData);  
	  
	    TimeField time = new TimeField();  
	    time.setFieldLabel("Time");  
	    simple.add(time, formData);  
	  
	    Button b = new Button("Submit");  
	    simple.add(b);  
	    simple.add(new Button("Cancel"));  
	  
	    simple.setButtonAlign(HorizontalAlignment.CENTER);
	  
	    vp.add(simple);  
	  }  
  
  private void createForm2() {
	  	FormPanel simple = new FormPanel(); 
	  	simple.setFrame(false); 
	    simple.setHeaderVisible(false);
	    simple.setHideLabels(true);
	    
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
	
		Button b = new Button("Submit");  
		simple.add(b);  
		simple.add(new Button("Cancel"));
		
		vp.add(simple);
  }
  
  private void createForm3() {
	  	final FormPanel simple = new FormPanel(); 
	  	simple.setFrame(false); 
	    simple.setHeaderVisible(false);
	    simple.setHideLabels(true);
	    
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
        
        Button btn = new Button("Reset");  
//        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//          @Override  
//          public void componentSelected(ButtonEvent ce) {  
//        	 simple.reset();  
//          }  
//        });  
        simple.add(btn);  
      
        btn = new Button("Submit");  
//        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//          @Override  
//          public void componentSelected(ButtonEvent ce) {  
//            if (!simple.isValid()) {  
//              return;  
//            }  
//            // normally would submit the form but for example no server set up to  
//            // handle the post  
//            // panel.submit();  
//            MessageBox.info("Action", "You file was uploaded", null);  
//          }  
//        });  
        simple.add(btn);
		
		vp.add(simple);
  }
 
  private void createForm4() {
	  	final FormPanel simple = new FormPanel(); 
	  	simple.setLayout(new FillLayout());
	  	simple.setFrame(false); 
	    simple.setHeaderVisible(false);
	    
	    simple.addText("Mean: 40");
	    simple.addText("Median: 30");
	    simple.addText("Max: 30");
	    simple.addText("Min: 30");
	    simple.addText("75%: 30");
	    simple.addText(" ");
		Button button = new Button("Reset");
		button.setSize("60px","20px");
		simple.add(button);
		vp.add(simple);
  }
  
  private void configPanel(final ContentPanel panel) {  
    panel.setCollapsible(true);  
    panel.setAnimCollapse(false);  
//    panel.getHeader().addTool(new ToolButton("x-tool-gear"));  
    panel.getHeader().addTool(  
        new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() {  
  
          @Override  
          public void componentSelected(IconButtonEvent ce) {  
            panel.removeFromParent();  
          }  
  
        }));  
  }  
  
  public ChartModel getVerticalAreaChartModel()   
  {   
    //Create a ChartModel with the Chart Title and some style attributes  
    ChartModel cm = new ChartModel("Boardings per stop", "font-size: 14px; font-family:      Verdana; text-align: center;");  
     
    XAxis xa = new XAxis();  
    //set the maximum, minimum and the step value for the X axis 
    for(int i =0; i<48;i++) {
    	xa.addLabels(Integer.toString(i));
    }
    xa.setOffset(true);  
    cm.setXAxis(xa);  
      
    YAxis ya = new YAxis();  
    //Add the labels to the Y axis    
    ya.setRange(0, 220, 50);    
    cm.setYAxis(ya);  
    
    //create a Area Chart object and add points to the object    
    AreaChart achart = new AreaChart();
    achart.setFillAlpha(0.3f);  
    achart.setColour("#00aa00");  
    achart.setFillColour("#00aa00");
    achart.setTooltip("#val#Boardings");   
    for (int n = 0; n < 48; n++) {   
    	achart.addValues(Math.abs(Math.cos(Random.nextDouble())*200*Math.cos(n/30)));  
    } 

    AreaChart area1 = new AreaChart();  
    area1.setFillAlpha(0.3f);  
    area1.setColour("#ff0000");  
    area1.setFillColour("#ff0000");  
    for (int n = 0; n < 48; n++) { 
        area1.addValues(Math.abs(Math.cos(Random.nextDouble())*150*Math.cos(n/30)));   
    }  
    
    //add the bchart as the Chart Config of the ChartModel  
    cm.addChartConfig(achart);
    cm.addChartConfig(area1); 
    
    return cm;    
  } 

  private ChartListener listener = new ChartListener() {  
      
	    public void chartClick(ChartEvent ce) {  
	      Info.display("Chart Clicked", "You selected {0}.", "" + ce.getValue());  
	    }  
	  };
  private ChartModel getPieChartData() {  
	    ChartModel cm = new ChartModel( "", 
	        "font-size: 10px; font-family: Verdana; text-align: center;");  
	    cm.setBackgroundColour("#fffff5");  
//	    Legend lg = new Legend(Position.RIGHT, true);  
//	    lg.setPadding(10);  
//	    cm.setLegend(lg);  
	      
	    PieChart pie = new PieChart();  
	    pie.setAlpha(0.5f);  
	    pie.setNoLabels(true);  
	    pie.setTooltip("#label# $#val#<br>#percent#");  
	    pie.setColours("#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff");  
	    pie.addSlices(new PieChart.Slice(100, "Mich/Lad", ""));  
	    pie.addSlices(new PieChart.Slice(200, "Mich/Ost", ""));  
	    pie.addSlices(new PieChart.Slice(150, "Mich/Hyd", ""));  
	    pie.addSlices(new PieChart.Slice(120, "Mich/Ren", ""));  
	    pie.addSlices(new PieChart.Slice(60, "Mich/Tor", ""));
	    pie.addChartListener(listener);  
	  
	    cm.addChartConfig(pie);  
	    return cm;  
	  }
  public void onResize()
  {
	  System.out.println("RESIZE");
  }
}
