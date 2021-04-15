package SPARTA_Communication;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.CardColour;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;

public class StateToSpartaConverter {
	
	
	private  String[] keys = {"discard_pile", "complete_card_knowledge", "current_player_offset", 
			"num_players", "fireworks", "observed_hands", "card_knowledge", 
			"current_player", "deck_size", "life_tokens", "legal_moves_as_int", 
			"information_tokens", "legal_moves"};
	
	
	public static String[] spartaColors = {"r","o","y","g","b"};

	
	public  static JSONObject toSpartaJSON(GameState state, int playerId) {
		
		JSONObject obj = new JSONObject();
		
		int cardsRemainingInDeck = state.getDeck().getCardsLeft();
		obj.put("cardsRemainingInDeck", cardsRemainingInDeck);
		
		int currentScore = state.getScore();
		obj.put("currentScore", currentScore);
		
		
		int hintStonesRemaining = state.getInfomation();
		obj.put("hintStonesRemaining", hintStonesRemaining);
		
		int mulligansRemaining = state.getLives();
		obj.put("mulligansRemaining", mulligansRemaining);
		
		
//		int playerId
		Hand nextPlayersHand = state.getHand( (playerId + 1)%state.getPlayerCount() );
		
		JSONArray cards = new JSONArray();
		for (int i=0; i<nextPlayersHand.getSize(); i++) {
			Card c = nextPlayersHand.getCard(i);
			cards.add(cardToSparta(c));
		}
		obj.put("cards",cards);
		
		JSONArray piles = new JSONArray();
		for (int i = 0; i<5 ; i++) {
			piles.add(state.getTableValue(spartaColorAsString(spartaColors[i])));
		}
		obj.put("piles",piles);
		
		obj.put("deckComposition", getDeckCompositionHashMap(state));
		
		
		JSONArray discards = new JSONArray();
		for (Card c: state.getDiscards()) {
			discards.add(cardToSparta(c));
		}
		obj.put("discards", discards);
		
		
		
		JSONArray predictions = getPredictionsJSON(state,playerId);
		
		obj.put("predictions", predictions);
		
		// This is a stand-in for the cheatMyCards entry, which might need to be provided at the gamerunner level.
		// For now, it just states that all my cards are 1r
		JSONArray cheatMyCards = new JSONArray();
		for (int i = 0; i<5 ; i++) {
			cheatMyCards.add("1r");
		}
		obj.put("cheatMyCards", cheatMyCards);
		
		// The next two entries are also stand-ins which might need to be provided at the gamerunner level.
		// For now, just gives id 49 .. 40 to all cards in the game
		// There might be a problem at the end of the game if one of the players has 4 cards.
		JSONArray cardIds = new JSONArray();
		for (int i = 0; i<5 ; i++) {
			cardIds.add(String.valueOf(49-i));
		}
		obj.put("cardIds", cardIds);
		
		JSONArray partnerCardIds = new JSONArray();
		for (int i = 0; i<5 ; i++) {
			partnerCardIds.add(String.valueOf(44-i));
		}
		obj.put("partnerCardIds", partnerCardIds);
		
		
		// This is also a stand-in. For now, prints all actions of the game in reverse order as string
		JSONArray moveHistory = new JSONArray();
		int historySize = state.getActionHistory().size();
		if (historySize>0) {
			for (int i = 0; i<historySize-1;i++) {
				System.out.println(i);
				moveHistory.add(String.valueOf(state.getActionHistory().get(historySize-(i+1)).action));
			}
		}
		
		obj.put("moveHistory", moveHistory);
		
		
		return obj;

		
		
		
//		String sparta_state_string = 
//				"{'cardsRemainingInDeck': " + Sting.valueOf(cardsRemainingInDeck)+  ", "
//				+ "'currentScore': "+ currentScore + ", "
//				+ "'hintStonesRemaining':" + 8, "
//				+ "'mulligansRemaining': 3,"
//				+ " 'playerId': 0,"
//				+ " 'partnerId': 1, "
//				+ "'cards': ['2g', '2b', '5o', '5y', '3o'], "
//				+ "'cheatMyCards': ['1b', '1g', '2y', '1o', '2b'], "
//				+ "'cardsIds': [49, 48, 47, 46, 45], "
//				+ "'partnerCardsIds': [44, 43, 42, 41, 40],"
//				+ " 'piles': [0, 0, 0, 0, 0],"
//				+ " 'discards': [], "
//				+ "'hintPlayer': -1, "
//				+ "'hintValue': -1, "
//				+ "'hintType': '???', "
//				+ "'hintIdx': [], "
//				+ "'flash_message': '',"
//				+ "'predictions': [[[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],"
//					+ " [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1], "
//					+ "[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],"
//					+ " [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1], "
//					+ "[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]],"
//					+ " [[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],"
//					+ "[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1], "
//					+ "[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1], "
//					+ "[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1], "
//					+ "[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]]], "
//				+ "'deckComposition': {'1r': 3, '2r': 2, '3r': 2, '4r': 2, '5r': 1, '1o': 3, '2o': 2,"
//					+ " '3o': 2, '4o': 2, '5o': 1, '1y': 3, '2y': 2, '3y': 2, '4y': 2,"
//					+ " '5y': 1, '1g': 3, '2g': 2, '3g': 2, '4g': 2, '5g': 1,"
//					+ " '1b': 3, '2b': 2, '3b': 2, '4b': 2, '5b': 1},"
//				+ " 'isPlayerTurn': True, "
//				+ "'gameOver': False, "
//				+ "'botName': 'RandomBot', "
//				+ "'moveHistory': [], "
//				+ "'seed': 695488012}";

		

		
	}
	
	
	public static String cardToSparta(Card c) {
		int value = c.value;
		CardColour colour = c.colour;
		
		String s = "";
		s += String.valueOf(value);
		s += convertColorToSparta(colour);
		
		return s;
	}
	
