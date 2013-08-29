Guide to installing HADOOP and executing jobs.
%%%%%%%%%%%%
Installing HADOOP (standalone mode)
%%%%%%%%%%%%
-Follow the steps in 
	http://mysoftwareuniverse.blogspot.com/2012/10/setting-up-hadoop-103-on-os-x-mountain.html
  *When configuring hadoop-env.sh change line:
	"-Djava.security.krb5.realm=OX.AC.UK -Djava.security.krb5.kdc=kdc0.ox.ac.uk:kdc1.ox.ac.uk"
	to:
	"-Djava.security.krb5.realm= -Djava.security.krb5.kdc="
-If you get an error saying: "HDFS error: could only be replicated to 0 nodes, instead of 1"
	stop all hadoop services
	delete dfs/name and dfs/data directories
	hadoop namenode -format # Answer with a capital Y
	start hadoop services

%%%%%%%%%%%%
Executing a job
%%%%%%%%%%%%
Steps:
-Initialize HDFS
	(If a problem arises with "could only be replicated to 0 nodes , instead of 1", remove data and name directories under /usr/local/hadoop/HDFS)
	/usr/local/hadoop/bin/hadoop namenode -format 
-Start hadoop
	/usr/local/hadoop/bin/start-all.sh
-Create the java script with the map reduce classes
	script.java
-cd into the directory with your java script
-Make a directory where the classes will be stored
	mkdir <nameDirectory_classes>
-Execute
	javac -classpath /usr/local/hadoop/hadoop-core-1.0.3.jar -d <nameDirectory_classes> script.java 
-Jar the contents of the <nameDirectory_classes>
	jar -cvf <script_name>.jar -C <nameDirectory_classes>/ .
-Put the files to work on in the input directory
	(If you do not know the directories in which you can put the data use hadoop dfs -ls / also use hadoop dfs -mkdir to create a directory)
	hadoop dfs -put <data_directowry>/ <input_directory> (/tmp/input)
-Run the application
	hadoop jar script.jar org.myorg.<ClassName> <input_directory> <output_directory>
-See the results
	hadoop dfs -cat /<output_directory>/*

%%%%%%%%%%%%
To use this from Eclipse do last 5 steps using:
%%%%%%%%%%%%
	hadoop dfs -rmr /tmp/output

	javac -classpath /usr/local/hadoop/hadoop-core-1.0.3.jar -d MapByStop_classes src/main/java/dssg/MapByStop.java

	jar -cvf MapByStop.jar -C MapByStop_classes/ .

	hadoop jar MapByStop.jar dssg.MapByStop /tmp/input /tmp/output

	hadoop dfs -cat /tmp/output/*

%%%%%%%%%%%%
Using ECLIPSE and MAVEN it is easier to do it this way (in the cta-hadoop directory):
%%%%%%%%%%%%
	hadoop dfs -rmr /tmp/output
	mvn clean package
You will see a .jar in target that says ...SNAPSHOT-job.jar Run:
	hadoop jar target/MapByStop.jar dssg.MapByStop /tmp/input /tmp/output
or
	hadoop jar target/MapByStop-job.jar  /tmp/input /tmp/output
To see the output fil
	hadoop dfs -cat /tmp/output/*
	(The output file is named part-00000)

To save the output hdfs file into a local file use:
	hadoop dfs -cat /tmp/ouput/* > file.txt

Commands

hadoop dfs

-ls / # directories valid for input data
-cat <output directory>/*  # give the output for the job
-put <input folder> <file path> # 

Usefull resources

HDFS data: http://hadoop.apache.org/docs/stable/file_system_shell.html#put
EMP (AWS): http://commoncrawl.org/mapreduce-for-the-masses/
Tutorial: http://hadoop.apache.org/docs/r1.1.1/mapred_tutorial.html#Example%3A+WordCount+v1.0
Book: "Hadoop: the definite guide", "Hadoop in action"

Problems

Error: unable to create new native thread
attempt_201307011525_0002_m_000000_0: 2013-07-01 15:45:30.239 java[97675:1203] Unable to load realm info from SCDynamicStore
