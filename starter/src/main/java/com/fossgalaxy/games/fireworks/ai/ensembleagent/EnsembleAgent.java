<<<<<<< HEAD
package com.fossgalaxy.games.fireworks.ai.rodrigocanaan;
=======
package com.fossgalaxy.games.fireworks.ai.ensembleagent;
>>>>>>> c0b8e0190f2eed135c290160ea52fed1ad7c3ce0

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
<<<<<<< HEAD
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.ai.rodrigocanaan.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.rodrigocanaan.MatchupTables;
import com.fossgalaxy.games.fireworks.ai.rodrigocanaan.PlayerStats;
=======
import com.fossgalaxy.games.fireworks.ai.ensembleagent.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.ensembleagent.MatchupTables;
import com.fossgalaxy.games.fireworks.ai.ensembleagent.PlayerStats;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
>>>>>>> c0b8e0190f2eed135c290160ea52fed1ad7c3ce0
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;
import com.fossgalaxy.games.fireworks.state.HistoryEntry;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.actions.DiscardCard;
import com.fossgalaxy.games.fireworks.state.actions.PlayCard;
import com.fossgalaxy.games.fireworks.state.actions.TellColour;
import com.fossgalaxy.games.fireworks.state.actions.TellValue;

import Evolution.Rulebase;

/**
 * A sample agent for the learning track.
 * 
 * This agent demonstrates how to get the player IDs for the paired agents.
 * 
 * 
 * You can see more agents online at:
 * https://git.fossgalaxy.com/iggi/hanabi/tree/master/src/main/java/com/fossgalaxy/games/fireworks/ai
 */
public class EnsembleAgent implements Agent {

	private Map<String, Map<Action, Integer>> actionHistory;
	
	private Map<String, PlayerStats> playerStatsRecord;

	private  ArrayList<Agent>  agents;
	
	private Random random;
	private int myID;
	private String[] currentPlayers;
	
	private int myTurnCount;
	private int hintsAvailable;
	
	
	int numPlayers;
	int numCards;
	private Card[][] playerHands;
	private double[][] playabilityMask;
	
	int totalmoves;
	int specializedmoves;
	int defaultmoves;
	int randommoves;
	
	Agent[] defaultAgentByNumPlayers;
 	
	public EnsembleAgent() {
		this.actionHistory = new HashMap<>();
		this.playerStatsRecord = new HashMap<>();
		totalmoves = 0;
		defaultmoves = 0;
		randommoves = 0;
		
   		//Best agent evolved with Map-Elites for 2 players. Communicativeness is about 0.5 and risk aversion is about 0.8. In my testing, this scores around 20.5 in Mirror
		int [] twoPlayerChromosome = {67,103,7,35,100,52,77,20,16,28,23,27,37,43,101} ; 
		//Agent with overall best pairings in Map-Elites for 3 players. Communicativeness is about 0.55 and risk aversion is about 0.85. 
		int [] threePlayerChromosome = {7,33,50,50,67,36,51,50,41,3,82,103,55,31,83};
		//Agent with overall best pairings in Map-Elites for 4 players. Communicativeness is about 0.6 and risk aversion is about 0.75. 
		int [] fourPlayerChromosome = {8,50,64,46,46,68,59,26,44,27,2,98,86,64,3}; 
		//Agent with overall best pairings in Map-Elites for 5 players. Communicativeness is about 0.7 and risk aversion is about 0.8. 
		int [] fivePlayerChromosome = {64,50,58,7,25,68,21,29,30,54,99,28,31,14,71};
	
		Rulebase rb = new Rulebase(false);
		defaultAgentByNumPlayers  = new Agent[4];
		defaultAgentByNumPlayers[0] = rb.makeAgent(twoPlayerChromosome);
		defaultAgentByNumPlayers[1] = rb.makeAgent(threePlayerChromosome);
		defaultAgentByNumPlayers[2] = rb.makeAgent(fourPlayerChromosome);
		defaultAgentByNumPlayers[3] = rb.makeAgent(fivePlayerChromosome);
	}
	
	@Override
	public void receiveID(int agentID, String[] names) {
		this.myID = agentID;
		this.currentPlayers = names;
		this.random = new Random();
		myTurnCount = 0;
		hintsAvailable = 8;
		
		for (String name:currentPlayers) {
			if (name != "you") {
				if (!playerStatsRecord.containsKey(name)) {
					playerStatsRecord.put(name, new PlayerStats(0.0, 0.0, 0.0, 0.0, 0));
				}
			}
		}
		

	}



