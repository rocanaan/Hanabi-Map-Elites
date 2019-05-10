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
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.NewTestSuite;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.ai.username.HumanControlledAgent;

import Evolution.Rulebase;
import MapElites.StateActionPair;



public class GetStateActionArchiveFromFile {
	
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
		String fileName = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/Run1Copy";
		String fileName2 = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/Run2";
		int sizeDim1 = 20;
		int sizeDim2 = 20;
		int numPlayers = 2;
		int minNumPlayers = numPlayers;
		int maxNumPlayers = numPlayers;
		int numGames = 100;
		boolean useSameSeed = false; //TODO:
		
		Mode mode  = Mode.SIMPLE;
		
		ArrayList<ReportAgent> reportPlayers = makeReportAgentsFromFile(fileName, sizeDim1, sizeDim2, rulebaseStandard);
		Vector<AgentPlayer> agentPlayers  = makeAgentPlayersFromAgent(reportPlayers,sizeDim1,sizeDim2);
		Vector<AgentPlayer> agentPlayers2 = null;
		if (mode == Mode.CROSSPOPULATION) {
			agentPlayers2 =  makeAgentsFromFile(fileName2, sizeDim1, sizeDim2, rulebaseStandard);
		}

		Vector<Map<Integer, Vector<Double>>> populationResults = null;
		
		if (mode == Mode.SIMPLE) {
			populationResults = NewTestSuite.mirrorPopulationEvaluation(agentPlayers, minNumPlayers, maxNumPlayers, numGames, useSameSeed);
		}
		if (mode == Mode.CROSSPOPULATION) {
			populationResults = NewTestSuite.crossPopulationEvaluation(agentPlayers, agentPlayers2, minNumPlayers, maxNumPlayers, numGames);
		}
		
		
		
		if (mode == Mode.INTRAPOPULATION) {
			populationResults = NewTestSuite.intraPopulationEvaluation(agentPlayers, minNumPlayers, maxNumPlayers, numGames);
		}
		
		String outputFileName = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/20190508_2P_100_Different_Seeds";
		try {
			FileOutputStream file = new FileOutputStream(outputFileName); // TODO: There should bbe a class just to serialize, another to gather the data
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
		
		populationResults = null;
		
		try
        {    
            // Reading the object from a file 
            FileInputStream file = new FileInputStream(outputFileName); 
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
            System.err.println(ex);
        } 
          
        catch(ClassNotFoundException ex) 
        { 
            System.out.println("ClassNotFoundException is caught"); 
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
					double average = 0;
					int count = 0;
					double max = 0;
					int m_max = 0;
					int n_max = 0;
					for (int m = 0; m < sizeDim1; m++) {
						for (int n = 0; n<sizeDim2; n++) {
							int index = n + sizeDim1*m + sizeDim2*sizeDim1*j +sizeDim1*sizeDim2*sizeDim1*i;
							Vector<Double> scores = new Vector<Double>();
							for (Vector<Double> results: populationResults.get(index).values()) {
								for (double result: results) {
									scores.add(result);
								}
							}
							double score = 	Utils.Utils.getMean(scores);
							
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
						
							//System.out.println ("[" +i + ","+j+"] ["+m+","+n+"] index = " + index + " score = " + score);
						}
					}
					if (count >0) {
						average = average/count;
						averageAdHocScore[i][j] = average;
					}
					dim1BestPair[i][j] = m_max;
					dim2BestPair[i][j] = n_max;
					countBestPartner[m_max][n_max] +=1;
				}
				System.out.println("Global max is " + global_max + " between agents ["+i_global_max+","+j_global_max+"] and ["+m_global_max+","+n_global_max+"]" );
				
			}
			
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
		
		printStateActionDatabase(reportPlayers,sizeDim1,sizeDim2);
		
