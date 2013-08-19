package dssg.simulator;

import java.io.Reader;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;

import dssg.shared.Config;
import umontreal.iro.lecuyer.probdist.BinomialDist;
import umontreal.iro.lecuyer.rng.RandomStream;

public class PassengerOffModelBinom implements PassengerOffModel {
  class ModelParams {
    double[] llTimeOfDay;
    double[] llDayType;
    double[] llMonth;
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
    ModelParams params = this.busStopToParams.get(busStopId);

    int dayIdx = Config.DAYTYPE_WEEKDAY;
    int dayId = day.getDayOfWeek();
    if (dayId == DateTimeConstants.SATURDAY || dayId == DateTimeConstants.SUNDAY) 
      dayIdx = Config.DAYTYPE_WEEKEND;
    int monthIdx = day.getMonthOfYear() - 1;

    double lpDayTypeFactor = params.llDayType[dayIdx];
    double lpMonthFactor = params.llMonth[monthIdx];
    
    int mTimeIdx = Config.getBucket(arrivalTime);
    double lpTimeOfDayFactor = params.llTimeOfDay[mTimeIdx];

    double logP = lpTimeOfDayFactor + lpDayTypeFactor + lpMonthFactor;

    int n = arrivingLoad;
    double p = Math.exp(logP);
    double u = rng.nextDouble();

    return BinomialDist.inverseF(n, p, u); 
  }

}
