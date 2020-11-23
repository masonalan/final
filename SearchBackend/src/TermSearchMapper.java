import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TermSearchMapper extends Mapper<Object, Text, Text, Text> {
    
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException { 
        String[] components = value.toString().split("\t");
        int pairsIndex = components.length - 1;
        int wordIndex = components.length - 2;
        String word = components[wordIndex].toLowerCase();
        if (word.contains(context.getConfiguration().get(Driver.ARG).toLowerCase())) {
        	String[] pairs = components[pairsIndex].replaceAll("[{}]", "").split(", ");
        	for (int i = 0; i < pairs.length; i ++) {
        		String[] pair = pairs[i].split("=");
        		context.write(new Text(pair[0]), new Text(pair[1]));
        	}
        }
    }
}
