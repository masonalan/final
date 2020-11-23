package search;
import java.util.ArrayList;

public class TopNResult implements Comparable<TopNResult> {
	public int count;
	public String word;
	
	public TopNResult(String line) {
		System.out.println(line);
		String[] components = line.split("\t");
		this.count = Integer.parseInt(components[0]);
		this.word = components[1];
	}
	
	@Override
	public int compareTo(TopNResult that) {
		return this.count < that.count ? 1 : (this.count == that.count ? 0 : -1);
	}
	
	static String[][] formatForTable(ArrayList<TopNResult> results) {
		String[][] output = new String[results.size()][3];
		for (int i = 0; i < results.size(); i ++) {
			TopNResult result = results.get(i);
			String[] row = new String[2];
			row[0] = result.word;
			row[1] = Integer.toString(result.count);
			output[i] = row;
		}
		return output;
	}
}
