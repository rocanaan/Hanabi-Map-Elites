package com.fossgalaxy.games.fireworks.ai.ensembleagent;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.ai.rule.PlaySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;
import com.fossgalaxy.games.fireworks.annotations.AgentConstructor;
import com.fossgalaxy.games.fireworks.state.*;
import com.fossgalaxy.games.fireworks.state.actions.*;
import com.fossgalaxy.games.fireworks.state.events.CardInfo;
import com.fossgalaxy.games.fireworks.state.events.CardInfoColour;
import com.fossgalaxy.games.fireworks.state.events.CardInfoValue;
import com.fossgalaxy.games.fireworks.state.events.CardPlayed;
import com.fossgalaxy.games.fireworks.state.events.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 * An agent that gets initialized with a rule and prints gamestate and history, letting the human player pick either one of the actions or executing the rule
 */
public class DeterminePartnerBehaviorAgent implements Agent {

    private Random random;
    //private Rule testRule = new TellUnambiguous2();
    
    private int nextHistoryIndex = 0;
    private double[][] playabilityMasks = {{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};
    private int turnCount = 0;
    private int countRandomActions = 0;
    private boolean assumeRandom = false;
    
    private GameState pastState = null;

    public DeterminePartnerBehaviorAgent() {
        this.random = new Random();
    }
    
    public void  DeterminePartnerBehavior(int playerID, GameState gameState) {
    		LinkedList<GameEvent> history = gameState.getHistory();
    		System.out.println("hi");
    		if (turnCount == 0) {
    			int playCount = 0;	
    			for (GameEvent event : history){
    				if (event instanceof CardPlayed) {
    					playCount ++;
    				}
    			}
        		if (playCount > 1) {
        			// On the first turn of the game, simply assume that if there has been more than 1 play actions before my first two, one of them was random, as it is very unlikely that it was not
        			countRandomActions++;
        		}
    		}
    		else {
    			System.out.println("hey");
    			while (nextHistoryIndex < history.size()) {
    				GameEvent event = history.get(nextHistoryIndex);
    				System.out.println("Checking event " + nextHistoryIndex);
    				System.out.println(event);
    				
    				// If it is a play event, say it was a random action if the player card had less than 1 probabilty
    				if (event instanceof CardPlayed) {
    					System.out.println("Analysing played event");
    					int actorID = -1;
    					for (int i = 1; i<=5; i++) {
    						if (event.toString().contains("player " + i)) {
    							actorID= i;
    							break;
    						}
    					}
    					System.out.print("Actor id = " + actorID);
    					if (actorID == playerID || actorID == -1) {
    						nextHistoryIndex++;
    						continue;
    					}
					int slot = -1;
    					if (actorID!=-1) {
    						for (int j = 0; j<5; j++) {
        						if (event.toString().contains("slot " + j)) {
        							slot= j;
        							break;
        						}
        					}
    					}
    					System.out.print("Actor slot = " + slot);
    					if (slot == -1) {
    						nextHistoryIndex++;
    						continue;
    					}
    					System.out.println("Event " + event + " with probability mask " + playabilityMasks[actorID][slot]);
    					System.out.print("Probability mask [");
    					for(int i = 0; i< 5; i++) {
    						System.out.print(playabilityMasks[actorID][i]+",");
    					}
    					System.out.println("]");
    					if (playabilityMasks[actorID][slot] <1) {
    						countRandomActions++;
    						System.out.println("This event looks random to me");
    					}
    				}// end play event
    				System.out.println("Finished analysing played event");
    				
    				// If it is a hint event, update the receiving player's playability mask
    				if (event instanceof CardInfo) {
    					CardInfo hint = (CardInfo)event;
    					int playerTold = hint.getPlayerTold();
    					Integer[] slots = hint.getSlots();
    					if (playerTold != playerID) {
    						Hand hand = pastState.getHand(playerTold);
    						if(hint instanceof CardInfoColour) {
    							CardInfoColour colorHint = (CardInfoColour)hint;
    							CardColour color = colorHint.getColour();
    							hand.setKnownColour(color,slots);
    							playabilityMasks[playerTold] = getPlayabilityMask(playerTold, pastState);
    						}
    						if(hint instanceof CardInfoValue) {
    							CardInfoValue valueHint = (CardInfoValue)hint;
    							int value = valueHint.getValue();
    							hand.setKnownValue(value,slots);
    							playabilityMasks[playerTold] = getPlayabilityMask(playerTold, pastState);
    						}
    					}
    				}// end hint event
    				
    				nextHistoryIndex++;
    			}
    			
    		}
    		

    		
    		if (countRandomActions >=1) {
    			assumeRandom = true;
    		}
    		
    		pastState = gameState.getCopy();
    		for (int i = 0; i<5; i++) {
    			playabilityMasks[i] = getPlayabilityMask(i,pastState);
    		}
    		
    		turnCount++;
    		nextHistoryIndex = history.size();

    }
    
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

    public Action doMove(int playerID, GameState gameState) {
        //print current game state
    		System.out.println(" ---------- Showing Game State ---------");
        showGameState(playerID, gameState);
        
		System.out.println(" ---------- Showing History ---------");

        showHistory(playerID,gameState);

        //get all legal moves as a list
        List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(playerID, gameState));
        
        
        // 1) Print the gamestate
        // 2) Print available actions
        // 3) Ask for user input
        // 4) Implement the chosen action
        
        // Later on exchange 3 for tree search, neural network, evolutionary action planning, whatever

        //display all legal moves
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("attempting to determine partner behavior");
        DeterminePartnerBehavior(playerID, gameState);
        
        showLegalActions(possibleMoves);
       
        
        //choose a random item from that list and return it
        //int moveToMake = random.nextInt(possibleMoves.size());
     
        
        // CHECKING IF A RULE CAN FIRE
//        Rule r = new PlaySafeCard();
//        
//        System.out.println(r.canFire(0, gameState));
//        System.out.println(r.canFire(1, gameState));
//        System.out.println(r.couldFire(0, gameState));
//        System.out.println(r.couldFire(1, gameState));
        
        //get the move to take from human input
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter the number of the action you want to take: ");
        int moveToMake = reader.nextInt();
        assert moveToMake >= 0 && moveToMake < possibleMoves.size() : "Invalid move";

        return possibleMoves.get(moveToMake);
    }

