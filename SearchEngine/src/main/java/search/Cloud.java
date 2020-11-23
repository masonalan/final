package search;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.dataproc.v1.HadoopJob;
import com.google.cloud.dataproc.v1.JobControllerClient;
import com.google.cloud.dataproc.v1.JobControllerSettings;
import com.google.cloud.dataproc.v1.JobMetadata;
import com.google.cloud.dataproc.v1.JobPlacement;
import com.google.cloud.dataproc.v1.Job;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;

public class Cloud {

	private static final String PROJECT_ID = "search-engine-296119";
	private static final String REGION = "us-central1";
	private static final String CLUSTER = "search-cluster";
	private static final String BUCKET = "search-engine-bucket";
	private static final String ENDPOINT = REGION + "-dataproc.googleapis.com:443";

	private static final String INVERTED_INDEX_JOB = "1";
	private static final String SEARCH_FOR_TERM_JOB = "2";
	private static final String GET_TOP_N_JOB = "3";
	
	private static final String INVERTED_INDEX_OUTPUT = "out";
	private static final String SEARCH_OUTPUT = "out2";
	private static final String TOP_N_OUTPUT = "out3";

	private static Storage storage;
	private static FixedCredentialsProvider provider;
	
	public static long lastJobRuntime;

	public Cloud(String credentialsJson) throws FileNotFoundException, IOException {
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsJson))
				.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
		storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
		provider = FixedCredentialsProvider.create(credentials);

	}

	public void generateInvertedIndices() throws IOException, InterruptedException, ExecutionException {
		delete(INVERTED_INDEX_OUTPUT);
		submitJob(INVERTED_INDEX_JOB);
	}

	public ArrayList<SearchResult> searchForTerm(String term) throws IOException, InterruptedException, ExecutionException {
		delete(SEARCH_OUTPUT);
		submitJob(SEARCH_FOR_TERM_JOB, term);
		Iterable<Blob> blobs = get(SEARCH_OUTPUT);
		ArrayList<SearchResult> results = new ArrayList<>();
		for (Blob blob : blobs) {
			ReadChannel channel = blob.reader();
			BufferedReader reader = new BufferedReader(Channels.newReader(channel, "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				SearchResult result = new SearchResult(line);
				results.add(result);
			}
		}
		Collections.sort(results);
		return results;
	}

	public ArrayList<TopNResult> getTopN(int n) throws IOException, InterruptedException, ExecutionException {
		delete(TOP_N_OUTPUT);
		submitJob(GET_TOP_N_JOB, Integer.toString(n));
		Iterable<Blob> blobs = get(TOP_N_OUTPUT);
		ArrayList<TopNResult> results = new ArrayList<>();
		for (Blob blob : blobs) {
			ReadChannel channel = blob.reader();
			BufferedReader reader = new BufferedReader(Channels.newReader(channel, "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				TopNResult result = new TopNResult(line);
				results.add(result);
			}
		}
		Collections.sort(results);
		return results;
	}
	
	private void delete(String blobName) {
		Iterable<Blob> blobs = storage.list(BUCKET, Storage.BlobListOption.prefix(blobName)).iterateAll();
		for (Blob blob : blobs) {
			storage.delete(blob.getBlobId());
		}
	}
	
	private Iterable<Blob> get(String blobName) {
		return storage.list(BUCKET, Storage.BlobListOption.prefix(blobName)).iterateAll();
	}

	private void submitJob(String jobName) throws IOException, InterruptedException, ExecutionException {
		submitJob(jobName, null);
	}

	private void submitJob(String jobName, String arg) throws IOException, InterruptedException, ExecutionException {
		JobControllerSettings settings = JobControllerSettings.newBuilder().setEndpoint(ENDPOINT)
				.setCredentialsProvider(provider).build();
		try (JobControllerClient client = JobControllerClient.create(settings)) {
			JobPlacement placement = JobPlacement.newBuilder().setClusterName(CLUSTER).build();
			ArrayList<String> args = new ArrayList<String>(Arrays.asList("Driver", jobName));
			if (arg != null) {
				args.add(arg);
			}
			HadoopJob hadoopJob = HadoopJob.newBuilder()
					.setMainJarFileUri("gs://search-engine-bucket/SearchBackend.jar").addAllArgs(args).build();
			Job job = Job.newBuilder().setPlacement(placement).setHadoopJob(hadoopJob).build();
			OperationFuture<Job, JobMetadata> request = client.submitJobAsOperationAsync(PROJECT_ID, REGION, job);
			long start = new Date().getTime();
			request.get();
			Cloud.lastJobRuntime = (new Date().getTime()) - start;
		}
	}
}
