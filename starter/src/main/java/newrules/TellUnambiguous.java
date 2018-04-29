package newrules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fossgalaxy.games.fireworks.ai.rule.AbstractRule;
import com.fossgalaxy.games.fireworks.ai.rule.AbstractTellRule;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.state.Card;
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

public class TellUnambiguous extends AbstractTellRule{
	
	private boolean prioritizePlayableCollisions;
	
	public TellUnambiguous() {
		this.prioritizePlayableCollisions = true;
	}
	
	public TellUnambiguous(boolean prioritizePlayable) {
		this.prioritizePlayableCollisions = prioritizePlayable;
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
			// Time to check whether the player told already knows partial information on
			// the playable cards
			// And how the missing information collides with the playable/unplayable cards
			
//			System.out.print("Playable cards [");
//			for (int playable:playableMask) {
//				System.out.print(playable+",");
//			}
//			System.out.println("]");

			
			if (!hasPlayable) {
				continue; // go to next player
			}

			int bestSlotSoFar = -1;
			String typeOfClueBestSlot = "";
			int bestSlotPlayableCollisions = -1;
			int bestSlotUnplayableCollisions = 999;
			
			for (int candidateSlot = 0; candidateSlot < state.getHandSize(); candidateSlot++)
			{
				if (playableMask[candidateSlot] == 0) {
					continue;
				}
				
				//if I don't know the value of the current candidate, check how many collisions you get by hinting that value
				if (hand.getKnownValue(candidateSlot) == null) {
					int playableValueCollisions = 0;
					int unplayableValueCollisions = 0;
						for (int collisionTest = 0; collisionTest < state.getHandSize(); collisionTest++) {
							if (collisionTest == candidateSlot) {
								continue;
							}
							if (hand.getKnownValue(collisionTest) == null &&  hand.getCard(collisionTest).value == hand.getCard(candidateSlot).value) {
								// Value collision detected
								if (playableMask[collisionTest] == 1) {
									playableValueCollisions++;
								}
								else {
									unplayableValueCollisions++;
								}
							}
						}
						if (prioritizePlayableCollisions) {
							if (playableValueCollisions > bestSlotPlayableCollisions ||
									(playableValueCollisions == bestSlotPlayableCollisions && unplayableValueCollisions < bestSlotUnplayableCollisions)) {
								bestSlotSoFar = candidateSlot;
								typeOfClueBestSlot = "Value";
								bestSlotPlayableCollisions = playableValueCollisions;
								bestSlotUnplayableCollisions = unplayableValueCollisions;
							}
						}
						else if (!prioritizePlayableCollisions) {
							if (unplayableValueCollisions < bestSlotUnplayableCollisions ||
									(unplayableValueCollisions == bestSlotUnplayableCollisions && playableValueCollisions > bestSlotPlayableCollisions)) {
								bestSlotSoFar = candidateSlot;
								typeOfClueBestSlot = "Value";
								bestSlotPlayableCollisions = playableValueCollisions;
								bestSlotUnplayableCollisions = unplayableValueCollisions;
							}
						}
					}
				
				//if I don't know the color of the current candidate, check how many collisions you get by hinting that color
				if (hand.getKnownColour(candidateSlot) == null) {
					int playableColorCollisions = 0;
					int unplayableColorCollisions = 0;
					for (int collisionTest = 0; collisionTest < state.getHandSize(); collisionTest++) {
						if (collisionTest == candidateSlot) {
							continue;
						}
						if (hand.getKnownColour(collisionTest) == null &&  hand.getCard(collisionTest).colour == hand.getCard(candidateSlot).colour) {
							// Color collision detected
							if (playableMask[collisionTest] == 1) {
								playableColorCollisions++;
							}
							else {
								unplayableColorCollisions++;
							}
						}
					}
					if (prioritizePlayableCollisions) {
						if (playableColorCollisions > bestSlotPlayableCollisions ||
								(playableColorCollisions == bestSlotPlayableCollisions && unplayableColorCollisions < bestSlotUnplayableCollisions)) {
							bestSlotSoFar = candidateSlot;
							typeOfClueBestSlot = "Color";
							bestSlotPlayableCollisions = playableColorCollisions;
							bestSlotUnplayableCollisions = unplayableColorCollisions;
						}
					}
					else if (!prioritizePlayableCollisions) {
						if (unplayableColorCollisions < bestSlotUnplayableCollisions ||
								(unplayableColorCollisions == bestSlotUnplayableCollisions && playableColorCollisions > bestSlotPlayableCollisions)) {
							bestSlotSoFar = candidateSlot;
							typeOfClueBestSlot = "Color";
							bestSlotPlayableCollisions = playableColorCollisions;
							bestSlotUnplayableCollisions = unplayableColorCollisions;
						}
					}
				}
				
			}
			
			if (typeOfClueBestSlot == "Color") {
				return new TellColour(nextPlayer,hand.getCard(bestSlotSoFar).colour);
			}
			if (typeOfClueBestSlot == "Value") {
				return new TellValue(nextPlayer,hand.getCard(bestSlotSoFar).value);
			}
		}
		return null;	
		
    }
        
   
	
    public boolean isPlayable(Card card, GameState state) {
        return state.getTableValue(card.colour) + 1 == card.value;
    }

}

