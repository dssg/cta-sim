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
    return this.loadParams(paramReader,null);
  }
  public Map<String,ModelParams> loadParams(Reader paramReader, String fitId) {
    Map<String, ModelParams> buildMap = new HashMap<String, ModelParams>();
    Gson gson = new Gson();
    JsonParser parser = new JsonParser();
    JsonObject fits = parser.parse(paramReader).getAsJsonObject();
    
    // if no fitId specified, select the latest one
    if(fitId == null) {
      fitId = "";
      for(Entry<String,JsonElement> fitEntry : fits.entrySet()) {
        String key = fitEntry.getKey();
        if(fitId.compareTo(key) < 0)
          fitId = key;
      }
    }

    // build map from busStopId ( taroute,tageoid ) to ModelParams object
    for(Entry<String,JsonElement> fitEntry : fits.entrySet()) {
      if(fitEntry.getKey().equals(fitId)) {
        JsonObject taroutes = fitEntry.getValue().getAsJsonObject();
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
        break;
      }
    }
    return Collections.unmodifiableMap(buildMap);
  }
}
