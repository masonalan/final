import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Driver {
	
	public static final String ARG = "arg2";
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(); 
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs(); 
  
        if (otherArgs.length == 0) { 
            System.err.println("No Action Specified"); 
            System.exit(2); 
        }
        if (otherArgs.length == 2) {
        	conf.set(ARG, otherArgs[1]);
        }
        
        Job job = Job.getInstance(conf);
        job.setJarByClass(Driver.class);
        
        if (otherArgs[0].equals("1")) {
        	job.setMapperClass(InvertedIndicesMapper.class); 
            job.setReducerClass(InvertedIndicesReducer.class); 
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
            FileInputFormat.setInputDirRecursive(job, true);
            FileInputFormat.addInputPath(job, new Path("gs://search-engine-bucket/Data"));
            FileOutputFormat.setOutputPath(job, new Path("gs://search-engine-bucket/out"));
        } else if (otherArgs[0].equals("2")) {
        	job.setMapperClass(TermSearchMapper.class); 
            job.setReducerClass(TermSearchReducer.class); 
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
            FileInputFormat.addInputPath(job, new Path("gs://search-engine-bucket/out"));
            FileOutputFormat.setOutputPath(job, new Path("gs://search-engine-bucket/out2"));
        } else if (otherArgs[0].equals("3")) {
        	job.setMapperClass(TopNMapper.class); 
            job.setReducerClass(TopNReducer.class); 
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(LongWritable.class);
            job.setNumReduceTasks(1);
            FileInputFormat.addInputPath(job, new Path("gs://search-engine-bucket/out"));
            FileOutputFormat.setOutputPath(job, new Path("gs://search-engine-bucket/out3"));
        }
        System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
