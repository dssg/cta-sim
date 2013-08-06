package dssg.shared;

import java.io.File;

public class ProjectConstants {
  public static final File PROJECT_PATH = new File(System.getProperty("user.dir"));
  private static final String REL_RES_PATH = "src/main/resources";
  public static final File RESOURCES_PATH = new File(PROJECT_PATH,REL_RES_PATH);

  public static final String AGENCY_TIMEZONE = "America/Chicago";
  public static final String AGENCY_NAME = "Chicago Transit Authority";
}
