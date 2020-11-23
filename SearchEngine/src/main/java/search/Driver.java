package search;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Driver implements ViewDelegate {
	
	private View view;
	private Cloud cloud;
	
	private Driver() throws FileNotFoundException, IOException {
		cloud = new Cloud("Credentials.json");
		view = new View();
		view.delegate = this;
	}
	
	@Override
	public void handleBuildInvertedIndices() throws IOException, InterruptedException, ExecutionException {
		cloud.generateInvertedIndices();
		
	}

	@Override
	public ArrayList<SearchResult> handleTermSearch(String query) throws IOException, InterruptedException, ExecutionException {
		return cloud.searchForTerm(query);
	}

	@Override
	public ArrayList<TopNResult> handleTopN(int n) throws IOException, InterruptedException, ExecutionException {
		return cloud.getTopN(n);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Driver driver = new Driver();
	}

}
