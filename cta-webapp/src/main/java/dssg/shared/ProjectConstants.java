package dssg.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
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
  
  
  public static final Map<String,Integer> BUS_PRACTICAL_MAX_CAPACITY;
  static {
    BUS_PRACTICAL_MAX_CAPACITY = new HashMap<String,Integer>();
    // TODO: verify that this is a sensible way to handle buses filling to
    //   capacity or implement a better method
    BUS_PRACTICAL_MAX_CAPACITY.put("30ft", 40);
    BUS_PRACTICAL_MAX_CAPACITY.put("40ft", 70);
    BUS_PRACTICAL_MAX_CAPACITY.put("60ft", 95);
  }


  public static final File RESOURCES_PATH = new File("/var/lib/ctasim");
  public static final File CONFIG_YAML = new File(RESOURCES_PATH,"config.yaml");
  // TODO: move model fit paths to config file
  public static final File MODEL_FIT_PATH = new File(RESOURCES_PATH,"model-fit");
  public static final File MODEL_FIT_BOARD = new File(MODEL_FIT_PATH,"boardParams.json");
  public static final File MODEL_FIT_ALIGHT = new File(MODEL_FIT_PATH,"alightParams.json");
  public static final String AWS_ACCESS_KEY;
  public static final String AWS_SECRET_ACCESS_KEY;
  public static final AWSCredentials AWS_CREDENTIALS;
  public static final String S3_BUCKET;
  public static final String DATABASE_USER;
  public static final String DATABASE_PASSWORD;
  public static final String DATABASE_URL;


  // TODO: can we set the bundle path in application-context-webapp.xml from the config file?
  static {
    String readAwsAccessKey = null;
    String readAwsSecretKey = null;
    String readS3Bucket = null;
    String readDatabaseUser = null;
    String readDatabasePassword = null;
    String readDatabaseUrl = null;
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

      // Database config
      Class.forName("org.postgresql.Driver");
      readDatabaseUser = (String) config.get("database_user");
      readDatabasePassword = (String) config.get("database_password");
      readDatabaseUrl = (String) config.get("database_jdbc_url");

		} catch (FileNotFoundException e) {
		  // TODO: handle the exception properly
			System.err.println("Cannot find config YAML file." + e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
		  // TODO: handle the exception properly
			System.err.println("Cannot find database driver." + e);
      e.printStackTrace();
    } finally {
      AWS_ACCESS_KEY = readAwsAccessKey;
      AWS_SECRET_ACCESS_KEY = readAwsSecretKey;
      AWS_CREDENTIALS = new AWSCredentials(readAwsAccessKey,
        readAwsSecretKey);
      S3_BUCKET = readS3Bucket;
      DATABASE_USER = readDatabaseUser;
      DATABASE_PASSWORD = readDatabasePassword;
      DATABASE_URL = readDatabaseUrl;
		}
  }

  public static Connection getDatabaseConnection() throws SQLException {
      return DriverManager.getConnection(DATABASE_URL,DATABASE_USER,DATABASE_PASSWORD);
  }
}
