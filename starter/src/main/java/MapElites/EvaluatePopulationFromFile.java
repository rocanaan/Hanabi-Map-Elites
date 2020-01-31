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
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.NewTestSuite;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import Evolution.Rulebase;

public class EvaluatePopulationFromFile {
	
	private class EstimatedBehavior{
		private double[] behaviorVector;
		public EstimatedBehavior(double[] behaviorVector) {
			this.behaviorVector = behaviorVector;
		}
	}

	public enum Mode {
		SIMPLE, CROSSPOPULATION, INTRAPOPULATION, GAUNTLET
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
		String fileName = "/Users/rodrigocanaan/Dev/MapElitesResults/2p1";
//		String fileName = "/Users/rodrigocanaan/Dev/MapElitesResults/5p/population999999";
//		String fileName = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/ChromosomesRun1M";
		String fileName2 = "/Users/rodrigocanaan/Dev/MapElitesResults/2p2";
		int sizeDim1 = 20;
		int sizeDim2 = 20;
		int numPlayers = 2;
		int minNumPlayers = numPlayers;
		int maxNumPlayers = numPlayers;
		int numGames = 5;
		boolean usePrecomputedResults = false; //If true, will read precomputed results from result file. If false, will load agents from agents file and compute.
//		int numGames = 1000;
//		boolean usePrecomputedResults = true; //If true, will read precomputed results from result file. If false, will load agents from agents file and compute.
		// TODO: This should bb extracted
		
		Mode mode  = Mode.INTRAPOPULATION;
		


		Vector<Map<Integer, Vector<Double>>> populationResults = null;
		Vector<Vector<Map<Integer, Vector<Double>>>> gauntletResults = null;

		Vector<AgentPlayer> agentPlayers = makeAgentsFromFile(fileName, sizeDim1, sizeDim2, rulebaseStandard);
		Vector<AgentPlayer> agentPlayers2 = null;
		if (mode == Mode.CROSSPOPULATION || mode == Mode.INTRAPOPULATION) {
			agentPlayers2 = makeAgentsFromFile(fileName2, sizeDim1, sizeDim2, rulebaseStandard);
		}

		// Precomputed
		if (usePrecomputedResults) {
			String inputFileName = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/ReevaluationINTRAPOPULATION20190512_030806";
			try {
				// Reading the object from a file
				FileInputStream file = new FileInputStream(inputFileName);
				ObjectInputStream in = new ObjectInputStream(file);

				// Method for deserialization of object
				populationResults = (Vector<Map<Integer, Vector<Double>>>) in.readObject();

				in.close();
				file.close();

				System.out.println("Object has been deserialized ");

			}

			catch (IOException ex) {
				System.out.println("IOException is caught");
			}

			catch (ClassNotFoundException ex) {
				System.out.println("ClassNotFoundException is caught");
			}
		}

