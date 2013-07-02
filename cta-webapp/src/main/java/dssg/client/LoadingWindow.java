package dssg.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;  
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;  
import com.extjs.gxt.ui.client.widget.LayoutContainer;   
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;  
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;  
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout; 
import com.google.gwt.user.client.Element;  
import com.google.gwt.user.client.ui.Image;

public class LoadingWindow extends LayoutContainer {

	public LoadingWindow()
	{
	}
	
	protected void onRender(Element parent, int index) {  
	    super.onRender(parent, index);  
	    final BorderLayout layout = new BorderLayout();  
	    setLayout(layout);
	  
	    LayoutContainer north = new LayoutContainer();

	// North  information
	    north.setBorders(false);
	    north.setLayout(new RowLayout(Orientation.HORIZONTAL));
	    north.setStyleAttribute("background-color", "#660033");
	    
	    
	    Image image = new Image();
	    /*image.setUrl("http://www.colorhexa.com/000033.png");
	    north.add(image, new RowData(.03, 1, new Margins(4)));
	    image = new Image();*/
	    image.setUrl("http://www.transitchicago.com/images/global/hdr-cta.gif");
	    north.add(image, new RowData(.32, 1, new Margins(4)));
	    /*image = new Image();
	    image.setUrl("http://www.colorhexa.com/000033.png");
	    north.add(image, new RowData(.55, 1, new Margins(4)));
	    image = new Image();
	    image.setUrl("http://dssg.io/img/logo.png");
	    north.add(image, new RowData(.07, 1, new Margins(4)));
	    image = new Image();
	    image.setUrl("http://www.colorhexa.com/000033.png");
	    north.add(image, new RowData(.03, 1, new Margins(4)));*/
	         
	// Layout data  
	    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 80);  
	    northData.setCollapsible(false);
	    northData.setHideCollapseTool(true);     
	    northData.setMargins(new Margins(0, 0, 5, 0)); 
	  
	    add(north, northData);  
	  }
	
}
