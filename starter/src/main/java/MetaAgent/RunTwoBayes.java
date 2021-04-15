package MetaAgent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.configuration2.XMLConfiguration;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.CardColour;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;
import com.fossgalaxy.games.fireworks.state.HistoryEntry;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.actions.DiscardCard;
import com.fossgalaxy.games.fireworks.state.actions.PlayCard;
import com.fossgalaxy.games.fireworks.state.actions.TellColour;
import com.fossgalaxy.games.fireworks.state.actions.TellValue;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

import Evolution.Rulebase;
import MetaAgent.HistogramAgent;
import MetaAgent.MatchupTables;
import MetaAgent.PlayerStats;


import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.math3.distribution.NormalDistribution;
/**
 * A sample agent for the learning track.
 * 
 * This agent demonstrates how to get the player IDs for the paired agents.
 * 
 * 
 * You can see more agents online at:
 * https://git.fossgalaxy.com/iggi/hanabi/tree/master/src/main/java/com/fossgalaxy/games/fireworks/ai
 */
public class RunTwoBayes{

	    
	    // A small main function to play the Bayes agent using the same training pool and strategy pool
	    public static void main(String[] args){
			String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
			
			// Experiment parameters
			int numTrainingGames = 400; //400 in the paper
			int numEvaluationGames = 10;
			int numEvalRepetitions = 200;
			int numPlayers = 2;
			String experimentName = "FinalTOG5by5" + File.separator + "G1G1"; 

			// Agent1 parameters
			int turnsAdaptationThreshold1 = Integer.MAX_VALUE;
			int gamesAdaptationThreshold1 = 1;
			double assumedBehaviorVariance1 = 0.1;
			String precomputedMatchupFile1 =  System.getProperty("user.dir")+ File.separator + "FinalTOG5by5" + File.separator + "Bayes1Matchups"; 
	    	


	    	
//	    	If reading from plain text chromosome files
//	    	String strategyChromosomeFile = "2P3";
//	    	String trainingChromosomeFile = strategyChromosomeFile;
//	    	String evaluationChromosomeFile = "2P3";
//			HashMap<String, Agent> strategyPool = AgentLoaderFromFile.makeAgentMapFromFile(strategyChromosomeFile, false, true);
//			HashMap<String, Agent> trainingPool = AgentLoaderFromFile.makeAgentMapFromFile(trainingChromosomeFile, false, true); //TODO: If precomputed, training pool has to be the set of partner agents in the matchup file
//			HashMap<String, Agent> evaluationPool = AgentLoaderFromFile.makeAgentMapFromFile(evaluationChromosomeFile, false, true);

//	  		If reading from JSON iteration files  
	    	String strategyChromosomeFile1 = "/Users/rodrigocanaan/Dev/MapElitesResults/WorkflowTest/MapElites/5by5final1/iteration999999.json";
	    	String trainingChromosomeFile1 = strategyChromosomeFile1;
			HashMap<String, Agent> strategyPool1 = AgentLoaderFromFile.makeAgentMapFromJSON(strategyChromosomeFile1, false, true);
			HashMap<String, Agent> trainingPool1 = AgentLoaderFromFile.makeAgentMapFromJSON(trainingChromosomeFile1, false, true); //TODO: If precomputed, training pool has to be the set of partner agents in the matchup file
			
//			strategyPool = getGauntlet();
//			trainingPool = getGauntlet();
//			evaluationPool = getGauntlet();
			

			Set<String> trainingPoolCandidates1 = strategyPool1.keySet();

			
			MultiKeyMap<String, MatchupInformation> MUs1 = BayesAdaptiveAgent.parsePrecomputedMUFile(precomputedMatchupFile1);

			
			// Agent2 parameters
			int turnsAdaptationThreshold2 = Integer.MAX_VALUE;
			int gamesAdaptationThreshold2 = Integer.MAX_VALUE;
			double assumedBehaviorVariance2 = 0.1;
			String precomputedMatchupFile2 =  System.getProperty("user.dir")+ File.separator + "FinalTOG5by5" + File.separator + "Bayes1Matchups"; 
	    	


	    	
//	    	If reading from plain text chromosome files
//	    	String strategyChromosomeFile = "2P3";
//	    	String trainingChromosomeFile = strategyChromosomeFile;
//	    	String evaluationChromosomeFile = "2P3";
//			HashMap<String, Agent> strategyPool = AgentLoaderFromFile.makeAgentMapFromFile(strategyChromosomeFile, false, true);
//			HashMap<String, Agent> trainingPool = AgentLoaderFromFile.makeAgentMapFromFile(trainingChromosomeFile, false, true); //TODO: If precomputed, training pool has to be the set of partner agents in the matchup file
//			HashMap<String, Agent> evaluationPool = AgentLoaderFromFile.makeAgentMapFromFile(evaluationChromosomeFile, false, true);

//	  		If reading from JSON iteration files  
	    	String strategyChromosomeFile2 = "/Users/rodrigocanaan/Dev/MapElitesResults/WorkflowTest/MapElites/5by5final1/iteration999999.json";
	    	String trainingChromosomeFile2 = strategyChromosomeFile2;
			HashMap<String, Agent> strategyPool2 = AgentLoaderFromFile.makeAgentMapFromJSON(strategyChromosomeFile2, false, true);
			HashMap<String, Agent> trainingPool2 = AgentLoaderFromFile.makeAgentMapFromJSON(trainingChromosomeFile2, false, true); //TODO: If precomputed, training pool has to be the set of partner agents in the matchup file
			
//			strategyPool = getGauntlet();
//			trainingPool = getGauntlet();
//			evaluationPool = getGauntlet();
			

			Set<String> trainingPoolCandidates2 = strategyPool2.keySet();

			
			MultiKeyMap<String, MatchupInformation> MUs2 = BayesAdaptiveAgent.parsePrecomputedMUFile(precomputedMatchupFile2);
			
			
			
			double overallAverage = 0;
			for (int i=0; i<numEvalRepetitions;i++) {
				BayesAdaptiveAgent ba1 = new BayesAdaptiveAgent(strategyPool1, trainingPoolCandidates1, MUs1, experimentName + File.separator + date+ "bayes1Logger", String.valueOf(i),turnsAdaptationThreshold1, gamesAdaptationThreshold1, assumedBehaviorVariance1 );
				BayesAdaptiveAgent ba2 = new BayesAdaptiveAgent(strategyPool2, trainingPoolCandidates2, MUs2, experimentName + File.separator + date+ "bayes2Logger", String.valueOf(i),turnsAdaptationThreshold2, gamesAdaptationThreshold2, assumedBehaviorVariance2 );
	
				
				
				HashMap<String, StatsSummary> results = new HashMap<String, StatsSummary>();
				HashMap<String, ArrayList<Integer>> detailedResults = new HashMap<String, ArrayList<Integer>>();
	            Random random = new Random();
	            long seed = random.nextLong();
	            
				random = new Random(seed);
				StatsSummary statsSummary = new BasicStats();
				
				ArrayList<Integer> gameScores = new ArrayList<Integer>();

	               
		        // run the test games
		        for (int gameCount=0; gameCount<numEvaluationGames; gameCount++) {
		        	
		            GameRunner runner = new GameRunner("test-game", numPlayers);
		            
		            runner.addNamedPlayer("Bayes1", new AgentPlayer("Bayes1", ba1));
		            runner.addNamedPlayer("Bayes2", new AgentPlayer("Bayes2", ba2));

		            
		    		
		    		System.out.println(String.format("Starting to play game %d", gameCount));
		            GameStats stats = runner.playGame(random.nextLong());
		            System.out.println("Finished game");
		            statsSummary.add(stats.score);
		            gameScores.add(stats.score);
		            System.out.println(String.format("Played game %d, score %d", gameCount, stats.score));
		            
		        }
		        
		        results.put("TwoBayes",statsSummary);
		        detailedResults.put("TwoBayes",gameScores);
		        
		        System.out.println(String.format("Playing Two Bayes  got average score %f" , statsSummary.getMean()));
	
	//			ba.agentLogWriter.close();
				BufferedWriter resultsWriter = null;
				try {
					resultsWriter = new BufferedWriter(new FileWriter(experimentName + File.separator +  date+ "Results_" + String.valueOf(i), true));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("Failed to open Bayes agent internal log file");
					e.printStackTrace();
				}
				writeLog(String.format("Strategy and Training: %s  Training games: %d Evaluation games: %d Turn adaptation: %d Game adaptation: %d Assumed variance: %f\n", 
						strategyChromosomeFile1,  numTrainingGames, numEvaluationGames, turnsAdaptationThreshold1, gamesAdaptationThreshold1, assumedBehaviorVariance1), resultsWriter);
				System.out.println(String.format("Strategy and Training: %s Training games: %d Evaluation games: %d Turn adaptation: %d Game adaptation: %d Assumed variance: %f\n", 
						strategyChromosomeFile1, numTrainingGames, numEvaluationGames, turnsAdaptationThreshold1, gamesAdaptationThreshold1, assumedBehaviorVariance1));
				System.out.println("=-=-=-=-Printing Final Results=-=-=-=-=-=-");
				String individualGameScores = "";
				double runAverage = 0;
				for (String theirID:results.keySet()) {
					StatsSummary MU = results.get(theirID);
			        System.out.println(String.format("Playing Bayes with %s got average score %f" , theirID, MU.getMean()));
			        writeLog(String.format("Playing Bayes with %s got average score %f\n" , theirID, MU.getMean()), resultsWriter);
			        runAverage+=MU.getMean();
			        for (int s: detailedResults.get(theirID)) {
			        	individualGameScores += (s + " ");
			        }
		        	individualGameScores += "\n";
				}
	//			System.out.println(individualGameScores);
				runAverage = runAverage/results.keySet().size();
				overallAverage += (runAverage/numEvalRepetitions);
				System.out.println(String.valueOf(runAverage));
				writeLog(String.valueOf(runAverage),resultsWriter);
			}
			System.out.println(String.format("Overall average is %f", overallAverage));
	    }
	
	private static void writeLog(String log, String error, BufferedWriter writer) {
		try {
			writer.append(log);
			writer.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(error);
			e.printStackTrace();
		}
	}
	
	private static void writeLog(String log, BufferedWriter writer) {
		writeLog(log, "", writer);
	}
	

		
}
