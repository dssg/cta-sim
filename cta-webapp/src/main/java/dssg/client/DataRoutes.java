package dssg.client;

import com.extjs.gxt.ui.client.data.BaseModel;

public class DataRoutes extends BaseModel{
	
	public DataRoutes(){
		
	}
	
	public DataRoutes(Integer id, String name) {
		setId(id);
		setName(name);
	}
	
	
	
	public Integer getId() {
		Integer open = (Integer) get("id");
	    return open.intValue();
	}

	public void setId(Integer id) {
		set("id", id);
	}
	
	public String getName() {
		return (String) get("name");
	}
	
	public void setName(String name) {
		set("name", name);
	}
	
	
}
