package dssg.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.jets3t.service.security.AWSCredentials;
import org.yaml.snakeyaml.Yaml;

/**
 * This class contains constants for the project like timezone and default
 * model parameters such as time bucket size
 * @author jtbates
 *
 */
public class ProjectConstants {
  public static final String AGENCY_TIMEZONE = "America/Chicago";
  public static final String AGENCY_NAME = "Chicago Transit Authority";

  private static final int SECONDS_IN_DAY = 60*60*24;

  // number of seconds for bucket, default is half hour bucket
  public static final int BUCKET_SIZE = 60*30;
  public static final int NUM_BUCKETS = SECONDS_IN_DAY / BUCKET_SIZE;
  static {
    assert SECONDS_IN_DAY % BUCKET_SIZE == 0 : "Bucket size does not evenly divide day";
  }

  public static int DAYTYPE_WEEKDAY = 0;
  public static int DAYTYPE_WEEKEND = 1;

  public static int getBucket(int seconds) {
    return (seconds / BUCKET_SIZE) % NUM_BUCKETS;
  }
  
  
  public static final File RESOURCES_PATH = new File("/var/lib/ctasim");
  public static final File CONFIG_YAML = new File(RESOURCES_PATH,"config.yaml");
  // TODO: move model fit paths to config file
  public static final File MODEL_FIT_PATH = new File(RESOURCES_PATH,"model-fit");
  public static final File MODEL_FIT_BOARD = new File(MODEL_FIT_PATH,"boardParams.json");
  public static final File MODEL_FIT_ALIGHT = new File(MODEL_FIT_PATH,"alightParams.json");
  public static final String awsAccessKey;
  public static final String awsSecretKey;
  public static final AWSCredentials awsCredentials;
  public static final String s3Bucket;
  // TODO: can we set the bundle path in application-context-webapp.xml from the config file?
  static {
    String readAwsAccessKey = null;
    String readAwsSecretKey = null;
    String readS3Bucket = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(CONFIG_YAML));

  		// Create YAML object
  		final Yaml configYaml = new Yaml();
  		Map<String, Object> config = (Map<String, Object>) configYaml.load(reader);
  		
  		// AWS Credential information
      readAwsAccessKey = (String) config.get("aws_access_key");
      readAwsSecretKey = (String) config.get("aws_secret_access_key");
  
  		// S3 bucket information
      readS3Bucket = (String) config.get("aws_s3bucket_name");
		} catch (FileNotFoundException e1) {
		  // TODO: handle the exception properly
			e1.printStackTrace();
			System.err.println("Cannot find config YAML file." + e1);
		} finally {
      awsAccessKey = readAwsAccessKey;
      awsSecretKey = readAwsSecretKey;
      awsCredentials = new AWSCredentials(readAwsAccessKey,
        readAwsSecretKey);
      s3Bucket = readS3Bucket;
		}
  }
}
