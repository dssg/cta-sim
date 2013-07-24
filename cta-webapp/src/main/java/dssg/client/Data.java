package dssg.client;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Data {

	// TEMP
	public static List<MyData> getStats() {
		List<MyData> stats = new ArrayList<MyData>();
		for (int i = 0; i <= 80; i++)
			stats.add(new MyData(Integer.toString(i), Math.random() * 200, Math
					.random() * 5, Math.random() * 3, Math.random() * 4 + 20));

		return stats;

	}

	public static List<MyRoutes> getRoutes() {
		List<MyRoutes> routes = new ArrayList<MyRoutes>();
		routes.add(new MyRoutes(2, "2.Hyde-Park-Exp"));
		routes.add(new MyRoutes(3, "3.King Drive"));
		routes.add(new MyRoutes(6, "6.Jack-Park-Exp"));
		routes.add(new MyRoutes(7, "7.Harrison"));
		routes.add(new MyRoutes(9, "9.Ashland"));

		return routes;

	}

	/**
	 * Get the data from simulation services containing the time and the values
	 * for the LOAD at a given time window
	 */
	public static void getData(SimulationServiceAsync simulationService,
			final GwtPortalContainer gwtPortalContainer, Integer route,
			Integer startT, Integer stopT) {
		simulationService.getResults(route, startT, stopT,
				new AsyncCallback<List<Number>>() {
					@Override
					public void onSuccess(List<Number> output) {
						Info.display("Sucess in getting data @DATA.", "Number of data points:"+Integer.toString(output.toArray().length));;
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
				new AsyncCallback<List<MyParameters>>() {
					@Override
					public void onSuccess(List<MyParameters> output) {
						Info.display("TEST\nSucess in getting data @DATA.", "Number of data points:"+Integer.toString(output.toArray().length));;
						
					}

					@Override
					public void onFailure(Throwable e) {
						Info.display("TEST\nFailure in getting data", "");
					}
				});
	}

}