		else {

			// Instantiate agents and run games
			agentPlayers = makeAgentsFromFile(fileName, sizeDim1, sizeDim2, rulebaseStandard);
			// agentPlayers2 = deserializeAgents(serialName);
			if (mode == Mode.CROSSPOPULATION || mode == Mode.INTRAPOPULATION) {
				agentPlayers2 = makeAgentsFromFile(fileName2, sizeDim1, sizeDim2, rulebaseStandard);
			}

			if (mode == Mode.GAUNTLET) {
				String[] testPoolNames = { "RuleBasedIGGI", "RuleBasedInternal", "RuleBasedOuter", "SampleLegalRandom",
						"RuleBasedVanDeBergh", "RuleBasedFlawed", "RuleBasedPiers" };
				Vector<AgentPlayer> gauntlet = new Vector<AgentPlayer>();
				for (String other : testPoolNames) {
					AgentPlayer otherAgent = new AgentPlayer(other, AgentUtils.buildAgent(other));
					gauntlet.add(otherAgent);
				}
				gauntletResults = NewTestSuite.mixedPopulationEvaluation(agentPlayers, gauntlet, minNumPlayers,
						maxNumPlayers, numGames);
			}

			if (mode == Mode.SIMPLE) {
				populationResults = NewTestSuite.mirrorPopulationEvaluation(agentPlayers, minNumPlayers, maxNumPlayers,
						numGames);
			}
			if (mode == Mode.CROSSPOPULATION) {
				populationResults = NewTestSuite.crossPopulationEvaluation(agentPlayers, agentPlayers2, minNumPlayers,
						maxNumPlayers, numGames);
			}

			if (mode == Mode.INTRAPOPULATION) {
				populationResults = NewTestSuite.intraPopulationEvaluation(agentPlayers, minNumPlayers, maxNumPlayers,
						numGames);
			}

			// Serialize results
			String outputFileName = "/Users/rodrigocanaan/Dev/MapElitesResults/WorkflowTest/EvaluateFromFile/ToG/" + numGames;
//					+ mode.toString();
//			String outputFileName = "/Users/EUGENE/Documents/Hanabi/Agents/Reevaluation" + numGames + mode.toString();
			String dateTime = Utils.Utils.getDateTimeString();
			try {
				FileOutputStream file = new FileOutputStream(outputFileName + dateTime); // TODO: There should bbe a
																							// class just to serialize,
																							// another to gather the
																							// data
				ObjectOutputStream out = new ObjectOutputStream(file);
				out.writeObject(populationResults);
				out.close();
				file.close();
				System.out.println("Object has been serialized");

			} catch (IOException ex) {
				System.err.println("Failed to serialize");
			}
		}

		// Printing results

