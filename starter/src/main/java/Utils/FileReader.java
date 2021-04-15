package Utils;

import java.io.*;

public class FileReader {
	static int[][] getAgentsFromFile (String filename){
		
		try {
			FileInputStream fstream = new FileInputStream(filename);
			
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			while((strLine = br.readLine()) !=null) {
				System.out.println(strLine);
			}
			
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return null;
	}
	
	public static void  main(String args[]) {
		getAgentsFromFile("results");
	}
}