	public  static CardColour spartaColorAsString(String s) {
		switch (s) {
		case "r":
			return CardColour.RED;
		case "o":
			return CardColour.ORANGE;
		case "y":
			return CardColour.WHITE;
		case "g":
			return CardColour.GREEN;
		case "b":
			return CardColour.BLUE;
		}
		return null;
	}	
	
	public  static int spartaColorAsInt(String s) {
		switch (s) {
		case "r":
			return 0;
		case "o":
			return 1;
		case "y":
			return 2;
		case "g":
			return 3;
		case "b":
			return 4;
		}
		return -1;
	}
	

	
	public static String convertColorToSparta(CardColour c) {
		switch (c){
		
//		RED, BLUE, GREEN, ORANGE, WHITE
		case RED:
			return spartaColors[0];
		case ORANGE:
			return spartaColors[1];	
		case WHITE:
			return spartaColors[2];
		case GREEN:
			return spartaColors[3];
		case BLUE: // all colors match, except white.
			return spartaColors[4];
			
		}
		return null;
	}
	
	public static LinkedHashMap getDeckCompositionHashMap(GameState state) {
		LinkedHashMap<String, Integer> m = new LinkedHashMap<String, Integer>(25);
		
		m.put("1r",3);
		m.put("2r",2);
		m.put("3r",2);
		m.put("4r",2);
		m.put("5r",1);
		m.put("1o",3);
		m.put("2o",2);
		m.put("3o",2);
		m.put("4o",2);
		m.put("5o",1);
		m.put("1y",3);
		m.put("2y",2);
		m.put("3y",2);
		m.put("4y",2);
		m.put("5y",1);
		m.put("1g",3);
		m.put("2g",2);
		m.put("3g",2);
		m.put("4g",2);
		m.put("5g",1);
		m.put("1b",3);
		m.put("2b",2);
		m.put("3b",2);
		m.put("4b",2);
		m.put("5b",1);
		
//		Remove cards from discard pile
		for (Card c:state.getDiscards()) {
			m.put(cardToSparta(c), m.get(cardToSparta(c))-1);
		}
		
		for (CardColour colour:CardColour.values()) {
			int tableValue = state.getTableValue(colour);
			
			for (int i = 1; i<= tableValue; i++) {
				String card =   "" + i + convertColorToSparta(colour);
				m.put(card, m.get(card)-1);
			}
			
			
		}
		
		
		
		return m;
	}
	
	
	
	public static int[][][] getPredictions(GameState state, int agentID){
		
		int[][][] predictions = new int[2][5][25];
		
		
		for (int currentPlayer = 0; currentPlayer <state.getPlayerCount(); currentPlayer++) {
			List<Card> deck = state.getDeck().toList();
			
//			If it is not our hand, add their cards to the deck
			if (currentPlayer!=0) {
				for (int slot = 0; slot<state.getHandSize(); slot++) {
					Card c = state.getHand((agentID + currentPlayer)%state.getPlayerCount()).getCard(slot);
					deck.add(c);
				}
				
			}
			
			Map<Integer, List<Card>> possibleCards = DeckUtils.bindBlindCard( agentID, state.getHand((agentID + currentPlayer) % state.getPlayerCount()), deck);
			
			for (int slot: possibleCards.keySet()){
				List<Card> cards = possibleCards.get(slot);
				
				for (Card c: cards) {
					int index = 5* spartaColorAsInt(convertColorToSparta(c.colour)) + c.value-1;
					predictions[currentPlayer][slot][index] = 1;
				}
				
			}
			
		}
		
		System.out.println(Arrays.deepToString(predictions));
		return predictions;
	}
	
	public static JSONArray getPredictionsJSON (GameState state, int agentID){
		
		int [][][] p = getPredictions( state,  agentID);
				
		JSONArray predictions = new JSONArray();
		
		
		for (int [][] playerHand: p) {
			JSONArray hand = new JSONArray();
			
			for (int [] playerCard : playerHand) {
				JSONArray card = new JSONArray();
				
				for (int possibleValue : playerCard) {
					card.add(possibleValue);
				}
				hand.add(card);
			}
			
			predictions.add(hand);
			
		}
		
		
		return predictions;
		
	}

}
