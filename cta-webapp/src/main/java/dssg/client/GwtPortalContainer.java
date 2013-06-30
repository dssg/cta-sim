package dssg.client; 

  
import java.util.ArrayList;  
import java.util.Collections;
import java.util.List;  
  

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;  
import com.extjs.gxt.ui.client.Style.LayoutRegion;  
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.IconButtonEvent;  
import com.extjs.gxt.ui.client.event.SelectionListener;  
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.store.ListStore;  
import com.extjs.gxt.ui.client.util.Margins;  
import com.extjs.gxt.ui.client.widget.ContentPanel;  
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;  
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;  
import com.extjs.gxt.ui.client.widget.custom.Portal;  
import com.extjs.gxt.ui.client.widget.custom.Portlet;  
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;  
import com.extjs.gxt.ui.client.widget.grid.ColumnData;  
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;  
import com.extjs.gxt.ui.client.widget.grid.Grid;  
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;  
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;  
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;  
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;  
import com.extjs.gxt.ui.client.widget.layout.FitLayout;  
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.i18n.client.NumberFormat;  
import com.google.gwt.user.client.Element;  
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.widgets.layout.VerticalLayout;

import dssg.simulator.SimulationInstance;
import com.extjs.gxt.ui.client.widget.Slider;
  
public class GwtPortalContainer extends LayoutContainer {
	public GwtPortalContainer() {
	}  
  
