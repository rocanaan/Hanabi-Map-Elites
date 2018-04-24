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
public class SampleLegalRandom implements Agent {
    private Random random;

    public SampleLegalRandom() {
        this.random = new Random();
    }

    public Action doMove(int playerID, GameState gameState) {
        //TODO replace this with your agent

        //get all legal moves as a list
        List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(playerID, gameState));
        
        
        

        //choose a random item from that list and return it
        int moveToMake = random.nextInt(possibleMoves.size());
        return possibleMoves.get(moveToMake);
    }

}
