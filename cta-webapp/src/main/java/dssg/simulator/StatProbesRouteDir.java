package dssg.simulator;

import java.util.LinkedHashMap;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import dssg.shared.Config;

public class StatProbesRouteDir {
  private final int NUM_BUCKETS = Config.NUM_BUCKETS;

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
    this.q3FlowByTimeByStop = new double[NUM_BUCKETS][numStops];
    this.maxFlowByTime = new double[NUM_BUCKETS];
  }
  
  public void add(LogStopEvent event) {
    this.finalized = false;
    String tageoid = event.getTageoid();
    Integer currentStop = this.tageoidToStopNum.get(tageoid);
    // ignore if stop not in canonical pattern
    if(currentStop == null) return;

    int runId = event.getRunId();
    int departTime = event.getTime_actual_depart();
    int load = event.getPassengers_in();
    int timeBucket = Config.getBucket(departTime);

    // Count the number of stops back in the canonical pattern back until
    // the last one that was stopped at by this trip
    // This allows flow from express routes to be accounted for over the 
    // intermediate stops
    LogStopEvent pEvent = event;
    Integer lastCanonStop =  null;
    while(true) {
      pEvent = pEvent.getLastEvent();
      if(pEvent == null) break;
      lastCanonStop = this.tageoidToStopNum.get(pEvent.getTageoid());
      if(lastCanonStop != null) break;
    }
    if(lastCanonStop == null) lastCanonStop = currentStop - 1;
    
    for(int stopNum = lastCanonStop + 1; stopNum <= currentStop; stopNum++) {
      DoubleArrayList flows = this.flowByTimeByStop[timeBucket][stopNum];
      flows.set(runId, flows.get(runId) + load);
      this.loadByTimeByStop[timeBucket][stopNum].add(load);
    }
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
    return this.q3LoadByTimeByStop;
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
