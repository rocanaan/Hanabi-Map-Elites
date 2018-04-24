package Evolution;

import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;

public class FitnessEvaluation {
	
		
		
	
		public static double[] calculateFitness(Population population, int numGames, boolean mirrored)
		
		{
			Individual[] individuals = population.getIndividuals();
			double[] populationFitness = new double[individuals.length];
			
			Rule[] ruleset = Rulebase.getRuleset();
			
			Vector<AgentPlayer> agents = new Vector<AgentPlayer>();
			
			// Create an AgentPlayer instance for each chromossome
			int count = 0;
			for (Individual individual : individuals)
			{
				Rule[] agentRules = new Rule[ruleset.length];
				for  (int i=0 ; i < individual.getChromosomeLength(); i++)
				{
					agentRules[i] = Rulebase.ruleMapping(individual.getGene(i));
				}
				HistogramAgent agent = Rulebase.makeAgent(agentRules);
				agents.add(new AgentPlayer("histogramAgent"+count, agent));
				count++;
			}
			//TODO: 5 (number of players) and 10 (number of games per evaluation) should not be hardcoded. Potentially later take measures so every evaluation uses the sam random seed.
			PopulationEvaluationSummary pes = null;
			if (mirrored) {
				pes = TestSuite.mirrorPopulationEvaluation(agents, 5, numGames);
			}
			//TODO: create a class that returns the testpool
			else {
				Vector<AgentPlayer> baselinePool = Rulebase.GetBaselineAgentPlayers();
				pes = TestSuite.mixedPopulationEvaluation(agents, baselinePool, 5, numGames);
			}
			
			for (int i = 0; i < individuals.length; i++) {
				populationFitness[i] = pes.getScoreIndividualAgent(i);
				System.out.println(pes);
			}
			
			return populationFitness;
		}
}