		System.out.println(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
          

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
	


	public static void printStateActionDatabase(ArrayList<ReportAgent> agents, int sizeDim1, int sizeDim2) {
		ArrayList<ActionDatabaseEntry> database = new ArrayList<ActionDatabaseEntry>();
		for (int i = 0; i<sizeDim1; i++) {
			for(int j = 0; j<sizeDim2; j++) {
				ReportAgent agent = agents.get(j + sizeDim1*i);
				ActionDatabaseEntry entry = new ActionDatabaseEntry(i,j,agent.getStateActionArchive());
				database.add(entry);
				
				for(StateActionPair sap: agent.getStateActionArchive()) {
//					System.out.println("State action pair for agent ["+i+","+j+"]:");
//					System.out.println(sap.state);
//					System.out.println(sap.action);
				}
				
			}
		}
		
		
		String outputFileName = "/Users/rodrigocanaan/Dev/HanabiResults/ActionDatabase/20190508_2P_100";
		try {
			FileOutputStream file = new FileOutputStream(outputFileName); // TODO: There should bbe a class just to serialize, another to gather the data
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(database);
			out.close();
			file.close();
            System.out.println("Database has been serialized"); 

		}
	    catch(IOException ex) 
        { 
            System.err.println("Failed to serialize database"); 
            System.err.println(ex);
        } 
		
		
		try
        {    
            // Reading the object from a file 
            FileInputStream file = new FileInputStream(outputFileName); 
            ObjectInputStream in = new ObjectInputStream(file); 
              
            // Method for deserialization of object 
            database = (ArrayList<ActionDatabaseEntry>)in.readObject(); 
              
            in.close(); 
            file.close(); 
              
            System.out.println("Object has been deserialized "); 
			for(ActionDatabaseEntry entry:database) {
				for(StateActionPair sap:entry.stateActionArchive) {
					System.out.println("De-serialized state action pair for agent ["+entry.dim1+","+entry.dim2+"]:");
					System.out.println(sap.state);
					HumanControlledAgent.showGameState(0, sap.state); //TODO: Think if methods in showGameState should really be static or should make another class for it
					System.out.println(sap.action);
				}
	
			}

        } 
	    catch(IOException ex) 
        { 
            System.err.println("Failed to deserialize database"); 
            System.err.println(ex);
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

		
		
		
	}
	
	public static ArrayList<ReportAgent> makeReportAgentsFromFile(String fileName, int sizeDim1, int sizeDim2, boolean rulebaseStandard) {
		 Rulebase rb = new Rulebase(rulebaseStandard);
	        String thisLine;
	        ArrayList<ReportAgent> agents = new ArrayList<ReportAgent>();
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
	                    agents.add(new ReportAgent(rb.makeAgent(rules1),true));
	                } else {
	                    agents.add(new ReportAgent(rb.makeAgent(rules1),true));
	               }
	            }
	            br.close();
	        } catch (Exception e) {
	        		System.err.println(e);;
	        }
	        return agents;
	}
	
	public static Vector<AgentPlayer> makeAgentPlayersFromAgent(ArrayList<ReportAgent> reportPlayers, int sizeDim1, int sizeDim2){
		Vector<AgentPlayer> agentPlayers = new Vector<AgentPlayer>();
		for (int i = 0; i<sizeDim1; i++) {
			for (int j = 0; j<sizeDim2; j++) {
				
				Agent agent = reportPlayers.get(j + sizeDim1*i);
				agentPlayers.add(new AgentPlayer("Agent [ " + i + " , " + j + "]", agent));
			}
		}
		return agentPlayers;
	}
	
	public static Vector<AgentPlayer> makeAgentsFromFile(String fileName, int sizeDim1, int sizeDim2, boolean rulebaseStandard) {
        Rulebase rb = new Rulebase(rulebaseStandard);
        String thisLine;
        ArrayList<ReportAgent> agents = new ArrayList<ReportAgent>();
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
                    agents.add(new ReportAgent(rb.makeAgent(rules1),true));
                } else {
                    agents.add(new ReportAgent(rb.makeAgent(rules1),true));
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
				
				ReportAgent ha = agents.get(j + sizeDim1*i);
				agentPlayers.add(new AgentPlayer("report agent [ " + i + " , " + j + "]", ha));
			}
		}
		return agentPlayers;
    }
}
