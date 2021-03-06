package dssg.server;

import java.io.File;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import dssg.client.DataStats;
import dssg.client.S3CommunicationService;
import dssg.shared.S3Communication;

public class S3CommunicationServiceImpl extends RemoteServiceServlet implements
    S3CommunicationService {
  private S3Communication s3c;
  protected File configPath;

  S3CommunicationServiceImpl() {
    this.s3c = new S3Communication();
  }

  /*
   * --- UPLOAD File to S3 Bucket ---
   */
  @Override
  public List<DataStats> uploadFile(String filename) {
    return this.s3c.uploadFile(filename);
  }

  /*
   * --- Download Parameters from S3 Bucket --- Downloads the parameters for the
   * simulation from a given bucket specified in a Yaml file
   */
  @Override
  public List<String> downloadParameters() {
    return this.s3c.downloadParameters();
  }
}