	@Override
	public Action doMove(int agentID, GameState state) {
		// this is where you make decisions on your turn.
		updateHistogram(state);
		
		
		
		if (myTurnCount == 0) {
			numPlayers = state.getPlayerCount();
			numCards = state.getHandSize();
			//System.out.println("Setup " + numPlayers +  "  " + numCards + " Turn count " + myTurnCount);
			playerHands = new Card[numPlayers][numCards];
			playabilityMask = new double[numPlayers][numCards];
			
		
			
			for (int playerIndex = 0; playerIndex<numPlayers; ++playerIndex) {
				//System.out.format("Player %d's hand", playerIndex);
				if (playerIndex!=agentID) {
					for (int slotIndex = 0; slotIndex<numCards; ++slotIndex) {
						playerHands[playerIndex][slotIndex] = state.getCardAt(playerIndex, slotIndex);
						playabilityMask [playerIndex] = getPlayabilityMask(playerIndex,state);
						//System.out.print(" " + playerHands[playerIndex][slotIndex].colour + " " +  playerHands[playerIndex][slotIndex].value);
						//System.out.print(" " + playabilityMask[playerIndex][slotIndex]);

					}
										
				}
				
			}
			String file = numPlayers+"P";

		    agents = AgentLoaderFromFile.makeAgentsFromFile(file, 20, 20, false);

		}
		

		GameState workingState = state.getCopy();
		
		List<HistoryEntry> lastMoves = getLastMoves(state);
		int numMoves = lastMoves.size();
		//System.out.println("My ID is " + agentID + " There were " + numMoves + " moves since last state on my turn count " + myTurnCount);
		if (myTurnCount !=0 ) { //skipping a possibly incomplete first round in order to not have to deal with edge cases
			Collections.reverse(lastMoves);
			for(HistoryEntry move:lastMoves) {
				int activePlayerID = move.playerID;
				if (activePlayerID!=agentID) {
					// Update player specific information;
					PlayerStats currentPlayerStats = playerStatsRecord.get(currentPlayers[activePlayerID]);
					
					if (move.action instanceof TellColour || move.action instanceof TellValue) {
						currentPlayerStats.numHints = currentPlayerStats.numHints+1;
					}
					if (hintsAvailable>0) {
						currentPlayerStats.numPossibleHints = currentPlayerStats.numPossibleHints+1;
					}
					if (move.action instanceof PlayCard) {
						PlayCard play = (PlayCard)move.action;
						currentPlayerStats.numPlays = currentPlayerStats.numPlays+1;
						//currentPlayerStats.totalPlayability = currentPlayerStats.totalPlayability + playabilityMask[activePlayerID][play.slot];
					}
					currentPlayerStats.totalInteractions+=1;
					//System.out.println("Total Interactions with player " + currentPlayers[activePlayerID] + " is "  + playerStatsRecord.get(currentPlayers[activePlayerID]).totalInteractions);
				}

				
				
				// Update general state information - that is: number of hints availabble after this move
				if (move.action instanceof PlayCard) {
					PlayCard playAction = (PlayCard)move.action;
					//System.out.println("Card slot " + playAction.slot);
					//System.out.println("Active PLayer ID " + activePlayerID);

					Card playedCard = playerHands[activePlayerID][playAction.slot];
					//System.out.println("Played card was " + playerHands[activePlayerID][playAction.slot]);// + " color " + playerHands[activePlayerID][playAction.slot].colour + " value " + playerHands[activePlayerID][playAction.slot].value);
					if (playedCard!=null && playedCard.value == 5) {
						if (state.getTableValue(playedCard.colour)==5) {
							++hintsAvailable;
						}
					}
					
				}
				if (move.action instanceof DiscardCard) {
					++hintsAvailable;
				}
				if (move.action instanceof TellColour || move.action instanceof TellValue) {
					--hintsAvailable;
				}
			}
		
		}
		
		
//		//System.out.println("Hints available after this action: "  + hintsAvailable);
		
//		for (int playerIndex = 0; playerIndex<numPlayers; ++playerIndex) {
//			//System.out.format("Player %d's hand", playerIndex);
//			if (playerIndex!=agentID) {
//				for (int slotIndex = 0; slotIndex<numCards; ++slotIndex) {
//					playerHands[playerIndex][slotIndex] = state.getCardAt(playerIndex, slotIndex);
//					//System.out.print(" " + playerHands[playerIndex][slotIndex].colour + " " +  playerHands[playerIndex][slotIndex].value);
//				}
//				
//			}
//		}
		
		if (hintsAvailable != state.getInformation()) {
			//System.out.println("-------- WARNING: hints were miscalculated ----- ");
		}
        hintsAvailable = state.getInformation();
		++myTurnCount;
		
		playerHands = new Card[numPlayers][numCards];
		playabilityMask = new double[numPlayers][numCards];
		
		for (int playerIndex = 0; playerIndex<numPlayers; ++playerIndex) {
			//System.out.format("Player %d's hand", playerIndex);
			if (playerIndex!=agentID) {
				for (int slotIndex = 0; slotIndex<numCards; ++slotIndex) {
					playerHands[playerIndex][slotIndex] = state.getCardAt(playerIndex, slotIndex);
					playabilityMask [playerIndex] = getPlayabilityMask(playerIndex,state);
					//System.out.print(" " + playerHands[playerIndex][slotIndex].colour + " " +  playerHands[playerIndex][slotIndex].value);
					//System.out.print(" " + playabilityMask[playerIndex][slotIndex]);

				}
									
			}
			
		}
		
		
		// calculate possible moves
        //List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(agentID, state));

		PlayerStats partnerStats = playerStatsRecord.get(currentPlayers[(agentID+1)%numPlayers]);
		double communicativeness = partnerStats.getCommunicativeness();
		double riskAversion = partnerStats.getRiskAversion();
<<<<<<< HEAD
		riskAversion = 0.8;
=======
>>>>>>> c0b8e0190f2eed135c290160ea52fed1ad7c3ce0
		int threshold = 0;
		Action action = null;
		if (partnerStats.totalInteractions>=threshold){
			ArrayList<Integer> partnerDimensions = getNiche(communicativeness,riskAversion);
			int [][][] matchups = MatchupTables.getMatchups(numPlayers);
			int myDim1 = matchups[partnerDimensions.get(0)][partnerDimensions.get(1)][0];
			int myDim2 = matchups[partnerDimensions.get(0)][partnerDimensions.get(1)][1];
<<<<<<< HEAD
			System.out.println(myDim1);
			System.out.println(myDim2);
	        Agent agent = agents.get(myDim2 + 20*myDim1);
	        try {
        			System.out.println("Trying to get Action");
	        		action = agent.doMove(agentID, state);
	        		System.out.println(action);
//	        		System.exit(-1)
	        }
	        catch(Exception e) {
	        		System.err.println("oops");
	        		System.err.println(e);
=======
	        Agent agent = agents.get(myDim2 + 20*myDim1);
	        try {
	        		action = agent.doMove(agentID, state);
	        }
	        catch(Exception e) {
>>>>>>> c0b8e0190f2eed135c290160ea52fed1ad7c3ce0
	        		action = null;
	        }
		}
		
		if (action != null) {
			specializedmoves++;
		}
<<<<<<< HEAD
=======
<<<<<<<< HEAD:starter/src/main/java/com/fossgalaxy/games/fireworks/ai/rodrigocanaan/Rodrigocanaan.java
>>>>>>> c0b8e0190f2eed135c290160ea52fed1ad7c3ce0
		else {// if threshold hasn't been achieved, default to the action that our initial agent would take
	        try {
        			action = defaultAgentByNumPlayers[numPlayers-2].doMove(agentID, state);
	        }
	        catch(Exception e) {
        			action = null;
	        }
	        if (action!=null){
	        	defaultmoves++;
	        }
	        else {
	        	 List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(agentID, state));
<<<<<<< HEAD
=======
========
		if (action == null) { // if action is still null for some reason, such as the agent returning an invalid action, default to our initial agent
	        List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(agentID, state));
>>>>>>>> c0b8e0190f2eed135c290160ea52fed1ad7c3ce0:starter/src/main/java/com/fossgalaxy/games/fireworks/ai/ensembleagent/EnsembleAgent.java
>>>>>>> c0b8e0190f2eed135c290160ea52fed1ad7c3ce0

	 	        //choose a random item from that list and return it
	 	        int moveToMake = random.nextInt(possibleMoves.size());
	 	        action = possibleMoves.get(moveToMake);
	 	        randommoves++;
	        }
		}
		totalmoves++;
		double specializedRatio = (double)specializedmoves/(double)totalmoves;
		double defaultRatio = (double)defaultmoves/(double)totalmoves;
		double randomRatio = (double)randommoves/(double)totalmoves;

		System.out.println("Specialized moves " + specializedRatio);
		System.out.println("Default moves " + defaultRatio);
		System.out.println("Random moves " + randomRatio);

		return action;

	        

//        return possibleMoves.get(moveToMake);
	}
	
	
	public static ArrayList<Integer> getNiche(double feature1, double feature2){
		
		ArrayList<Integer> coords = new ArrayList<Integer>();
		int d1=20;
		int d2=20;
		int c1 = (int) Math.floor(feature1 / (1.0/(double)d1));
		if (c1 == d1) {
			c1 --;
		}
		
		int c2 = (int) Math.floor(feature2 / (1.0/d2));
		if (c2 == d2) {
			c2 --;
		}
//		//System.out.println("Calculating second niche");
//		//System.out.println(feature2);
//		//System.out.println(1/d2);
//		//System.out.println(c2);
		
		coords.add(c1);
		coords.add(c2);
		return coords;
	}
	/**
	 * A sample history measure, keeping track of how many times a player has made a given move.
	 * 
	 * For the 'learning' track, this will work across games. You'd probably want to do something more
	 * complex, but this is just provided as an example :).
	 * 
	 * @param state the current game state
	 */
	private void updateHistogram(GameState state) {
		// calculate history for previous moves
		List<HistoryEntry> lastMoves = getLastMoves(state);
		for (HistoryEntry entry : lastMoves) {
			
			String playerName = currentPlayers[entry.playerID];
			//grab this histogram for this player
			Map<Action, Integer> histogram = actionHistory.get(playerName);
			if (histogram == null) {
				histogram = new HashMap<>();
				actionHistory.put(playerName, histogram);
			}
			
			// update the histogram
			int previousValue = histogram.getOrDefault(entry.action, 0);
			histogram.put(entry.action, previousValue+1);
		}

	}
	
