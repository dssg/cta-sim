package dssg.client;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MyRoutes extends BaseModel{
	
	public MyRoutes(){
		
	}
	
	public MyRoutes(Integer id, String name) {
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
