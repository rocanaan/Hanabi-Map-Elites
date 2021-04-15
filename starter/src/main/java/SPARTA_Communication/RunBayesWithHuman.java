package SPARTA_Communication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

public class RunBayesWithHuman {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
		String beliefPath = "";//"test";
		int numPlayers = 2;
		 Random random = new Random();
		 //String agentName = "RuleBasedPiers";
		 //GameRunner runner = new GameRunner("test-game", numPlayers);
		 String pathToStates = "SpartaIntegrationTest/states";
		 String pathToActions = "SpartaIntegrationTest/actions";
		 //Agent spartaHuman = new humanSpartaAgent(pathToStates, pathToActions);
	     //runner.addPlayer(new AgentPlayer("spartaHuman", spartaHuman));
	    // Agent rb = AgentUtils.buildAgent(agentName);
		// runner.addPlayer (new AgentPlayer("rb", rb)) ;
		 
		 //runner.playGame(random.nextLong());
		
		// Write a main loop that starts a Bayes bot with some pre-computed information at a certain file path and a human sparta bot and plays a game
		
		//TODO: XIANBO implement the snippets below and also the required functionality in BayesAgent (check TODOs on that file as well)
		
		 
		// Bayes
		int numTrainingGames = 4; //400 in the paper
		int numEvaluationGames = 1;
		int numEvalRepetitions = 1;
		// Agent parameters
		int turnsAdaptationThreshold = Integer.MAX_VALUE;
		int gamesAdaptationThreshold = Integer.MAX_VALUE;
		double assumedBehaviorVariance = 0.1;
		String theirID = "human";
		String precomputedMatchupFile =  System.getProperty("user.dir")+ File.separator + "FinalTOG5by5" + File.separator + "Bayes3Matchups"; 
//					String precomputedMatchupFile =  System.getProperty("user.dir")+ File.separator + "5by5tests" + File.separator + "20201130185201MatchupInfo"; 
		String experimentName = "FinalTOG5by5" + File.separator + "GauntletGeneralistE3"; 	
    	String strategyChromosomeFile = "5by5final3/1iteration999999.json";
    	String trainingChromosomeFile = strategyChromosomeFile;
    	String evaluationChromosomeFile = "5by5final3/1iteration999999.json";
		HashMap<String, Agent> strategyPool = AgentLoaderFromFile.makeAgentMapFromJSON(strategyChromosomeFile, false, true);
		HashMap<String, Agent> trainingPool = AgentLoaderFromFile.makeAgentMapFromJSON(trainingChromosomeFile, false, true); //TODO: If precomputed, training pool has to be the set of partner agents in the matchup file
		HashMap<String, Agent> evaluationPool = AgentLoaderFromFile.makeAgentMapFromJSON(evaluationChromosomeFile, false, true);
		strategyPool = BayesAdaptiveAgent.getGauntlet();
		//trainingPool = BayesAdaptiveAgent.getGauntlet();
		Set<String> trainingPoolCandidates = strategyPool.keySet();
		//Set<String> evaluationPoolCandidates = evaluationPool.keySet();
		MultiKeyMap<String, MatchupInformation> MUs = null;


		if (beliefPath == null || beliefPath == "") {
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
			

//			String overview = "";
//			String detailed = "Detailed Scores\n";
			for (MultiKey key: MUs.keySet()) {
				System.out.println(key.getKey(0));
				System.out.println(key.getKey(1));
				MatchupInformation MU = MUs.get(key);
				//writeLog(String.format("%s;%s: %f %f %f\n", key.getKey(0), key.getKey(1), MU.EstimatedBCValues.get("communicativeness"), MU.EstimatedBCValues.get("IPP"), MU.scoreMean),matchupWriter);
				//System.out.println(String.format("%s;%s: %f %f %f\n", key.getKey(0), key.getKey(1), MU.EstimatedBCValues.get("communicativeness"), MU.EstimatedBCValues.get("IPP"), MU.scoreMean));
				//				detailed+= String.format("%s %s:",  key.getKey(0), key.getKey(1));
//				for (Double score:MU.gameScores) {
//					detailed+=(String.format(" %f",score));
//				}
//				detailed+="\n";
			}
//			writeLog(overview,matchupWriter);
//			writeLog(detailed,matchupWriter);
			/*
			try {
				matchupWriter.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			*/	
		}

		
		beliefInfoToFile
		
		
		
