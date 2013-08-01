package dssg.simulator;

import java.io.Reader;
import java.util.Collections;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import umontreal.iro.lecuyer.probdist.NegativeBinomialDist;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

public class PassengerOnModelNegBinom implements PassengerOnModel {
  // number of seconds for bucket, in this case we bucket by half hour
  private static final int BUCKET_SIZE = 60*30;
  private static final int SECONDS_IN_DAY = 60*60*24;
  private static final int NUM_BUCKETS = SECONDS_IN_DAY / BUCKET_SIZE;
  static {
    assert SECONDS_IN_DAY % BUCKET_SIZE == 0 : "Bucket size does not evenly divide day";
  }

  private static int WEEKDAY = 0;
  private static int WEEKEND = 1;
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

    Map<String, ModelParams> buildMap = new HashMap<String, ModelParams>();
    Gson gson = new Gson();
    JsonParser parser = new JsonParser();
    JsonObject taroutes = parser.parse(paramReader).getAsJsonObject();
    for(Entry<String,JsonElement> routeEntry : taroutes.entrySet()) {
      String taroute = routeEntry.getKey();
      JsonObject directions = routeEntry.getValue().getAsJsonObject();
      for(Entry<String,JsonElement> directionEntry : directions.entrySet()) {
        JsonObject tageoids = directionEntry.getValue().getAsJsonObject();
        for(Entry<String,JsonElement> tageoidEntry : tageoids.entrySet()) {
          String tageoid = tageoidEntry.getKey();
          ModelParams params = gson.fromJson(tageoidEntry.getValue(),ModelParams.class);
          String busStopId = taroute + "," + tageoid;
          buildMap.put(busStopId, params);
        }
      }
    }
    this.busStopToParams = Collections.unmodifiableMap(buildMap);
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
    ModelParams params = this.busStopToParams.get(busStopId);
    // TODO: allow sample across midnight

    int dayIdx = WEEKDAY;
    int dayId = day.getDayOfWeek();
    if (dayId == DateTimeConstants.SATURDAY || dayId == DateTimeConstants.SUNDAY) dayIdx = WEEKEND;
    int monthIdx = day.getMonthOfYear() - 1;

    double llDayTypeFactor = params.lambdaDayType[dayIdx];
    double llMonthFactor = params.lambdaMonth[monthIdx];
    
    int beginTimeIdx = lastDepart / BUCKET_SIZE;
    double beginBucketFraction = (BUCKET_SIZE - (lastDepart % BUCKET_SIZE)) / (double) BUCKET_SIZE;
    int endTimeIdx = thisDepart / BUCKET_SIZE;
    double endBucketFraction = (thisDepart % BUCKET_SIZE) / (double) 1800;

    int sample = 0;
    for(int timeIdx = beginTimeIdx; timeIdx <= endTimeIdx; timeIdx++) {
      double bucketFraction = 1;
      if(timeIdx == beginTimeIdx) bucketFraction = beginBucketFraction;
      if(timeIdx == endTimeIdx) bucketFraction = endBucketFraction;
      double rhoTimeOfDayFactor = params.rhoTimeOfDay[timeIdx];
      double llTimeOfDayFactor = params.lambdaTimeOfDay[timeIdx];
  
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
