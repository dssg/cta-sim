package dssg.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.yaml.snakeyaml.Yaml;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import dssg.client.MyData;
import dssg.client.MyParameters;
import dssg.client.S3CommunicationService;

public class S3CommunicationServiceImpl extends RemoteServiceServlet implements
		S3CommunicationService {

	/*
	 * --- UPLOAD File to S3 Bucket ---
	 */
	@Override
	public List<MyData> uploadFile(String filename) {
		// Read Yaml file with information for S3
		BufferedReader reader = null;
		try {
			String inputPath = this.getServletContext().getRealPath(
					"/WEB-INF/classes/configFile.yaml");
			reader = new BufferedReader(new FileReader(inputPath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.err.println("Cannot find YAML file." + e1);
		}
		// Create YAML object
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
		List<MyData> stats = new ArrayList<MyData>();

		// UPLOAD FILE SERVICE
		S3Service s3Service;
		try {
			// FIXME get it to work
			// s3Service = new RestS3Service(awsCredentials);
			// File fileData = new File(filename);
			// S3Object fileObject = new S3Object(fileData);
			// s3Service.putObject(s3Bucket, fileObject);

			// Test example
			s3Service = new RestS3Service(awsCredentials);
			S3Bucket[] myBuckets = s3Service.listAllBuckets();
			System.out.println("How many buckets to I have in S3? "
					+ myBuckets.length);

			return stats;
		} catch (S3ServiceException serviceExcpetion) {
			serviceExcpetion.printStackTrace();
		}

		return stats;

	}

	/*
	 * --- Download Parameters from S3 Bucket --- Downloads the parameters for
	 * the simulation from a given bucket specified in a Yaml file
	 */
	@Override
	public List<MyParameters> downloadParameters() {
		// Read Yaml file with information for S3
		BufferedReader reader2 = null;
		try {
			String inputPath = this.getServletContext().getRealPath(
					"/WEB-INF/classes/configFile.yaml");
			reader2 = new BufferedReader(new FileReader(inputPath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.err.println("Cannot find YAML file." + e1);
		}
		// Create YAML object
		final Yaml yaml = new Yaml();
		Map<String, Object> map = (Map<String, Object>) yaml.load(reader2);

		// AWS Credential information
		String awsAccessKey = (String) map.get("aws_access_key");
		String awsSecretKey = (String) map.get("aws_secret_access_key");
		AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey,
				awsSecretKey);

		// S3 bucket information
		String s3Bucket = (String) map.get("aws_s3bucket_name");

		// Name of the file with the parameters
		String file = (String) map.get("parameterFile");

		// Data type
		List<MyParameters> parameters = new ArrayList<MyParameters>();

		// -- DOWNLOAD SERVICE --
		S3Service s3Service;
		try {
			s3Service = new RestS3Service(awsCredentials);
			// FIXME change testText.txt to actual value for the parameter file
			S3Object object = s3Service.getObject(s3Bucket, file);
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(
					object.getDataInputStream()));
			String data = null;

			// Read the file and store the values
			while ((data = reader1.readLine()) != null) {
				System.out.println(data);
				//FIXME everything works up to this point, make the rest work.
				String[] parts = data.split(",");
				for (int i = 0; i < parts.length; i++) {
					parameters.add(new MyParameters(i, Double
							.parseDouble(parts[i])));
				}
			}
			return parameters;

		} catch (S3ServiceException e2) {
			e2.printStackTrace();
		} catch (ServiceException e3) {
			e3.printStackTrace();
		} catch (NumberFormatException e4) {
			e4.printStackTrace();
		} catch (IOException e5) {
			e5.printStackTrace();
		}

		List<MyParameters> nothing = new ArrayList<MyParameters>();
		return nothing;

	}
}