package Utils;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.username.*;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.mcts.MCTSPredictor;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;


import Evolution.Rulebase;

import java.util.Random;
import java.util.Vector;

/**
 * Game runner for testing.
 *
 * This will run a bunch of games with your agent so you can see how it does.
 */
public class HistogramEvaluationRandom 
{
    public static void main( String[] args )
    {
        int populationSize = 100;
        int numGames = 100;
        
        HistogramAgent[] agents = new HistogramAgent[populationSize];
        Vector<AgentPlayer> agentPlayers = new Vector<AgentPlayer>();
        
        int chromossomeLength = Rulebase.getChromossomeLength();

      
        for (int i = 0; i<populationSize; i++) {
        		int[] chromossome = Rulebase.getRandomChromossome(chromossomeLength);
        		System.out.println("Chromossome " + i + " " + chromossome);
        		ProductionRuleAgent pra = new ProductionRuleAgent();
        		for (int gene : chromossome) {
        			pra.addRule(Rulebase.ruleMapping(gene));
        		}
        		agents[i] = new HistogramAgent(pra);
        		agentPlayers.add(new AgentPlayer("agent"+i,agents[i]));
        }

        TestSuite.mirrorPopulationEvaluation(agentPlayers, 5, numGames);
        
        int i =0;
        Vector<Double> lastIndexes = new Vector<Double>();
        Vector<Double> numRules = new Vector<Double>();
        for (HistogramAgent h : agents){
           	System.out.println("Results for agent " + i);
           	//h.printHistogram();
           	int last = h.getLastFiredRuleIndex();
           	System.out.println(last);
           	lastIndexes.add((double)last);
           	int total = h.getNumberOfFiredRules();
           	System.out.println(total);
           	numRules.add((double)total);
           	System.out.println(" ");
           	i++;
        }
        
        System.out.println("Metric: last index fired:");
        System.out.println("Mean: " + Utils.getMean(lastIndexes));
        System.out.println("SD: " + Utils.getStandardDeviation(lastIndexes));
        System.out.println("Min : " + Utils.getMin(lastIndexes));
        System.out.println("Max: " + Utils.getMax(lastIndexes));
        
        System.out.println("Metric: number of rules fired:");
        System.out.println("Mean: " + Utils.getMean(numRules));
        System.out.println("SD: " + Utils.getStandardDeviation(numRules));
        System.out.println("Min : " + Utils.getMin(numRules));
        System.out.println("Max: " + Utils.getMax(numRules));
    }
}
