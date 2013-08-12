package dssg.simulator;

import java.io.Reader;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;

import dssg.shared.ProjectConstants;
import umontreal.iro.lecuyer.probdist.NegativeBinomialDist;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

public class PassengerOnModelNegBinom implements PassengerOnModel {
  private int BUCKET_SIZE = ProjectConstants.BUCKET_SIZE;
  private int NUM_BUCKETS = ProjectConstants.NUM_BUCKETS;

  class ModelParams {
    double[] llTimeOfDay;
    double[] llDayType;
    double[] llMonth;
    double[] rhoTimeOfDay;
  }
  Map<String, ModelParams> busStopToParams;

  public PassengerOnModelNegBinom(Reader paramReader) {
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
  public int sample(String busStopId, DateMidnight day, int lastDepart, int thisDepart, RandomStream rng) {
    ModelParams params = this.busStopToParams.get(busStopId);

    int dayIdx = ProjectConstants.DAYTYPE_WEEKDAY;
    int dayId = day.getDayOfWeek();
    if (dayId == DateTimeConstants.SATURDAY || dayId == DateTimeConstants.SUNDAY)
      dayIdx = ProjectConstants.DAYTYPE_WEEKEND;
    int monthIdx = day.getMonthOfYear() - 1;

    double llDayTypeFactor = params.llDayType[dayIdx];
    double llMonthFactor = params.llMonth[monthIdx];
    
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
      double llTimeOfDayFactor = params.llTimeOfDay[mTimeIdx];
  
      double logLambda = llTimeOfDayFactor + llDayTypeFactor + llMonthFactor;
      double lambda = Math.exp(logLambda);
      double dispersion = rhoTimeOfDayFactor;
      double mean = lambda * dispersion * bucketFraction;
  
      double n = dispersion;
      double p = 1 - (mean / (mean + dispersion));
      double u = rng.nextDouble();
  
      sample += NegativeBinomialDist.inverseF(n,p,u);
    }
    return sample; 
  }
}
