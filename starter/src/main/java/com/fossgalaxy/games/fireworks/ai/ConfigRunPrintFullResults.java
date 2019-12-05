package com.fossgalaxy.games.fireworks.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Evolution.Rulebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

public class ConfigRunPrintFullResults {
	static String[] modes = { "Cross", "Mirror", "Mixed", "Pairwise" };
	static int modeNum;
	static String mode = modes[modeNum];
	static int minNumPlayers;
	static int maxNumPlayers; // Will play games of 2, 3, 4 and 5 players with a value of maxNumPlayers = 5
	static int numGames;
	static boolean includeBaselineAgents;
	static boolean rbBool;
	static ArrayList<ArrayList<Integer>> Chromossome = new ArrayList<ArrayList<Integer>>();
	static int[][] agentChromossomes;

	public static void main(String[] args) {
		readCSV();
		Rulebase rb = new Rulebase(rbBool);
		int[][] agentChromossomes = Chromossome.stream().map(u -> u.stream().mapToInt(i -> i).toArray())
				.toArray(int[][]::new);

		// This is the set of agents we are testing AGAINST
		// Note: This is the same testing pool used by Walton-Rivers
		String[] testPoolNames = { "RuleBasedIGGI", "RuleBasedInternal", "RuleBasedOuter", "SampleLegalRandom",
				"RuleBasedVanDeBergh", "RuleBasedFlawed", "RuleBasedPiers" };

		Vector<AgentPlayer> population = new Vector<AgentPlayer>();
		Vector<AgentPlayer> evolvedPopulation = new Vector<AgentPlayer>();
		Vector<AgentPlayer> testPool = new Vector<AgentPlayer>();
//		AgentPlayer newAgent = null;

//		String[] agentNames;//
		if (modeNum <= 1) {
			String[] agentNames = { "RuleBasedIGGI", "RuleBasedInternal", "RuleBasedOuter", "RuleBasedLegalRandom",
					"RuleBasedVanDeBergh", "RuleBasedFlawed", "RuleBasedPiers" };
			if (includeBaselineAgents) {
				for (String name : agentNames) {
					AgentPlayer newAgent = (new AgentPlayer(name,
							new HistogramAgent((ProductionRuleAgent) AgentUtils.buildAgent(name))));
					if (modeNum == 1) { // MIRROR
						population.add(newAgent);
					}
					if (modeNum == 0) { // CROSS
						evolvedPopulation.add(newAgent);
					}
				}
			}
		}
		if (modeNum >= 2) {
			String[] agentNames = { "RuleBasedIGGI", "RuleBasedInternal", "RuleBasedOuter", "SampleLegalRandom",
					"RuleBasedVanDeBergh", "RuleBasedFlawed", "RuleBasedPiers" };
			if (includeBaselineAgents) {
				for (String name : agentNames) {
					AgentPlayer newAgent = new AgentPlayer(name, AgentUtils.buildAgent(name));
					population.add(newAgent);
				}
			}
		}

		// Read agents from agentChromossomes and add them to the population
		for (int i = 0; i < agentChromossomes.length; i++) {
			int[] chromossome = agentChromossomes[i];
			Rule[] rules = new Rule[chromossome.length];
			for (int j = 0; j < chromossome.length; j++) {
				rules[j] = rb.ruleMapping(chromossome[j]);
			}
			HistogramAgent agent = rb.makeAgent(rules);
			population.add(new AgentPlayer("Agent " + i, agent));
		}
		/////////// FOR MIXED///////////////
		// Mixed
		int[] twoPlayerChromosomeMixed = { 67, 69, 62, 57, 46, 8, 33, 36, 19, 44, 54, 26 };
		int[] threePlusPlayerChromosomeMixed = { 46, 10, 7, 67, 45, 29, 4, 71, 17, 43, 19 };
		int[][] specializedChromosomeMixed = { twoPlayerChromosomeMixed, threePlusPlayerChromosomeMixed };
		SpecializedAgent specializedMixed = new SpecializedAgent(specializedChromosomeMixed, rb);
		// Mirror
		int[] twoPlayerChromosomeMirror = { 68, 7, 63, 8, 67, 66, 52, 35, 36, 51, 33, 13, 54, 16, 48, 37, 69, 19, 4, 5,
				44, 61, 27, 49, 34, 39, 18, 46, 0, 59, 65, 29, 3, 23, 70, 24, 58, 38, 42, 32, 6, 56, 9, 14, 41, 30, 11,
				45, 43, 21, 2, 15, 1, 50, 26, 62, 28, 57, 22, 64, 20, 10, 31, 55, 40, 47, 60, 71, 17, 53, 12, 25 };
		int[] threePlusPlayerChromosomeMirror = { 63, 41, 50, 7, 67, 29, 32, 0, 39, 35, 54, 38, 13, 45, 16, 40, 55, 19,
				46, 12, 58, 25, 24, 14, 56, 49, 61, 28, 44, 2, 53, 20, 60, 27, 43, 66, 68, 33, 15, 23, 1, 3, 71, 30, 21,
				69, 52, 59, 6, 36, 31, 51, 9, 11, 37, 17, 10, 5, 70, 62, 42, 22, 8, 48, 18, 34, 65, 4, 47, 26, 64, 57 };
		int[][] specializedChromosomeMirror = { twoPlayerChromosomeMirror, threePlusPlayerChromosomeMirror };
		SpecializedAgent specializedMirror = new SpecializedAgent(specializedChromosomeMirror, rb);
		////////////////////////////////////
		Vector<Map<Integer, Vector<Double>>> populationResults = null;
		Vector<Vector<Map<Integer, Vector<Double>>>> populationResultsMixed = null;
		Vector<Double> populationResultsPair = null;
		if (modeNum == 3) {// PAIRWISE
			populationResultsPair = NewTestSuite.ConstantNumberPlayersTest(2, numGames, population.get(0),
					population.get(1), new Random());
			System.out.println(Utils.Utils.getMean(populationResultsPair));
			return;
		}

		if (modeNum == 1) { // MIRROR
			populationResults = NewTestSuite.mirrorPopulationEvaluation(population, minNumPlayers, maxNumPlayers,
					numGames);
		}
		if (modeNum == 0) { // CROSS
			populationResults = NewTestSuite.crossPopulationEvaluation(population, evolvedPopulation, minNumPlayers,
					maxNumPlayers, numGames);
		}
		if (modeNum == 2) { // MIXED
			population.add(new AgentPlayer("Specialized Mixed", specializedMixed));
			population.add(new AgentPlayer("Specialized Mirror", specializedMirror));
			for (String other : testPoolNames) {
				AgentPlayer otherAgent = new AgentPlayer(other, AgentUtils.buildAgent(other));
//				System.out.println("OTHER AGENT: "+otherAgent);
				testPool.add(otherAgent);
			}
			populationResultsMixed = NewTestSuite.mixedPopulationEvaluation(population, testPool, minNumPlayers, maxNumPlayers, numGames);
			System.out.println("Printing full match list in mirror mode:");
			for (int playerID = 0; playerID < populationResultsMixed.size(); playerID++) {
				
				double sum = 0;
				int numMatches = 0;
				String playerName = population.get(playerID).toString();
				Vector<Map<Integer, Vector<Double>>> playerResults = populationResultsMixed.get(playerID);
				for (int partnerID = 0; partnerID < playerResults.size(); partnerID++) {

					Map<Integer, Vector<Double>> pairingResults = playerResults.get(partnerID);
					for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
						Vector<Double> matchResults = pairingResults.get(gameSize);
						for (double result : matchResults) {
//							System.out.println(playerID + "," + partnerID + "," + gameSize + "," + result);
							sum += result;
							numMatches++;
						}
					}
				}
			}
		}

