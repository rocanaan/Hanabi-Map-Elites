package com.fossgalaxy.games.fireworks.ai.username;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.iggi.IGGIFactory;
import com.fossgalaxy.games.fireworks.ai.mcts.MCTS;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;

/**
 * Example showing how to build a production rule agent.
 *
 * These agents execute a series of rules in order.
 * 
 * You can see more agents online at:
 * https://git.fossgalaxy.com/iggi/hanabi/tree/master/src/main/java/com/fossgalaxy/games/fireworks/ai
 */
public class SampleAgents {

    // see https://git.fossgalaxy.com/iggi/hanabi/blob/master/src/main/java/com/fossgalaxy/games/fireworks/ai/iggi/IGGIFactory.java#L114
    @AgentBuilderStatic("hanabi_piers")
    public static Agent buildCompPiers() {
        return IGGIFactory.buildPiersPlayer();
    }

    // see https://git.fossgalaxy.com/iggi/hanabi/blob/master/src/main/java/com/fossgalaxy/games/fireworks/ai/iggi/IGGIFactory.java#L61
    @AgentBuilderStatic("hanabi_iggi2")
    public static Agent buildCompIGGI2Player() {
        return IGGIFactory.buildIGGIPlayer();
    }

    // see https://git.fossgalaxy.com/iggi/hanabi/blob/master/src/main/java/com/fossgalaxy/games/fireworks/ai/iggi/IGGIFactory.java#L99
    @AgentBuilderStatic("hanabi_flawed")
    public static Agent buildCompFlawed() {
        return IGGIFactory.buildFlawedPlayer();
    }

    // see https://git.fossgalaxy.com/iggi/hanabi/blob/master/src/main/java/com/fossgalaxy/games/fireworks/ai/iggi/IGGIFactory.java#L168
    @AgentBuilderStatic("hanabi_random")
    public static Agent buildCompRandom() {
        return IGGIFactory.buildRandom();
    }



}
