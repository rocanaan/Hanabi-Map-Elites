/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossgalaxy.games.fireworks.ai;

import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;

import Evolution.Rulebase;

/**
 *
 * @author sparK1N9
 */
public class SpecializedAgent implements Agent{
    Agent[] specialists;
    
    // sp should be an array with size 2 or 4. 
    // if array size is 2: index 0 should be the agent for 2-payers, index 1 should be for 3-5 players 
    // if array size is 4: agents should be in order from 2-players to 5-players 
    public SpecializedAgent(Agent[] sp){
        specialists = sp;
    }
    
    public SpecializedAgent(int[][] chromosomes, Rulebase rb) {
    		specialists = new Agent[chromosomes.length];
    		for (int i = 0; i< chromosomes.length; i++) {
    			int[] c = chromosomes[i];
    			Rule[] rules = new Rule[c.length];
    			for (int j = 0; j< c.length; j++) {
    				rules[j] = rb.ruleMapping(c[j]);
    			}
    			specialists[i] = rb.makeAgent(rules);
    		}
    }

    @Override
    public Action doMove(int agentID, GameState state) {
        int pc = state.getPlayerCount();
        Action move = null;
        if (pc == 2){
            move = specialists[0].doMove(agentID, state);
        }
        else if (specialists.length == 2){
            move = specialists[1].doMove(agentID, state);
        }
        else{
            move = specialists[pc-2].doMove(agentID, state);
        }
        return move;
    }
    
}
