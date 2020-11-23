import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class TopNReducer extends Reducer<Text, LongWritable, LongWritable, Text> {
	private TreeMap<Long, String> map; 
	 
	@Override
    public void setup(Context context) throws IOException, InterruptedException 
    { 
        map = new TreeMap<Long, String>(); 
    } 
  
    @Override
    public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException { 
        String word = key.toString(); 
        long frequency = 0;
        for (LongWritable value : values) { 
            frequency = value.get(); 
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
            context.write(new LongWritable(count), new Text(name)); 
        } 
    } 
}
