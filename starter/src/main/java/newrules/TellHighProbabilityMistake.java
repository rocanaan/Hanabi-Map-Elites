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

public class TellHighProbabilityMistake extends AbstractTellRule{
	
	private double threshold;
	private boolean checkFalsePositive;
	
	public TellHighProbabilityMistake(boolean falsePositive, double threshold) {
		this.threshold = threshold;
		this.checkFalsePositive = falsePositive;
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

			
			int[] playableMask = new int[state.getHandSize()];
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
					playableMask[slot] = 1;
					hasPlayable = true;
					continue;
				}
				playableMask[slot] = 0;

			}

			// Now we have an array representing whether each slot on that player's hand is
			// playable;

			List<Double> probabilityPlayable = getProbabiltyPlayable(nextPlayer, state, hand);
			//System.out.println("probability playable mask " +probabilityPlayable);
			
			int prioritySlot = -1;
			
			if (checkFalsePositive) {
				// Keep track of the unplayable card with highest playable probability
				double maxProbability = threshold;
				for (int slot = 0; slot < probabilityPlayable.size(); slot++)
				{
					if (playableMask[slot] == 0 && probabilityPlayable.get(slot) >= maxProbability) {
						maxProbability = probabilityPlayable.get(slot);
						prioritySlot = slot;
					}
				}
			}
			else {
				// Keep track of the playable card with lowest playable probability
				double minProbability = threshold;
				int maxSlot = -1;
				for (int slot = 0; slot < probabilityPlayable.size(); slot++)
				{
					if (playableMask[slot] == 1 && probabilityPlayable.get(slot) <= minProbability) {
						minProbability = probabilityPlayable.get(slot);
						prioritySlot = slot;
					}
				}
			}
	

			
			if (prioritySlot !=-1) {
				Hand newHand = state.getHand(i);
				return tellMissingPrioritiseValue(newHand, i, prioritySlot);	
			}
			continue;
			
		}
		return null;
    }
        
	
	// TODO: This should be refactored into a AgentUtils class
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

