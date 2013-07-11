package dssg.client;

import java.util.ArrayList;
import java.util.List;


public class Data {
	
	public static List<MyStats> getStats(){
		List<MyStats> stats = new ArrayList<MyStats>();
		for(int i=0; i<=80; i++)
			stats.add(new MyStats(Integer.toString(i),Math.random()*200,Math.random()*5,Math.random()*3,Math.random()*4+20));
		
		return stats;
		
	}
	
	public static List<MyRoutes> getRoutes(){
		List<MyRoutes> routes = new ArrayList<MyRoutes>();
		routes.add(new MyRoutes(2,"2.Hyde-Park-Exp"));
		routes.add(new MyRoutes(3,"3.King Drive"));
		routes.add(new MyRoutes(6,"6.Jack-Park-Exp"));
		routes.add(new MyRoutes(7,"7.Harrison"));
		routes.add(new MyRoutes(9,"9.Ashland"));
			
		return routes;
		
	}

}
