package dssg.client;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MyParameters extends BaseModel{
	public MyParameters() {
		
	}
	
	// Usage for hour values for charts
	public MyParameters(Integer id, Double parameter) {
		set("id", id);
		set("parameter", parameter);
	}
	
	public int getId() {
		Integer open = (Integer) get("id");
	    return open.intValue();
	}
	
	public double getParameter() {
		Double open = (Double) get("parameter");
	    return open.longValue();
	}
}
