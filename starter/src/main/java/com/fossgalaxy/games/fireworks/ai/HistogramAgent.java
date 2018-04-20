package com.fossgalaxy.games.fireworks.ai;

import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import java.util.ArrayList;
import java.util.List;


public class HistogramAgent extends ProductionRuleAgent{
    ArrayList<Integer> histogram;
    int aID;
    protected List<Rule> rules;
    
    HistogramAgent(ProductionRuleAgent pra){
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
}
