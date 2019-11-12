package com.fossgalaxy.games.fireworks.ai;

import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class HistogramAgent extends ProductionRuleAgent implements Serializable{
    public ArrayList<Integer> histogram;
    int aID;
    protected List<Rule> rules;
    
    public HistogramAgent(ProductionRuleAgent pra){
        histogram = new ArrayList<Integer>();
        rules = pra.getRules();
        for (Rule rule : rules){
            histogram.add(0);
        }
        // Fallback if none of the rules fire
        histogram.add(0);
    }
    
    @Override
    public Action doMove(int agentID, GameState state) {
        aID = agentID;
        int index = 0;
        for (Rule rule : rules) {
            if (rule.canFire(agentID, state)) {
                Action selected = rule.execute(agentID, state);
                histogram.set(index, histogram.get(index)+1);
                return selected;
            }
            else{
                index++;
            }
        }
        histogram.set(index,histogram.get(index)+1 );
        return doDefaultBehaviour(agentID, state);
    }
    
    public void printHistogram(){
        int index = 0;
        System.out.println("Agent "+aID+":");
        int totalFired = 0;
        for (Integer i : histogram){
            if (index < histogram.size()-1) {
            	System.out.println("Rule "+index+  " " + rules.get(index) + " fired "+i+" times.");
            }
            else {
            	System.out.println("Fallback fired "+i+" times.");
            }
            index++;
            totalFired += i;
        }
        if (totalFired >0) {
        System.out.print("[");
	        for (int i = 0; i<histogram.size();i++) {
	        	System.out.print((double)histogram.get(i)/(double)totalFired);
	        	if (i < histogram.size()-1) {
	            	System.out.print(",");
	        	}
	        }
	        System.out.println(']');
        }
    }
    
    
    // Index of last rule fired at least minTimes
    public int getLastFiredRuleIndex(int minTimes) {
    		int last = -1;
    		for (int i = 0; i< histogram.size(); i++) {
    			int activations = histogram.get(i);
    			if (activations > minTimes) {
    				last = i;
    			}
    		}
    		return last;
    }
    
    // Index of last rule fired at least once
    public int getLastFiredRuleIndex() {
		return getLastFiredRuleIndex(1);
    }

    
    //Number of rules that fired at least minTimes
    public int getNumberOfFiredRules(int minTimes) {
    		int fired = 0;
    		for (Integer i: histogram) {
    			if (i>=minTimes) {
    				fired ++;
    			}
    		}
    		return fired;
    }
    
    // Number of rules fired at least once
    public int getNumberOfFiredRules() {
    		return getNumberOfFiredRules(1);
    }
    
   
}
