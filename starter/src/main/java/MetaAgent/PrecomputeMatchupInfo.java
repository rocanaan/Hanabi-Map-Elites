package MetaAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

import MapElites.ReportAgent;

/*
 * This class provides a method that precomputes the relevant match-up information and sample code to print it based on a file
 * It has a few problems to be fixed in the near future:
 * a) Rulebased standard is confusing, should be abstracted away
 * b) Requires consumer to know the names of the Behavioral characteristics in advance.
 * 
 */

public class PrecomputeMatchupInfo {

	public static MultiKeyMap<String, MatchupInformation> precomputeMatchups( Map<String, Agent> ourAgents,  Map<String, Agent> theirAgents, int numPlayers, int numGames){
		
		MultiKeyMap<String, MatchupInformation> precomputedMUs = new MultiKeyMap<String, MatchupInformation> ();
		
		for (String ourID: ourAgents.keySet()) {
			
			ReportAgent us = new ReportAgent(ourAgents.get(ourID));
			for (String theirID: theirAgents.keySet()) {
				Agent them = theirAgents.get(theirID);
				
				StatsSummary stats = runTestGames(us, ourID, them, theirID, numPlayers, numGames);
				
				double comm = us.getCommunicativeness();
				double ipp = us.getInformationPlays();
				
				Map<String,Double> estimatedBCValues = new HashMap<String,Double>();
				estimatedBCValues.put("communicativeness", comm);
				estimatedBCValues.put("IPP", ipp);

				
				MatchupInformation matchup = new MatchupInformation(ourID, theirID, estimatedBCValues, null, stats.getMean(), 0, numGames);
				precomputedMUs.put(ourID,theirID , matchup);
				us.resetStats();
			}
	
			
			
		}
		
		return precomputedMUs;
	}
	
	public static void writePrecomputedMatchupsToJSON(MultiKeyMap<String, MatchupInformation> precomputeMatchups) {
		//XIANBO to do: output it to JSON
	}
	
	public static MultiKeyMap<String, MatchupInformation> readPrecomputedMatchupsFromJSON (String filename) {
		return null;
		//XIANBO to do: output it to JSON
	}
	
	// This function is replicated in other parts of the codebase with small variations. TODO Consolidate.
	public static StatsSummary runTestGames(Agent us, String ourID, Agent them, String theirID, int numPlayers, int numGames, long seed) {
    	// the parameters for the st
        StatsSummary statsSummary = new BasicStats();
               
        // run the test games
        for (int gameCount=0; gameCount<numGames; gameCount++) {
            GameRunner runner = new GameRunner("test-game", numPlayers);
            
            runner.addNamedPlayer(ourID, new AgentPlayer(ourID, us));
            for (int nextPlayer = 1; nextPlayer< numPlayers; nextPlayer++);{
            	runner.addNamedPlayer(ourID, new AgentPlayer(ourID, us));
            }
            
           
            GameStats stats = runner.playGame(seed);
            statsSummary.add(stats.score);
            
        }

        return statsSummary;
    }
	
	public static StatsSummary runTestGames(Agent us, String ourID, Agent them, String theirID, int numPlayers, int numGames) {
    	// the parameters for the st
		
		Random random = new Random();
		long seed = random.nextLong();
		return runTestGames(us, ourID, them, theirID, numPlayers, numGames, seed);
        
    }
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = "5by5";
		System.out.println("hi");
		 Map<String, Agent> agents = AgentLoaderFromFile.makeAgentMapFromFile(fileName, false, true);
		
		 
		System.out.println(agents.keySet());

		 MultiKeyMap<String, MatchupInformation> MUs = precomputeMatchups(agents, agents, 2, 2);
		
			System.out.println("hi3");

		for (String ourID: agents.keySet()) {
			for (String theirID: agents.keySet()) {
				MatchupInformation MU = MUs.get(ourID,theirID);
				
				System.out.println(String.format("%s %s %f %f %f" , MU.myID, MU.partnerID, MU.EstimatedBCValues.get("communicativeness"), MU.EstimatedBCValues.get("IPP"), MU.scoreMean));
			}
		}

	}

}
