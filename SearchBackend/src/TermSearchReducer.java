import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class TermSearchReducer extends Reducer<Text, Text, Text, Text> {
	
	 @Override
	 public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException { 
		 int count = 0;
	     for (Text value : values) {
	    	 //context.write(key, value);
	    	 count += Integer.parseInt(value.toString());
	     }
	     context.write(key, new Text(Integer.toString(count)));
	 }
}
