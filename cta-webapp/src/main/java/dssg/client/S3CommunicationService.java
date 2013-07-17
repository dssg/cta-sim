package dssg.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("s3Com")
public interface S3CommunicationService extends RemoteService{
		
	public List<MyData> uploadFile(String filename);
	public List<MyParameters> downloadParameters();

}
