package dssg.simulator;

import java.io.Reader;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;

import umontreal.iro.lecuyer.probdist.NegativeBinomialDist;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

public class PassengerOnModelNegBinom implements PassengerOnModel {
  private static final int BUCKET_SIZE = ModelConstants.BUCKET_SIZE;
  private static final int NUM_BUCKETS = ModelConstants.NUM_BUCKETS;

  private transient final RandomStream rand;
  
  class ModelParams {
    double[] lambdaTimeOfDay;
    double[] lambdaDayType;
    double[] lambdaMonth;
    double[] rhoTimeOfDay;
  }
  Map<String, ModelParams> busStopToParams;
  

  public PassengerOnModelNegBinom(Reader paramReader) {
    this.rand = new MRG32k3a();
    ModelParamsReader<ModelParams> parser = new ModelParamsReader<ModelParams>(ModelParams.class);
    this.busStopToParams = parser.loadParams(paramReader);
  }

  /**
   * Draws a sample from the passenger on model
   * @param busStopId  string representing the route + stop: routeId,stopId
   * @param day        the current day
   * @param lastDepart departure time of the previous bus, in seconds since midnight
   * @param thisDepart departure time of the current bus, in seconds since midnight
   * @return the number of passengers who want to board the bus
   */
  @Override
  public int sample(String busStopId, DateMidnight day, int lastDepart, int thisDepart) {
    // We don't have all stops trained yet just - just use an example stop
    // ModelParams params = this.busStopToParams.get(busStopId);
    // TODO: fix parameter loading
    ModelParams params = this.busStopToParams.entrySet().iterator().next().getValue();
    // TODO: allow sample across midnight

    int dayIdx = ModelConstants.DAYTYPE_WEEKDAY;
    int dayId = day.getDayOfWeek();
    if (dayId == DateTimeConstants.SATURDAY || dayId == DateTimeConstants.SUNDAY)
      dayIdx = ModelConstants.DAYTYPE_WEEKEND;
    int monthIdx = day.getMonthOfYear() - 1;

    double llDayTypeFactor = params.lambdaDayType[dayIdx];
    double llMonthFactor = params.lambdaMonth[monthIdx];
    
    int beginTimeIdx = lastDepart / BUCKET_SIZE;
    double beginBucketFraction = (BUCKET_SIZE - (lastDepart % BUCKET_SIZE)) / (double) BUCKET_SIZE;
    int endTimeIdx = thisDepart / BUCKET_SIZE;
    double endBucketFraction = (thisDepart % BUCKET_SIZE) / (double) BUCKET_SIZE;

    int sample = 0;
    for(int timeIdx = beginTimeIdx; timeIdx <= endTimeIdx; timeIdx++) {
      double bucketFraction = 1;
      if(timeIdx == beginTimeIdx) bucketFraction = beginBucketFraction;
      if(timeIdx == endTimeIdx) bucketFraction = endBucketFraction;
      int mTimeIdx = timeIdx % NUM_BUCKETS;
      double rhoTimeOfDayFactor = params.rhoTimeOfDay[mTimeIdx];
      double llTimeOfDayFactor = params.lambdaTimeOfDay[mTimeIdx];
  
      double logLambda = llTimeOfDayFactor + llDayTypeFactor + llMonthFactor;
      double lambda = Math.exp(logLambda);
      double dispersion = rhoTimeOfDayFactor;
      double mean = lambda * dispersion * bucketFraction;
  
      double n = dispersion;
      double p = mean / (mean + dispersion);
      double u = this.rand.nextDouble();
  
      sample += NegativeBinomialDist.inverseF(n,p,u);
    }
    return sample; 
  }
}
