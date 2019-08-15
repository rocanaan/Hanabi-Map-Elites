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

        return doDefaultBehaviour(agentID, state);
    }
    
    public void printHistogram(){
        int index = 1;
        System.out.println("Agent "+aID+":");
        for (Integer i : histogram){
            System.out.println("Rule "+index+" fired "+i+" times.");
            index++;
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
