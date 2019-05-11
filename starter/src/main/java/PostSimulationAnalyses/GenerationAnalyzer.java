package PostSimulationAnalyses;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

// Methods to analyse the results of "generation" of map elites

public class GenerationAnalyzer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final File folder = new File("/Users/rodrigocanaan/Dev/HanabiResults/Evolution/20190507");
		
		Vector<String> filenames = getFileNamesFromFolder (folder);
		Map<Integer,int[][][]> populations = getPopulationsFromFolder(filenames);
		for (int generation: populations.keySet()) {
			int countNonZero = 0;
			int[][][] population = populations.get(generation);
			for (int i = 0; i<population.length;i++) {
				for (int j = 0; j<population[i].length;j++) {
					for (int k = 0; k<population[i][j].length;k++) {
						if (population[i][j][k]!=0) {
							countNonZero += 1;
							break;
						}
					}
				}
			}
			System.out.println(generation + " " + countNonZero);
		}
		
		//getMapsFromFolder(filenames);

	}
	
	
	public static Vector<String> getFileNamesFromFolder(final File folder) {
		Vector<String> vectorNames = new Vector<String>();
		int i = 0;
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            getFileNamesFromFolder(fileEntry);
	        } else {
	        		vectorNames.add(folder+"/"+fileEntry.getName());
	        }
	    }
	    return vectorNames;
	}
	
	public static Map<Integer, Double[][]> getMapsFromFolder(Vector<String> filenames){
		Map<Integer, Double[][]> result = new HashMap<Integer,Double[][]>();
		
		for (String name: filenames) {
			if (name.contains("map")){
				int generation = Integer.parseInt(name.split("map")[1]);
				System.out.println(generation);
				double[][] map = readMapFromFile(name);
				for (int i=0 ; i<20;i++) {
					for (int j = 0; j<20; j++) {
						System.out.println(map[i][j]);
					}
				}
			}
		}
		return result;
		
	}
	
	public static Map<Integer, int[][][]> getPopulationsFromFolder(Vector<String> filenames){
		Map<Integer, int[][][]> result = new HashMap<Integer,int[][][]>();
		
		for (String name: filenames) {
			if (name.contains("population")){
				int generation = Integer.parseInt(name.split("population")[1]);
				System.out.println(generation);
				int[][][] population = readPopulationsFromFile(name);
				result.put(generation,population);
			}
		}
		return result;
		
	}
	
	public static double[][] readMapFromFile (String fileName){
		
		try
        {    
			System.out.println("Deserializing " + fileName);
            // Reading the object from a file 
            FileInputStream file = new FileInputStream(fileName); 
            ObjectInputStream in = new ObjectInputStream(file); 
              
            // Method for deserialization of object 
            double[][] map = (double[][])in.readObject(); 
              
            in.close(); 
            file.close(); 
              
            System.out.println("Map has been deserialized "); 
            return map;

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
		return null;
	}
	
	public static int[][][] readPopulationsFromFile (String fileName){
		
		try
        {    
			System.out.println("Deserializing " + fileName);
            // Reading the object from a file 
            FileInputStream file = new FileInputStream(fileName); 
            ObjectInputStream in = new ObjectInputStream(file); 
              
            // Method for deserialization of object 
            int[][][] population = (int[][][])in.readObject(); 
              
            in.close(); 
            file.close(); 
              
            System.out.println("Population has been deserialized "); 
            return population;

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
		return null;
	}



}
