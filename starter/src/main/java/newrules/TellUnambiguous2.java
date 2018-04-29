package newrules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.ai.rule.AbstractRule;
import com.fossgalaxy.games.fireworks.ai.rule.AbstractTellRule;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.CardColour;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;
import com.fossgalaxy.games.fireworks.state.TimedHand;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.actions.PlayCard;
import com.fossgalaxy.games.fireworks.state.actions.TellColour;
import com.fossgalaxy.games.fireworks.state.actions.TellValue;
import com.fossgalaxy.games.fireworks.state.events.CardDrawn;
import com.fossgalaxy.games.fireworks.state.events.CardInfo;
import com.fossgalaxy.games.fireworks.state.events.CardPlayed;
import com.fossgalaxy.games.fireworks.state.events.CardReceived;
import com.fossgalaxy.games.fireworks.state.events.GameEvent;

//import com.fossgalaxy.games.fireworks.ai.rule.AbstractRule;
//import com.fossgalaxy.games.fireworks.state.Card;
//import com.fossgalaxy.games.fireworks.state.GameState;
//import com.fossgalaxy.games.fireworks.state.Hand;
//import com.fossgalaxy.games.fireworks.state.TimedHand;
//import com.fossgalaxy.games.fireworks.state.actions.Action;
//import com.fossgalaxy.games.fireworks.state.actions.PlayCard;
//import com.fossgalaxy.games.fireworks.state.events.CardInfo;
//import com.fossgalaxy.games.fireworks.state.events.GameEvent;
//
//import java.util.LinkedList;

public class TellUnambiguous2 extends AbstractTellRule{
	
	
	
	private double weightPlayable = 1;
	private double weightUnplayable = 0;
	
	public TellUnambiguous2() {
		this.weightPlayable = 1;
		this.weightUnplayable = 0;
	}
	
	public TellUnambiguous2(int weightPlayable, int weightUnplayable) {
		this.weightPlayable = weightPlayable;
		this.weightUnplayable = weightUnplayable;
	}
	
	@Override
	public Action execute(int playerID, GameState state) {

		for (int i = 0; i < state.getPlayerCount(); i++) {
			int nextPlayer = (playerID + i) % state.getPlayerCount();
			Hand hand = state.getHand(nextPlayer);
			
			
			// gard against trying to tell ourselves things
			if (nextPlayer == playerID) {
				continue;
			}

			//System.out.println("Evaluating hand of player " + nextPlayer);

			
			double[] playableMask = new double[state.getHandSize()];
			boolean hasPlayable = false;
			for (int slot = 0; slot < state.getHandSize(); slot++) {

				Card card = hand.getCard(slot);
				if (card == null) {
					playableMask[slot] = 0;
					continue;
				}

//				int currTable = state.getTableValue(card.colour);
//				if (card.value != currTable + 1) {
				if (isPlayable(card,state)) {
					playableMask[slot] = weightPlayable;
					hasPlayable = true;
					continue;
				}
				playableMask[slot] = weightUnplayable;

			}

			// Now we have an array representing whether each slot on that player's hand is
			// playable;
			// Time to check whether the player told already knows partial information on
			// the playable cards
			// And how the missing information collides with the playable/unplayable cards
			
//			System.out.print("Playable cards: [");
//			for (double playable:playableMask) {
//				System.out.print(playable+",");
//			}
//			System.out.println("]");

			
			if (!hasPlayable) {
				continue; // go to next player
			}
			
			List<Double> probabilityPlayable = getProbabiltyPlayable(nextPlayer, state, hand);
			//System.out.println("probability playable mask " +probabilityPlayable);
			
			int bestValue = -1;
			CardColour bestColor = null;
			double bestScore = -100;
			
			for (int value = 1; value<=5; value++) {
				GameState cloneState = state.getCopy();
				List<Integer> matchingValueSlots = new ArrayList<Integer>();
				for (int slot = 0; slot<hand.getSize(); slot++) {
					if (hand.getCard(slot).value == value) {
						matchingValueSlots.add(slot);
					}
				}
				Integer[] slots = matchingValueSlots.toArray(new Integer[matchingValueSlots.size()]);
				if (slots.length ==0) {
					continue;
				}
//				for (Integer s: slots) {
//					System.out.print(s);
//				}
				Hand newHand = cloneState.getHand(nextPlayer);
				newHand.setKnownValue(value, slots);
//				for (int s = 0; s<=5;s++) {
//					System.out.println(newHand.getKnownValue(s));
//				}
				probabilityPlayable = getProbabiltyPlayable(nextPlayer, cloneState, newHand);
				//System.out.println("probability playable after telling value  " + value + " : " +probabilityPlayable);
				double currentScore = 0;
				for (int slot =0; slot<probabilityPlayable.size(); slot++) {
					currentScore += (playableMask[slot] * probabilityPlayable.get(slot));
				}
				if (currentScore > bestScore) {
					bestValue = value;
					bestScore = currentScore;
				}
				//System.out.println("value of this hint is " + currentScore);
			}
			
			for (CardColour color : CardColour.values()) {
				GameState cloneState = state.getCopy();
				List<Integer> matchingColorSlots = new ArrayList<Integer>();
				for (int slot = 0; slot<hand.getSize(); slot++) {
					if (hand.getCard(slot).colour == color) {
						matchingColorSlots.add(slot);
					}
				}
				Integer[] slots = matchingColorSlots.toArray(new Integer[matchingColorSlots.size()]);
				if (slots.length ==0) {
					continue;
				}
//				for (Integer s: slots) {
//					System.out.print(s);
//				}
				Hand newHand = cloneState.getHand(nextPlayer);
				newHand.setKnownColour(color, slots);
//				for (int s = 0; s<=5;s++) {
//					System.out.println(newHand.getKnownValue(s));
//				}
				probabilityPlayable = getProbabiltyPlayable(nextPlayer, cloneState, newHand);
				//System.out.println("probability playable after telling value  " + color + " : " +probabilityPlayable);
				double currentScore = 0;
				for (int slot =0; slot<probabilityPlayable.size(); slot++) {
					currentScore += (playableMask[slot] * probabilityPlayable.get(slot));
				}
				if (currentScore > bestScore) {
					bestValue = -1;
					bestColor = color;
					bestScore = currentScore;
				}
				//System.out.println("value of this hint is " + currentScore);
			}
			
			if (bestValue != -1) {
				//System.out.println("Best hint is value " + bestValue + " with a score of " + bestScore);
				return new TellValue(nextPlayer,bestValue);
			}
			
			else {
				//System.out.println("Best hint is color " + bestColor + " with a score of " + bestScore);
				return new TellColour(nextPlayer,bestColor);
			}
		}
			
		return null;	
		
    }
        
	public static List<Double> getProbabiltyPlayable(int targetID, GameState gameState, Hand hand){
		List<Double> probabilities = new ArrayList<Double>();
		
		if (targetID < gameState.getPlayerCount()) {
			
			List<Card> deck = gameState.getDeck().toList();
			
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
		
		return probabilities;
	}
	
    public static boolean isPlayable(Card card, GameState state) {
        return state.getTableValue(card.colour) + 1 == card.value;
    }

}

