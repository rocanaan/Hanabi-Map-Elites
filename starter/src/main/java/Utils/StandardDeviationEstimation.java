package Utils;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.username.*;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
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
public class StandardDeviationEstimation 
{
    public static void main( String[] args )
    {
        Rulebase rb = new Rulebase(false);
        int numGames = 1;
        int repetitions = 200;
        boolean mirror = false;
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
 		
 		int[] chromossome6 = {57,61,7,10,68,64,60,32,37,36,43,34,13,39,27,21,44,23,20,55,31,9,41,65,0,40,24,52,22,17,45,28,2,59,48,25,67,62,26,53,8,16,63,56,5,30,66,49,6,3,1,29,51,58,54,14,15,47,50,42,35,11,33,18,4,12,46,38,19};
 		Rule[] rules6 = new Rule[chromossome6.length+1];
 		for (int i = 0; i < chromossome6.length; i++) {
 			rules6[i] = rb.ruleMapping(chromossome6[i]);
 		}
 		HistogramAgent evolvedMixed5 = rb.makeAgent(rules6);
 		agents.add(evolvedMixed5);
 		population.add(new AgentPlayer("Evolved Mixed 3", evolvedMixed5));
 		
 		int[] chromossome7 = {41,7,68,35,45,65,28,34,36,13,61,25,37,19,59,46,31,30,15,49,6,3,1,29,51,58,54,48,47,66,50,42,56,18,33,0,4,12,53,2,39,57,14,24,8,44,16,55,64,67,26,27,52,32,23,21,40,10,22,17,62,63,60,43,11,9,38,5,20};
 		Rule[] rules7 = new Rule[chromossome7.length+1];
 		for (int i = 0; i < chromossome7.length; i++) {
 			rules7[i] = rb.ruleMapping(chromossome7[i]);
 		}
 		HistogramAgent evolvedMixed4 = rb.makeAgent(rules7);
 		agents.add(evolvedMixed4);
 		population.add(new AgentPlayer("Evolved Mixed 4", evolvedMixed4));

 		
 		int[] chromossome8 = {7,10,21,35,56,13,60,68,36,26,27,2,19,65,32,66,53,8,44,16,55,17,24,12,15,64,38,50,37,62,45,46,41,34,47,29,3,51,58,54,14,9,31,40,42,63,33,0,5,39,4,23,61,43,22,11,57,48,25,1,20,28,52,6,18,67,59,49,30};
 		Rule[] rules8 = new Rule[chromossome8.length+1];
 		for (int i = 0; i < chromossome8.length; i++) {
 			rules8[i] = rb.ruleMapping(chromossome8[i]);
 		}
 		HistogramAgent evolvedMirror2= rb.makeAgent(rules8);
 		agents.add(evolvedMirror2);
 		population.add(new AgentPlayer("Evolved Mirror 2", evolvedMirror2));
 		
 		int[] chromossome9 = {52,38,7,10,13,62,36,27,19,55,17,24,34,64,31,37,41,54,48,32,21,0,39,53,18,2,1,68,8,40,9,26,60,67,12,23,50,59,61,3,63,44,22,14,66,35,15,45,20,28,42,57,51,58,65,4,47,25,43,16,49,56,30,33,29,11,6,5,46};
 		Rule[] rules9 = new Rule[chromossome9.length+1];
 		for (int i = 0; i < chromossome9.length; i++) {
 			rules9[i] = rb.ruleMapping(chromossome9[i]);
 		}
 		HistogramAgent evolvedMirror3= rb.makeAgent(rules9);
 		agents.add(evolvedMirror3);
 		population.add(new AgentPlayer("Evolved Mirror 3", evolvedMirror3));
 		
// 		int[] chromossome6 = {6,40,8,10,13,28,37,22,18,49,2,5,24,33,31,35,26,14,38,34,21,44,25,9,15,19,36,32,45,39,47,20,3,4,29,7,16,48,42,17,43,1,27,46,0,41,12,30,23,11};
// 		Rule[] rules6 = new Rule[chromossome6.length+1];
// 		for (int i = 0; i < chromossome6.length; i++) {
// 			rules6[i] = Rulebase.ruleMapping(chromossome6[i]);
// 		}
// 		HistogramAgent evolvedMixed3 = Rulebase.makeAgent(rules6);
// 		agents.add(evolvedMixed3);
// 		population.add(new AgentPlayer("Evolved Mixed 3", evolvedMixed3));
// 		
// 		int[] chromossome7 = {8,1,10,13,28,22,18,41,38,35,9,34,0,30,33,43,2,15,25,40,21,17,11,19,32,31,46,20,42,47,6,29,23,24,37,3,7,5,14,49,48,36,12,27,39,16,44,4,45,26};
// 		Rule[] rules7 = new Rule[chromossome7.length+1];
// 		for (int i = 0; i < chromossome7.length; i++) {
// 			rules7[i] = Rulebase.ruleMapping(chromossome7[i]);
// 		}
// 		HistogramAgent evolvedMixed4 = Rulebase.makeAgent(rules7);
// 		agents.add(evolvedMixed4);
// 		population.add(new AgentPlayer("Evolved Mixed 4", evolvedMixed4));
// 		
// 		int[] chromossome8 = {7,35,2,41,46,47,13,36,23,16,17,48,49,44,30,6,8,27,22,29,37,40,12,42,25,5,38,28,20,34,9,45,31,33,39,3,43,32,24,21,26,18,15,4,14,0,11,1,10,19};
// 		Rule[] rules8 = new Rule[chromossome8.length+1];
// 		for (int i = 0; i < chromossome8.length; i++) {
// 			rules8[i] = Rulebase.ruleMapping(chromossome8[i]);
// 		}
// 		HistogramAgent evolvedMirror2= Rulebase.makeAgent(rules8);
// 		agents.add(evolvedMirror2);
// 		population.add(new AgentPlayer("Evolved Mirror 2", evolvedMirror2));
// 		
// 		int[] chromossome9 = {7,35,2,41,46,47,13,36,23,16,17,48,49,44,30,6,8,27,22,29,37,40,12,42,25,5,38,28,20,34,9,45,31,33,39,3,43,32,24,21,26,18,15,4,14,0,11,1,10,19};
// 		Rule[] rules9 = new Rule[chromossome9.length+1];
// 		for (int i = 0; i < chromossome9.length; i++) {
// 			rules9[i] = Rulebase.ruleMapping(chromossome9[i]);
// 		}
// 		HistogramAgent evolvedMirror3= Rulebase.makeAgent(rules9);
// 		agents.add(evolvedMirror3);
// 		population.add(new AgentPlayer("Evolved Mirror 3", evolvedMirror3));

     		
// 		int[] chromossome4 = {46,8,10,13,28,17,20,22,18,29,49,41,38,34,9,24,37,45,35,47,0,12,15,14,6,43,48,2,23,27,1,21,42,16,44,4,7,25,3,40,39,26,33,11,19,36,32,31,5,30};
// 		Rule[] rules4 = new Rule[chromossome4.length+1];
// 		for (int i = 0; i < chromossome4.length; i++) {
// 			rules4[i] = Rulebase.ruleMapping(chromossome4[i]);
// 		}
// 		HistogramAgent PartialEvolvedMixed1 = Rulebase.makeAgent(rules4);
// 		agents.add(PartialEvolvedMixed1);
// 		population.add(new AgentPlayer("Partial Evolved Mixed 1", PartialEvolvedMixed1));
// 		
// 		int[] chromossome5 = {8,10,13,28,33,22,18,49,41,5,24,37,45,35,15,14,6,34,27,21,44,25,9,26,19,36,32,31,39,42,20,3,4,46,29,1,7,16,48,47,17,0,12,30,23,11,38,43,40,2};
// 		Rule[] rules5 = new Rule[chromossome5.length+1];
// 		for (int i = 0; i < chromossome5.length; i++) {
// 			rules5[i] = Rulebase.ruleMapping(chromossome5[i]);
// 		}
// 		HistogramAgent partialEvolvedMixed2 = Rulebase.makeAgent(rules5);
// 		agents.add(partialEvolvedMixed2);
// 		population.add(new AgentPlayer(" Partial Evolved Mixed 2", partialEvolvedMixed2));
// 		
// 		int[] chromossome6 = {8,13,17,45,16,34,9,48,24,2,0,4,25,46,6,41,26,49,15,42,33,28,21,39,31,18,22,3,40,10,12,20,43,36,37,32,30,35,47,1,38,44,19,29,14,7,27,23,11,5};
// 		Rule[] rules6 = new Rule[chromossome6.length+1];
// 		for (int i = 0; i < chromossome6.length; i++) {
// 			rules6[i] = Rulebase.ruleMapping(chromossome6[i]);
// 		}
// 		HistogramAgent PartialEvolvedMixed3 = Rulebase.makeAgent(rules6);
// 		agents.add(PartialEvolvedMixed3);
// 		population.add(new AgentPlayer("Partial Evolved Mixed 3", PartialEvolvedMixed3));
// 		
// 		int[] chromossome7 = {47,8,10,41,45,20,35,12,11,34,9,37,0,26,15,39,6,33,48,2,23,49,1,21,42,16,44,4,7,25,3,40,27,43,29,18,36,32,31,5,30,46,22,13,17,28,19,14,38,24};
// 		Rule[] rules7 = new Rule[chromossome7.length+1];
// 		for (int i = 0; i < chromossome7.length; i++) {
// 			rules7[i] = Rulebase.ruleMapping(chromossome7[i]);
// 		}
// 		HistogramAgent PartialEvolvedMixed4 = Rulebase.makeAgent(rules7);
// 		agents.add(PartialEvolvedMixed4);
// 		population.add(new AgentPlayer("Partial Evolved Mixed 4", PartialEvolvedMixed4));
 		
 		Vector<AgentPlayer> baseline = rb.GetBaselineAgentPlayers();
     
 		for (AgentPlayer b:baseline) {
 			population.add(b);
 		}
       
        Vector<Vector<Double>> scores = new Vector<Vector<Double>>();
        
        for (int j = 0; j<population.size();j++) {
        		scores.add(new Vector<Double>());
        }
        
        for (int i = 0; i<repetitions; i++) {
        		PopulationEvaluationSummary pes = TestSuite.mixedPopulationEvaluation(population, baseline, 5, numGames);
        		for (int j = 0; j<population.size();j++) {
        			Vector<Double> individualScore = scores.get(j);
        			individualScore.add(pes.getScoreIndividualAgent(j));
        		}
        }
        
        for (int j = 0; j<population.size(); j++) {
        		System.out.println("Agent " + population.get(j) + " stats: ");
            System.out.println("Mean: " + Utils.getMean(scores.get(j)));
            System.out.println("SD: " + Utils.getStandardDeviation(scores.get(j)));
            System.out.println("Min : " + Utils.getMin(scores.get(j)));
            System.out.println("Max: " + Utils.getMax(scores.get(j)));
        		
        }
        
//        int i =0;
//        Vector<Double> lastIndexes = new Vector<Double>();
//        Vector<Double> numRules = new Vector<Double>();
//        for (HistogramAgent h : agents){
//           	System.out.println("Results for agent " + i);
//           	h.printHistogram();
//           	int last = h.getLastFiredRuleIndex();
//           	System.out.println(last);
//           	lastIndexes.add((double)last);
//           	int total = h.getNumberOfFiredRules();
//           	System.out.println(total);
//           	numRules.add((double)total);
//           	System.out.println(" ");
//           	i++;
//        }
//        
//        System.out.println("Metric: last index fired:");
//        System.out.println("Mean: " + Utils.getMean(lastIndexes));
//        System.out.println("SD: " + Utils.getStandardDeviation(lastIndexes));
//        System.out.println("Min : " + Utils.getMin(lastIndexes));
//        System.out.println("Max: " + Utils.getMax(lastIndexes));
//        
//        System.out.println("Metric: number of rules fired:");
//        System.out.println("Mean: " + Utils.getMean(numRules));
//        System.out.println("SD: " + Utils.getStandardDeviation(numRules));
//        System.out.println("Min : " + Utils.getMin(numRules));
//        System.out.println("Max: " + Utils.getMax(numRules));
    }
}
