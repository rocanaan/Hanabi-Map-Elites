package PostSimulationAnalyses;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;

import MapElites.ActionDatabaseEntry;
import MapElites.ReportAgent;

public class CalculateActionSimilarity {
	
	public static void main(String[] args) {
		String inputFileNameDatabase = "/Users/rodrigocanaan/Dev/HanabiResults/ActionDatabase/1M20190511_054448";
		String chromosomeFileName = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/ChromosomesRun1M";
		String chromosomeFileName2 = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/Run1Copy";

		int sizeDim1 = 20;
		int sizeDim2 = 20;

		
		int[][] validMask = GetStateActionArchiveFromFile.getValidMaskFromFile(chromosomeFileName,sizeDim1,sizeDim2);
		ArrayList<ReportAgent> agents = GetStateActionArchiveFromFile.makeReportAgentsFromFile(chromosomeFileName, sizeDim1, sizeDim2, false);
		Agent[][] a = new Agent[sizeDim1][sizeDim2];
		for (int i= 0; i<sizeDim1; i++) {
			for(int j=0; j<sizeDim2; j++) {
				a[i][j] = (Agent) agents.get(j+sizeDim1*i);
			}
		}
		
		
		
		
		ArrayList<ActionDatabaseEntry> database = getStateActionDatabase(inputFileNameDatabase);
		int count = getCount(database, validMask);
		System.out.println("Count: " + count);

		// Intra
		int[][][][] similarityMatchups = getActionSimilarityMatchups(database, a, validMask, sizeDim1, sizeDim2);
		printHistogram(similarityMatchups,validMask,sizeDim1,sizeDim2,count);
		
		
		//Cross
//		int[][] validMask2 = GetStateActionArchiveFromFile.getValidMaskFromFile(chromosomeFileName2,sizeDim1,sizeDim2);
//		ArrayList<ReportAgent> agents2 = GetStateActionArchiveFromFile.makeReportAgentsFromFile(chromosomeFileName2, sizeDim1, sizeDim2, false);
//		Agent[][] a2 = new Agent[sizeDim1][sizeDim2];
//		for (int i= 0; i<sizeDim1; i++) {
//			for(int j=0; j<sizeDim2; j++) {
//				a2[i][j] = (Agent) agents2.get(j+sizeDim1*i);
//			}
//		}
//		
//		int[][] cross = getCrossPopulationSimilarity(database, a, validMask, a2, validMask2, sizeDim1, sizeDim2);
//		printCrossPopulationSimilarity(cross,sizeDim1,sizeDim2,count);
		
		
	}
	
	public static void printHistogram (int[][][][] matchups, int[][]validMask, int sizeDim1, int sizeDim2, int count) {
		int[] histogram = new int[sizeDim1+sizeDim2];
		int[] countOcurrences = new int[sizeDim1+sizeDim2];


		for(int i = 0; i< sizeDim1; i++) {
			for(int j = 0; j< sizeDim2; j++) {
				for(int m = 0; m< sizeDim1; m++) {
					for(int n = 0; n< sizeDim2; n++) {
						if (i<=m || j<=n) {
							if (validMask[i][j] == 1 && validMask[m][n] == 1) {
								int distance = Math.abs(i-m)+ Math.abs(j-n);
								histogram[distance] += matchups[i][j][m][n];
								countOcurrences[distance] += 1;
							}
						}
					}
				}
			}
		}
		
		for (int distance=0; distance<sizeDim1+sizeDim2; distance++) {
			double mean = (double) histogram[distance] / (double) (countOcurrences[distance]*count);
			System.out.println(countOcurrences[distance] + " Ocurrences at distance " + distance + " With a total of " + histogram[distance] + " and mean "+mean);
		}
		
		for (int distance=0; distance<sizeDim1+sizeDim2; distance++) {
			double mean = (double) histogram[distance] / (double) (countOcurrences[distance]*count);
			System.out.println(distance + " " + mean);
		}
		

	}
	
	public static int getCount(ArrayList<ActionDatabaseEntry> database, int[][] validMask) {
		int count = 0;
		for (ActionDatabaseEntry entry : database) {
			if (validMask[entry.dim1][entry.dim2] == 1) {
				count+=entry.stateActionArchive.size();
			}
		}
		return count;
	}
	
