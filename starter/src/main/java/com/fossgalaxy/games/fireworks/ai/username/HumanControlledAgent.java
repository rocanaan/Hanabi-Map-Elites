package com.fossgalaxy.games.fireworks.ai.username;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.annotations.AgentConstructor;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple agent that performs a random move.
 *
 * <b>IMPORTANT</b> You should rename this agent to your username
 */
public class HumanControlledAgent implements Agent {
    private Random random;

    public HumanControlledAgent() {
        this.random = new Random();
    }

    public Action doMove(int playerID, GameState gameState) {
        //TODO replace this with your agent

        //get all legal moves as a list
        List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(playerID, gameState));
        
        
        // 1) Print the gamestate
        // 2) Print available actions
        // 3) Ask for user input
        // 4) Implement the chosen action
        
        // Later on exchange 3 for tree search, neural network, evolutionary action planning, whatever

        //choose a random item from that list and return it
        int moveToMake = random.nextInt(possibleMoves.size());
        return possibleMoves.get(moveToMake);
    }

}
