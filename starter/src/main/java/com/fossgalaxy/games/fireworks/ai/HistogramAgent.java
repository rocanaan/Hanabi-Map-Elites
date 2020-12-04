package com.fossgalaxy.games.fireworks.ai;

import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HistogramAgent extends ProductionRuleAgent implements Serializable{
    public ArrayList<Integer> histogram;
    int aID;
    protected List<Rule> rules;
    private boolean verbose = false;
    
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
        	try {
	            if (rule.canFire(agentID, state)) {
	                Action selected = rule.execute(agentID, state);
	                histogram.set(index, histogram.get(index)+1);
	                return selected;
	            }
        	}
        	catch (Exception e) {
        		if (verbose) {
	        		System.err.println("Exception " + e + " when executing rule " + rule);
	        		e.printStackTrace();
        		}
        	}
            index++;
        }
        histogram.set(index,histogram.get(index)+1 );
        try {
        	Action backup = doDefaultBehaviour(agentID, state);
        	if (backup != null){
        		return backup;
        	}
        }
        catch (Exception e) {
        	if (verbose) {
        		System.err.println("Exception " + e + " when trying to do backup action");
        	}
        }
        List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(agentID, state));
        //choose a random item from that list and return it
        int moveToMake = new Random().nextInt(possibleMoves.size());
        return possibleMoves.get(moveToMake);
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
