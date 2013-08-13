package dssg.shared;

import java.io.File;

/**
 * This class contains constants for the project like timezone and default
 * model parameters such as time bucket size
 * @author jtbates
 *
 */
public class ProjectConstants {
  public static final File PROJECT_PATH = new File(System.getProperty("user.dir"));
  private static final String REL_RES_PATH = "src/main/resources/";
  public static final File RESOURCES_PATH = new File(PROJECT_PATH, REL_RES_PATH);

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
}
