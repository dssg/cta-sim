package dssg.client;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("simulate")
public interface SimulationService extends RemoteService {
  String submitSimulation(String route, Date startTime,
    Date endTime) throws IllegalArgumentException, FileNotFoundException;
  
  public List<Number> getResults(String simId);
}
