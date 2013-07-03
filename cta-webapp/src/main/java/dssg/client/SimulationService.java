package dssg.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("simulate")
public interface SimulationService extends RemoteService {
  String simulationServer(String route, Date date, long startTime,
    long endTime) throws IllegalArgumentException;
}
