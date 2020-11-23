package search;
import java.util.ArrayList;

public class SearchResult implements Comparable<SearchResult> {
	public int count;
	public String filename;
	public String path;
	
	public SearchResult(String line) {
		String[] components = line.split("\t");
		this.count = Integer.parseInt(components[1]);
		String[] pathComponents = components[0].split("Data/")[1].split("/");
		int levels = pathComponents.length;
		this.filename = pathComponents[levels - 1];
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < levels - 1; i ++) {
			builder.append(pathComponents[i] + "/");
		}
		this.path = builder.toString();
	}
	
	@Override
	public int compareTo(SearchResult that) {
		return this.count < that.count ? 1 : (this.count == that.count ? 0 : -1);
	}
	
	static String[][] formatForTable(ArrayList<SearchResult> results) {
		String[][] output = new String[results.size()][3];
		for (int i = 0; i < results.size(); i ++) {
			SearchResult result = results.get(i);
			String[] row = new String[3];
			row[0] = result.filename;
			row[1] = result.path;
			row[2] = Integer.toString(result.count);
			output[i] = row;
		}
		return output;
	}
}
