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
import org.apache.commons.math3.distribution.NormalDistribution;
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
	private HashMap<String, Agent> myStrategyPool;
	private ArrayList<String> trainingPoolCandidates;
	private MultiKeyMap<String, MatchupInformation> precomputedMatchupInfo;
	
	// These relate to evaluation partners, which are not known in advance
	private MultiKeyMap<String, Double> beliefDistribution;   // One entry would look like { ("anonymous1", "13 7"), 1/400}
	
//	private MultiKeyMap<String, PlayerStats> evaluationMatchupInfo; // Note: Currently we're not storing each match-up information in the long term
	
	private HashMap<String, PlayerStats> rollingMatchupInfo;   // This is the information on the current match-up for a given partner.
	private HashMap<String, Integer> rollingGamesWithPartner;
	private HashMap<String, Integer> rollingTurnsWithPartner;	
	
	
//	The following variables keep track of game state changes to compute BCs. They are assigned on the call to receiveID()
//	TODO: Perhaps they should be consolidated in a separate class
	private int myTurnCount; //This keeps track of the number of turns in the current game. Currently this is only used to do some set-up at game start. TODO: check if can be ommited in favor of GameState variable.
	private int hintsAvailable; //This keeps track of hints available over the tracked turns. Not the same as the hints available on the actual current turn.
	private int myID; // This maintains our turn order index throughout the current game. Not sure if needed as doMove() also receives the current ID 
	private String currentResponseID; // This maintains the ID of the strategy in strategy pool that is currently being used.
	private String[] currentPlayers;
	
	private Card[][] playerHands;
    private double[][] playabilityMask;
    private boolean[][] knowsColorMask;
    private boolean [][] knowsRankMask;
	
	// Hyperparameters TODO: Decide whether these will be passed by constructor, read from a config file etc
	private int turnsAdaptationThreshold = Integer.MAX_VALUE;
	private int gamesAdaptationThreshold = 2;
	private double assumedBehaviorVariance; // This is the knob that lets us determine by how much to update a given match-up's behavior information. In the general case, this could vary per match-up and depend on the threshold of games/turns. We are simplifying it with a single number.
	
	public BayesAdaptiveAgent(HashMap<String, Agent> myStrategyPool, ArrayList<String> trainingPoolCandidates,
			MultiKeyMap<String, MatchupInformation> precomputedMatchupInfo) {
		super();
		this.myStrategyPool = myStrategyPool;
		this.trainingPoolCandidates = trainingPoolCandidates;
		this.precomputedMatchupInfo = precomputedMatchupInfo;
		
		
//		evaluationMatchupInfo = new MultiKeyMap<String, PlayerStats>();
	} //TODO: Write a function that initializes this class by generating all the precomputed object from our files


	// GameRunner should call this at the start of each game.
	public void receiveID(int agentID, String[] names) {
		this.myID = agentID;
		this.currentPlayers = names;
		myTurnCount = 0;
		hintsAvailable = 8;
		
		for (String name:currentPlayers) {
			if (name != "you") {
				if (!beliefDistribution.containsKey(name)) {
					rollingGamesWithPartner.put(name, 0);
					rollingTurnsWithPartner.put(name, 0);
					for (String trainingPartner:trainingPoolCandidates) {
						MultiKey<String> key = new MultiKey<String>(name, trainingPartner);
						beliefDistribution.put(key, 1.0/trainingPoolCandidates.size());
					}	
				}
				rollingGamesWithPartner.put(name, rollingGamesWithPartner.get(name)+1); //TODO: This assumes each agent identifier is unique and would not generalize for multiplayer games where more than one player has the same identifier
				currentResponseID = getResponse(name);
			}
		}
	}


	@Override
	public Action doMove(int agentID, GameState state) {
		// TODO Auto-generated method stub
		String theirID = currentPlayers[(agentID+1)%state.getPlayerCount()]; //TODO: get their ID
		
		
		
		// Some start of game set-up
		if (myTurnCount == 0) {
			int numPlayers = state.getPlayerCount();
			int numCards = state.getHandSize();
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
		}
		
		updateHistory(agentID, state); // new values for IPP, Comm, etc TODO Xianbo
		// if (adaptationCondition(){
		if (rollingGamesWithPartner.get(theirID) > gamesAdaptationThreshold || rollingTurnsWithPartner.get(theirID) > turnsAdaptationThreshold) {
			updateBeliefs(currentResponseID,theirID); // update belief distribution using Bayes theorem, if either number of games or turns is > thresholdTODO
			rollingGamesWithPartner.put(theirID, 0);
			rollingTurnsWithPartner.put(theirID, 0);
		}
//	}
			
			
		currentResponseID = getResponse(theirID); //almost done
		Agent responsePolicy = myStrategyPool.get(currentResponseID);
		
		Action action = responsePolicy.doMove(agentID, state);

		return action;
	}
	
	
	String getResponse(String theirID) {
		
		double maxExpectedScore = Integer.MIN_VALUE;
		String bestExpectedResponse = "";
		
		for (String possibleResponse: myStrategyPool.keySet()) {
			
			double weightedScore = 0.0;
			
			for (String possiblePartner:trainingPoolCandidates) {
				double matchupScore = precomputedMatchupInfo.get(possibleResponse, possiblePartner).scoreMean;
				double matchupWeight = beliefDistribution.get(theirID, possiblePartner);
				weightedScore += matchupScore*matchupWeight;
			}
			
			if (weightedScore > maxExpectedScore){
				maxExpectedScore = weightedScore;
				bestExpectedResponse = possibleResponse;
			}
			
		}
		
		return bestExpectedResponse;
		
	}
	
	//TODO: Currently hardcodes the names of dimensions
	void updateBeliefs(String ourID, String theirID) {
		
		double totalProbability = 0;
		double observedD1 = rollingMatchupInfo.get(theirID).getCommunicativeness();
		double observedD2 = rollingMatchupInfo.get(theirID).getCommunicativeness();
		
		
		for (String possiblePartner:trainingPoolCandidates) {
			double expectedD1 = precomputedMatchupInfo.get(ourID, theirID).EstimatedBCValues.get("communicativeness");
			double expectedD2 = precomputedMatchupInfo.get(ourID, theirID).EstimatedBCValues.get("IPP");
			
			double diff1 = expectedD1 - observedD1;
			double diff2 = expectedD2 - observedD2;
			
			NormalDistribution n = new NormalDistribution(0,assumedBehaviorVariance);
			
			double p1 = n.density(diff1);
			double p2 = n.density(diff2);
			double joint_probability = p1*p2;
			
			beliefDistribution.put(theirID, possiblePartner, joint_probability*beliefDistribution.get(theirID,possiblePartner)) ;
			totalProbability += beliefDistribution.get(theirID, possiblePartner);

		}
		
		//Normalizing all beliefs at the end
		for (String possiblePartner:trainingPoolCandidates) {
			beliefDistribution.put(theirID, possiblePartner, beliefDistribution.get(theirID,possiblePartner)/totalProbability);
		}
	}
		
		
		
		