		if (modeNum <= 1) {
			System.out.println("Printing full match list in mirror mode:");
			for (int playerID = 0; playerID < populationResults.size(); playerID++) {
				Map<Integer, Vector<Double>> playerResults = populationResults.get(playerID);
				String playerName = population.get(playerID).toString();
				for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
					Vector<Double> matchResults = playerResults.get(gameSize);
					for (double result : matchResults) {
//						System.out.println(playerID + ",self," + gameSize + "," + result);
					}
				}
			}
		}

		boolean printSummary = true;
		if (printSummary) {
			System.out.println("");
			System.out.println("Summary of results by player:");

			double maxScore = -1;
			int bestPlayer = -1;
			if (modeNum <= 1) {
				for (int playerID = 0; playerID < populationResults.size(); playerID++) {
					System.out.println("Summary for player " + playerID);
					Vector<Double> individualResults = new Vector<Double>();
					Map<Integer, Vector<Double>> playerResults = populationResults.get(playerID);

					String playerName = population.get(playerID).toString();

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
							System.out.println("   Player " + playerID + " has a mean score of " + sum / n
									+ " in games of size " + gameSize);
						}
					}
					double mean = Utils.Utils.getMean(individualResults);
					System.out.println("Player " + playerID + "'s mean score: " + mean);
					double SD = Utils.Utils.getStandardDeviation(individualResults);
					System.out.println("SD: " + SD + " SEM: " + SD / Math.sqrt(individualResults.size()));

					if (mean > maxScore) {
						maxScore = mean;
						bestPlayer = playerID;
					}
					System.out.println("");
				}
			} else if (modeNum == 2) {
				for (int playerID = 0; playerID < populationResultsMixed.size(); playerID++) {
					System.out.println("Summary for player " + playerID);
					String playerName = population.get(playerID).toString();
					Vector<Map<Integer, Vector<Double>>> playerResults = populationResultsMixed.get(playerID);
					Vector<Double> individualResults = new Vector<Double>();

					double[] sumByGameSize = new double[6];
					int[] countByGameSize = new int[6];

					for (int partnerID = 0; partnerID < playerResults.size(); partnerID++) {
						Map<Integer, Vector<Double>> pairingResults = playerResults.get(partnerID);
						for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
							Vector<Double> matchResults = pairingResults.get(gameSize);
							for (double result : matchResults) {
								sumByGameSize[gameSize] += result;
								countByGameSize[gameSize] += 1;
								individualResults.add(result);
							}
						}
					}
					for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
						int n = countByGameSize[gameSize];
						if (n != 0) {
							double sum = sumByGameSize[gameSize];
							System.out.println("   Player " + playerID + " has a mean score of " + sum / n
									+ " in games of size " + gameSize);
						}
					}
					double mean = Utils.Utils.getMean(individualResults);
					System.out.println("Player " + playerID + "'s mean score: " + mean);
					double SD = Utils.Utils.getStandardDeviation(individualResults);
					System.out.println("SD: " + SD + " SEM: " + SD / Math.sqrt(individualResults.size()));
					if (mean > maxScore) {
						maxScore = mean;
						bestPlayer = playerID;
					}
					System.out.println("");
				}
			}

			System.out.println("Best overall player is " + bestPlayer + " with a mean score of " + maxScore
					+ " over all game sizes");
			if (modeNum <= 1) {
				for (AgentPlayer agent : population) {
					Agent a = agent.policy;
					if (a instanceof HistogramAgent) {
						HistogramAgent h = (HistogramAgent) a;
//						h.printHistogram();
					}
				}
			}
		}
	}// end of the main fun

	public static void readCSV() {
		String basePath = new File("").getAbsolutePath();
		String pathFile = basePath.concat("/csvfiles/paths.csv");
		String csvFile = basePath.concat("/csvfiles/").concat(mode).concat(".csv");
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		//READING PATHS.CSV, FOR MODENUM ONLY
		try {
			br = new BufferedReader(new FileReader(pathFile));
			while ((line = br.readLine()) != null) {
				String[] conf = line.split(cvsSplitBy);
				if (conf[0].equals("currentMode")) {
					modeNum = Integer.parseInt(conf[1]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] conf = line.split(cvsSplitBy);
				if (conf[0].equals("minNumPlayers")) {
					minNumPlayers = Integer.parseInt(conf[1]);
				} else if (conf[0].equals("MODE")) {
					mode = conf[1];
					System.out.println("MODE :" + mode);
				} else if (conf[0].equals("maxNumPlayers")) {
					maxNumPlayers = Integer.parseInt(conf[1]);
				} else if (conf[0].equals("numGames")) {
					numGames = Integer.parseInt(conf[1]);
				} else if (conf[0].equals("includeBaselineAgents")) {
					includeBaselineAgents = Boolean.parseBoolean(conf[1]);
				} else if (conf[0].equals("rb")) {
					rbBool = Boolean.parseBoolean(conf[1]);
				} else if (conf[0].equals("agentChromossomes")) {
					ArrayList<Integer> curChromo = new ArrayList<Integer>();
//					System.out.println(line);
					for (int a = 1; a < conf.length; a++) {
						curChromo.add(Integer.parseInt(conf[a]));
					}
					Chromossome.add(curChromo);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