	public static ArrayList<ActionDatabaseEntry> getStateActionDatabase(String inputFileName) {
		ArrayList<ActionDatabaseEntry> database = new ArrayList<ActionDatabaseEntry>();
		  try
		  {    
		      // Reading the object from a file 
		      FileInputStream file = new FileInputStream(inputFileName); 
		      ObjectInputStream in = new ObjectInputStream(file); 
		        
		      // Method for deserialization of object 
		      database = (ArrayList<ActionDatabaseEntry>)in.readObject(); 
		        
		      in.close(); 
		      file.close(); 
		    
		
		  } 
		  catch(IOException ex) 
		  { 
		      System.err.println("Failed to deserialize database"); 
		      System.err.println(ex);
		  } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		  } 
		return database;
	}
	
	public static void printCrossPopulationSimilarity(int[][] crossSimilarityMatchups, int sizeDim1, int sizeDim2, int count) {
		for(int i = 0; i< sizeDim1; i++) {
			for(int j = 0; j< sizeDim2; j++) {
				double mean = (double)crossSimilarityMatchups[i][j]/(double) count;
				System.out.print(mean + " ");
			}
			System.out.println("");
		}		
	}
	

	
	public static int[][] getCrossPopulationSimilarity (ArrayList<ActionDatabaseEntry> database, Agent[][] agents1, int[][] validMask1,
			Agent[][] agents2, int[][] validMask2,
			int sizeDim1, int sizeDim2){
		int[][] crossSimilarityMatchups = new int[sizeDim1][sizeDim2];
		
		for (ActionDatabaseEntry entry:database) {
			System.out.println("Checking gamestates collected by entry "+ entry.dim1 + " " + entry.dim2);
			if(validMask1[entry.dim1][entry.dim2]==1 && validMask2[entry.dim1][entry.dim2]==1) {	
				for(StateActionPair sap:entry.stateActionArchive) {
					GameState state = sap.state;
					int agentID = sap.agentID;
					for(int i = 0; i< sizeDim1; i++) {
						for(int j = 0; j< sizeDim2; j++) {
							Action action1;
							Action action2;
							try {
								action1 =agents1[i][j].doMove(agentID, state);
							}
							catch (Exception e){
								action1 =null;
							}
							try {
								action2 =agents2[i][j].doMove(agentID, state);
							}
							catch(Exception e) {
								action2 = null;
							}
							if (action1==null && action2==null) {
								crossSimilarityMatchups[i][j] +=1;
							}
							else if (action1!=null && action1.equals(action2)) {
								crossSimilarityMatchups[i][j] +=1;
							}
						}
					}
				}	
			}
		}
		return crossSimilarityMatchups;
	}
	
	//TODO this could generalize to hamming distance by just changing the function
	public static int[][][][] getActionSimilarityMatchups (ArrayList<ActionDatabaseEntry> database, Agent[][] agents, int[][] validMask, int sizeDim1, int sizeDim2){
		int[][][][] actionSimilarityMatchups = new int[sizeDim1][sizeDim2][sizeDim1][sizeDim2];
		
		for (ActionDatabaseEntry entry:database) {
			System.out.println("Checking gamestates collected by entry "+ entry.dim1 + " " + entry.dim2);
			if(validMask[entry.dim1][entry.dim2]==1) {	
				for(StateActionPair sap:entry.stateActionArchive) {
					GameState state = sap.state;
					int agentID = sap.agentID;
					
					Action[][] agentActions = new Action[sizeDim1][sizeDim2];
					for(int i = 0; i< sizeDim1; i++) {
						for(int j = 0; j< sizeDim2; j++) {
							try {
								agentActions[i][j] = agents[i][j].doMove(agentID,state);
							}
							catch (Exception e){
								agentActions[i][i] = null;
							}
						}
					}
					
					for(int i = 0; i< sizeDim1; i++) {
						for(int j = 0; j< sizeDim2; j++) {
							for(int m = 0; m< sizeDim1; m++) {
								for(int n = 0; n< sizeDim2; n++) {
//									if (agentActions[i][j]!=null && agentActions[i][j].equals(agentActions[m][n]) && validMask[i][j] == 1 && validMask[m][n] ==1) {
									if (agentActions[i][j] == null) {
										if (agentActions[m][n] == null) {
											actionSimilarityMatchups[i][j][m][n] += 1;

										}
									}
									else if (agentActions[i][j].equals(agentActions[m][n]) && validMask[i][j] == 1 && validMask[m][n] ==1) {
										actionSimilarityMatchups[i][j][m][n] += 1;
									}
								}
							}
						}
					}
		
				}
			}
		}
		return actionSimilarityMatchups;
	}

}
