import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Parse {
	private Classify cdc = null;

	public Parse(String propFile) {
		cdc = new Classify(propFile);
	}

	public void trainData() throws IOException {
		if (cdc.globalFlags.loadClassifier == null) {
			// Otherwise we attempt to train one and exit if we don't succeed
			if (!cdc.trainClassifier()) {
				return;
			}
		}
	}

	public ArrayList<String> testResults(ArrayList<String> inputFile)
			throws IOException {
		String testFile = cdc.globalFlags.testFile;
		BufferedWriter bw ;
		if (inputFile != null && testFile != null) {
			
			bw = new BufferedWriter(new FileWriter(testFile));
			for(String t : inputFile){
				bw.write("\t"+t);
				bw.newLine();
			}
			bw.close();
			return cdc.testClassifier(testFile);
		}
		return null;
	}
	
	
	public ArrayList<String> sortResults(ArrayList<String> input) throws IOException {
		List<sorter> list = new ArrayList<sorter>();
		ArrayList<String> out = new ArrayList<String>();	
		for(String c : input){
		
			String[] tokens = c.trim().split("[\t]+");
			sorter obj = new sorter();
			obj.setStr(c);
			obj.setVal(Double.parseDouble(tokens[2]));
			list.add(obj);
		}
		
		Collections.sort(list, new sorter());
		
		for (sorter obj : list) {
			out.add(obj.getStr());
		}
		return out;
	} 

	

	public class sorter implements Comparator<sorter> {
		private String str;
		private Double val;

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}

		public Double getVal() {
			return val;
		}

		public void setVal(Double val) {
			this.val = val;
		}

		public int compare(sorter obj1, sorter obj2) {
			return -Double.compare(obj1.getVal(), obj2.getVal());
		}
	}

}
