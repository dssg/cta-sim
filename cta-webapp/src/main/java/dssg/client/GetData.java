package dssg.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GetData {

	// TEMP
	public static List<DataStats> getStats() {
		List<DataStats> stats = new ArrayList<DataStats>();
		for (int i = 0; i <= 80; i++)
			stats.add(new DataStats(Integer.toString(i), Math.random() * 200, Math
					.random() * 5, Math.random() * 3, Math.random() * 4 + 20));

		return stats;

	}

	public static List<DataRoutes> getRoutes() {
		List<DataRoutes> routes = new ArrayList<DataRoutes>();
		routes.add(new DataRoutes(2, "2.Hyde-Park-Exp"));
		routes.add(new DataRoutes(3, "3.King Drive"));
		routes.add(new DataRoutes(6, "6.Jack-Park-Exp"));
		routes.add(new DataRoutes(7, "7.Harrison"));
		routes.add(new DataRoutes(9, "9.Ashland"));

		return routes;

	}

	/**
	 * Get the data from simulation services containing the time and the values
	 * for the LOAD at a given time window
	 */
	public static void getData(SimulationServiceAsync simulationService,
			final GwtPortalContainer gwtPortalContainer, String route,
			Date date, Integer startT, Integer stopT) {
	  String simId = route + date + startT + stopT;
		simulationService.getResults(simId,
				new AsyncCallback<Map<String, Integer[]>>() {
					@Override
					public void onSuccess(Map<String, Integer[]> output) {
						Info.display("Sucess in getting data @DATA.", "Number of data points: " 
    					+ Integer.toString(output.get("max_load_N").length) + " , " + Integer.toString(output.get("max_load_S").length));
						gwtPortalContainer.updateCharts(output);
					}

					@Override
					public void onFailure(Throwable e) {
						Info.display("Failure in getting data", "");
					}
				});
	}
	
	public static void testS3(S3CommunicationServiceAsync s3ComunicationService) {
		s3ComunicationService.downloadParameters(
				new AsyncCallback<List<String>>() {
					@Override
					public void onSuccess(List<String> output) {
						Info.display("TEST S3\nSucess in getting data @DATA.", "Number of data points:"+Integer.toString(output.toArray().length));;
						
					}

					@Override
					public void onFailure(Throwable e) {
						Info.display("TEST S3\nFailure in getting data", "");
					}
				});
	}

}
