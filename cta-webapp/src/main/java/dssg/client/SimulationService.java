package dssg.client;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("simulate")
public interface SimulationService extends RemoteService {
  String submitSimulation(Set<String> routeAndDirs, Date startTime,
    Date endTime) throws IllegalArgumentException;
  
  //Starts simulation and returns the results when done
  public Map<String, Integer[]> getResults(String route, String direction, Date date, Integer startTime, Integer endTime);

}
 