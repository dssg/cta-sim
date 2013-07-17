package dssg.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.yaml.snakeyaml.Yaml;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import dssg.client.MyStats;
import dssg.client.S3CommunicationService;

public class S3CommunicationServiceImpl extends RemoteServiceServlet implements
		S3CommunicationService {

	@Override
	public List<MyStats> uploadFile(String filename) {
		// Read Yaml file with information for S3
		Reader reader = null;
		try {
			reader = new FileReader("configFile.yaml");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.err.println("Cannot find YAML file." + e1);
		}
		final Yaml yaml = new Yaml();
		Map<String, Object> map = (Map<String, Object>) yaml.load(reader);

		// AWS Credential information
		String awsAccessKey = (String) map.get("aws_access_key");
		String awsSecretKey = (String) map.get("aws_secret_access_key");
		AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey,
				awsSecretKey);

		// S3 bucket information
		String s3Bucket = (String) map.get("aws_s3bucket_name");

		// Data type
		List<MyStats> stats = new ArrayList<MyStats>();

		// SERVICE
		S3Service s3Service;
		try {
			s3Service = new RestS3Service(awsCredentials);
			// Test service
			// S3Bucket[] myBuckets = s3Service.listAllBuckets();
			// System.out.println("How many buckets to I have in S3? " +
			// myBuckets.length);
			// Upload service
			// File fileData = new File(filename);
			// S3Object fileObject = new S3Object(fileData);
			// s3Service.putObject(s3Bucket, fileObject);
			// Download service
			S3Object object = s3Service.getObject(s3Bucket, "testText.txt");
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(
					object.getDataInputStream()));
			String data = null;

			
			while ((data = reader1.readLine()) != null) {
				System.out.println(data);
				String[] parts = data.split(",");
				for(int i = 0; i < parts.length; i++)
				stats.add(new MyStats((double)i, Double.parseDouble(parts[i])));
			}

			return stats;
		} catch (S3ServiceException e) {
			e.printStackTrace();
			// } catch (NoSuchAlgorithmException e2) {
			// e2.printStackTrace();
			// } catch (IOException e3) {
			// e3.printStackTrace();
		} catch (ServiceException e4) {
			e4.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stats;

	}
}