		// TODO: Printing should be its own function
		// TODO: Trying to do too much by serializing and analyzing the data in the same
		// class
		if (mode == mode.GAUNTLET) {
			// TODO: GAUNTLET currently does not care about zero results
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j < sizeDim2; j++) {
					double sum = 0;
					int numMatches = 0;
					int playerID = j + sizeDim1 * i;
					Vector<Map<Integer, Vector<Double>>> playerResults = gauntletResults.get(playerID);

					for (int partnerID = 0; partnerID < playerResults.size(); partnerID++) {
						Map<Integer, Vector<Double>> pairingResults = playerResults.get(partnerID);
						for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
							Vector<Double> matchResults = pairingResults.get(gameSize);
							for (double result : matchResults) {
								// System.out.println(playerID + "," +partnerID + "," + gameSize + "," +
								// result);
								sum += result;
								numMatches++;
							}
						}
					}
					System.out.print((sum / numMatches) + " ");
				}
				System.out.println("");
			}

		} else if (mode != Mode.INTRAPOPULATION) {
			double maxScore = -1;
			int bestPlayer = -1;

			System.out.println("SIZE");
			System.out.println(populationResults.size());
			double averageSD = 0;
			double maxSD = 0;
			double minSD = 9999;
			int countNotNull = 0;

			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j < sizeDim2; j++) {
					int playerID = j + sizeDim1 * i;
					System.out.println("Summary for player " + i + "," + j);
					Vector<Double> individualResults = new Vector<Double>();
					Map<Integer, Vector<Double>> playerResults = populationResults.get(playerID);
					// String playerName = population.get(playerID).toString();
					for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
						double sum = 0;
						int n = 0;
						Vector<Double> matchResults = playerResults.get(gameSize);
						for (double result : matchResults) {
							individualResults.add(result);
							sum += result;
							n++;
						}
						if (n != 0) {
							System.out.println("   Player " + i + "," + j + " has a mean score of " + sum / n
									+ " in games of size " + gameSize);
						}
					}
					double mean = Utils.Utils.getMean(individualResults);
					if (mean > 0) {
						countNotNull++;
					}
					System.out.println("Player " + i + "," + j + "'s mean score: " + mean);
					double SD = Utils.Utils.getStandardDeviation(individualResults);
					if (SD > maxSD) {
						maxSD = SD;
					}
					if (SD < minSD) {
						minSD = SD;
					}
					averageSD += SD;
					System.out.println("SD: " + SD + " SEM: " + SD / Math.sqrt(individualResults.size()));
					//
					if (mean > maxScore) {
						maxScore = mean;
						bestPlayer = playerID;
					}
					System.out.println("");
				}
			}
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j < sizeDim2; j++) {
					int playerID = j + sizeDim1 * i;
					Vector<Double> individualResults = new Vector<Double>();
					Map<Integer, Vector<Double>> playerResults = populationResults.get(playerID);
					// String playerName = population.get(playerID).toString();
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
			averageSD = averageSD / countNotNull;
			System.out.println("Not null " + countNotNull);
			System.out.println("SD (min max avg) " + minSD + " " + maxSD + " " + averageSD);
			System.out.println(maxScore);
			System.out.println((int) (bestPlayer / sizeDim1));
			System.out.println(bestPlayer % sizeDim1);
		}

		else if (mode == Mode.INTRAPOPULATION) { // TODO: This should actually be m ranging from i to size, and n from j
													// to size
			double[][][][] matchupTable = new double[sizeDim1][sizeDim2][sizeDim1][sizeDim2];
			double[][][][] averageTable = new double[sizeDim1][sizeDim2][sizeDim1][sizeDim2];

			// First we do selfplay results for sanity (discard any agent with self-play of
			// zero)

			double[][] selfPlayTable = new double[sizeDim1][sizeDim2];
			int[][] validMask = new int[sizeDim1][sizeDim2];

			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j < sizeDim2; j++) {
					int index = getMatchupIndex(i, j, i, j, sizeDim1, sizeDim2);
					Vector<Double> scores = new Vector<Double>();
					for (Vector<Double> results : populationResults.get(index).values()) {
						for (double result : results) {
							scores.add(result);
						}
					}
					double score = Utils.Utils.getMean(scores);
					selfPlayTable[i][j] = score;
					if (score == 0) {
						validMask[i][j] = 0;
					} else {
						validMask[i][j] = 1;
					}

				}

			}

			double[][] averageAdHocScore = new double[sizeDim1][sizeDim2];
			double[][] averageNaiveScore = new double[sizeDim1][sizeDim2];
			int[][] dim1BestPair = new int[sizeDim1][sizeDim2];
			int[][] dim2BestPair = new int[sizeDim1][sizeDim2];
			int[][] countBestPartner = new int[sizeDim1][sizeDim2];

			double global_max = 0;
			int i_global_max = 0;
			int j_global_max = 0;
			int m_global_max = 0;
			int n_global_max = 0;

			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j < sizeDim2; j++) {
					if (selfPlayTable[i][j] == 0) {
						averageAdHocScore[i][j] = 0;
					} else {
						double averagePairwiseScore = 0;
						double averageNaiveMatchup = 0;
						int count = 0;
						double max = 0;
						int m_max = 0;
						int n_max = 0;
						for (int m = 0; m < sizeDim1; m++) {
							for (int n = 0; n < sizeDim2; n++) {
								if (selfPlayTable[m][n] != 0) {
									double score = 0;
									if (matchupTable[i][j][m][n] == 0) {
										int index = getMatchupIndex(i, j, m, n, sizeDim1, sizeDim2);
										Vector<Double> scores = new Vector<Double>();
										for (Vector<Double> results : populationResults.get(index).values()) {
											for (double result : results) {
												scores.add(result);
											}
										}
										index = getMatchupIndex(m, n, i, j, sizeDim1, sizeDim2);
										for (Vector<Double> results : populationResults.get(index).values()) {
											for (double result : results) {
												scores.add(result);
											}
										}

										score = Utils.Utils.getMean(scores);
									} else {
										score = matchupTable[i][j][m][n];
									}

									averagePairwiseScore += score;
									averageNaiveMatchup += (selfPlayTable[i][j] + selfPlayTable[m][n]) / 2;
									count += 1;
									if (score > max) {
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
									averageTable[i][j][m][n] = (selfPlayTable[i][j] + selfPlayTable[m][n]) / 2;
									averageTable[m][n][i][j] = (selfPlayTable[i][j] + selfPlayTable[m][n]) / 2;

									// System.out.println ("[" +i + ","+j+"] ["+m+","+n+"] index = " + index + "
									// score = " + score);
								} else {
									matchupTable[i][j][m][n] = 0;
									matchupTable[m][n][i][j] = 0;
									averageTable[i][j][m][n] = 0;
									averageTable[m][n][i][j] = 0;
								}
							}
						}

						if (count > 0) {
							averageNaiveMatchup = averageNaiveMatchup / count;
							averagePairwiseScore = averagePairwiseScore / count;
							averageAdHocScore[i][j] = averagePairwiseScore;
							averageNaiveScore[i][j] = averageNaiveMatchup;
						}
						dim1BestPair[i][j] = m_max;
						dim2BestPair[i][j] = n_max;
						countBestPartner[m_max][n_max] += 1;

					}
				}

			}

			System.out.println("Score by distance");
			PostSimulationAnalyses.CalculateActionSimilarity.printHistogram(matchupTable, validMask, sizeDim1, sizeDim2,
					1);
			System.out.println("Naive Score by distance");
			PostSimulationAnalyses.CalculateActionSimilarity.printHistogram(averageTable, validMask, sizeDim1, sizeDim2,
					1);

			System.out.println("Global max is " + global_max + " between agents [" + i_global_max + "," + j_global_max
					+ "] and [" + m_global_max + "," + n_global_max + "]");
			System.out.println("For comparison, the self play score for [" + i_global_max + "," + j_global_max + "] is "
					+ matchupTable[i_global_max][j_global_max][i_global_max][j_global_max]);
			System.out.println("For comparison, the self play score for [" + m_global_max + "," + n_global_max + "] is "
					+ matchupTable[m_global_max][n_global_max][m_global_max][n_global_max]);
			// TODO: Use games across the diagonal

			// Print averages
			System.out.println("Printing averages");
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j < sizeDim2; j++) {
					System.out.print(averageAdHocScore[i][j] + " ");
				}
				System.out.println("");
			}

			System.out.println("Printing naive averages");
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j < sizeDim2; j++) {
					System.out.print(averageNaiveScore[i][j] + " ");
				}
				System.out.println("");
			}

			System.out.println("Printing best partner");
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j < sizeDim2; j++) {
					System.out.print("{" + dim1BestPair[i][j] + "," + dim2BestPair[i][j] + "} ");
				}
				System.out.println("");
			}

			System.out.println("Printing count best partner");
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j < sizeDim2; j++) {
					System.out.print(countBestPartner[i][j] + " ");
				}
				System.out.println("");
			}

			System.out.println("Doing matchups based on best pair");
			System.out.println("In this population:");
			double sum = 0;
			int count = 0;
			for (int i = 0; i < sizeDim1; i++) {
				for (int j = 0; j < sizeDim2; j++) {
					double score = 0;
					if (validMask[i][j] > 0) {
						int dim1 = dim1BestPair[i][j];
						int dim2 = dim2BestPair[i][j];
						score = matchupTable[i][j][dim1][dim2];
						sum += score;
						count += 1;
					}
					System.out.print(score + " ");
				}
				System.out.println("");
			}
			System.out.println((sum / count));
			//TODO: extract this out into an across populations method
