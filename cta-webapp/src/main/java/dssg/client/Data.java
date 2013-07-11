package dssg.client;

import java.util.ArrayList;
import java.util.List;


public class Data {
	
	public static List<MyStats> getStats(){
		List<MyStats> stats = new ArrayList<MyStats>();
		for(int i=0; i<80; i++)
			stats.add(new MyStats(Integer.toString(i),Math.random()*10,Math.random()*20,Math.random()*25,Math.random()*30));
		
		return stats;
		
	}

}
