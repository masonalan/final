import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TopNMapper extends Mapper<Object, Text, Text, LongWritable> {
	
	private TreeMap<Long, String> map; 
	  
    @Override
    public void setup(Context context) throws IOException, InterruptedException { 
    	map = new TreeMap<Long, String>(); 
    }
	
	@Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] components = value.toString().split("\t");
        int pairsIndex = components.length - 1;
        int wordIndex = components.length - 2;
        String word = components[wordIndex];
        String[] pairs = components[pairsIndex].replaceAll("[{}]", "").split(", ");
        long frequency = 0;
    	for (int i = 0; i < pairs.length; i ++) {
    		String[] pair = pairs[i].split("=");
    		frequency += Integer.parseInt(pair[1]);
    	}
    	map.put(frequency, word);
    	int n = context.getConfiguration().getInt(Driver.ARG, -1);
    	if (map.size() > n) {
    		map.remove(map.firstKey());
    	}
    }
	
	@Override
    public void cleanup(Context context) throws IOException, InterruptedException { 
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            long count = entry.getKey(); 
            String name = entry.getValue(); 
            context.write(new Text(name), new LongWritable(count)); 
        } 
    } 
}
