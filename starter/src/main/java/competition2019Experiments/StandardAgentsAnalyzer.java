package competition2019Experiments;

import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;

import Evolution.Rulebase;
import MapElites.ReportAgent;

public class StandardAgentsAnalyzer {
	
	
	public static void main(String[] args) {
		
		int numPlayers = 5;
		int numGames = 1000;
		
		
		String[] testPoolNames = {"RuleBasedIGGI", "RuleBasedInternal", "RuleBasedOuter", "SampleLegalRandom", "RuleBasedVanDeBergh", "RuleBasedFlawed", "RuleBasedPiers"};
		// Create vector of baseline report agents
		Vector<Agent> baselinePolicies;
        baselinePolicies = Rulebase.GetBaselinePolicies();
        Vector <ReportAgent> baselinePool = new Vector<ReportAgent>();
        Vector <AgentPlayer> agentPlayers = new Vector<AgentPlayer>();
        
        for (Agent policy: baselinePolicies) {
        		ReportAgent ra = new ReportAgent(policy);
        		baselinePool.add(ra);
        }
        
        int i = 0;
        for (ReportAgent ra: baselinePool) {
        		AgentPlayer ap = new AgentPlayer("Agent" + i, ra);
        		agentPlayers.add(ap);
        		++i;
        }
        
        
		PopulationEvaluationSummary pes = TestSuite.mirrorPopulationEvaluation(agentPlayers, numPlayers, numPlayers, numGames);
		
		i = 0;
        for (ReportAgent ra: baselinePool) {
	    		System.out.println("Agent " +  testPoolNames[i] + " has behavior dimensions:");
	    		double dim1 = (double)ra.hintsGiven/(double)ra.possibleHints;
	    		System.out.println(dim1);
	    		double dim2 = (double)ra.totalPlayability/(double)ra.countPlays;
	    		System.out.println(dim2);
	    		System.out.println(pes.getScoreIndividualAgent(i));
	    		++i;
        }
    }

       


}
