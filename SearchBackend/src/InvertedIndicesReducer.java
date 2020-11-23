import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class InvertedIndicesReducer extends Reducer<Text, Text, Text, Text> {
	
	@SuppressWarnings("unchecked")
	@Override 
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		@SuppressWarnings("rawtypes")
		HashMap map = new HashMap();
		int count = 0;
		for (Text text : values) {
			String string = text.toString();
			if (map != null && map.get(string) != null) {
				count = (int)map.get(string);
				map.put(string, ++ count);
			} else {
				map.put(string, 1);
			}
		}
		context.write(key, new Text(map.toString()));
	}
}
