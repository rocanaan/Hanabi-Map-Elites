package NeuralNetworkControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.ai.rule.PlaySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.ai.rule.logic.HandUtils;
import com.fossgalaxy.games.fireworks.annotations.AgentConstructor;
import com.fossgalaxy.games.fireworks.state.*;
import com.fossgalaxy.games.fireworks.state.actions.*;
import com.fossgalaxy.games.fireworks.state.events.GameEvent;


public class GamestateEvaluator {
	
	private static int[] cardCopies = new int[]{-1, 3, 2, 2, 2, 1};
	
	public class CardProbabiltyPair{
		public int slot;
		public double probability;
		
		public CardProbabiltyPair(int slot, double probabilty) {
			this.slot = slot;
			this.probability = probability;
		}
		
	};
	
	private int playerID;
	private GameState gameState;
	
	public GamestateEvaluator( int playerID, GameState gameState) {
		this.playerID = playerID;
		this.gameState = gameState;
	}
	

	
	public List<Double> getProbabiltyPlayable(int targetID){
		List<Double> probabilities = new ArrayList<Double>();
		
		if (targetID < gameState.getPlayerCount()) {
		
			Map<Integer, List<Card>> possibleCards = DeckUtils.bindBlindCard(targetID, gameState.getHand(targetID), gameState.getDeck().toList());
			
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
	
	public List<Double> getProbabiltyUseless(int targetID){
		List<Double> probabilities = new ArrayList<Double>();
		
		if (targetID < gameState.getPlayerCount()) {
		
			Map<Integer, List<Card>> possibleCards = DeckUtils.bindBlindCard(targetID, gameState.getHand(targetID), gameState.getDeck().toList());
			
			for (Map.Entry<Integer, List<Card>> entry : possibleCards.entrySet()) {
	            double probability = DeckUtils.getProbablity(entry.getValue(), x -> isUseless(x, gameState));
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
	
	public int getLives() {
		return gameState.getLives();
	}
	
	public int getInformation() {
		return gameState.getInfomation();
	}
	
	public int getDeckSize() {
		return gameState.getDeck().getCardsLeft();
	}
	
	public List<Double> getProbabiltyNecessary(int targetID){
List<Double> probabilities = new ArrayList<Double>();
		
		if (targetID < gameState.getPlayerCount()) {
		
			Map<Integer, List<Card>> possibleCards = DeckUtils.bindBlindCard(targetID, gameState.getHand(targetID), gameState.getDeck().toList());
			
			for (Map.Entry<Integer, List<Card>> entry : possibleCards.entrySet()) {
	            double probability = DeckUtils.getProbablity(entry.getValue(), x -> isNecessary(x, gameState));
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
	
	public CardProbabiltyPair getHighestProbabilityPlayable() {

		Map<Integer, List<Card>> possibleCards = DeckUtils.bindBlindCard(playerID, gameState.getHand(playerID), gameState.getDeck().toList());

        double bestSoFar = 0;
        int bestSlot = -1;
        for (Map.Entry<Integer, List<Card>> entry : possibleCards.entrySet()) {
            double probability = DeckUtils.getProbablity(entry.getValue(), x -> isPlayable(x, gameState));
            if (probability >= bestSoFar) {
                bestSlot = entry.getKey();
                bestSoFar = probability;
            }
        }
		
		return new CardProbabiltyPair(bestSlot, bestSoFar);
	}
	
	public CardProbabiltyPair getHighestProbabilityUseless() {

		Map<Integer, List<Card>> possibleCards = DeckUtils.bindBlindCard(playerID, gameState.getHand(playerID), gameState.getDeck().toList());

        double bestSoFar = 0;
        int bestSlot = -1;
        for (Map.Entry<Integer, List<Card>> entry : possibleCards.entrySet()) {
            double probability = DeckUtils.getProbablity(entry.getValue(), x -> isUseless(x, gameState));
            if (probability >= bestSoFar) {
                bestSlot = entry.getKey();
                bestSoFar = probability;
            }
        }
		
		return new CardProbabiltyPair(bestSlot, bestSoFar);
	}
	
    private boolean isPlayable(Card card, GameState state) {
        return state.getTableValue(card.colour) + 1 == card.value;
    }
    
    private boolean isUseless(Card card, GameState state) {
        return HandUtils.isSafeToDiscard(state, card.colour, card.value);
    }
    
    private boolean isNecessary(Card card, GameState state) {
        // Can't be necessary if needed again.
        if (HandUtils.isSafeToDiscard(state, card.colour, card.value)) {
            return false;
        }

        // Quicker to process this one.
        if (card.value == 5) return true;

        // Is it last one?
        // Is a card necessary if someone else has it?
//        long count = state.getDeck().toList().stream().filter(x -> x.equals(card)).count();
        long discardCount = state.getDiscards().stream().filter(x -> x.equals(card)).count();
//        return count == 0;
        return discardCount == cardCopies[card.value] - 1;
    }

}
