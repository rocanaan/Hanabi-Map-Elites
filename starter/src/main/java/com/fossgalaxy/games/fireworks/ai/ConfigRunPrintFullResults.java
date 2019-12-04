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
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

public class ConfigRunPrintFullResults {
	static String[] modes = { "Cross", "Mirror", "Mixed", "Pairwise" };
	static String mode = modes[0];
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
		String[] agentNames = { "RuleBasedIGGI", "RuleBasedInternal", "RuleBasedOuter", "RuleBasedLegalRandom",
				"RuleBasedVanDeBergh", "RuleBasedFlawed", "RuleBasedPiers" };

		Vector<AgentPlayer> population = new Vector<AgentPlayer>();

//		switch (mode) {
//		case "Cross":
//			System.out.println("Cross");
//			
//			break;
//		case "Mirror":
//			System.out.println("Mirror");
//			
//			break;
//		case "Mixed":
//			System.out.println("Mixed");
//			break;
//		case "Pairwise":
//			System.out.println("Pairwise");
//			break;
//		default:
//			System.out.println("no match");
//		}
		Vector<AgentPlayer> evolvedPopulation = new Vector<AgentPlayer>();
		if(mode.equals("Cross")) {
			if (includeBaselineAgents) {
				for (String name : agentNames) {
					AgentPlayer newAgent = (new AgentPlayer(name,
							new HistogramAgent((ProductionRuleAgent) AgentUtils.buildAgent(name))));
					evolvedPopulation.add(newAgent);
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
			Vector<Map<Integer, Vector<Double>>> populationResults = NewTestSuite.mirrorPopulationEvaluation(population,
					minNumPlayers, maxNumPlayers, numGames);
		}
		else if(mode.equals("Mirror")) {
			if (includeBaselineAgents) {
				for (String name : agentNames) {
					AgentPlayer newAgent = (new AgentPlayer(name,
							new HistogramAgent((ProductionRuleAgent) AgentUtils.buildAgent(name))));
					population.add(newAgent);
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
			Vector<Map<Integer, Vector<Double>>> populationResults = NewTestSuite.crossPopulationEvaluation(population, evolvedPopulation, minNumPlayers, maxNumPlayers, numGames);
		}

		System.out.println("Printing full match list in mirror mode:");
		for (int playerID = 0; playerID < populationResults.size(); playerID++) {
			Map<Integer, Vector<Double>> playerResults = populationResults.get(playerID);
			String playerName = population.get(playerID).toString();
			for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
				Vector<Double> matchResults = playerResults.get(gameSize);
				for (double result : matchResults) {
					System.out.println(playerID + ",self," + gameSize + "," + result);
				}
			}
		}

		boolean printSummary = true;
		if (printSummary) {
			System.out.println("");
			System.out.println("Summary of results by player:");

			double maxScore = -1;
			int bestPlayer = -1;

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

			System.out.println("Best overall player is " + bestPlayer + " with a mean score of " + maxScore
					+ " over all game sizes");

			for (AgentPlayer agent : population) {
				Agent a = agent.policy;
				if (a instanceof HistogramAgent) {
					HistogramAgent h = (HistogramAgent) a;
					h.printHistogram();
				}
			}
		}
	}// end of the main fun

	public static void readCSV() {
		String basePath = new File("").getAbsolutePath();
		String csvFile = basePath.concat("/csvfiles/").concat(mode).concat(".csv");
//	    System.out.println(csvFile);
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] conf = line.split(cvsSplitBy);
//				System.out.println(" [name= " + conf[0] + " , var=" + conf[1] + "]");
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
