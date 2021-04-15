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



/**
 * A sample agent for the learning track.
 * 
 * This agent demonstrates how to get the player IDs for the paired agents.
 * 
 * 
 * You can see more agents online at:
 * https://git.fossgalaxy.com/iggi/hanabi/tree/master/src/main/java/com/fossgalaxy/games/fireworks/ai
 */
public class TriangulationMetaAgent implements Agent {
	
	public enum BehaviorCharacteristic{
		COMMUNICATIVENESS, RISKAVERSION, IPP
	}

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
	private boolean[][] knowsColorMask;
	private boolean [][] knowsRankMask;
	
	int totalmoves;
	int specializedmoves;
	int defaultmoves;
	int randommoves;
	
	private int theirGivenDim1;
	private int theirGivenDim2;
	private int myGivenDim1;
	private int myGivenDim2;
	
	private static boolean useRiskAversion = true;
	
	Agent[] defaultAgentByNumPlayers;
	
	
 	
	public TriangulationMetaAgent() {
		this.actionHistory = new HashMap<>();
		this.playerStatsRecord = new HashMap<>();
		totalmoves = 0;
		defaultmoves = 0;
		randommoves = 0;
		
   		//TODO: This should be the coordinates of the generalist agent, not its chromosome, which is outdated
		//Best agent evolved with Map-Elites for 2 players. Communicativeness is about 0.5 and risk aversion is about 0.8. In my testing, this scores around 20.5 in Mirror
//		int [] twoPlayerChromosome = {67,103,7,35,100,52,77,20,16,28,23,27,37,43,101} ; // old rules
//		int [] twoPlayerChromosome = {56,61,8,68,57,33,63,65,28,104,34,37,102,19,101}; // ToG 2P
		int [] twoPlayerChromosome = {8,10,67,61,101,38,33,37,2,102,36,93,27,23,19}; //ToG 2P2, niche 9,16
//		int [] twoPlayerChromosome = {58,52,10,32,65,75,12,20,20,26,69,94,55,36,19}; //new rules
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
		
		this.setGivenDimensions(-1,-1,-1,-1);

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
					playerStatsRecord.put(name, new PlayerStats(0.0, 0.0, 0.0, 0.0, 0.0, 0));
				}
			}
		}
		
		

	}
	
	public void setGivenDimensions(int theirD1, int theirD2, int myD1, int myD2) {
		this.theirGivenDim1=theirD1;
		this.theirGivenDim2=theirD2;
		this.myGivenDim1=myD1;
		this.myGivenDim2=myD2;
				
	}



	@Override
	public Action doMove(int agentID, GameState state) {
		// this is where you make decisions on your turn.
		updateHistogram(state);
		
		System.out.println("Starting move with agent ID " + agentID);
		
		
		
		if (myTurnCount == 0) {
			numPlayers = state.getPlayerCount();
			numCards = state.getHandSize();
			//System.out.println("Setup " + numPlayers +  "  " + numCards + " Turn count " + myTurnCount);
			playerHands = new Card[numPlayers][numCards];
			playabilityMask = new double[numPlayers][numCards];
			knowsColorMask = new boolean[numPlayers][numCards];
			knowsRankMask = new boolean[numPlayers][numCards];

		
			
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
			//TODO: Currently, does not generalize to other numbers of players

			String file = "2P2";

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
					
					
					if (move.action instanceof TellColour) {
						currentPlayerStats.numHints = currentPlayerStats.numHints+1;
						
						TellColour tc = (TellColour)move.action;
						CardColour color = tc.colour;
						int targetPlayerID = tc.player;
						if (targetPlayerID != agentID) {
							int cardIndex = 0;
							for (Card card:playerHands[targetPlayerID]) {
								if (card.colour == color) {
									knowsColorMask[targetPlayerID][cardIndex] = true;
								}
								++cardIndex;
							}
						}
						
					}
					if (move.action instanceof TellValue) {
						currentPlayerStats.numHints = currentPlayerStats.numHints+1;
						
						TellValue tv = (TellValue)move.action;
						int rank = tv.value;
						int targetPlayerID = tv.player;
						if (targetPlayerID != agentID) {
							int cardIndex = 0;
							for (Card card:playerHands[targetPlayerID]) {
								if (card.value == rank) {
									knowsRankMask[targetPlayerID][cardIndex] = true;
								}
								++cardIndex;
							}
						}
						
					}
					if (hintsAvailable>0) {
						currentPlayerStats.numPossibleHints = currentPlayerStats.numPossibleHints+1;
					}
					if (move.action instanceof PlayCard) {
						PlayCard play = (PlayCard)move.action;
						currentPlayerStats.numPlays = currentPlayerStats.numPlays+1;
						
						
						currentPlayerStats.totalPlayability = currentPlayerStats.totalPlayability + playabilityMask[activePlayerID][play.slot];
						
						
						int informationPlay = 0;
						if (knowsColorMask[activePlayerID][play.slot]) {
							informationPlay +=1;
						}
						if (knowsRankMask[activePlayerID][play.slot]) {
							informationPlay +=1;
						}
						currentPlayerStats.totalInformationPlays+=informationPlay;
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
		
		// Update what each player knows about their hand
		for (int playerID = 0; playerID < numPlayers; ++playerID) {
			Hand hand = state.getHand(playerID);
			for (int cardIndex = 0; cardIndex<hand.getSize(); ++cardIndex) {
				playerHands[playerID][cardIndex] = hand.getCard(cardIndex);
				if (hand.getKnownColour(cardIndex)!=null) {
					knowsColorMask[playerID][cardIndex] = true;
				}
				else {
					knowsColorMask[playerID][cardIndex] = false;
				}
				if (hand.getKnownValue(cardIndex)!=null) {
					knowsRankMask[playerID][cardIndex] = true;
				}
				else {
					knowsRankMask[playerID][cardIndex] = false;
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
		double informationPlays = partnerStats.getInformationPlays();
		System.out.println("Communicativeness " + communicativeness);
		System.out.println("Risk Aversion " + riskAversion);
		System.out.println("Average Information per play " + informationPlays);


		int threshold = 0;
		Action action = null;
		if (partnerStats.totalInteractions>=threshold){
			ArrayList<Integer> partnerDimensions;
			if (useRiskAversion) {
				partnerDimensions = getNiche(communicativeness,riskAversion);
			}
			else {
				partnerDimensions = getNiche(communicativeness,informationPlays);
			}

			
			
			int [][][] matchups = MatchupTables.getMatchups(numPlayers);
			
			int theirDim1 = partnerDimensions.get(0);
			int theirDim2 = partnerDimensions.get(1);
			
			System.out.println("Inferred Partner Indexes: " + theirDim1 + " " + theirDim2);
			
			if (this.theirGivenDim1 >= 0) {
				theirDim1 = this.theirGivenDim1;
			}
			if (this.theirGivenDim2 >= 0) {
				theirDim2 = this.theirGivenDim2;
			}
			
			System.out.println("Partner Indexes given by Oracle: " + theirGivenDim1 + " " + theirGivenDim2);

			System.out.println("Final Partner Indexes: " + theirDim1 + " " + theirDim2);


			int myDim1 = matchups[theirDim1][theirDim2][0];
			int myDim2 = matchups[theirDim1][theirDim2][1];
			
			System.out.println("My dimensions calculated by Match-up Tables: " + myDim1 + " " + myDim2);
			System.out.println("My dimensions Given by the oracle: " + myGivenDim1 + " " + myGivenDim2);

			
			if (myGivenDim1 >= 0) {
				myDim1 = myGivenDim1;
			}
			if (myGivenDim2 >= 0) {
				myDim2 = myGivenDim2;
			}
			
			System.out.println("My final dimensions :" + myDim1 + " " + myDim2);


			//TODO: check correct order of dimensions
	        Agent agent = agents.get(20*myDim1 + myDim2);
	        try {
	        		action = agent.doMove(agentID, state);
	        }
	        catch(Exception e) {
	        		action = null;
	        }
		}
		if (action != null) {
			specializedmoves++;
		}
		else {   // if threshold hasn't been achieved, default to the action that our initial agent would take
	        try {
        			action = defaultAgentByNumPlayers[numPlayers-2].doMove(agentID, state);
	        }
	        catch(Exception e) {
        			action = null;
	        }
	        if (action != null) {
	        	defaultmoves++;
	        }
	        else { // if action is still null for some reason, such as the agent returning an invalid action, default to our initial agent
		        List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(agentID, state));

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
	    
	    
	    public void printPlayerStats() {
	        int i = 0;
	        for(String key:playerStatsRecord.keySet()) {
	            PlayerStats stats = playerStatsRecord.get(key);
	            
	            String message =  "I estimate partner "  + key + " to have communicativeness of " +  String.valueOf(stats.getCommunicativeness()) + " , risk aversion of " + String.valueOf(stats.getRiskAversion())  +  " and information per plays of " + String.valueOf(stats.getInformationPlays()) + " after number of interactions " + String.valueOf(stats.totalInteractions); 
	            System.out.println(message);
	        }
	    }
	    
	    public double getEstimatedDimensions(String key,BehaviorCharacteristic bc) {
	    	if (bc==BehaviorCharacteristic.COMMUNICATIVENESS){
	    		return playerStatsRecord.get(key).getCommunicativeness();
	    	}
	    	else if (bc==BehaviorCharacteristic.RISKAVERSION){
	    		return playerStatsRecord.get(key).getRiskAversion();
	    	}
	    	else if (bc==BehaviorCharacteristic.IPP){
	    		return playerStatsRecord.get(key).getInformationPlays();
	    	}
	    	return 0;
	    }
	    
	    
	    public  void toXML(int[] generalist,int[][][]matchups,String file) {
	    	XMLConfiguration configCreate = new XMLConfiguration();
	    	
	    	configCreate.addProperty("generalist",generalist);
	    	configCreate.addProperty("matchups",matchups);
	    	configCreate.addProperty("file", file);
	    }
	    
	    public void readFile(String file) {
	    	
	    }

		
}
