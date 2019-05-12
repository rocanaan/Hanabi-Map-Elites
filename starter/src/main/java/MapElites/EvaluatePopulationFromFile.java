package MapElites;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.NewTestSuite;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;

import Evolution.Rulebase;

public class EvaluatePopulationFromFile {
	
	public enum Mode{
		SIMPLE, CROSSPOPULATION, INTRAPOPULATION
	}
	
//	public class Pairing{
//		int a1d1;
//		int a1d2;
//		int a2d1;
//		int a2d2;
//		
//		public Pairing(int a1d1,int a1d2, int a2d1, int a2d2) {
//			this.a1d1=a1d1;
//			this.a1d2=a1d2;
//			this.a2d1=a2d1;
//			this.a2d2=a2d2;
//		}
//	}
	

	public static void main(String[] args) {
		boolean rulebaseStandard = false;
		Rulebase rb = new Rulebase(rulebaseStandard);
		String fileName = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/ChromosomesRun1M";
		String fileName2 = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/Run1Copy";
		int sizeDim1 = 20;
		int sizeDim2 = 20;
		int numPlayers = 2;
		int minNumPlayers = numPlayers;
		int maxNumPlayers = numPlayers;
		int numGames = 100;
		boolean usePrecomputedResults = false; //If true, will read precomputed results from result file. If false, will load agents from agents file and compute.
		// TODO: This should bb extracted
		
		Mode mode  = Mode.CROSSPOPULATION;
		
		Vector<Map<Integer, Vector<Double>>> populationResults = null;

		if (usePrecomputedResults) {
			String inputFileName = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/Reevaluation20190510_201535";
			try
	        {    
	            // Reading the object from a file 
	            FileInputStream file = new FileInputStream(inputFileName); 
	            ObjectInputStream in = new ObjectInputStream(file); 
	              
	            // Method for deserialization of object 
	            populationResults = (Vector<Map<Integer, Vector<Double>>>)in.readObject(); 
	              
	            in.close(); 
	            file.close(); 
	              
	            System.out.println("Object has been deserialized "); 
	
	        } 
	          
	        catch(IOException ex) 
	        { 
	            System.out.println("IOException is caught"); 
	        } 
	          
	        catch(ClassNotFoundException ex) 
	        { 
	            System.out.println("ClassNotFoundException is caught"); 
	        } 
		}
		
		else {

			Vector<AgentPlayer> agentPlayers  = makeAgentsFromFile(fileName, sizeDim1, sizeDim2, rulebaseStandard);
			Vector<AgentPlayer> agentPlayers2 = null;
			if (mode == Mode.CROSSPOPULATION) {
				agentPlayers2 =  makeAgentsFromFile(fileName2, sizeDim1, sizeDim2, rulebaseStandard);
			}
	
			
			if (mode == Mode.SIMPLE) {
				populationResults = NewTestSuite.mirrorPopulationEvaluation(agentPlayers, minNumPlayers, maxNumPlayers, numGames);
			}
			if (mode == Mode.CROSSPOPULATION) {
				populationResults = NewTestSuite.crossPopulationEvaluation(agentPlayers, agentPlayers2, minNumPlayers, maxNumPlayers, numGames);
			}
			
			
			
			if (mode == Mode.INTRAPOPULATION) {
				populationResults = NewTestSuite.intraPopulationEvaluation(agentPlayers, minNumPlayers, maxNumPlayers, numGames);
			}
			
			String outputFileName = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/Reevaluation" + mode.toString();
			String dateTime = Utils.Utils.getDateTimeString();
			try {
				FileOutputStream file = new FileOutputStream(outputFileName+dateTime); // TODO: There should bbe a class just to serialize, another to gather the data
				ObjectOutputStream out = new ObjectOutputStream(file);
				out.writeObject(populationResults);
				out.close();
				file.close();
	            System.out.println("Object has been serialized"); 
	
			}
		    catch(IOException ex) 
	        { 
	            System.err.println("Failed to serialize"); 
	        } 
		}
		
		
		
		
  
		
		// TODO: Printing should be its own function
		// TODO: Trying to do too much by serializing and analyzing the data in the same class
		if (mode != Mode.INTRAPOPULATION) {
			double maxScore = -1;
			int bestPlayer = -1;
			
			System.out.println("SIZE");
			System.out.println(populationResults.size());
	
			for (int i = 0; i < sizeDim1; i++) {
				for(int j=0; j<sizeDim2; j++) {
					int playerID = j+sizeDim1*i;
					System.out.println("Summary for player " + i +"," +j);
					Vector<Double> individualResults = new Vector<Double>();
					Map<Integer, Vector<Double>> playerResults = populationResults.get(playerID);
					//String playerName = population.get(playerID).toString();
					for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
						double sum = 0;
						int n = 0;
						Vector<Double> matchResults = playerResults.get(gameSize);
						for (double result : matchResults) {
							individualResults.add(result);
							sum+=result;
							n++;
						}
						if(n!=0) {
							System.out.println("   Player " + i + "," + j + " has a mean score of " + sum/n + " in games of size " + gameSize);
						}
					}
					double mean = Utils.Utils.getMean(individualResults);
					System.out.println("Player " + i + "," + j + "'s mean score: " + mean );
					double SD = Utils.Utils.getStandardDeviation(individualResults);
					System.out.println("SD: " + SD + " SEM: " + SD/Math.sqrt(individualResults.size()));
	//				
					if (mean > maxScore) {
						maxScore = mean;
						bestPlayer = playerID;
					}	
					System.out.println("");
					

				}
			}
			for (int i = 0; i < sizeDim1; i++) {
				for(int j=0; j<sizeDim2; j++) {
					int playerID = j+sizeDim1*i;
					Vector<Double> individualResults = new Vector<Double>();
					Map<Integer, Vector<Double>> playerResults = populationResults.get(playerID);
					//String playerName = population.get(playerID).toString();
					for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
						Vector<Double> matchResults = playerResults.get(gameSize);
						for (double result : matchResults) {
							individualResults.add(result);
						}
					}
					double mean = Utils.Utils.getMean(individualResults);
					System.out.print(mean + " ");
				}
				System.out.println("");
			}
			System.out.println(maxScore);
			System.out.println((int)(bestPlayer/sizeDim1));
			System.out.println(bestPlayer%sizeDim1);
		}
		
		else if (mode == Mode.INTRAPOPULATION){ //TODO: This should actually be m ranging from i to size, and n from j to size
			double[][][][] matchupTable = new double[sizeDim1][sizeDim2][sizeDim1][sizeDim2];
			
			//First we do selfplay results for sanity (discard any agent with self-play of zero)
			
			double[][] selfPlayTable = new double[sizeDim1][sizeDim2];
			int[][] validMask = new int[sizeDim1][sizeDim2];

			
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j<sizeDim2; j++) {
					int index = getMatchupIndex(i,j,i,j,sizeDim1,sizeDim2);
					Vector<Double> scores = new Vector<Double>();
					for (Vector<Double> results: populationResults.get(index).values()) {
						for (double result: results) {
							scores.add(result);
						}
					}
					double score = 	Utils.Utils.getMean(scores);			
					selfPlayTable[i][j] = score;
					if (score == 0) {
						validMask[i][j] = 0;
					}
					else {
						validMask[i][j] = 1;
					}
					
				}

			}
			

			double [][] averageAdHocScore = new double[sizeDim1][sizeDim2];
			int [][] dim1BestPair = new int[sizeDim1][sizeDim2];
			int [][] dim2BestPair = new int[sizeDim1][sizeDim2];
			int [][] countBestPartner = new int[sizeDim1][sizeDim2];

			double global_max = 0;
			int i_global_max = 0;
			int j_global_max = 0;
			int m_global_max = 0;
			int n_global_max = 0;
			
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j<sizeDim2; j++) {
					if (selfPlayTable[i][j] ==0) {
						averageAdHocScore[i][j] = 0;
					}
					else{
						double average = 0;
						int count = 0;
						double max = 0;
						int m_max = 0;
						int n_max = 0;
						for (int m = 0; m < sizeDim1; m++) {
							for (int n = 0; n<sizeDim2; n++) {
								if (selfPlayTable[m][n] !=0) {
									double score = 0;
									if (matchupTable[i][j][m][n] == 0) {
										int index = getMatchupIndex(i,j,m,n,sizeDim1,sizeDim2);
										Vector<Double> scores = new Vector<Double>();
										for (Vector<Double> results: populationResults.get(index).values()) {
											for (double result: results) {
												scores.add(result);
											}
										}
										index = getMatchupIndex(m,n,i,j,sizeDim1,sizeDim2);
										for (Vector<Double> results: populationResults.get(index).values()) {
											for (double result: results) {
												scores.add(result);
											}
										}
										
										score = 	Utils.Utils.getMean(scores);			
									}
									else {
										score = matchupTable[i][j][m][n];
									}
		
									
									average += score;
									count+=1;
									if (score>max) {
										max = score;
										m_max = m;
										n_max = n;
									}
									
									if (score > global_max) {
										 global_max = score;
										i_global_max = i;
										j_global_max = j;
										m_global_max = m;
										n_global_max = n; 
									}
									
									matchupTable[i][j][m][n] = score;
									matchupTable[m][n][i][j] = score;
								
									//System.out.println ("[" +i + ","+j+"] ["+m+","+n+"] index = " + index + " score = " + score);
								}
								else {
									matchupTable[i][j][m][n] = 0;
									matchupTable[m][n][i][j] = 0;
								}
							}
						}

						if (count > 0) {
							average = average/count;
							averageAdHocScore[i][j] = average;
						}
						dim1BestPair[i][j] = m_max;
						dim2BestPair[i][j] = n_max;
						countBestPartner[m_max][n_max] +=1;
				
					}
				}
				
			}
			
			
			PostSimulationAnalyses.CalculateActionSimilarity.printHistogram(matchupTable, validMask, sizeDim1, sizeDim2, 1);
			
			System.out.println("Global max is " + global_max + " between agents ["+i_global_max+","+j_global_max+"] and ["+m_global_max+","+n_global_max+"]" );
			System.out.println("For comparison, the self play score for ["+i_global_max+","+j_global_max+"] is "+ matchupTable[i_global_max][j_global_max][i_global_max][j_global_max]);
			System.out.println("For comparison, the self play score for ["+m_global_max+","+n_global_max+"] is "+ matchupTable[m_global_max][n_global_max][m_global_max][n_global_max]);
			//TODO: Use games across the diagonal
			
			// Print averages
			System.out.println("Printing averages");
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j<sizeDim2; j++) {
					System.out.print(averageAdHocScore[i][j] + " ");
				}
				System.out.println("");
			}
			
			System.out.println("Printing best partner");
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j<sizeDim2; j++) {
					System.out.print("["+dim1BestPair[i][j]+","+dim2BestPair[i][j]+"] ");
				}
				System.out.println("");
			}
			
			System.out.println("Printing count best partner");
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j<sizeDim2; j++) {
					System.out.print(countBestPartner[i][j]+" ");
				}
				System.out.println("");
			}
			

			
			
		}
