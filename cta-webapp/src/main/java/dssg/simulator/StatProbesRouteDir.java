package dssg.simulator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import dssg.shared.ProjectConstants;

public class StatProbesRouteDir {
  private final int NUM_BUCKETS = ProjectConstants.NUM_BUCKETS;

  private final int numRuns;
  private final int numStops;
  private final LinkedHashMap<String,Integer> tageoidToStopNum;

  private DoubleArrayList loadByTimeByStop[][];
  private DoubleArrayList flowByTimeByStop[][];
  
  // 75th percentiles and their max over stops
  private double q3LoadByTimeByStop[][];
  private double maxLoadByTime[];
  private double q3FlowByTimeByStop[][];
  private double maxFlowByTime[];
  
  private boolean finalized;

  public StatProbesRouteDir(int numRuns, LinkedHashMap<String,Integer> tageoidToStopNum) {
    // TODO: Handle different patterns correctly
    this.numRuns = numRuns;
    this.numStops = tageoidToStopNum.size();
    this.tageoidToStopNum = tageoidToStopNum;
      
    this.loadByTimeByStop = new DoubleArrayList[NUM_BUCKETS][numStops];
    this.flowByTimeByStop= new DoubleArrayList[NUM_BUCKETS][numStops];
    for(int i=0; i < NUM_BUCKETS; i++)
      for(int j=0; j < this.numStops; j++) {
        this.loadByTimeByStop[i][j] = new DoubleArrayList();
        this.flowByTimeByStop[i][j] = new DoubleArrayList(new double[numRuns]);
      }
    
    this.q3LoadByTimeByStop = new double[NUM_BUCKETS][numStops];
    this.maxLoadByTime = new double[NUM_BUCKETS];
  }
  
  public void add(LogStopEvent event) {
    this.finalized = false;
    int runId = event.getRunId();
    int departTime = event.getTime_actual_depart();
    int stopNum = this.tageoidToStopNum.get(event.getTageoid());
    int load = event.getPassengers_in();

    int timeBucket = ProjectConstants.getBucket(departTime);

    DoubleArrayList flows = this.flowByTimeByStop[timeBucket][stopNum];
    flows.set(runId, flows.get(runId) + load);
    this.loadByTimeByStop[timeBucket][stopNum].add(load);
  }

  public void postCompute() {
    for(int i=0; i < NUM_BUCKETS; i++)
      for(int j=0; j < this.numStops; j++) {
        DoubleArrayList loads = this.loadByTimeByStop[i][j];
        loads.sort();
        double q3Load = Descriptive.quantile(loads, 0.75);
        this.q3LoadByTimeByStop[i][j] = q3Load;
        if(q3Load > maxLoadByTime[i])
          maxLoadByTime[i] = q3Load;
        DoubleArrayList flows = this.flowByTimeByStop[i][j];
        flows.sort();
        double q3Flow = Descriptive.quantile(flows, 0.75);
        this.q3FlowByTimeByStop[i][j] = q3Flow;
        if(q3Flow > maxFlowByTime[i])
          maxFlowByTime[i] = q3Flow;
      }
    this.finalized = true;
  }

  public double[][] getQ3LoadByTimeByStop() {
    if(!this.finalized) return null;
    return this.q3FlowByTimeByStop;
  }

  public double[][] getQ3FlowByTimeByStop() {
    if(!this.finalized) return null;
    return this.q3FlowByTimeByStop;
  }

  public double[] getMaxLoadByTime() {
    if(!this.finalized) return null;
    return this.maxLoadByTime;
  }

  public double[] getMaxFlowByTime() {
    if(!this.finalized) return null;
    return this.maxFlowByTime;
  }
}
