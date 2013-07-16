package dssg.server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Map;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.security.AWSCredentials;
import org.yaml.snakeyaml.Yaml;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import dssg.client.S3CommunicationService;

public class S3CommunicationServiceImpl extends RemoteServiceServlet implements S3CommunicationService{
	
	public int uploadFile(String filename) {
        Reader reader = null;
        try {
			reader = new FileReader("configFile.yaml");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.err.println("Cannot find YAML file." + e1);
		}
        final Yaml yaml = new Yaml();
        Map<String,Object> map = (Map<String,Object>)yaml.load(reader);
        
        String awsAccessKey = (String) map.get("aws_access_key");
        String awsSecretKey = (String) map.get("aws_secret_access_key");
        AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey, awsSecretKey);
        
        S3Service s3Service;
		try {
			s3Service = new RestS3Service(awsCredentials);
			S3Bucket[] myBuckets = s3Service.listAllBuckets();
	        System.out.println("How many buckets to I have in S3? " + myBuckets.length);
	        return myBuckets.length;
		} catch (S3ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return -1;
        
	}
}