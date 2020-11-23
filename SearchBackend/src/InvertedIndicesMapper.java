import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.Mapper;

public class InvertedIndicesMapper extends Mapper<LongWritable, Text, Text, Text> {
	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		Path path = ((FileSplit)context.getInputSplit()).getPath();
		String parent = path.getParent().toString();
		String filename = path.getName();
		String line = value.toString();
		String words[] = line.split(" ");
		for (String word : words) {
			//word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
			if (word.matches(".*[a-zA-Z]+.*")) {
				context.write(new Text(word.toLowerCase()), new Text(parent + "/" + filename));
			}
		}
	}
}
