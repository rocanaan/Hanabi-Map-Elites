package Utils;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.username.*;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.mcts.MCTSPredictor;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
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
public class HistogramEvaluationEvolved 
{
    public static void main( String[] args )
    {
        Rulebase rb = new Rulebase(false);
        int numGames = 100;
        Vector<AgentPlayer> population = new Vector<AgentPlayer>();
        Vector<HistogramAgent> agents = new Vector<HistogramAgent>();
        
        // Best agent on the GA
     	int[] chromossome1 = {8,13,28,6,33,35,20,38,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
     	Rule[] rules1 = new Rule[chromossome1.length];
     	for (int i = 0; i < chromossome1.length; i++) {
     		rules1[i] = rb.ruleMapping(chromossome1[i]);
     	}
     	HistogramAgent evolvedHAgent = rb.makeAgent(rules1);
     	agents.add(evolvedHAgent);
     	population.add(new AgentPlayer("evolvedAgent", evolvedHAgent));
     		
     	// Variation putting Osawa discard (38) before oldestNoInfo (28)
     	int[] chromossome2 = {8,13,38,28,6,33,35,20,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
     	Rule[] rules2 = new Rule[chromossome2.length];
     	for (int i = 0; i < chromossome2.length; i++) {
     		rules2[i] = rb.ruleMapping(chromossome2[i]);
     	}
     	HistogramAgent variantHAgent = rb.makeAgent(rules2);
     	agents.add(variantHAgent);
     	population.add(new AgentPlayer("variantAgent", variantHAgent));
     		
 		// Variation putting Osawa discard (38) before oldestNoInfo (28) and add hailMary
 		int[] chromossome3 = {42,8,13,38,28,6,33,35,20,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
 		Rule[] rules3 = new Rule[chromossome3.length+1];
 		for (int i = 0; i < chromossome3.length; i++) {
 			rules3[i] = rb.ruleMapping(chromossome3[i]);
 		}
 		HistogramAgent HailMaryHAgent = rb.makeAgent(rules3);
 		agents.add(HailMaryHAgent);
 		population.add(new AgentPlayer("Hail Mary", HailMaryHAgent));
 		
 		int[] chromossome4 = {46,8,10,13,28,17,20,22,18,29,49,41,32,34,9,24,37,45,35,47,3,43,15,14,0,12,48,42,23,27,1,21,19,16,44,4,7,25,6,40,39,26,33,11,2,36,38,31,5,30};
 		Rule[] rules4 = new Rule[chromossome4.length+1];
 		for (int i = 0; i < chromossome4.length; i++) {
 			rules4[i] = rb.ruleMapping(chromossome4[i]);
 		}
 		HistogramAgent evolvedMixed1 = rb.makeAgent(rules4);
 		agents.add(evolvedMixed1);
 		population.add(new AgentPlayer("Evolved Mixed 1", evolvedMixed1));
 		
 		int[] chromossome5 = {46,8,10,13,28,17,20,22,18,29,49,41,38,34,9,24,37,45,35,47,0,12,15,14,6,43,48,2,23,27,1,21,42,16,44,4,7,25,3,40,39,26,33,11,19,36,32,31,5,30};
 		Rule[] rules5 = new Rule[chromossome5.length+1];
 		for (int i = 0; i < chromossome5.length; i++) {
 			rules5[i] = rb.ruleMapping(chromossome5[i]);
 		}
 		HistogramAgent evolvedMixed2 = rb.makeAgent(rules5);
 		agents.add(evolvedMixed2);
 		population.add(new AgentPlayer("Evolved Mixed 2", evolvedMixed2));
 		
 		int[] chromossome6 = {6,40,8,10,13,28,37,22,18,49,2,5,24,33,31,35,26,14,38,34,21,44,25,9,15,19,36,32,45,39,47,20,3,4,29,7,16,48,42,17,43,1,27,46,0,41,12,30,23,11};
 		Rule[] rules6 = new Rule[chromossome6.length+1];
 		for (int i = 0; i < chromossome6.length; i++) {
 			rules6[i] = rb.ruleMapping(chromossome6[i]);
 		}
 		HistogramAgent evolvedMixed3 = rb.makeAgent(rules6);
 		agents.add(evolvedMixed3);
 		population.add(new AgentPlayer("Evolved Mixed 3", evolvedMixed3));
 		
 		int[] chromossome7 = {8,1,10,13,28,22,18,41,38,35,9,34,0,30,33,43,2,15,25,40,21,17,11,19,32,31,46,20,42,47,6,29,23,24,37,3,7,5,14,49,48,36,12,27,39,16,44,4,45,26};
 		Rule[] rules7 = new Rule[chromossome7.length+1];
 		for (int i = 0; i < chromossome7.length; i++) {
 			rules7[i] = rb.ruleMapping(chromossome7[i]);
 		}
 		HistogramAgent evolvedMixed4 = rb.makeAgent(rules7);
 		agents.add(evolvedMixed4);
 		population.add(new AgentPlayer("Evolved Mixed 4", evolvedMixed4));
     		
// 		int[] chromossome4 = {46,8,10,13,28,17,20,22,18,29,49,41,38,34,9,24,37,45,35,47,0,12,15,14,6,43,48,2,23,27,1,21,42,16,44,4,7,25,3,40,39,26,33,11,19,36,32,31,5,30};
// 		Rule[] rules4 = new Rule[chromossome4.length+1];
// 		for (int i = 0; i < chromossome4.length; i++) {
// 			rules4[i] = rb.ruleMapping(chromossome4[i]);
// 		}
// 		HistogramAgent evolvedMixed1 = rb.makeAgent(rules4);
// 		agents.add(evolvedMixed1);
// 		population.add(new AgentPlayer("Evolved Mixed 1", evolvedMixed1));
// 		
// 		int[] chromossome5 = {8,10,13,28,33,22,18,49,41,5,24,37,45,35,15,14,6,34,27,21,44,25,9,26,19,36,32,31,39,42,20,3,4,46,29,1,7,16,48,47,17,0,12,30,23,11,38,43,40,2};
// 		Rule[] rules5 = new Rule[chromossome5.length+1];
// 		for (int i = 0; i < chromossome5.length; i++) {
// 			rules5[i] = rb.ruleMapping(chromossome5[i]);
// 		}
// 		HistogramAgent evolvedMixed2 = rb.makeAgent(rules5);
// 		agents.add(evolvedMixed2);
// 		population.add(new AgentPlayer("Evolved Mixed 2", evolvedMixed2));
// 		
// 		int[] chromossome6 = {8,13,17,45,16,34,9,48,24,2,0,4,25,46,6,41,26,49,15,42,33,28,21,39,31,18,22,3,40,10,12,20,43,36,37,32,30,35,47,1,38,44,19,29,14,7,27,23,11,5};
// 		Rule[] rules6 = new Rule[chromossome6.length+1];
// 		for (int i = 0; i < chromossome6.length; i++) {
// 			rules6[i] = rb.ruleMapping(chromossome6[i]);
// 		}
// 		HistogramAgent evolvedMixed3 = rb.makeAgent(rules6);
// 		agents.add(evolvedMixed3);
// 		population.add(new AgentPlayer("Evolved Mixed 3", evolvedMixed3));
// 		
// 		int[] chromossome7 = {47,8,10,41,45,20,35,12,11,34,9,37,0,26,15,39,6,33,48,2,23,49,1,21,42,16,44,4,7,25,3,40,27,43,29,18,36,32,31,5,30,46,22,13,17,28,19,14,38,24};
// 		Rule[] rules7 = new Rule[chromossome7.length+1];
// 		for (int i = 0; i < chromossome7.length; i++) {
// 			rules7[i] = rb.ruleMapping(chromossome7[i]);
// 		}
// 		HistogramAgent evolvedMixed4 = rb.makeAgent(rules7);
// 		agents.add(evolvedMixed4);
// 		population.add(new AgentPlayer("Evolved Mixed 4", evolvedMixed4));	
     
       

        TestSuite.mirrorPopulationEvaluation(population, 5, numGames);
        
        int i =0;
        Vector<Double> lastIndexes = new Vector<Double>();
        Vector<Double> numRules = new Vector<Double>();
        for (HistogramAgent h : agents){
           	System.out.println("Results for agent " + i);
           	h.printHistogram();
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
