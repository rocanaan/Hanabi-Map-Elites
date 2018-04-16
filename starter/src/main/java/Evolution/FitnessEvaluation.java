package Evolution;

import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;

public class FitnessEvaluation {
	
		

	
		public static double[] calculateFitness(Population population, boolean mirrored)
		
		{
			Individual[] individuals = population.getIndividuals();
			double[] populationFitness = new double[individuals.length];
			
			Rule[] ruleset = Rulebase.getRuleset();
			
			Vector<AgentPlayer> agents = new Vector<AgentPlayer>();
			
			// Create an AgentPlayer instance for each chromossome
			for (Individual individual : individuals)
			{
				Rule[] agentRules = new Rule[ruleset.length];
				for  (int i=0 ; i < individual.getChromosomeLength(); i++)
				{
					agentRules[i] = Rulebase.ruleMapping(individual.getGene(i));
				}
				AgentPlayer agent = Rulebase.makeAgent(agentRules);
				agents.add(agent);
			}
			//TODO: 5 (number of players) and 10 (number of games per evaluation) should not be hardcoded. Potentially later take measures so every evaluation uses the sam random seed.
			PopulationEvaluationSummary pes = null;
			if (mirrored) {
				pes = TestSuite.mirrorPopulationEvaluation(agents, 5, 100);
			}
			//TODO: create a class that returns the testpool
//			else {
//				PopulationEvaluationSummary pes = TestSuite.mixedPopulationEvaluation(agents, 5, 10);
//			}
			
			for (int i = 0; i < individuals.length; i++) {
				populationFitness[i] = pes.getScoreIndividualAgent(i);
				System.out.println(pes);
			}
			
			return populationFitness;
		}
}
