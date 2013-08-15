package dssg.simulator;

import java.io.Reader;
import java.util.Map;

import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.rng.RandomStream;

public class BusServiceModelNormal implements BusServiceModel {
  class ModelParams {
    double alpha;
    double beta;
  }
  Map<String, ModelParams> routeAndDirToParams;

  public BusServiceModelNormal(Reader paramReader) {
    // TODO: uncomment when we have the service model params
    //ModelParamsReader<ModelParams> parser = new ModelParamsReader<ModelParams>(ModelParams.class);
    //this.routeAndDirToParams = parser.loadParams(paramReader);
  }


  // TODO: determine if we need separate models for dwell time and for time
  //   between departing one stop and arriving at the next
  /**
   * Draws a sample from the bus service model 
   * @param routeAndDir    a string identifier for the route and direction:
   *                       routeId,directionId
   * @param prevDelta      the difference between last scheduled departure and 
   *                       the actual departure time, in seconds
   * @param schedInterval  the time scheduled between departure from the
   *                       previous stop and the next, in seconds
   * @return schedule deviation for departure from the next stop, in seconds
   */
  @Override
  public int sample(String routeAndDir, int prevDelta, int schedInterval, RandomStream rng) {
    // TODO: get fit params
    //ModelParams params = this.routeAndDirToParams.get(routeAndDir);
    
    double u = rng.nextDouble();
    double mu = 0;
    double sigma = 5;

    double sample = NormalDist.inverseF(mu, sigma, u);
    return (int)Math.round(sample); 
  }
}
