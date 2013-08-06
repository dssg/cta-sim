package dssg.simulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import dssg.shared.ProjectConstants;

public class LogBatch extends Thread {
  public final LogStopEvent POISON = new LogStopEvent();

  private static final File LOG_PATH = new File(ProjectConstants.RESOURCES_PATH,"logs");
  private BlockingQueue<LogStopEvent> eventQueue;
  private final String logName;

  
  public LogBatch(String logName) {
    this.eventQueue = new LinkedBlockingQueue<LogStopEvent>();
    this.logName = logName;
  }

  public void logEvent(LogStopEvent record) {
    eventQueue.add(record);
  }
  
  public void run() {
    CsvBeanWriter writer = null;
    try {
      try {
        final String[] header = LogStopEvent.getHeader();
        File writeFile = new File(this.LOG_PATH, this.logName + ".csv");
        writer = new CsvBeanWriter(new FileWriter(writeFile), CsvPreference.EXCEL_PREFERENCE);
        writer.writeHeader(header);
        while(!Thread.currentThread().isInterrupted()) {
          LogStopEvent event = this.eventQueue.take();
          if(event == this.POISON) break;
          writer.write(event, header);
        }
      } catch (InterruptedException e) {
        //  exit thread
      } finally {
        if(writer != null)
          writer.close();
      }
    } catch (IOException e) {
      // logging failed
      e.printStackTrace();
    }

  }
  
  public void finish() {
    this.logEvent(this.POISON);
  }
  
  public void cancel() {
    this.interrupt();
  }

}