//			System.out.println("Across populations:");
//			sum = 0;
//			count = 0;
//			Random random = new Random();
//			for (int i = 0; i < sizeDim1; i++) {
//				for (int j = 0; j < sizeDim2; j++) {
//					double thisScore = 0;
//					int thisCount = 0;
//					if (validMask[i][j] > 0) {
//						int dim1 = dim1BestPair[i][j];
//						int dim2 = dim2BestPair[i][j];
//						AgentPlayer bestPartner = agentPlayers.get(dim2 + sizeDim1 * dim1);
//						AgentPlayer alternateSubject = agentPlayers2.get(j + sizeDim1 * i);
//						Vector<Double> results = NewTestSuite.ConstantNumberPlayersTest(2, numGames, bestPartner,
//								alternateSubject, random);
//						for (double score : results) {
//							thisScore += score;
//							thisCount += 1;
//						}
//						thisScore = thisScore / thisCount;
//						sum += thisScore;
//						count += 1;
//
//					}
//					System.out.print(thisScore + " ");
//				}
//				System.out.println("");
//			}
//			System.out.println((sum / count));

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
		return (n + sizeDim1 * m + sizeDim2 * sizeDim1 * j + sizeDim1 * sizeDim2 * sizeDim1 * i);
	}

	public static Vector<AgentPlayer> deserializeAgents(String fileName) {
		Rulebase rb = new Rulebase(false);
		Vector<AgentPlayer> agentPlayers = null;
		int[][][] agents = PostSimulationAnalyses.GenerationAnalyzer.readPopulationsFromFile(fileName);
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				int[] chromosome = agents[i][j];
				Rule[] agentRules = new Rule[chromosome.length];
				for (int geneIndex = 0; geneIndex < chromosome.length; geneIndex++) {
					agentRules[geneIndex] = rb.ruleMapping(chromosome[geneIndex]);
				}
				HistogramAgent histo;
				histo = rb.makeAgent(agentRules);
				ReportAgent agent = new ReportAgent(histo);
				agentPlayers.add(new AgentPlayer("report agent " + i + "," + j, agent));
			}
		}
		return agentPlayers;
	}
