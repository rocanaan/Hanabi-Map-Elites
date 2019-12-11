/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossgalaxy.games.fireworks.ai.ensembleagent;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;

import Evolution.Rulebase;

/**
 *
 * @author Rodrigo Canaan
 */
public class BestPartner implements Agent{
    Agent[] specialists;
    Rulebase rb = new Rulebase(false);
    

    public BestPartner(){
    	
    		//Best agent evolved with Map-Elites for 2 players. Communicativeness is about 0.5 and risk aversion is about 0.8. In my testing, this scores around 20.5 in Mirror
		int [] twoPlayerChromosome = {67,103,7,35,100,52,77,20,16,28,23,27,37,43,101} ; 
		//Agent with overall best pairings in Map-Elites for 3 players. Communicativeness is about 0.55 and risk aversion is about 0.85. 
		int [] threePlayerChromosome = {7,33,50,50,67,36,51,50,41,3,82,103,55,31,83};
		//Agent with overall best pairings in Map-Elites for 4 players. Communicativeness is about 0.6 and risk aversion is about 0.75. 
		int [] fourPlayerChromosome = {8,50,64,46,46,68,59,26,44,27,2,98,86,64,3}; 
		//Agent with overall best pairings in Map-Elites for 5 players. Communicativeness is about 0.7 and risk aversion is about 0.8. 
		int [] fivePlayerChromosome = {64,50,58,7,25,68,21,29,30,54,99,28,31,14,71};
	
		
		specialists  = new Agent[4];
		specialists[0] = rb.makeAgent(twoPlayerChromosome);
		specialists[1] = rb.makeAgent(threePlayerChromosome);
		specialists[2] = rb.makeAgent(fourPlayerChromosome);
		specialists[3] = rb.makeAgent(fivePlayerChromosome);
		
    }

    @Override
    public Action doMove(int agentID, GameState state) {
        int pc = state.getPlayerCount();
        Action move = null;
        move = specialists[pc-2].doMove(agentID, state);
        return move;
    }
    
}
