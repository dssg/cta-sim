package dssg.client;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MyStats extends BaseModel {
	
	public MyStats() {
		
	}
	
	public MyStats(String stop, Double min, Double mean, Double th, Double max) {
		set("stop",stop);
		set("min",min);
		set("mean",mean);
		set("th",th);
		set("max",max);
	}
	
	public String getName() {
	    return (String) get("stop");
	}
	
	public double getMin() {
	    Double open = (Double) get("min");
	    return open.doubleValue();
	}
	
	public double getMean() {
	    Double open = (Double) get("mean");
	    return open.doubleValue();
	}
	
	public double getTh() {
	    Double open = (Double) get("th");
	    return open.doubleValue();
	}
	
	public double getMax() {
	    Double open = (Double) get("max");
	    return open.doubleValue();
	}
	
	// Usage for hour values for charts
	public MyStats(Double hour, Double value) {
		set("hour",hour);
		set("value",value);
	}
	
	public double getHour() {
		Double open = (Double) get("hour");
		return open.doubleValue();
	}
	
	public double getValue() {
		Double open = (Double) get("value");
		return open.doubleValue();
	}
}
