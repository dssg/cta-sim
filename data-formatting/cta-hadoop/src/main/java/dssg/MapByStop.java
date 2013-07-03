// Code to split the APC data by stop id. 

// INPUT: APC data for a quarter
// OUTPUT: folders with data for each stop

// This code my have problems with the number of threads it creates. When the number of routes is large enough it 
// will give an error saying "error unable to create new native thread hadoop". 
// **It has been tested in EMP and works properly.**

package dssg;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;

// Main class

public class MapByStop {
	
		// Map class
	
	    static class Map extends MapReduceBase implements Mapper <LongWritable, Text, Text, Text> {
	    	Text outputKey = new Text();
	    	Text outputLine = new Text();
	    	
	    	// Map function
	    	
	    	// For every line it reads and splits it to read the stop which is the first value.
	    	// Stop value is used as a key.
	    	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			   	String route = "0";
			   	String line = value.toString();
			   	
			   	if(!line.isEmpty()){
			   		String[] parts = line.split(",");
			   		if(!parts[0].isEmpty()){
				   		route =  parts[0];
			   		}
				}
			   	// Add code to partition data even further.
			   	
			   	outputKey.set(route);
			   	outputLine.set(line);
			  	output.collect(outputKey, outputLine);
			}
	   }
	   
		//Reduce function
	    
	    //Code to apply any extra function to the data while aggregating it.
	    // Example code sums the second row of the data for every route.
	    /* 
	    static class Reduce extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable> {
	    	// Reduce function
		     public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
		         double sum = 0;
		         while(values.hasNext()){
		         	sum += values.next().get();
		         }
		         output.collect(key, new DoubleWritable(sum));
		         
		     }
	   }
	   */ 
	    
	    //Partition by Stop Class
	    
	    // Creates the name files for the output data (essentially creates the "folders" for the output data)
	    public static class PartitionByStop extends MultipleTextOutputFormat<Text,Text>
	    {
	    	// File generation function
	    	protected String generateFileNameForKeyValue(Text key, Text value, String filename)
	    	{	
	    		// File name generated from key value
	    		String file;
	    		if(!key.toString().isEmpty()){
	    			file = key + "/" + filename;
	    		}
	    		else{
	    			file = "error" + filename;
	    		}
	    		return file;
	    	}
	    }
	    
	   // Main function and configuration module
	   public static void main(String[] args) throws Exception {
	   		if (args.length != 2) {
      			System.err.println("Usage: mapByStop <input path> <output path>");
      			System.exit(-1);
    		}
		     JobConf conf = new JobConf(MapByStop.class);
		     conf.setJobName("passenger count");

		     FileInputFormat.setInputPaths(conf, new Path(args[0]));
		     FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		     conf.setMapperClass(Map.class);
		// Uncomment if reducer needed  
		     // conf.setReducerClass(Reduce.class);

		     conf.setOutputKeyClass(Text.class);
    		 conf.setOutputValueClass(Text.class);
    		 conf.setOutputFormat(PartitionByStop.class);
    		 
    	//Comment out if Reduce function is used
    		 conf.setNumReduceTasks(0);

		     JobClient.runJob(conf);
	   }

}