package dssg.simulator;

import java.io.Reader;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;

import dssg.shared.ProjectConstants;
import umontreal.iro.lecuyer.probdist.BinomialDist;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

public class PassengerOffModelBinom implements PassengerOffModel {
  class ModelParams {
    double[] lpTimeOfDay;
    double[] lpDayType;
    double[] lpMonth;
  }
  Map<String, ModelParams> busStopToParams;

  public PassengerOffModelBinom(Reader paramReader) {
    ModelParamsReader<ModelParams> parser = new ModelParamsReader<ModelParams>(ModelParams.class);
    this.busStopToParams = parser.loadParams(paramReader);
  }

  /**
   * Draws a sample from the passenger off model
   * @param busStopId    string representing the route + stop: routeId,stopId
   * @param day          the current day
   * @param arrivalTime  arrival time of the bus, in seconds since midnight
   * @param arrivingLoad number of people on the bus when it arrives
   * @return the number of passengers who get off the bus
   */
  @Override
  public int sample(String busStopId, DateMidnight day, int arrivalTime, int arrivingLoad, RandomStream rng) {
    // We don't have all stops trained yet - use an example stop
    // ModelParams params = this.busStopToParams.get(busStopId);
    // TODO: fix parameter loading
    ModelParams params = this.busStopToParams.entrySet().iterator().next().getValue();

    int dayIdx = ProjectConstants.DAYTYPE_WEEKDAY;
    int dayId = day.getDayOfWeek();
    if (dayId == DateTimeConstants.SATURDAY || dayId == DateTimeConstants.SUNDAY) 
      dayIdx = ProjectConstants.DAYTYPE_WEEKEND;
    int monthIdx = day.getMonthOfYear() - 1;

    double lpDayTypeFactor = params.lpDayType[dayIdx];
    double lpMonthFactor = params.lpMonth[monthIdx];
    
    int mTimeIdx = ProjectConstants.getBucket(arrivalTime);
    double lpTimeOfDayFactor = params.lpTimeOfDay[mTimeIdx];

    double logP = lpTimeOfDayFactor + lpDayTypeFactor + lpMonthFactor;

    int n = arrivingLoad;
    double p = Math.exp(logP);
    double u = rng.nextDouble();

    return BinomialDist.inverseF(n, p, u); 
  }

}
