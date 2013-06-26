package dssg;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;


public class MapByStop {
	    static class Map extends MapReduceBase implements Mapper <LongWritable, Text, Text, DoubleWritable> {
		   
		   public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
			    String line = value.toString();
			   	String[] parts = line.split(",");
			  	String route =  parts[0];
			  	Double passengers = Double.parseDouble(parts[1]);

			  	output.collect(new Text(route), new DoubleWritable(passengers));
			   
			}
	   }
	   
	    static class Reduce extends MapReduceBase implements Reducer<Text, DoubleWritable, Text, DoubleWritable> {
		     public void reduce(Text key, Iterator<DoubleWritable> values, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException {
		         double sum = 0;
		         while(values.hasNext()){
		         	sum += values.next().get();
		         }
		         output.collect(key, new DoubleWritable(sum));
		         
		     }
	   }
	   
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
		     conf.setReducerClass(Reduce.class);

		    /*[*/conf/*]*/.setOutputKeyClass(Text.class);
    		/*[*/conf/*]*/.setOutputValueClass(DoubleWritable.class);

		     JobClient.runJob(conf);
	   }

}