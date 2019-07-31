package SmartState.Observations;


import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.BorderUIResource;

import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;

import SmartState.SmartStateObservation;

public class BoardStateObservation extends SmartStateObservation{
	
	
	public BoardStateObservation() {
		this.length = 0; //state.getPlayerCount()*state.getHandSize(); // TODO: The way the code is currently structured, I will not have a gamestate when the constructor is called
	}

	@Override
	public List<Double> getObservation(GameState state, int playerID) {
		List<Double> boardState = new ArrayList<Double>();
		
		int lives = state.getLives();
    	for (int i = 1; i < 5; i++) {
    		if (i <= lives) {
    			boardState.add(1.0);
    		} else {
    			boardState.add(0.0);
    		}
    	}
    	// Hints left - Bits 5 to 13
    	int hints = state.getInfomation();
    	for (int i = 0; i < 9; i++) {
    		if (i <= hints) {
    			boardState.add(1.0);
    		} else {
    			boardState.add(0.0);
    		}
    	}
    	// Deck size remaining - Bits 14 to 54
    	int deckSize = state.getDeck().getCardsLeft();
    	for (int i = 0; i < 41; i++) {
    		if (i <= deckSize) {
    			boardState.add(1.0);
    		} else {
    			boardState.add(0.0);
    		}
    	}
		
		return boardState;
	}

}