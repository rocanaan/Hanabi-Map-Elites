package SPARTA_Communication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

import MetaAgent.AgentLoaderFromFile;
import MetaAgent.BayesAdaptiveAgent;
import MetaAgent.MatchupInformation;
import MetaAgent.PrecomputeMatchupInfo;

public class RunBayesWithHuman2 {

	public static void main(String[] args){
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
		
		// Experiment parameters
		int numTrainingGames = 1; //400 in the paper  /400 10 10
		int numEvaluationGames = 1;
		int numEvalRepetitions = 10;
		boolean playWithHuman = true;
		String humanPlayerName = "hum1";
		String beliefInfoPath = "BeliefInfo.json";
		String rollingMatchupInfoPath = "RollingMatchupInfo.json";
		// Agent parameters
		int turnsAdaptationThreshold = Integer.MAX_VALUE;
		int gamesAdaptationThreshold = 1;
		double assumedBehaviorVariance = 0.1;
		boolean usePrecomputed = false;
		String precomputedMatchupFile =  System.getProperty("user.dir")+ File.separator + "FinalTOG5by5" + File.separator + "Bayes3Matchups"; 
//		String precomputedMatchupFile =  System.getProperty("user.dir")+ File.separator + "5by5tests" + File.separator + "20201130185201MatchupInfo"; 
		String experimentName = "FinalTOG5by5" + File.separator + "GauntletGeneralistE3"; 
    	


    	
//    	If reading from plain text chromosome files
//    	String strategyChromosomeFile = "2P3";
//    	String trainingChromosomeFile = strategyChromosomeFile;
//    	String evaluationChromosomeFile = "2P3";
//		HashMap<String, Agent> strategyPool = AgentLoaderFromFile.makeAgentMapFromFile(strategyChromosomeFile, false, true);
//		HashMap<String, Agent> trainingPool = AgentLoaderFromFile.makeAgentMapFromFile(trainingChromosomeFile, false, true); //TODO: If precomputed, training pool has to be the set of partner agents in the matchup file
//		HashMap<String, Agent> evaluationPool = AgentLoaderFromFile.makeAgentMapFromFile(evaluationChromosomeFile, false, true);

//  		If reading from JSON iteration files  
    	String strategyChromosomeFile = "5by5final3/1iteration999999.json";
    	String trainingChromosomeFile = strategyChromosomeFile;
    	String evaluationChromosomeFile = "5by5final3/1iteration999999.json";
    	HashMap<String, Agent> strategyPool = AgentLoaderFromFile.makeAgentMapFromJSON(strategyChromosomeFile, false, true);
		HashMap<String, Agent> trainingPool = AgentLoaderFromFile.makeAgentMapFromJSON(trainingChromosomeFile, false, true); //TODO: If precomputed, training pool has to be the set of partner agents in the matchup file
		HashMap<String, Agent> evaluationPool = AgentLoaderFromFile.makeAgentMapFromJSON(evaluationChromosomeFile, false, true);
		
		strategyPool = BayesAdaptiveAgent.getGauntlet();
		trainingPool = BayesAdaptiveAgent.getGauntlet();
		evaluationPool = BayesAdaptiveAgent.getGauntlet();
		
		
		Set<String> trainingPoolCandidates = strategyPool.keySet();
		Set<String> evaluationPoolCandidates = evaluationPool.keySet();



		int numPlayers = 2;
		
//		TODO: if there is no specified path to load precomputed information, play games and output json else read from the JSON
//		String precomputedPath = "";
//		if (precomputedPath == "") {
//			int numTrainingGames = 100;
//			MultiKeyMap<String, MatchupInformation> MUs = PrecomputeMatchupInfo.precomputeMatchups(strategyPool, trainingPool, numPlayers, numTrainingGames);
//			TODO: write this to JSON instead of raw text
//    	}
//    	else {
//    		TODO: read from the JSON specified in the path
//   		}
		
		MultiKeyMap<String, MatchupInformation> MUs = null;
		
		if (usePrecomputed == false){
		 // A very messy parser for the matchup file. TODO: Output the matchups in JSON instead, write a converter from current format to JSON for compatibiliy

	        
            {
            // TODO: This block has too many responsibilities: actually precomputing the matchups with the provided method and logging a bunch of stuff
            String matchupInfoFile = experimentName + File.separator +  date+ "MatchupInfo";
            
            BufferedWriter matchupWriter = null;
            try {
                matchupWriter = new BufferedWriter(new FileWriter(matchupInfoFile, true));
                matchupWriter.append(String.format("Training games = %d chromosome file = %s\n", numTrainingGames,strategyChromosomeFile));
                matchupWriter.flush();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
    
            // TODO: Rewrite this method into  precomputeSingleMatchup, which lets us get the results matchup per matchup and add them to file one by one
            // Also I do not want detailed score information on the file.
            MUs = PrecomputeMatchupInfo.precomputeMatchups(strategyPool, trainingPool, numPlayers, numTrainingGames);

            System.out.println("Finished precomputing matchups");
            // TODO: replace the next 4 lines where you print to the file with outputting a JSON
            

//          String overview = "";
//          String detailed = "Detailed Scores\n";
            for (MultiKey key: MUs.keySet()) {
                System.out.println(key.getKey(0));
                System.out.println(key.getKey(1));
                MatchupInformation MU = MUs.get(key);
                //writeLog(String.format("%s;%s: %f %f %f\n", key.getKey(0), key.getKey(1), MU.EstimatedBCValues.get("communicativeness"), MU.EstimatedBCValues.get("IPP"), MU.scoreMean),matchupWriter);
                System.out.println(String.format("%s;%s: %f %f %f\n", key.getKey(0), key.getKey(1), MU.EstimatedBCValues.get("communicativeness"), MU.EstimatedBCValues.get("IPP"), MU.scoreMean));
                //              detailed+= String.format("%s %s:",  key.getKey(0), key.getKey(1));
//              for (Double score:MU.gameScores) {
//                  detailed+=(String.format(" %f",score));
//              }
//              detailed+="\n";
            }
//          writeLog(overview,matchupWriter);
//          writeLog(detailed,matchupWriter);
            /*
            try {
                matchupWriter.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            */  
            }
		}
		
		
		 
			if (usePrecomputed) {
				MUs = new MultiKeyMap<String, MatchupInformation> ();
			}
		
			
		//Whether to load from file	
		//if path is not null, load from file
		BayesAdaptiveAgent tempBa = new BayesAdaptiveAgent(strategyPool, trainingPoolCandidates, MUs, experimentName + File.separator + date+ "bayesLogger", String.valueOf(-1),turnsAdaptationThreshold, gamesAdaptationThreshold, assumedBehaviorVariance );
		
		
		if (beliefInfoPath != null && beliefInfoPath != "")	{
			tempBa.loadBeliefInfoFromFile(beliefInfoPath);
			}
		tempBa.saveBeliefInfoToFile("BeliefInfo.json");
			
		if (rollingMatchupInfoPath != null && rollingMatchupInfoPath != "")	{
			tempBa.loadRollingMatchupInfoFromFile(rollingMatchupInfoPath);
		}
		tempBa.saveRollingMatchupInfoToFile("RollingMatchupInfo.json");
		
		BayesAdaptiveAgent ba = new BayesAdaptiveAgent(strategyPool, trainingPoolCandidates, MUs, experimentName + File.separator + date+ "bayesLogger", "0",turnsAdaptationThreshold, gamesAdaptationThreshold, assumedBehaviorVariance );
		double overallAverage = 0;
		for (int i=0; i<numEvalRepetitions;i++) {
						//load
			ba.loadRollingMatchupInfoFromFile("RollingMatchupInfo.json");
			ba.loadBeliefInfoFromFile("BeliefInfo.json");
			
			HashMap<String, StatsSummary> results = new HashMap<String, StatsSummary>();
			HashMap<String, ArrayList<Integer>> detailedResults = new HashMap<String, ArrayList<Integer>>();
            Random random = new Random();
            long seed = random.nextLong();
            
            //String arr[] = {"testplayer1"};
            //Set<String> humanPlayers = new HashSet<>(Arrays.asList(arr));;
			//for (String theirID : humanPlayers) {
                String theirID = humanPlayerName;
				random = new Random(seed);
				StatsSummary statsSummary = new BasicStats();
				
				ArrayList<Integer> gameScores = new ArrayList<Integer>();

	               
		        // run the test games
		        for (int gameCount=0; gameCount<numEvaluationGames; gameCount++) {
		        	
		            GameRunner runner = new GameRunner("test-game", numPlayers);
		            
		            runner.addNamedPlayer("Bayes", new AgentPlayer("Bayes", ba));
		            
		            Agent evaluationPartner = evaluationPool.get(theirID);
		            for (int nextPlayer = 1; nextPlayer< numPlayers; nextPlayer++);{
		            	
		            	if (playWithHuman == true){
		            		String pathToStates = "SpartaIntegrationTest/states";
			       		 	String pathToActions = "SpartaIntegrationTest/actions";
			            	Agent spartaHuman = new humanSpartaAgent(pathToStates, pathToActions);
			       		 	runner.addNamedPlayer(theirID, new AgentPlayer(humanPlayerName, spartaHuman));
		            	}
		            	else {
		            		runner.addNamedPlayer("test"+theirID, new AgentPlayer(theirID, evaluationPartner));
		            	}
		       		 	
		            }
		           
		    		
		    		System.out.println(String.format("Starting to play game %d with %s", gameCount, theirID));
		            GameStats stats = runner.playGame(random.nextLong());
		            System.out.println("Finished game");
		            statsSummary.add(stats.score);
		            gameScores.add(stats.score);
		            System.out.println(String.format("Played game %d with %s, score %d", gameCount, theirID, stats.score));
		            
		        }
		        
		        results.put(theirID,statsSummary);
		        detailedResults.put(theirID,gameScores);
		        
		        System.out.println(String.format("Playing Bayes with %s got average score %f" , theirID, statsSummary.getMean()));

			//}
//			ba.agentLogWriter.close();
			BufferedWriter resultsWriter = null;
			/*
			try {
				resultsWriter = new BufferedWriter(new FileWriter(experimentName + File.separator +  date+ "Results_" + String.valueOf(i), true));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Failed to open Bayes agent internal log file");
				e.printStackTrace();
			}
			*/
			//writeLog(String.format("Strategy and Training: %s Evaluation: %s Training games: %d Evaluation games: %d Turn adaptation: %d Game adaptation: %d Assumed variance: %f\n", 
			//		strategyChromosomeFile, evaluationChromosomeFile, numTrainingGames, numEvaluationGames, turnsAdaptationThreshold, gamesAdaptationThreshold, assumedBehaviorVariance), resultsWriter);
			System.out.println(String.format("Strategy and Training: %s Evaluation: %s Training games: %d Evaluation games: %d Turn adaptation: %d Game adaptation: %d Assumed variance: %f\n", 
					strategyChromosomeFile, evaluationChromosomeFile, numTrainingGames, numEvaluationGames, turnsAdaptationThreshold, gamesAdaptationThreshold, assumedBehaviorVariance));
			System.out.println("=-=-=-=-Printing Final Results=-=-=-=-=-=-");
			String individualGameScores = "";
			double runAverage = 0;
			for (String ID:results.keySet()) {
				StatsSummary MU = results.get(ID);
		        System.out.println(String.format("Playing Bayes with %s got average score %f" , theirID, MU.getMean()));
		        //writeLog(String.format("Playing Bayes with %s got average score %f\n" , theirID, MU.getMean()), resultsWriter);
		        runAverage+=MU.getMean();
		        for (int s: detailedResults.get(ID)) {
		        	individualGameScores += (s + " ");
		        }
	        	individualGameScores += "\n";
			}
//			System.out.println(individualGameScores);
			runAverage = runAverage/results.keySet().size();
			overallAverage += (runAverage/numEvalRepetitions);
			System.out.println(String.valueOf(runAverage));
			//writeLog(String.valueOf(runAverage),resultsWriter);
			
			//end of for loop of numEvaluations
			ba.saveRollingMatchupInfoToFile("RollingMatchupInfo.json");
			ba.saveBeliefInfoToFile("BeliefInfo.json");
		}
		System.out.println(String.format("Overall average is %f", overallAverage));
    }

		

	
	
	
	
	public void mainLoop(String beliefPath, String rollingMUInfoPath) {
		
		if (beliefPath == null || beliefPath == "") {
			// start bayes agent with no prior beliefs
		}
		else {
			// start bayes agent by passing the belief map and rolling matchup info 
		}

		// start a humanSpartaAgent
		
		// play a game
		
		// output belief distribution (or perhaps the matchupInfo) to file
		
	}

}
