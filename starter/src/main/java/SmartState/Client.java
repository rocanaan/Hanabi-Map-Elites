package SmartState;

import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.state.BasicState;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.CardColour;
import com.fossgalaxy.games.fireworks.state.Deck;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import SmartState.Observations.PartnerCardIsPlayableObservation;
import SmartState.Observations.PartnerCardIsUselessObservation;
import SmartState.Observations.PartnerKnowsCardColorObservation;
import SmartState.Observations.PartnerKnowsCardValueObservation;
import SmartState.Observations.ProbabilityCardIsNecessaryObservation;
import SmartState.Observations.ProbabilityCardIsPlayableObservation;
import SmartState.Observations.ProbabilityCardIsUselessObservation;

import java.io.*;
import java.util.Arrays;

public class Client {

	static Thread sent;
	static Thread receive;
	static Socket socket;
	static int handSize, playerCount;
	static int lives, infos, deckSize, curPlayer;
	static int currentPlayer,slotDiscard = -1,slotPlay;
	static Card[] playerhands;
	static List<String> visiblecardMatches = new ArrayList<String>();
	static int[] fireworks = new int[5];
	// TODO Parse msg --> Java game state object--> agent(chose from Python)-->action(Java->Python)
	public static void main(String args[]) {
		try {
			socket = new Socket("localhost", 9999);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sent = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedReader stdIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					while (true) {
//                            System.out.println("Trying to read...");
						String in = stdIn.readLine();
						if (in != null) {
//							System.out.println(in);
							converter(in);
							pyBoardState();
						}
						out.print("Try from Java" + "\r\n");
						out.flush();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		sent.start();
		try {
			sent.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void converter(String str) {
		String lines[] = str.split("\\r?\\n");

		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains("Life tokens")) {
				lives = Integer.parseInt(lines[i].replaceAll("[^0-9]", ""));
//				System.out.println("Lives: " + lives);
			} else if (lines[i].contains("Info tokens")) {
				infos = Integer.parseInt(lines[i].replaceAll("[^0-9]", ""));
//				System.out.println("Hints: " + infos);
			} else if (lines[i].contains("Deck size")) {
				deckSize = Integer.parseInt(lines[i].replaceAll("[^0-9]", ""));
//				System.out.println("Deck size: " + deckSize);
			} else if (lines[i].contains("hand_size")) {
				handSize = Integer.parseInt(lines[i].replaceAll("[^0-9]", ""));
//				System.out.println(lines[i]);
			} else if (lines[i].contains("players=")) {
				playerCount = Integer.parseInt(lines[i].replaceAll("[^0-9]", ""));
//				System.out.println(lines[i]);
			} 

			/*Track Card discards
			 * Current player: 0 / 1
			 * Player hands: [[B1, G2, B2, G1, R2], [B1, G5, B2, Y5, Y3]]
			 * Chose random legal move : Discard*/
			else if (lines[i].contains("Current player")) {
				currentPlayer = Integer.parseInt(lines[i].replaceAll("[^0-9]", ""));
//				System.out.println("CURRENT PLAYER: "+ currentPlayer);
			}
			else if (lines[i].contains("Player hands")) {
				List<String> allMatches = new ArrayList<String>();
				Pattern p = Pattern.compile("[A-Z]{1}\\d{1}");
				Matcher m = p.matcher(lines[i]);
				while (m.find()) {
					allMatches.add(m.group());
				}
				playerhands = listToCard(allMatches);
//				System.out.println("Player hands: "+ Arrays.toString(playerhands));
			}
			else if (lines[i].contains("Discard")) {
				slotDiscard = Integer.parseInt(lines[i].replaceAll("[^0-9]", ""));
				System.out.println(lines[i]);
			}
			/*Track all visible cards from hints
			 * possible regex patterns: 
			 * //"[BGRWY]{1}\\d{1}\\|": match w/o slot number
			 * //".{2}\\|"      any 2 char before |
			 * .replaceAll("\\W", "") take off |
			 * */
			else if (lines[i].contains("Card knowledge")) {
				List<String> allMatches = new ArrayList<String>();
				Pattern p = Pattern.compile(".{2}\\|" );
				Matcher m = p.matcher(lines[i]);
				while (m.find()) {
					allMatches.add(m.group().replaceAll("\\W", ""));
				}
				visiblecardMatches = allMatches;
				System.out.println("CARD KNOWLEDGE: "+visiblecardMatches);
			}			
			else if (lines[i].contains("Fireworks")) {//Fireworks: [1, 0, 0, 0, 0] Fireworks: R1 Y0 G0 W0 B0 
				List<String> allMatches = new ArrayList<String>();
				Pattern p = Pattern.compile("\\d{1}");
    	        Matcher m = p.matcher(lines[i]);
    	        while(m.find()) {
    	        	allMatches.add(m.group());
    	        }
    	        fireworks = listToFireworks(allMatches);
//    	        System.out.println("FIREWORKS"+Arrays.toString(fireworks));
			}
		}
	}

	static public GameState pyBoardState() {
		GameState s = new BasicState(handSize, playerCount);
		
		s.setInformation(infos);
		s.setLives(lives);
		s.getDeck().init();
		Deck deck = s.getDeck();
		Hand currentHand; 
		
		//For all visible cards: what 'visible' means?
		Card visibleCard = null;
		for (int i = 0; i < visiblecardMatches.size(); i++) {
			char[] a = visiblecardMatches.get(i).toCharArray();
			int slotID = i%5; 
			int playerID = i/5;
			//////////changed the position////////////
			currentHand = s.getHand(playerID);
			Integer[] slots = {Integer.valueOf(slotID)};
			currentHand.setHasCard(slotID, true);
			/////////////////////////////////////////
			
//			System.out.println("i: "+i+" playerID: "+playerID+" slotID: "+ slotID + " a: "+Arrays.toString(a));
			if(a[0]!='X'&&a[1]!='X') {
//				System.out.println("Visible: "+Arrays.toString(a)+" player/slot: "+playerID+" "+slotID);
				visibleCard = stringToCard(a);
				s.setCardAt(playerID, slotID, visibleCard);
				deck.remove(visibleCard);
			}else {  //when the cards are not 100% visible but we have them & have some infos about them.
//				Hand currentHand = s.getHand(playerID);
//				Integer[] slots = {Integer.valueOf(slotID)};
//				currentHand.setHasCard(slotID, true);
				
				if(a[0]!='X') {//when the current player knows the color
//					System.out.println("Colour only: "+Arrays.toString(a)+" player/slot: "+playerID+" "+slotID);
					currentHand.setKnownColour(charToCardColour(a[0]), slots);
				}else if(a[1]!='X') {//when the current player knows the value
//					System.out.println("Value only: "+Arrays.toString(a)+" player/slot: "+playerID+" "+slotID);
					Integer cardValue = Character.getNumericValue(a[1]);
					currentHand.setKnownValue(cardValue, slots);
				}
			}
			System.out.println("do we have hand?: "+s.getHand(currentPlayer).toString());
		}
	    //Discard a card
		if(slotDiscard>0) {
		    Card discardCard = playerhands[slotDiscard];
	        s.addToDiscard(discardCard);
	        deck.remove(discardCard);
		}

        //setTableValue Fireworks: R1 Y0 G0 W0 B0 
        CardColour[] fireworksColour = {CardColour.RED, CardColour.ORANGE,CardColour.GREEN,CardColour.WHITE,CardColour.BLUE};
		for (int i = 0; i < 5; i++){
			s.setTableValue(fireworksColour[i], fireworks[i]);
		}
//		System.out.println("do we have hand?: "+s.getHand(currentPlayer).toString());		
//		System.out.println("Move");
//		Agent a = AgentUtils.buildAgent("RuleBasedOuter");
//		System.out.println("Built");
//		System.out.println(a.doMove(currentPlayer, s));
		
		return s;
	}
	static CardColour charToCardColour(char a) {
		CardColour cardColor = null;
		switch (a) {
			case 'R':
				cardColor = CardColour.RED;
				break;
			case 'B':
				cardColor = CardColour.BLUE;
				break;
			case 'G':
				cardColor = CardColour.GREEN;
				break;
			case 'Y':
				cardColor = CardColour.ORANGE;
				break;
			case 'W':
				cardColor = CardColour.WHITE;
				break;
		}
		return cardColor;
	}
	static Card stringToCard(char[] a) {
		CardColour cardColor = null;
		Integer cardValue = null;
		switch (a[0]) {
			case 'R':
				cardColor = CardColour.RED;
				break;
			case 'B':
				cardColor = CardColour.BLUE;
				break;
			case 'G':
				cardColor = CardColour.GREEN;
				break;
			case 'Y':
				cardColor = CardColour.ORANGE;
				break;
			case 'W':
				cardColor = CardColour.WHITE;
				break;
		}
		cardValue = Character.getNumericValue(a[1]);
		Card card = new Card(cardValue, cardColor);
		return card;
	}
	static Card[] listToCard(List<String> list) {
		CardColour cardColor = null;
		Integer cardValue = null;
		Card[] cardDeck = new Card[10];
		for (int i = 0; i < list.size(); i++) {
			char[] a = list.get(i).toCharArray();
			switch (a[0]) {
			case 'R':
				cardColor = CardColour.RED;
				break;
			case 'B':
				cardColor = CardColour.BLUE;
				break;
			case 'G':
				cardColor = CardColour.GREEN;
				break;
			case 'Y':
				cardColor = CardColour.ORANGE;
				break;
			case 'W':
				cardColor = CardColour.WHITE;
				break;
			}
			cardValue = Character.getNumericValue(a[1]);
			Card card = new Card(cardValue, cardColor);
			cardDeck[i] = card;
		}
		return cardDeck;
	}
	static int[] listToFireworks(List<String> list) {
		int[] fireworks = new int[5];
		for (int i = 0; i < list.size(); i++) {
			char[] a = list.get(i).toCharArray();
			fireworks[i]= Character.getNumericValue(a[0]);
		}
		return fireworks;
	}

}