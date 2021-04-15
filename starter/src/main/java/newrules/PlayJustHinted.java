package newrules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fossgalaxy.games.fireworks.ai.rule.AbstractRule;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;
import com.fossgalaxy.games.fireworks.state.TimedHand;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.actions.PlayCard;
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

public class PlayJustHinted extends AbstractRule{
	
	private boolean singledOut;
	private boolean newestOnly;
	private int minLives;
	private double threshold;
	
	public PlayJustHinted() {
		this.singledOut = false;
		this.newestOnly = false;
		this.minLives = 0;
		this.threshold = 0.0;
	}
	
	public PlayJustHinted(boolean singledOut, boolean newestOnly, int minLives, double threshold) {
		this.singledOut = singledOut;
		this.newestOnly = newestOnly;
		this.minLives = minLives;
		this.threshold = threshold;
	}
	
	private boolean eventByPlayer(GameEvent event, int playerID) {
		if (event instanceof CardInfo) {
			if ( ((CardInfo) event).getPerformer() == playerID){
				return true;
			}
		}
		if (event instanceof CardReceived) {
			if ( ((CardReceived) event).getPlayerId() == playerID){
				return true;
			}
		}
		String s = "player " + playerID;
		if (event.toString().contains(s)) {
			return true;
		}
		return false;
	}
	
	private boolean isApplicable(CardInfo hint, int slot, GameState state, int playerID) {
		
		// If the rule requires a card to be singled out and the current hint is not unique, then it does not apply
		if (singledOut && !hint.isUnique()) {
			return false;
		}
		// If the rule requires the hinted card to be the newest and it is not, does not apply
		if (newestOnly) {
			int newestSlot = getNewestCard(state,playerID);
			// If the newest slot is not -1, this means we know what the newest slot is
			// If the newest slot is different from the current slot, then does not apply
			if (newestSlot!=-1 && newestSlot!=slot) {
				return false;
			}
		}
		// If none of the slots hinted refer to the current slot, it does not apply
		boolean containsSlot = false;
		Integer[] hintSlots = hint.getSlots();
		for (Integer hintSlot: hintSlots) {
			if (hintSlot == slot){
				containsSlot = true;
				break;
			}
		}
		if (!containsSlot) {
			return false;
		}

		// If none of the above disqualified the card, then it is applicable
		return true;
	}

	
    @Override
    public Action execute(int playerID, GameState state) {
        LinkedList<GameEvent> eventHistory = state.getHistory();
        
        
        // Add all tell events directed to the player to a list until up to our previous action
        boolean stop = false;
        int eventIndex = eventHistory.size()-1;
        LinkedList<CardInfo> hintsToPlayer = new LinkedList<CardInfo>();
        while (!stop) {
        	 	GameEvent event = eventHistory.get(eventIndex);
        	 	if ( (event == null)|| !eventByPlayer(event, playerID)) {
        	 		stop = true;
        	 		continue;
        	 	}
        	 	
        	 	else if ( event instanceof CardInfo) {
        	 		if ( ((CardInfo) event).getPlayerTold() == playerID){
        	 			hintsToPlayer.add((CardInfo)event);
        	 		}
        	 	}
        	 	
        	 	eventIndex--;
        }
        
        if (hintsToPlayer.isEmpty()) {
        		return null;
        }
        
        Hand hand = state.getHand(playerID);
        int size = hand.getSize();
        int[] mask = new int[size];
        
        // iterate over cards in hand, marking cards that are applicable to a rule;
        for (int i = 0; i < size; i++) {
        		mask[i] = 0;
        		// iterate over hints to see if the hint applies to that card slot
        		for (CardInfo hint : hintsToPlayer) {
        			if (isApplicable(hint, i, state, playerID)) {
        				mask[i] = 1;
        			}
        		}
        }
        
        // Of all the cards with an applicable hint, find the one with best probability
        Map<Integer, List<Card>> possibleCards = DeckUtils.bindBlindCard(playerID, state.getHand(playerID), state.getDeck().toList());

        double bestSoFar = -1;
        int bestSlot = -1;
        for (Map.Entry<Integer, List<Card>> entry : possibleCards.entrySet()) {
        		if (mask[entry.getKey()] == 1) {
	            double probability = DeckUtils.getProbablity(entry.getValue(), x -> isPlayable(x, state));
	            if (probability >= bestSoFar) {
	                bestSlot = entry.getKey();
	                bestSoFar = probability;
	            }
        		}
        }
        if (bestSlot == -1) return null;
        // If the best card has probability 1, then play it regardless of lives left or threshold
        if (bestSoFar >=1) {
        		return new PlayCard(bestSlot);
        }
        // Otherwise, play only if both the probability is higher than the threshold and current lives are hihger than minLives
        if (bestSoFar >= threshold && state.getLives() > minLives) {
        		return new PlayCard(bestSlot);
        }
        
        return null;
        
        
       
    }

    /**
     * This method will return -1 if someone doesn't want us to know what order our cards were delt in.
     *
     * @param state
     * @param playerID
     * @return
     */
    public static int getNewestCard(GameState state, int playerID) {
        try {
            TimedHand hand = (TimedHand) state.getHand(playerID);
            return hand.getNewestSlot();
        } catch (ClassCastException ex) {
            return -1;
        }
    }
    
    public boolean isPlayable(Card card, GameState state) {
        return state.getTableValue(card.colour) + 1 == card.value;
    }

}