	/**
	 * Method to get all moves made since this agent's last go.
	 * 
	 * @param state the current game state
	 * @return the moves made since the agent's last go.
	 */
	private List<HistoryEntry> getLastMoves(GameState state) {
		List<HistoryEntry> allHistory = state.getActionHistory();
		int historySize = allHistory.size();
		
		List<HistoryEntry> movesSinceLastGo = new ArrayList<>();
		
		//we want the last n moves, if possible
		int movesToGet = Math.min(state.getPlayerCount(), historySize);
		
		for (int i = 0; i < movesToGet; i++) {
			int moveIndex = (historySize - (1+i));
			
			//negative game events are events that don't belong to any player, eg. game setup
			HistoryEntry entry = allHistory.get(moveIndex);
			if (entry.playerID < 0) {
				continue;
			}
			
			movesSinceLastGo.add(allHistory.get(moveIndex));
		}
		
		return movesSinceLastGo;
	}
	

	
	//TODO This currently only works in 2-player games, as in games with more players both
	//the playability status of each card might have changed (if some cards were played) 
	//and the information a player has might also have changed (if they received hints)
	// To fix this, I need to either generate new GameState objects for each move, or keep track of which cards would be  playable and what information a player has after each move separatly
	 public static double[] getPlayabilityMask(int targetID, GameState gameState){
			List<Double> probabilities = new ArrayList<Double>();
			
			if (targetID < gameState.getPlayerCount()) {
				
				List<Card> deck = gameState.getDeck().toList();
				
				Hand hand = gameState.getHand(targetID);
				
				// Adding target player's hand to the deck, as they don't know about it
				for(int slot=0;slot<hand.getSize();slot++) {
					Card c = hand.getCard(slot);
					if (c != null) {
						deck.add(c);
					}
					
				}
			
				Map<Integer, List<Card>> possibleCards = DeckUtils.bindBlindCard(targetID, gameState.getHand(targetID), deck);
				
				for (Map.Entry<Integer, List<Card>> entry : possibleCards.entrySet()) {
		            double probability = DeckUtils.getProbablity(entry.getValue(), x -> isPlayable(x, gameState));
		            probabilities.add(probability);
		        }
				
				int size = probabilities.size();
				if (size < 5) {
					for (int i = 0; i < (5-size); i++) {
						probabilities.add(0.0);
					}
				}
				
			}
			
			else {
				for (int i = 0; i<5; i++) {
					probabilities.add(0.0);
				}
			}
			double[] probabilityArray = {0,0,0,0,0};
			
			int i = 0;
			for (Double d: probabilities) {
				probabilityArray[i] = d;
				i++;
			}
			return probabilityArray;
		}
	 
	 
	    public static boolean isPlayable(Card card, GameState state) {
	        return state.getTableValue(card.colour) + 1 == card.value;
	    }
	    
	    

		
}