    public void showGameState(int playerID, GameState gameState) {
        System.out.println("Player " + playerID + "'s turn");
        System.out.println("Information tokens: " + gameState.getInfomation() + "; Lives: " + gameState.getLives() + "; Deck size: " + gameState.getDeck().getCardsLeft());
        System.out.println("Discard pile: ");
        String discards = "";
        Collection<Card> dCards = gameState.getDiscards();
        for (Card c : dCards) {
            discards += c.colour + " " + c.value + " | ";
        }
        if (discards == "") {
            System.out.println("Empty");
        } else {
            discards = discards.substring(0, discards.length() - 2);
            System.out.println(discards);
        }
        System.out.println("Table: ");
        System.out.println("Blue: " + gameState.getTableValue(CardColour.valueOf("BLUE")) + " | "
                + "Green: " + gameState.getTableValue(CardColour.valueOf("GREEN")) + " | "
                + "Orange: " + gameState.getTableValue(CardColour.valueOf("ORANGE")) + " | "
                + "Red: " + gameState.getTableValue(CardColour.valueOf("RED")) + " | "
                + "White: " + gameState.getTableValue(CardColour.valueOf("WHITE")));
        int nPlayer = gameState.getPlayerCount();
        int hSize = gameState.getHandSize();
        for (int i = 0; i < nPlayer; i++) {
            System.out.println("Player " + i + "'s hand: ");
            Hand hand = gameState.getHand(i);
            for (int j = 0; j < hSize; j++) {
                Card aCard = hand.getCard(j);
                if (aCard == null) {
                    CardColour[] pColor = hand.getPossibleColours(j);
                    String colors = "";
                    for (CardColour cc : pColor) {
                        colors += cc + " ";
                    }
                    int[] pValue = hand.getPossibleValues(j);
                    String values = "";
                    for (int cv : pValue) {
                        values += cv + " ";
                    }
                    System.out.println(colors + " | " + values);
                } else {
                    System.out.println(aCard.colour + " " + aCard.value);
                }
            }
        }
    }

    public void showLegalActions(List<Action> possibleMoves) {
        System.out.println("Possible actions:");
        for (int i = 0; i < possibleMoves.size(); i++) {
            Action a = possibleMoves.get(i);
            if (a instanceof DiscardCard) {
                System.out.println(i + ". Discard card " + (((DiscardCard) a).slot));
            } else if (a instanceof PlayCard) {
                System.out.println(i + ". Play card " + (((PlayCard) a).slot));
            } else if (a instanceof TellColour) {
                System.out.println(i + ". Tell player " + ((TellColour) a).player + " about " + ((TellColour) a).colour);
            } else if (a instanceof TellValue) {
                System.out.println(i + ". Tell player " + ((TellValue) a).player + " about " + ((TellValue) a).value);
            }
        }
    }
    
    public void showHistory(int playerID, GameState gameState) {
    		System.out.println("Printing history");
    		LinkedList<GameEvent> history = gameState.getHistory();
    		
    		for (GameEvent h : history) {
    			System.out.println(h);
    		}
  
    }
}
