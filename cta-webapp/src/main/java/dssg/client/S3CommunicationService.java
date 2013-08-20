package dssg.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * UNUSED The client side stub for the RPC service. Used to communicate client
 * side with server side for uploads and downloads from Amazon S3 Services
 * 
 */
@RemoteServiceRelativePath("s3Com")
public interface S3CommunicationService extends RemoteService {

  public List<DataStats> uploadFile(String filename);

  public List<String> downloadParameters();

}
