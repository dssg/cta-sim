package dssg.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("s3Com")
public interface S3CommunicationService extends RemoteService{
		
	public int uploadFile(String filename);

}
