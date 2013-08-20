package dssg.client;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * Class that defines an object for storing info on a specific route 
 * IN: tageoid (unique route identifier) and name of the route
 * 
 * Used in GetData class
 */
public class DataRoutes extends BaseModel {
  // Initializers
  public DataRoutes() {

  }

  public DataRoutes(Integer id, String name) {
    setId(id);
    setName(name);
  }

  // General Methods
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
