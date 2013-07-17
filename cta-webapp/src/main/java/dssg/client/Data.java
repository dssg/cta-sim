package dssg.client;

import java.util.ArrayList;
import java.util.List;

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

	/*
	 * Get the parameters to run the simulation
	 */
	public static List<MyParameters> getParameters(S3CommunicationServiceAsync s3ComunicationService) {
		final List<MyParameters> data = new ArrayList<MyParameters>();
		s3ComunicationService.downloadParameters(new AsyncCallback<List<MyParameters>>() {
					@Override
					public void onSuccess(List<MyParameters> output) {
						System.out.println("Sucess in getting parameters.\nNumber of parameters:"
								+ Integer.toString(output.toArray().length));
						data.addAll(output);
					}

					@Override
					public void onFailure(Throwable e) {
						System.out.println("Failure in getting Parameters");

					}
				});
		return data;
	}

	/**
	 * Get the data from simulation services containing the time and the values
	 * for the LOAD at a given time window
	 */
	public static List<Number> getLoadData(Integer route, Integer startT, Integer stopT) {
		List<Number> data = new ArrayList<Number>();
		for (double n = startT; n <= stopT; n = n + .5) {
			data.add(Math.floor(Math.abs(150
					* Math.sin(n * Math.PI / 20 - Math.PI * 4 / 24)
					+ Random.nextDouble() * 80)));
		}
		return data;
	}

}
