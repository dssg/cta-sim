package dssg.simulator;

import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ModelParamsReader<ModelParams> {
  Class<ModelParams> modelParamsClass;
  
  public ModelParamsReader(Class<ModelParams> modelParamsClass) {
    this.modelParamsClass = modelParamsClass;
  }

  public Map<String,ModelParams> loadParams(Reader paramReader) {
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
          ModelParams params = gson.fromJson(tageoidEntry.getValue(),this.modelParamsClass);
          String busStopId = taroute + "," + tageoid;
          buildMap.put(busStopId, params);
        }
      }
    }
    return Collections.unmodifiableMap(buildMap);
  }
}
