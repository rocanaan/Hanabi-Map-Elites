package com.fossgalaxy.games.fireworks.ai.username;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.ai.rule.PlaySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.annotations.AgentConstructor;
import com.fossgalaxy.games.fireworks.state.*;
import com.fossgalaxy.games.fireworks.state.actions.*;
import com.fossgalaxy.games.fireworks.state.events.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

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
        //print current game state
        showGameState(playerID, gameState);
        
        showHistory(playerID,gameState);

        //get all legal moves as a list
        List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(playerID, gameState));
        
        
        // 1) Print the gamestate
        // 2) Print available actions
        // 3) Ask for user input
        // 4) Implement the chosen action
        
        // Later on exchange 3 for tree search, neural network, evolutionary action planning, whatever

        //display all legal moves
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
