package Utils;

import Evolution.*;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Main, executive class for the Traveling Salesman Problem.
 *
 * We don't have a real list of cities, so we randomly generate a number of them
 * on a 100x100 map.
 *
 * The TSP requires that each city is visited once and only once, so we have to
 * be careful when initializing a random Individual and also when applying
 * crossover and mutation. Check out the GeneticAlgorithm class for
 * implementations of crossover and mutation for this problem.
 *
 * @author bkanber
 *
 */
public class MinimizeChromosome {

    public static int[] minimizeChromosome(int[] chromosome, int numGames) {
        int minNumPlayers = 2;
        int maxNumPlayers = 5; // Will play games of 2, 3, 4 and 5 players with a value of maxNumPlayers = 5
        Rulebase rb = new Rulebase(false);

        String[] agentNames = {"RuleBasedIGGI", "RuleBasedInternal", "RuleBasedOuter", "SampleLegalRandom", "RuleBasedVanDeBergh", "RuleBasedFlawed", "RuleBasedPiers"};

        Vector<AgentPlayer> testPool = new Vector<AgentPlayer>();

        for (String name : agentNames) {
            AgentPlayer newAgent = new AgentPlayer(name, AgentUtils.buildAgent(name));
            testPool.add(newAgent);
        }

        Rule[] rules = new Rule[chromosome.length];
        for (int i = 0; i < chromosome.length; i++) {
            rules[i] = rb.ruleMapping(chromosome[i]);
        }
        HistogramAgent hAgent = rb.makeAgent(rules);
        Vector<AgentPlayer> population = new Vector();
        population.add(new AgentPlayer("hAgent", hAgent));
        
        TestSuite.mixedPopulationEvaluation(population, testPool, minNumPlayers, maxNumPlayers, numGames);

        ArrayList<Integer> chromosomeCounters = hAgent.histogram;
        int[] chromosomes = new int[hAgent.getNumberOfFiredRules()];
        int index = 0;
        for (int i = 0; i < chromosome.length; i++) {
            if (chromosomeCounters.get(i) != 0) {
                chromosomes[index] = chromosome[i];
                index++;
            }
        }
        return chromosomes;
    }

    // testing to see if it works
    public static void main(String[] args) {
        int [] c = {10,67,21,8,33,36,28,6,45,23,49,30,31,55,29,1,38,20,26,71,22,58,4,9,34,65,12,15,24,35,2,43,41,5,52,70,44,69,61,62,39,57,16,59,14,19,47,60,66,3,25,32,50,11,13,17,48,40,0,56,37,64,7,68,54,18,46,53,51,27,42,63};
        int[] mc = minimizeChromosome(c,100);
        for (int i:mc){
            System.out.print(i+" ");
        }
        // result: 10 67 8 33 36 6 45 23 49 30 29 
    }
}
