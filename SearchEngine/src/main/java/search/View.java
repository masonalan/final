package search;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

interface ViewDelegate {
	void handleBuildInvertedIndices() throws IOException, InterruptedException, ExecutionException;
	ArrayList<SearchResult> handleTermSearch(String query) throws IOException, InterruptedException, ExecutionException;
	ArrayList<TopNResult> handleTopN(int n) throws IOException, InterruptedException, ExecutionException;
}

public class View {
	
	public ViewDelegate delegate;
	
	private JFrame frame;
	private JPanel panel;
	private JButton buildButton;
	private JButton searchActionButton;
	private JButton topNActionButton;
	private JButton searchButton;
	private JButton topNButton;
	private JButton backButton;
	private JButton searchBackButton;
	private JButton topNBackButton;
	private JLabel queryLabel;
	private JLabel nValueLabel;
	private JLabel searchTermLabel;
	private JLabel searchExecutedLabel;
	private JTextField queryTextField;
	private JTextField nValueTextField;
	private JTable searchTable;
	private JScrollPane scrollPane;
	private Component spacer40;
	private Component spacer10;
	
	public View() {
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
		BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(boxLayout);
		
		spacer40 = Box.createRigidArea(new Dimension(40, 0));
		spacer10 = Box.createRigidArea(new Dimension(10, 0));
		
		buildButton = new JButton("Build Inverted Indices");
		buildButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					View.this.delegate.handleBuildInvertedIndices();
					JOptionPane.showMessageDialog(null, "Inverted Indices Build! Execution time: " + Cloud.lastJobRuntime + "ms");
					View.this.presentActions();
				} catch (IOException | InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		panel.add(buildButton);
		
		searchActionButton = new JButton("Search For Term");
		searchActionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				View.this.presentSearch();
			}
		});
		
		topNActionButton = new JButton("Top-N");
		topNActionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				View.this.presentTopN();
			}
		});
		
		queryLabel = new JLabel("Query: ");
		queryTextField = new JTextField(12);
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					String term = queryTextField.getText();
					ArrayList<SearchResult> results = View.this.delegate.handleTermSearch(term);
					View.this.presentSearchResults(term, SearchResult.formatForTable(results));
				} catch (IOException | InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		nValueLabel = new JLabel("N-Value: ");
		nValueTextField = new JTextField(12);
		
		topNButton = new JButton("Go");
		topNButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					int n = Integer.parseInt(nValueTextField.getText());
					ArrayList<TopNResult> results = View.this.delegate.handleTopN(n);
					View.this.presentTopNResults(n, TopNResult.formatForTable(results));
				} catch (NumberFormatException | IOException | InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		backButton = new JButton("Back");
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				View.this.presentActions();
			}
		});
		
		searchBackButton = new JButton("Back");
		searchBackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				View.this.presentSearch();
			}
		});
		
		topNBackButton = new JButton("Back");
		topNBackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				View.this.presentTopN();
			}
		});
		
		frame = new JFrame("CS1600 Search Engine");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	public void presentActions() {
		panel.remove(buildButton);
		panel.remove(queryLabel);
		panel.remove(queryTextField);
		panel.remove(searchButton);
		panel.remove(nValueLabel);
		panel.remove(nValueTextField);
		panel.remove(topNButton);
		panel.remove(spacer10);
		panel.remove(backButton);
		panel.add(searchActionButton);
		panel.add(spacer10);
		panel.add(topNActionButton);
		frame.pack();
	}
	
	public void presentSearch() {
		removeActions();
		if (searchTermLabel != null) {
			panel.remove(searchTermLabel);
			panel.remove(spacer10);
			panel.remove(spacer40);
			panel.remove(searchExecutedLabel);
			panel.remove(scrollPane);
			panel.remove(searchBackButton);
		}
		panel.add(backButton);
		panel.add(spacer40);
		panel.add(queryLabel);
		panel.add(queryTextField);
		panel.add(spacer10);
		panel.add(searchButton);
		frame.pack();
	}
	
	private void presentSearchResults(String term, String[][] data) {
		panel.remove(backButton);
		panel.remove(spacer40);
		panel.remove(queryLabel);
		panel.remove(queryTextField);
		panel.remove(spacer10);
		panel.remove(searchButton);
		searchTermLabel = new JLabel("Term: " + term);
		panel.add(searchTermLabel);
		panel.add(spacer10);
		searchExecutedLabel = new JLabel("Execution: " + Cloud.lastJobRuntime + "ms");
		panel.add(searchExecutedLabel);
		panel.add(spacer40);
		searchTable = new JTable(data, new String[]{"Filename", "Path", "Occurances"});
		scrollPane = new JScrollPane(searchTable);
		searchTable.setFillsViewportHeight(true);
		panel.add(scrollPane);
		panel.add(searchBackButton);
		frame.pack();
	}
	
	private void presentTopNResults(int n, String[][] data) {
		panel.remove(backButton);
		panel.remove(spacer40);
		panel.remove(nValueLabel);
		panel.remove(nValueTextField);
		panel.remove(spacer10);
		panel.remove(topNButton);
		searchTermLabel = new JLabel("N: " + n);
		panel.add(searchTermLabel);
		panel.add(spacer10);
		searchExecutedLabel = new JLabel("Execution: " + Cloud.lastJobRuntime + "ms");
		panel.add(searchExecutedLabel);
		panel.add(spacer40);
		searchTable = new JTable(data, new String[]{"Word", "Occurances"});
		scrollPane = new JScrollPane(searchTable);
		searchTable.setFillsViewportHeight(true);
		panel.add(scrollPane);
		panel.add(searchBackButton);
		frame.pack();
	}

	public void presentTopN() {
		removeActions();
		if (searchTermLabel != null) {
			panel.remove(searchTermLabel);
			panel.remove(spacer10);
			panel.remove(spacer40);
			panel.remove(searchExecutedLabel);
			panel.remove(scrollPane);
			panel.remove(topNBackButton);
		}
		panel.add(backButton);
		panel.add(spacer40);
		panel.add(nValueLabel);
		panel.add(nValueTextField);
		panel.add(spacer10);
		panel.add(topNButton);
		frame.pack();
	}
	
	private void removeActions() {
		panel.remove(searchActionButton);
		panel.remove(spacer10);
		panel.remove(topNActionButton);
	}
}