  @Override  
  protected void onRender(Element parent, int index) {  
    super.onRender(parent, index);  
    final BorderLayout layout = new BorderLayout();  
    setLayout(layout); 
  
    LayoutContainer north = new LayoutContainer();
    ContentPanel west = new ContentPanel(); 
    ContentPanel center = new ContentPanel();
    ContentPanel east = new ContentPanel();  

// North  information
    north.setBorders(false);
    north.setStyleAttribute("background-color", "#000033");
    
    Image image = new Image();
    image.setUrl("http://www.transitchicago.com/images/global/hdr-cta.gif");
    north.add(image);
    
 // East information
    east.setHeading("Additional Info"); 
    east.addText("Some more information about the charts.");
    
// West  information
   
    west.setBorders(true);
    west.setBodyBorder(true);  
    west.setHeading("Information Tools");
    west.setLayout(new AccordionLayout()); 
    
  
    	ContentPanel nav = new ContentPanel();  
    	nav.setHeading("Route Selection");  
    	nav.setBorders(false);  
    	nav.setCollapsible(true);
    	nav.setBodyStyle("fontSize: 12px; padding: 10px"); 
    	
   		TextField<Integer> field = new TextField<Integer>();
   		field.setFieldLabel("Route:");
   		field.setEmptyText("Route number");
   		nav.add(field, new FormData("100%"));
    	nav.add(new Button("View"));
    	 
    		
    	nav.collapse();
    west.add(nav);
    
    	nav = new ContentPanel();  
    	nav.setHeading("Chart Options");  
    	nav.setBorders(false);  
    	nav.setCollapsible(true);
    	nav.setBodyStyle("fontSize: 12px; padding: 10px; ");
    	nav.addText("AM");
    	Slider slider = new Slider();
    	nav.add(slider);
    	slider.setValue(30);
    	
    	nav.collapse();
    west.add(nav);  
    
  
    	ContentPanel settings = new ContentPanel();  
    	settings.setHeading("General Settings");  
    	settings.setBorders(false);
    	settings.setCollapsible(true);
    	settings.setBodyStyle("fontSize: 12px; padding: 10px"); 
    	settings.addText("Setting changes");
    	settings.collapse();
    west.add(settings);
    

// Center information
    center.setHeading("Charts");
    center.setScrollMode(Scroll.AUTOX);
    
    Portal portal = new Portal(2);  
    portal.setBorders(true);  
    portal.setStyleAttribute("backgroundColor", "white");  
    portal.setColumnWidth(0, .6);  
    portal.setColumnWidth(1, .4);
      
    
    Portlet portlet = new Portlet();  
    portlet.setHeading("Chart 1");  
    configPanel(portlet);  
    portlet.setLayout(new FitLayout());
    image = new Image();
    image.setUrl("http://static6.businessinsider.com/image/4e25f4d049e2aee37d070000/chart-of-the-day-apple-revenue-by-product-july-2011.jpg");
    portlet.add(image);
    portlet.setHeight(450);
    Resizable r = new Resizable(portlet);  
    r.setDynamic(true);
    portal.add(portlet, 0);
    
    portlet = new Portlet();  
    portlet.setHeading("Chart 2");  
    configPanel(portlet);  
    portlet.setLayout(new FitLayout());
    image = new Image();
    image.setUrl("http://1.bp.blogspot.com/-9BR4_CUROuk/UFwqimtBoUI/AAAAAAAABK4/6-APzpEtwyA/s1600/prettygraphs2.png");
    portlet.add(image);  
    portlet.setHeight(300);
    r = new Resizable(portlet);  
    r.setDynamic(true);
    portal.add(portlet, 1);
    
    portlet = new Portlet();  
    portlet.setHeading("Time window (24hrs)");  
    configPanel(portlet);
    slider = new Slider();
    slider.setIncrement(1);
    slider.setMaxValue(24);
    slider.setMinValue(0);
    slider.setTitle("Time Window");
    slider.setValue(12);
    portlet.add(slider);
    r = new Resizable(portlet);  
    r.setDynamic(true);
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
    
    center.add(portal);
    
    /*
    portlet = new Portlet();  
    portlet.setHeading("Chart 1");  
    configPanel(portlet);    
    portal.add(portlet, 0);  
    String url = "chart/open-flash-chart.swf";
    final Chart chart = new Chart(url);  
    chart.setBorders(true);  
    chart.setChartModel(getPieChartData()); 
    portlet.add(chart);
     */
     
// Layout data  
    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 80);  
    northData.setCollapsible(false);
    northData.setHideCollapseTool(true);     
    northData.setMargins(new Margins(0, 0, 5, 0));  
  
    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 250);  
    westData.setCollapsible(true); 
    westData.setSplit(true);
    westData.setMargins(new Margins(0,5,0,0));  
  
    BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
    centerData.setMargins(new Margins(0));  
  
    BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST,250);  
    eastData.setSplit(true);  
    eastData.setCollapsible(true);  
    eastData.setMargins(new Margins(0,0,0,5));    
  
    add(north, northData);  
    add(west, westData);  
    add(center, centerData);  
    add(east, eastData); 
  }  
  
  private Grid<SimulationInstanceData> createGrid() {  
    final NumberFormat currency = NumberFormat.getCurrencyFormat();  
  
    GridCellRenderer<SimulationInstanceData> gridNumber = new GridCellRenderer<SimulationInstanceData>() {  
      public String render(SimulationInstanceData model, String property, ColumnData config, int rowIndex,  
          int colIndex, ListStore<SimulationInstanceData> store, Grid<SimulationInstanceData> grid) {  
        Number value = model.<Number>get(property);  
        return value == null ? null : currency.format(model.<Number>get(property));  
      }  
    };  
  
    List<SimulationInstanceData> stocks = Collections.emptyList();  
  
    List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
  
    ColumnConfig column = new ColumnConfig();  
    column.setId("name");  
    column.setHeader("Company");  
    column.setWidth(200);  
    configs.add(column);  
  
    column = new ColumnConfig();  
    column.setId("symbol");  
    column.setHeader("Symbol");  
    column.setWidth(50);  
    configs.add(column);  
  
    column = new ColumnConfig();  
    column.setId("last");  
    column.setHeader("Last");  
    column.setAlignment(HorizontalAlignment.RIGHT);  
    column.setWidth(50);  
    column.setRenderer(gridNumber);  
    configs.add(column);  
  
    ListStore<SimulationInstanceData> store = new ListStore<SimulationInstanceData>();  
    store.add(stocks);  
  
    ColumnModel cm = new ColumnModel(configs);  
  
    Grid<SimulationInstanceData> g = new Grid<SimulationInstanceData>(store, cm);  
    g.setAutoExpandColumn("name");  
    g.setBorders(true);  
    return g;  
  }  
  
  private String getBogusText() {  
    return "<div class=text style='padding: 5px'> Random Text________</div>";  
  }  
  
  private void configPanel(final ContentPanel panel) {  
    panel.setCollapsible(true);  
    panel.setAnimCollapse(false);  
    panel.getHeader().addTool(new ToolButton("x-tool-gear"));  
    panel.getHeader().addTool(  
        new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() {  
  
          @Override  
          public void componentSelected(IconButtonEvent ce) {  
            panel.removeFromParent();  
          }  
  
        }));  
  }  
  
  private ChartModel getPieChartData() {  
	    ChartModel cm = new ChartModel("Sales by Region","font-size: 14px; font-family: Verdana; text-align: center;");  
	    cm.setBackgroundColour("#fffff5");  
	    Legend lg = new Legend(Position.RIGHT, true);  
	    lg.setPadding(10);  
	    cm.setLegend(lg);  
	      
	    PieChart pie = new PieChart();  
	    pie.setAlpha(0.5f);  
	    pie.setNoLabels(true);  
	    pie.setTooltip("#label# $#val#M<br>#percent#");  
	    pie.setColours("#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff");  
	    pie.addSlices(new PieChart.Slice(100, "AU","Australia"));  
	    pie.addSlices(new PieChart.Slice(200, "US", "USA"));  
	    pie.addSlices(new PieChart.Slice(150, "JP", "Japan"));  
	    pie.addSlices(new PieChart.Slice(120, "DE", "Germany"));  
	    pie.addSlices(new PieChart.Slice(60, "UK", "United Kingdom"));  
	     
	  
	    cm.addChartConfig(pie);  
	    return cm;  
	  }  
  
}  
