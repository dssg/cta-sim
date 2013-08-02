package dssg.simulator;

public class PassengerOffModelBinom implements PassengerOffModel {

  @Override
  public int sample(String busStopId, int arrivingLoad) {
    return arrivingLoad / 2;
  }

}
