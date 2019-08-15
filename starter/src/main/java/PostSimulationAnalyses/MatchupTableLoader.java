package PostSimulationAnalyses;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MatchupTableLoader {
	
	
	
	private static MatchupTable getMatchupTable(String fileName){
		int[][] dim1 = null;
		int[][] dim2 = null;
		try
        {    
			System.out.println("Deserializing " + fileName);
            // Reading the object from a file 
            FileInputStream file = new FileInputStream(fileName); 
            ObjectInputStream in = new ObjectInputStream(file); 
              
            // Method for deserialization of object 
            dim1 = (int[][])in.readObject(); 
            dim2 = (int[][])in.readObject();
              
            in.close(); 
            file.close(); 
             

        } 
          
        catch(IOException ex) 
        { 
            System.err.println("IOException is caught"); 
            System.err.println(ex);
        } 
          
        catch(ClassNotFoundException ex) 
        { 
            System.err.println("ClassNotFoundException is caught"); 
        } 
		return new MatchupTable (dim1,dim2);
	}
	
	private static class MatchupTable{
		public int[][] dim1BestPartner;
		public int[][] dim2BestPartner;
		
		public MatchupTable(int[][] dim1, int[][] dim2) {
			this.dim1BestPartner = dim1;
			this.dim2BestPartner = dim2;
		}
	}
	
	public static void main(String args[]) {
		String fileName = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/Reevaluation5INTRAPOPULATION20190803_025941";
		MatchupTable table = getMatchupTable(fileName);
		for (int i = 0; i< table.dim1BestPartner.length; i++) {
			for (int j = 0; j< table.dim1BestPartner[0].length; j++) {
				System.out.print("{"+table.dim1BestPartner[i][j]+","+table.dim2BestPartner[i][j]+"} ");
			}
			System.out.println("");
		}
	}
	
}
