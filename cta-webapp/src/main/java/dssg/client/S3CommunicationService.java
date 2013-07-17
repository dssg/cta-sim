package dssg.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("s3Com")
public interface S3CommunicationService extends RemoteService{
		
	public List<MyStats> uploadFile(String filename);
	public List<MyStats> downloadParameters();

}
