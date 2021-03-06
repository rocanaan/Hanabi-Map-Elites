package com.fossgalaxy.games.fireworks.ai.username;

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
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
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

import MetaAgent.AgentLoaderFromFile;
import MetaAgent.HistogramAgent;
import MetaAgent.MatchupTables;
import MetaAgent.PlayerStats;
import evolution.Rulebase;

/**
 * A sample agent for the learning track.
 * 
 * This agent demonstrates how to get the player IDs for the paired agents.
 * 
 * 
 * You can see more agents online at:
 * https://git.fossgalaxy.com/iggi/hanabi/tree/master/src/main/java/com/fossgalaxy/games/fireworks/ai
 */
public class SampleLearning implements Agent {

	private Map<String, Map<Action, Integer>> actionHistory;
	
	private Map<String, PlayerStats> playerStatsRecord;

	private  ArrayList<HistogramAgent>  agents;
	
	private Random random;
	private int myID;
	private String[] currentPlayers;
	
	private int myTurnCount;
	private int hintsAvailable;
	
	
	int numPlayers;
	int numCards;
	private Card[][] playerHands;
	private double[][] playabilityMask;
	
	public SampleLearning() {
		this.actionHistory = new HashMap<>();
		this.playerStatsRecord = new HashMap<>();
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
			System.out.println("Setup " + numPlayers +  "  " + numCards + " Turn count " + myTurnCount);
			playerHands = new Card[numPlayers][numCards];
			playabilityMask = new double[numPlayers][numCards];
			
		
			
			for (int playerIndex = 0; playerIndex<numPlayers; ++playerIndex) {
				System.out.format("Player %d's hand", playerIndex);
				if (playerIndex!=agentID) {
					for (int slotIndex = 0; slotIndex<numCards; ++slotIndex) {
						playerHands[playerIndex][slotIndex] = state.getCardAt(playerIndex, slotIndex);
						playabilityMask [playerIndex] = getPlayabilityMask(playerIndex,state);
						System.out.print(" " + playerHands[playerIndex][slotIndex].colour + " " +  playerHands[playerIndex][slotIndex].value);
						System.out.print(" " + playabilityMask[playerIndex][slotIndex]);

					}
										
				}
				
			}
			String file = numPlayers+"P";

		    agents = AgentLoaderFromFile.makeAgentsFromFile(file, 20, 20, false);

		}

		
		
		List<HistoryEntry> lastMoves = getLastMoves(state);
		int numMoves = lastMoves.size();
		System.out.println("My ID is " + agentID + " There were " + numMoves + " moves since last state on my turn count " + myTurnCount);
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
					System.out.println("Total Interactions with player " + currentPlayers[activePlayerID] + " is "  + playerStatsRecord.get(currentPlayers[activePlayerID]).totalInteractions);
				}

				
				
				// Update general state information - that is: number of hints availabble after this move
				if (move.action instanceof PlayCard) {
					PlayCard playAction = (PlayCard)move.action;
					System.out.println("Card slot " + playAction.slot);
					System.out.println("Active PLayer ID " + activePlayerID);

					Card playedCard = playerHands[activePlayerID][playAction.slot];
					System.out.println("Played card was " + playerHands[activePlayerID][playAction.slot]);// + " color " + playerHands[activePlayerID][playAction.slot].colour + " value " + playerHands[activePlayerID][playAction.slot].value);
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
		
		
//		System.out.println("Hints available after this action: "  + hintsAvailable);
		
//		for (int playerIndex = 0; playerIndex<numPlayers; ++playerIndex) {
//			System.out.format("Player %d's hand", playerIndex);
//			if (playerIndex!=agentID) {
//				for (int slotIndex = 0; slotIndex<numCards; ++slotIndex) {
//					playerHands[playerIndex][slotIndex] = state.getCardAt(playerIndex, slotIndex);
//					System.out.print(" " + playerHands[playerIndex][slotIndex].colour + " " +  playerHands[playerIndex][slotIndex].value);
//				}
//				
//			}
//		}
		
		if (hintsAvailable != state.getInformation()) {
			System.out.println("-------- WARNING: hints were miscalculated ----- ");
		}
        hintsAvailable = state.getInformation();
		++myTurnCount;
		
		playerHands = new Card[numPlayers][numCards];
		playabilityMask = new double[numPlayers][numCards];
		
		for (int playerIndex = 0; playerIndex<numPlayers; ++playerIndex) {
			System.out.format("Player %d's hand", playerIndex);
			if (playerIndex!=agentID) {
				for (int slotIndex = 0; slotIndex<numCards; ++slotIndex) {
					playerHands[playerIndex][slotIndex] = state.getCardAt(playerIndex, slotIndex);
					playabilityMask [playerIndex] = getPlayabilityMask(playerIndex,state);
					System.out.print(" " + playerHands[playerIndex][slotIndex].colour + " " +  playerHands[playerIndex][slotIndex].value);
					System.out.print(" " + playabilityMask[playerIndex][slotIndex]);

				}
									
			}
			
		}
		
		
		// calculate possible moves
        //List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(agentID, state));

		PlayerStats partnerStats = playerStatsRecord.get(currentPlayers[(agentID+1)%numPlayers]);
		double communicativeness = partnerStats.getCommunicativeness();
		double riskAversion = partnerStats.getRiskAversion();
		int threshold = 0;
		Action action = null;
		if (partnerStats.totalInteractions>=threshold){
			ArrayList<Integer> partnerDimensions = getNiche(communicativeness,riskAversion);
			int [][][] matchups = MatchupTables.getMatchups(numPlayers);
			int myDim1 = matchups[partnerDimensions.get(0)][partnerDimensions.get(1)][0];
			int myDim2 = matchups[partnerDimensions.get(0)][partnerDimensions.get(1)][1];
	        HistogramAgent agent = agents.get(myDim2 + 20*myDim1);
	        try {
	        		action = agent.doMove(agentID, state);
	        }
	        catch(Exception e) {
	        		action = null;
	        }
		}
		if (action == null) { // if threshold hasn't been achieved, default to the action that our initial agent would take
			HistogramAgent agent = agents.get(215);
	        try {
        			action = agent.doMove(agentID, state);
	        }
	        catch(Exception e) {
        			action = null;
	        }
		}
		if (action == null) { // if action is still null for some reason, such as the agent returning an invalid action, default to our initial agent
	        List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(agentID, state));

	        //choose a random item from that list and return it
	        int moveToMake = random.nextInt(possibleMoves.size());
	        action = possibleMoves.get(moveToMake);
		}
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
//		System.out.println("Calculating second niche");
//		System.out.println(feature2);
//		System.out.println(1/d2);
//		System.out.println(c2);
		
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
