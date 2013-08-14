package dssg.client;

import com.extjs.gxt.ui.client.data.BaseModel;

public class DataStats extends BaseModel {
	
	public DataStats() {
		
	}
	
	// Temporary usage for stats grid in GwtPortalContainer
	public DataStats(double hour, Integer load_at_beg, Integer load_at_mid, Integer load_at_mid2, Integer load_at_fin) {
		set("hour",hour);
		set("load_at_beg",load_at_beg);
		set("load_at_mid",load_at_mid);
		set("load_at_mid2",load_at_mid2);
		set("load_at_fin",load_at_fin);
	}
	 
	public double getHour() {
	  Double open = (Double) get("hour");
      return open.doubleValue();
	}
	
	public Integer getLoadAtBeg() {
	  Integer open = (Integer) get("load_at_beg");
	    return open.intValue();
	}
	public Integer getLoadAtMid() {
      Integer open = (Integer) get("load_at_mid");
        return open.intValue();
    }
	public Integer getLoadAtMid2() {
      Integer open = (Integer) get("load_at_mid2");
        return open.intValue();
    }
	public Integer getLoadAtFin() {
      Integer open = (Integer) get("load_at_fin");
        return open.intValue();
 
	}
	
}