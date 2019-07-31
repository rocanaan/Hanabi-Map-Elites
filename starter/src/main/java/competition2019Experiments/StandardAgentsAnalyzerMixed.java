package competition2019Experiments;

import java.util.Random;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.stats.StatsSummary;

import Evolution.Rulebase;
import MapElites.ReportAgent;

public class StandardAgentsAnalyzerMixed {
	
	
	public static void main(String[] args) {
		
		int numPlayers = 2;
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
        
        double[][] dim1Matrix = new double[agentPlayers.size()][agentPlayers.size()];
        double[][] dim2Matrix = new double[agentPlayers.size()][agentPlayers.size()];
        double[][] dim1MatrixPartner = new double[agentPlayers.size()][agentPlayers.size()];
        double[][] dim2MatrixPartner = new double[agentPlayers.size()][agentPlayers.size()];
        double[][] scoreMatrix = new double[agentPlayers.size()][agentPlayers.size()];



        
        int agent1Index = 0;
        for(AgentPlayer agent1:agentPlayers) {
        		int agent2Index = 0;
        		for (AgentPlayer agent2: agentPlayers) {
        			ReportAgent a1 = baselinePool.get(agent1Index);
        			ReportAgent a2 = baselinePool.get(agent2Index);
        			StatsSummary stats = TestSuite.ConstantNumberPlayersTest(numPlayers, numGames, agent1, agent2, new Random());
        			dim1Matrix[agent1Index][agent2Index] = (double) a1.hintsGiven / (double) a1.possibleHints;
        			dim2Matrix[agent1Index][agent2Index] = (double) a1.totalPlayability / (double) a1.countPlays;
        			dim1MatrixPartner[agent1Index][agent2Index] = a2.hintsGiven / (double)a2.possibleHints;
        			dim2MatrixPartner[agent1Index][agent2Index] = a2.totalPlayability / a2.countPlays;
        			scoreMatrix[agent1Index][agent2Index] = stats.getMean();
        			
        			a1.resetStats();
        			a2.resetStats();
        			++agent2Index;
        		}
        		++agent1Index;
        }
        
        
        System.out.println("dim1 agent 1");
        printMatrix(dim1Matrix,agentPlayers.size());
        System.out.println("dim2 agent 1");
        printMatrix(dim2Matrix,agentPlayers.size());
        System.out.println("dim1 agent 2");
        printMatrix(dim1MatrixPartner,agentPlayers.size());
        System.out.println("dim2 agent 2");
        printMatrix(dim2MatrixPartner,agentPlayers.size());
        System.out.println("scores");
        printMatrix(scoreMatrix,agentPlayers.size());
		
    }
	
	private static void printMatrix(double[][] matrix, int length) {
		for (int i = 0; i<length; ++i) {
			for (int j=0; j<length; ++j) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println("");
		}
	}

       


}
