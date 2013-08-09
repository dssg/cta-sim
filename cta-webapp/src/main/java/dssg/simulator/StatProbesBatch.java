package dssg.simulator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import dssg.shared.ProjectConstants;

public class StatProbesBatch extends Thread {
  private final int NUM_BUCKETS = ProjectConstants.NUM_BUCKETS;

  public final LogStopEvent POISON = new LogStopEvent();
  private BlockingQueue<LogStopEvent> eventQueue;

  private final int numRuns;
  private final HashMap<String,LinkedHashMap<String,Integer>> routeAndDirToStopIdToNum;
  private final HashMap<String,StatProbesRouteDir> routeAndDirToProbes;
  
  private boolean finalized;

  public StatProbesBatch(int numRuns, HashMap<String,LinkedHashMap<String,Integer>> routeAndDirToStopIdToNum) {
    // TODO: Handle different patterns correctly
    this.numRuns = numRuns;
    this.routeAndDirToStopIdToNum = routeAndDirToStopIdToNum;
    this.routeAndDirToProbes = new HashMap<String,StatProbesRouteDir>();

    for(String routeAndDir : routeAndDirToStopIdToNum.keySet()) {
      StatProbesRouteDir stopIdToNum = new StatProbesRouteDir(numRuns, routeAndDirToStopIdToNum.get(routeAndDir));
      this.routeAndDirToProbes.put(routeAndDir, stopIdToNum);
    }
  }
  
  public void add(LogStopEvent event) {
    String routeAndDir = event.getTaroute() + "," + event.getDir_group();
    this.finalized = false;
    StatProbesRouteDir probes = this.routeAndDirToProbes.get(routeAndDir);
    probes.add(event);
  }

  public void postCompute() {
    for(String routeAndDir : this.routeAndDirToProbes.keySet()) {
      StatProbesRouteDir probes = this.routeAndDirToProbes.get(routeAndDir);
      probes.postCompute();
    }
    this.finalized = true;
  }

  public double[][] getQ3LoadByTimeByStop(String routeAndDir) {
    if(!this.finalized) return null;
    StatProbesRouteDir probes = this.routeAndDirToProbes.get(routeAndDir);
    return probes.getQ3LoadByTimeByStop();
  }

  public double[][] getQ3FlowByTimeByStop(String routeAndDir) {
    if(!this.finalized) return null;
    StatProbesRouteDir probes = this.routeAndDirToProbes.get(routeAndDir);
    return probes.getQ3FlowByTimeByStop();
  }

  public double[] getMaxLoadByTime(String routeAndDir) {
    if(!this.finalized) return null;
    StatProbesRouteDir probes = this.routeAndDirToProbes.get(routeAndDir);
    return probes.getMaxLoadByTime();
  }

  public double[] getMaxFlowByTime(String routeAndDir) {
    if(!this.finalized) return null;
    StatProbesRouteDir probes = this.routeAndDirToProbes.get(routeAndDir);
    return probes.getMaxFlowByTime();
  }

  public void run() {
    try {
      while(!Thread.currentThread().isInterrupted()) {
        LogStopEvent event = this.eventQueue.take();
        if(event == this.POISON) {
          this.postCompute();
          break;
        }
        this.add(event);
      }
    } catch (InterruptedException e) {
      //  exit thread
    }
  }

  public void queue(LogStopEvent record) {
    this.eventQueue.add(record);
  }

  public void finish() {
    this.queue(this.POISON);
  }
  
  public void cancel() {
    this.interrupt();
  }
}
