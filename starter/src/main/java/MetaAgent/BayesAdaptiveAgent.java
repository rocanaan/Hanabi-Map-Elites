package MetaAgent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.configuration2.XMLConfiguration;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.CardColour;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;
import com.fossgalaxy.games.fireworks.state.HistoryEntry;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.actions.DiscardCard;
import com.fossgalaxy.games.fireworks.state.actions.PlayCard;
import com.fossgalaxy.games.fireworks.state.actions.TellColour;
import com.fossgalaxy.games.fireworks.state.actions.TellValue;

import Evolution.Rulebase;
import MetaAgent.HistogramAgent;
import MetaAgent.MatchupTables;
import MetaAgent.PlayerStats;


import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.collections4.keyvalue.MultiKey;
/**
 * A sample agent for the learning track.
 * 
 * This agent demonstrates how to get the player IDs for the paired agents.
 * 
 * 
 * You can see more agents online at:
 * https://git.fossgalaxy.com/iggi/hanabi/tree/master/src/main/java/com/fossgalaxy/games/fireworks/ai
 */
public class BayesAdaptiveAgent implements Agent {

	// Precomputed strategies, matchup information and training partner names. These all relate to training partners
	private Map<String, Agent> myStrategyPool;
	private ArrayList<String> trainingPoolCandidates;
	private Map<MultiKey<String>, MatchupInformation> precomputedMatchupInfo;
	
	// These relate to evaluation partners, which are not known in advance
	private Map<MultiKey<String>, Double> beliefDistribution;   // One entry would look like { ("anonymous1", "13 7"), 1/400}
	private Map<String, Integer> numGamesWithPartner;
	private Map<String, Integer> numTurnsWithPartner;
	
	
//	The following variables keep track of game state changes to compute BCs. They are assigned on the call to receiveID()
//	TODO: Perhaps they should be consolidated in a separate class
	private int myTurnCount; //This keeps track of the number of turns in the current game. Currently this is only used to do some set-up at game start. TODO: check if can be ommited in favor of GameState variable.
	private int hintsAvailable; //This keeps track of hints available over the tracked turns. Not the same as the hints available on the actual current turn.
	private int myID;
	private String[] currentPlayers;
	
	public BayesAdaptiveAgent(Map<String, Agent> myStrategyPool, ArrayList<String> trainingPoolCandidates,
			Map<MultiKey<String>, MatchupInformation> precomputedMatchupInfo) {
		super();
		this.myStrategyPool = myStrategyPool;
		this.trainingPoolCandidates = trainingPoolCandidates;
		this.precomputedMatchupInfo = precomputedMatchupInfo;
	} //TODO: Write a function that initializes this class by generating all the precomputed object from our files


	// GameRunner should call this at the start of each game.
	public void receiveID(int agentID, String[] names) {
		this.myID = agentID;
		this.currentPlayers = names;
		myTurnCount = 0;
		hintsAvailable = 8;
		
		for (String name:currentPlayers) {
			if (name != "you") {
				if (!numGamesWithPartner.containsKey(name)) {
					numGamesWithPartner.put(name, 0);
					numTurnsWithPartner.put(name, 0);
					for (String trainingPartner:trainingPoolCandidates) {
						MultiKey<String> key = new MultiKey<String>(name, trainingPartner);
						beliefDistribution.put(key, 1.0/trainingPoolCandidates.size());
					}	
				}
				numGamesWithPartner.put(name, numGamesWithPartner.get(name)+1);
			}
		}
	}


	@Override
	public Action doMove(int agentID, GameState state) {
		// TODO Auto-generated method stub
		String theirID; //TODO: get their ID
		
		updateHistory(); // new values for IPP, Comm, etc TODO Xianbo
		// if (adaptationCondition(){
			updateBeliefs(); // update belief distribution using Bayes theorem, if either number of games or turns is > thresholdTODO
//	}
			
			
		String responseID = getResponse(); //almost done
		Agent responsePolicy = myStrategyPool.get(responseID);
		
		Action action = responsePolicy.doMove(agentID, state);

		return action;
	}
	
	
	String  getResponse(String theirID) {
		
		for (String possibleResponse: myStrategyPool.keySet()) {
			
			double weightedScore = 0.0;
			
			for (String possiblePartner:trainingPoolCandidates) {
				double matchupScore = precomputedMatchupInfo.get( new  MultiKey<String>(possibleResponse, possiblePartner) ).scoreMean;
				double matchupWeight = beliefDistribution.get( new  MultiKey<String>(theirID, possiblePartner) ).scoreMean;
				weightedScore += beliefDistribution.get(new MultiKey<String>)
			}
			
		}
		
	}
	
	void updateBeliefs(double d1, double d2) {
		
	}
	
		
}