//		
//		System.out.println("Printing full match list in mirror mode:");
//		for (int i = 0; i < sizeDim1; i++) {
//			for(int j=0; j<sizeDim2;j++) {
//				int playerID = j+ sizeDim1*i;
//				Map<Integer, Vector<Double>> playerResults = populationResults.get(playerID);
//				String playerName = agentPlayers.get(playerID).toString();
//				for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
//					Vector<Double> matchResults = playerResults.get(gameSize);
//					for (double result : matchResults) {
//						System.out.println("Agent ["  + i + " , " + j + "]  : " + result);
//					}
//				}
//			}
//		}
//		
//        PopulationEvaluationSummary pes = null;
//        
//        
//		pes = TestSuite.mirrorPopulationEvaluation(agentPlayers, 2, 2, numGames);
//		
//		for (int i = 0; i<sizeDim1; i++) {
//			for (int j = 0; j<sizeDim2; j++) {
//				double score = pes.getScoreIndividualAgent(j + sizeDim1*i);
//				System.out.println("Score of individual ["+ i + "," + j + "] : " + score);
//			}
//		}
		
	}
	
	public static int getMatchupIndex(int i, int j, int m, int n, int sizeDim1, int sizeDim2) {
		return (n + sizeDim1*m + sizeDim2*sizeDim1*j +sizeDim1*sizeDim2*sizeDim1*i);	
	}
	
	
	//TODO: Extract into a class that gets the integers from file, then one that builds the agents. See chromosome similarity analyzer
	public static Vector<AgentPlayer> makeAgentsFromFile(String fileName, int sizeDim1, int sizeDim2, boolean rulebaseStandard) {
        Rulebase rb = new Rulebase(rulebaseStandard);
        String thisLine;
        ArrayList<HistogramAgent> agents = new ArrayList<HistogramAgent>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((thisLine = br.readLine()) != null) {
                //thisLine = thisLine.substring(1, thisLine.length() - 2);
            		thisLine = thisLine.replaceAll(" ", "");
            		thisLine = thisLine.replaceAll("\t", "");
            		String[] c = thisLine.split(",");
            		int[] chromossome = new int[c.length];
                for (int i = 0; i < c.length; i++) {
                    chromossome[i] = Integer.parseInt(c[i]);    
                }
               
                Rule[] rules1 = new Rule[chromossome.length];
                for (int i = 0; i < chromossome.length; i++) {

                    if (rulebaseStandard) {
                        rules1[i] = rb.ruleMapping(chromossome[i]);
                    } else {
                        rules1[i] = rb.ruleMapping(chromossome[i]);

                    }

                }
                if (rulebaseStandard) {
                    agents.add(rb.makeAgent(rules1));
                } else {
                    agents.add(rb.makeAgent(rules1));
               }
            }
            br.close();
        } catch (Exception e) {
        		System.err.println(e);;
        }
      
//        return agents;
//        ArrayList<HistogramAgent> agents = makeAgentsFromFile(fileName, rulebaseStandard);
		if(agents !=null) {
			System.out.println("not null");
		}
		else {
			System.out.println("null");
		}
		
		Vector<AgentPlayer> agentPlayers = new Vector<AgentPlayer>();
		for (int i = 0; i<sizeDim1; i++) {
			for (int j = 0; j<sizeDim2; j++) {
				
				HistogramAgent ha = agents.get(j + sizeDim1*i);
				agentPlayers.add(new AgentPlayer("report agent [ " + i + " , " + j + "]", ha));
			}
		}
		return agentPlayers;
    }
}