//<<<<<<< HEAD
//	
//	//TODO: Extract into a class that gets the integers from file, then one that builds the agents. See chromosome similarity analyzer
//	public static Vector<AgentPlayer> makeAgentsFromFile(String fileName, int sizeDim1, int sizeDim2, boolean rulebaseStandard) {
//        Rulebase rb = new Rulebase(rulebaseStandard);
//        String thisLine;
//        ArrayList<HistogramAgent> agents = new ArrayList<HistogramAgent>();
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(fileName));
//            while ((thisLine = br.readLine()) != null) {
//                //thisLine = thisLine.substring(1, thisLine.length() - 2);
//            		thisLine = thisLine.replaceAll(" ", "");
//            		thisLine = thisLine.replaceAll("\t", "");
//            		String[] c = thisLine.split(",");
//            		int[] chromossome = new int[c.length];
//                for (int i = 0; i < c.length; i++) {
//                    chromossome[i] = Integer.parseInt(c[i]);    
//                }
//               
//                Rule[] rules1 = new Rule[chromossome.length];
//                for (int i = 0; i < chromossome.length; i++) {
//
//                    if (rulebaseStandard) {
//                        rules1[i] = rb.ruleMapping(chromossome[i]);
//                    } else {
//                        rules1[i] = rb.ruleMapping(chromossome[i]);
//
//                    }
//
//                }
//                if (rulebaseStandard) {
//                    agents.add(rb.makeAgent(rules1));
//                } else {
//                    agents.add(rb.makeAgent(rules1));
//               }
//            }
//            br.close();
//        } catch (Exception e) {
//        		System.err.println(e);;
//        }
////		try
////        {    
////
////            // Reading the object from a file 
////            FileInputStream file = new FileInputStream(fileName); 
////
////            ObjectInputStream in = new ObjectInputStream(file); 
////
////            // Method for deserialization of object 
////            int[][][] chromosomes  = (int[][][])in.readObject(); 
////            
////            for (int[][] v : chromosomes) {
////            	for (int[] c : v) {
////
////                  
////                   Rule[] rules1 = new Rule[c.length];
////                   for (int i = 0; i < c.length; i++) {
////                       rules1[i] = rb.ruleMapping(c[i]);
////                       System.out.print(c[i] + " ");
////                   }
////                   agents.add(rb.makeAgent(rules1));
////                   System.out.println("");
////            	}
////            
////            }
////            
////            System.out.println(chromosomes);
////              
////            in.close(); 
////            file.close(); 
////              
////            System.out.println("Object has been deserialized "); 
////
////        } 
////          
////        catch(IOException ex) 
////        { 
////            System.out.println(ex); 
////        } 
////          
////        catch(ClassNotFoundException ex) 
////        { 
////            System.out.println("ClassNotFoundException is caught"); 
////        } 
//      
//=======

	// TODO: Extract into a class that gets the integers from file, then one that
	// builds the agents. See chromosome similarity analyzer
	public static Vector<AgentPlayer> makeAgentsFromFile(String fileName, int sizeDim1, int sizeDim2,
			boolean rulebaseStandard) {
		Rulebase rb = new Rulebase(rulebaseStandard);
		String thisLine;
		boolean isBinary = false;

		ArrayList<HistogramAgent> agents = new ArrayList<HistogramAgent>();

		File aFile = new File(fileName);

		try {
			isBinary = isBinaryFile(aFile);
//			System.out.println("is that binary file? " + );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isBinary) {
			binaryFileParse(fileName, agents, rb);
		} else {
			textFileParse(fileName, agents, rb, rulebaseStandard);
		}


//        return agents;
//        ArrayList<HistogramAgent> agents = makeAgentsFromFile(fileName, rulebaseStandard);
		if (agents != null) {
			System.out.println("not null");
		} else {
			System.out.println("null");
		}

		Vector<AgentPlayer> agentPlayers = new Vector<AgentPlayer>();
		for (int i = 0; i < sizeDim1; i++) {
			for (int j = 0; j < sizeDim2; j++) {

				HistogramAgent ha = agents.get(j + sizeDim1 * i);
				ReportAgent ra = new ReportAgent(ha);
				agentPlayers.add(new AgentPlayer("report agent [ " + i + " , " + j + "]", ra));
			}
		}
		return agentPlayers;
	}

	static void binaryFileParse(String fileName, ArrayList<HistogramAgent> agents, Rulebase rb) {
		try {
			// Reading the object from a file
			FileInputStream file = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(file);
			// Method for deserialization of object
			int[][][] chromosomes = (int[][][]) in.readObject();
			for (int[][] v : chromosomes) {
				for (int[] c : v) {
					Rule[] rules1 = new Rule[c.length];
					for (int i = 0; i < c.length; i++) {
						rules1[i] = rb.ruleMapping(c[i]);
//						System.out.print(c[i] + " ");
					}
					agents.add(rb.makeAgent(rules1));
//					System.out.println("");
				}
			}
			System.out.println(chromosomes);
			in.close();
			file.close();
			System.out.println("Object has been deserialized ");
//			return true;
		} catch (IOException ex) {
			System.out.println(ex);
		} catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException is caught");
		}
	}

	static void textFileParse(String fileName, ArrayList<HistogramAgent> agents, Rulebase rb,
			boolean rulebaseStandard) {
		try {
			String thisLine;
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			while ((thisLine = br.readLine()) != null) {
				// thisLine = thisLine.substring(1, thisLine.length() - 2);
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
//			return true;
		} catch (Exception e) {
			System.err.println(e);
//			return false;
		}
	}

	public static boolean isBinaryFile(File f) throws FileNotFoundException, IOException {
		FileInputStream in = new FileInputStream(f);
		int size = in.available();
		if (size > 1024)
			size = 1024;
		byte[] data = new byte[size];
		in.read(data);
		in.close();
		int ascii = 0;
		int other = 0;
		for (int i = 0; i < data.length; i++) {
			byte b = data[i];
			if (b < 0x09)
				return true;
			if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D)
				ascii++;
			else if (b >= 0x20 && b <= 0x7E)
				ascii++;
			else
				other++;
		}

		if (other == 0)
			return false;
		return 100 * other / (ascii + other) > 95;
	}
}