//		for possible_x2 in range(dimsize):
//            for possible_y2 in range(dimsize):
//                expected_matchup = matchups[chosen_x1][chosen_y1][possible_x2][possible_y2]
//                expected_d1 = expected_matchup.est_d1
//                expected_d2 = expected_matchup.est_d2
//                
//                diff_1 = expected_d1-observed_d1
//                diff_2 = expected_d2-observed_d2
//                
//                # Here we assume that diff_1 and diff_2 are independently and normally distributed ~N(0,std**2)
//                
//                n = norm(0,variance)
//                p1 = n.pdf(diff_1) # This is the probability of observing a behavior diff_1 from the expected value.
//                p2 = n.pdf(diff_2)
//                joint_probability = p1*p2 
//                
//                #Bayes
//                belief_distribution[possible_x2][possible_y2] = p1*p2*belief_distribution[possible_x2][possible_y2]
//                total_probability +=p1*p2*belief_distribution[possible_x2][possible_y2]
//    
//        
//        belief_distribution = belief_distribution/total_probability # Normalizing so that it sums to 1
//        bayes_adaptive_score[x2][y2] = score # Right now I'm always updating to the score of the most recent episode, not taking into account previous episodes and not doing early stopping based on convergence.
//        i+=1
//        print(score)

	
	
	
	// TODO: This method is duplicated from MetaAgent with slight variations. Needs to be consolidated. Alternatively, some of its functions could be moved to the game runner.
	public PlayerStats updateHistory(int agentID, GameState state) {
	    
		   /*
		    * The following variables in comment are global, which are used in this function.
	        private int myTurnCount;
	        private int hintsAvailable;
	        int numPlayers;
	        int numCards;
	        private Card[][] playerHands;
	        private double[][] playabilityMask;
	        private boolean[][] knowsColorMask;
	        private boolean [][] knowsRankMask;
		    */
	        GameState workingState = state.getCopy();
	        
//	      TODO: BUG FIX: There seems to be an instance where the agent records a hint as being given without having hints available
	        
	        List<HistoryEntry> lastMoves = getLastMoves(state);
	        int numMoves = lastMoves.size();
	        //System.out.println("My ID is " + agentID + " There were " + numMoves + " moves since last state on my turn count " + myTurnCount);
	        if (myTurnCount !=-10 ) { //check if needed, skipping a possibly incomplete first round in order to not have to deal with edge cases
	            Collections.reverse(lastMoves);
	            for(HistoryEntry move:lastMoves) {
	                

	                int activePlayerID = move.playerID;
	                if (activePlayerID!=agentID) {
	                    // Update player specific information;
	                    PlayerStats currentPlayerStats = rollingMatchupInfo.get(currentPlayers[activePlayerID]); // Note: getting the match-up information independently for each history entry should cover the 3+ player case, but is untested in the current version
	                    
	                    
	                    if (move.action instanceof TellColour) {
	                        currentPlayerStats.numHints = currentPlayerStats.numHints+1;
	                        currentPlayerStats.numPossibleHints = currentPlayerStats.numPossibleHints+1;
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
	                    else if (move.action instanceof TellValue) {
	                        currentPlayerStats.numHints = currentPlayerStats.numHints+1;
	                        currentPlayerStats.numPossibleHints = currentPlayerStats.numPossibleHints+1;
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
	                    else if (hintsAvailable>0) {
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
	        for (int playerID = 0; playerID < state.getPlayerCount(); ++playerID) {
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
	        
	        
//	      //System.out.println("Hints available after this action: "  + hintsAvailable);
	        
//	      for (int playerIndex = 0; playerIndex<numPlayers; ++playerIndex) {
//	          //System.out.format("Player %d's hand", playerIndex);
//	          if (playerIndex!=agentID) {
//	              for (int slotIndex = 0; slotIndex<state.getHandSize(); ++slotIndex) {
//	                  playerHands[playerIndex][slotIndex] = state.getCardAt(playerIndex, slotIndex);
//	                  //System.out.print(" " + playerHands[playerIndex][slotIndex].colour + " " +  playerHands[playerIndex][slotIndex].value);
//	              }
//	              
//	          }
//	      }
	        
	        if (hintsAvailable != state.getInformation()) {
	            //System.out.println("-------- WARNING: hints were miscalculated ----- ");
	        }
	        hintsAvailable = state.getInformation();
	        ++myTurnCount;
	        
	        playerHands = new Card[state.getPlayerCount()][state.getHandSize()];
	        playabilityMask = new double[state.getPlayerCount()][state.getHandSize()];
	        
	        for (int playerIndex = 0; playerIndex<state.getPlayerCount(); ++playerIndex) {
	            //System.out.format("Player %d's hand", playerIndex);
	            if (playerIndex!=agentID) {
	                for (int slotIndex = 0; slotIndex<state.getHandSize(); ++slotIndex) {
	                    playerHands[playerIndex][slotIndex] = state.getCardAt(playerIndex, slotIndex);
	                    playabilityMask [playerIndex] = getPlayabilityMask(playerIndex,state);
	                    //System.out.print(" " + playerHands[playerIndex][slotIndex].colour + " " +  playerHands[playerIndex][slotIndex].value);
	                    //System.out.print(" " + playabilityMask[playerIndex][slotIndex]);

	                }
	                                    
	            }
	            
	        }
	        
	        
	        // calculate possible moves
	        //List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(agentID, state));

	        PlayerStats partnerStats = rollingMatchupInfo.get(currentPlayers[(agentID+1)%state.getPlayerCount()]); //TODO: This assumes I'm always going to adapt to the stats displayed by the next player in the turn order;
	        double communicativeness = partnerStats.getCommunicativeness();
	        double riskAversion = partnerStats.getRiskAversion();
	        double informationPlays = partnerStats.getInformationPlays();
	        System.out.println("Communicativeness " + communicativeness);
	        System.out.println("Risk Aversion " + riskAversion);
	        System.out.println("Average Information per play " + informationPlays);
	        
	        return partnerStats;

		}
	
	// TODO: This method is duplicated from MetaAgent. Needs to be consolidated.
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
	
	// TODO: This method is duplicated from MetaAgent. Needs to be consolidated.
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
