package dssg.simulator;

/**
 * This class contains default constants for models, such as the bucket size
 * for time of day parameters.
 * @author jtbates
 *
 */
public class ModelConstants {
  private static final int SECONDS_IN_DAY = 60*60*24;

  // number of seconds for bucket, default is half hour bucket
  public static final int BUCKET_SIZE = 60*30;
  public static final int NUM_BUCKETS = SECONDS_IN_DAY / BUCKET_SIZE;
  static {
    assert SECONDS_IN_DAY % BUCKET_SIZE == 0 : "Bucket size does not evenly divide day";
  }
  
  public static int DAYTYPE_WEEKDAY = 0;
  public static int DAYTYPE_WEEKEND = 1;
  
}