		double overallAverage = 0;
		for (int i=0; i<numEvalRepetitions;i++) {
			//in or out for loop?
			BayesAdaptiveAgent ba = new BayesAdaptiveAgent(strategyPool, trainingPoolCandidates, MUs, experimentName + File.separator + date+ "bayesLogger", String.valueOf(i),turnsAdaptationThreshold, gamesAdaptationThreshold, assumedBehaviorVariance );
			if (beliefPath != null || beliefPath != "") {
				ba.loadBeliefFromFile(beliefPath);
			}
			HashMap<String, StatsSummary> results = new HashMap<String, StatsSummary>();
			HashMap<String, ArrayList<Integer>> detailedResults = new HashMap<String, ArrayList<Integer>>();
            long seed = random.nextLong();
			random = new Random(seed);
			StatsSummary statsSummary = new BasicStats();
			
			ArrayList<Integer> gameScores = new ArrayList<Integer>();

	        for (int gameCount=0; gameCount<numEvaluationGames; gameCount++) {
	        	
	            GameRunner runner = new GameRunner("test-game", numPlayers);
	            
	            runner.addNamedPlayer("Bayes", new AgentPlayer("Bayes", ba));
	            Agent spartaHuman = new humanSpartaAgent(pathToStates, pathToActions);
	            runner.addPlayer(new AgentPlayer("spartaHuman", spartaHuman));
	            
	           
	    		
	    		System.out.println(String.format("Starting to play game %d", gameCount));
	            GameStats stats = runner.playGame(random.nextLong());
	            System.out.println("Finished game");
	            statsSummary.add(stats.score);
	            gameScores.add(stats.score);
	            System.out.println(String.format("Played game %d, score %d", gameCount, stats.score));
	            
	            
	        }
	       
	        
	        results.put(theirID,statsSummary);
	        detailedResults.put(theirID,gameScores);
		        
		    System.out.println(String.format("Playing Bayes with %s got average score %f" , theirID, statsSummary.getMean()));
		    ba.beliefInfoToFile("BayesInfo1");	
		
//			ba.agentLogWriter.close();
			
		
			//writeLog(String.format("Strategy and Training: %s Evaluation: %s Training games: %d Evaluation games: %d Turn adaptation: %d Game adaptation: %d Assumed variance: %f\n", 
			//		strategyChromosomeFile, evaluationChromosomeFile, numTrainingGames, numEvaluationGames, turnsAdaptationThreshold, gamesAdaptationThreshold, assumedBehaviorVariance), resultsWriter);
			System.out.println(String.format("Strategy and Training: %s Evaluation: %s Training games: %d Evaluation games: %d Turn adaptation: %d Game adaptation: %d Assumed variance: %f\n", 
					strategyChromosomeFile, evaluationChromosomeFile, numTrainingGames, numEvaluationGames, turnsAdaptationThreshold, gamesAdaptationThreshold, assumedBehaviorVariance));
			System.out.println("=-=-=-=-Printing Final Results=-=-=-=-=-=-");
			String individualGameScores = "";
			double runAverage = 0;
			for (String theirID1:results.keySet()) {
				StatsSummary MU = results.get(theirID1);
		        System.out.println(String.format("Playing Bayes with %s got average score %f" , theirID1, MU.getMean()));
		        //writeLog(String.format("Playing Bayes with %s got average score %f\n" , theirID, MU.getMean()), resultsWriter);
		        runAverage+=MU.getMean();
		        for (int s: detailedResults.get(theirID1)) {
		        	individualGameScores += (s + " ");
		        }
	        	individualGameScores += "\n";
			}
//			System.out.println(individualGameScores);
			runAverage = runAverage/results.keySet().size();
			overallAverage += (runAverage/numEvalRepetitions);
			System.out.println(String.valueOf(runAverage));
			//writeLog(String.valueOf(runAverage),resultsWriter);
		

		System.out.println(String.format("Overall average is %f", overallAverage));
		}
		

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
