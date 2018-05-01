package Evolution;

import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;

public class FitnessEvaluation {

//		// Calculate fitness using a specific pool of test partners
//		public static double[] calculateFitness(Population population, int numGames, Vector<AgentPlayer> testPartners) {
//			Individual[] individuals = population.getIndividuals();
//			double[] populationFitness = new double[individuals.length];
//			
//			Rule[] ruleset = Rulebase.getRuleset();
//			
//			Vector<AgentPlayer> agents = new Vector<AgentPlayer>();
//			
//			// Create an AgentPlayer instance for each chromossome
//			int count = 0;
//			for (Individual individual : individuals)
//			{
//				Rule[] agentRules = new Rule[ruleset.length];
//				for  (int i=0 ; i < individual.getChromosomeLength(); i++)
//				{
//					agentRules[i] = Rulebase.ruleMapping(individual.getGene(i));
//				}
//				HistogramAgent agent = Rulebase.makeAgent(agentRules);
//				agents.add(new AgentPlayer("histogramAgent"+count, agent));
//				count++;
//			}
//			PopulationEvaluationSummary pes = null;
//			pes = TestSuite.mixedPopulationEvaluation(agents, testPartners, 5, numGames);
//			
//			for (int i = 0; i < individuals.length; i++) {
//				populationFitness[i] = pes.getScoreIndividualAgent(i);
//				System.out.println(pes);
//			}
//			
//			return populationFitness;
//			
//		}
//		
//		// Calculate fitness either in mirror mode or in mixed mode with the standard test pool
//		public static double[] calculateFitness(Population population, int numGames, boolean mirrored)
//		
//		{
//			Individual[] individuals = population.getIndividuals();
//			double[] populationFitness = new double[individuals.length];
//			
//			Rule[] ruleset = Rulebase.getRuleset();
//			
//			Vector<AgentPlayer> agents = new Vector<AgentPlayer>();
//			
//			// Create an AgentPlayer instance for each chromossome
//			int count = 0;
//			for (Individual individual : individuals)
//			{
//				Rule[] agentRules = new Rule[ruleset.length];
//				for  (int i=0 ; i < individual.getChromosomeLength(); i++)
//				{
//					agentRules[i] = Rulebase.ruleMapping(individual.getGene(i));
//				}
//				HistogramAgent agent = Rulebase.makeAgent(agentRules);
//				agents.add(new AgentPlayer("histogramAgent"+count, agent));
//				count++;
//			}
//			//TODO: 5 (number of players) and 10 (number of games per evaluation) should not be hardcoded. Potentially later take measures so every evaluation uses the sam random seed.
//			PopulationEvaluationSummary pes = null;
//			if (mirrored) {
//				pes = TestSuite.mirrorPopulationEvaluation(agents, 5, numGames);
//			}
//			//TODO: create a class that returns the testpool
//			else {
//				Vector<AgentPlayer> baselinePool = Rulebase.GetBaselineAgentPlayers();
//				pes = TestSuite.mixedPopulationEvaluation(agents, baselinePool, 5, numGames);
//			}
//			
//			for (int i = 0; i < individuals.length; i++) {
//				populationFitness[i] = pes.getScoreIndividualAgent(i);
//				System.out.println(pes);
//			}
//			
//			return populationFitness;
//		}
    public static double[] calculateFitness(Population population, int numGames, boolean mirrored, Vector<AgentPlayer> testPool, int minNumPlayers, int maxNumPlayers, boolean rulebaseStandard) {
        Rulebase rb = new Rulebase(rulebaseStandard);
        Individual[] individuals = population.getIndividuals();
        double[] populationFitness = new double[individuals.length];

        Rule[] ruleset;
        if (rulebaseStandard) {
            ruleset = rb.getRuleset();
        } else {
            ruleset = rb.getRuleset();
        }

        Vector<AgentPlayer> agents = new Vector<AgentPlayer>();

        // Create an AgentPlayer instance for each chromossome
        int count = 0;
        for (Individual individual : individuals) {
            Rule[] agentRules = new Rule[ruleset.length];
            for (int i = 0; i < individual.getChromosomeLength(); i++) {
                agentRules[i] = rb.ruleMapping(individual.getGene(i));
            }
            HistogramAgent agent;
            agent = rb.makeAgent(agentRules);
            agents.add(new AgentPlayer("histogramAgent" + count, agent));
            count++;
        }
        //TODO: 5 (number of players) and 10 (number of games per evaluation) should not be hardcoded. 
        // Potentially later take measures so every evaluation uses the sam random seed.
        PopulationEvaluationSummary pes = null;
        if (testPool == null) {
            if (mirrored) {
                pes = TestSuite.mirrorPopulationEvaluation(agents, minNumPlayers, maxNumPlayers, numGames);
            } //TODO: create a class that returns the testpool
            else {
                Vector<AgentPlayer> baselinePool;
                baselinePool = rb.GetBaselineAgentPlayers();
                pes = TestSuite.mixedPopulationEvaluation(agents, baselinePool, minNumPlayers, maxNumPlayers, numGames);
            }
        } else {
            pes = TestSuite.mixedPopulationEvaluation(agents, testPool, minNumPlayers, maxNumPlayers, numGames);
        }

        for (int i = 0; i < individuals.length; i++) {
            populationFitness[i] = pes.getScoreIndividualAgent(i);
            System.out.println(pes);
        }

        return populationFitness;
    }

}
