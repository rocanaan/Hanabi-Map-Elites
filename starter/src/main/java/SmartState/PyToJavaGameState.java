package SmartState;

import java.awt.Color;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.state.BasicState;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.CardColour;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

public class PyToJavaGameState {
	

	//If you want to parse directly from the strin
	public static GameState convertPyToJava(String descriptor) {
		return null;
	}
	
	public static GameState convertPyToJava(int[] array) {
		return null;
	}
	
	public static int getAgentIDFromPy(int[] array) {
		return 0;
	}
	
	public static void main (String[] args) {
		
		Agent human = AgentUtils.buildAgent("HumanControlledAgent");
		
		int numPlayers =2;
		
		
		BasicState state = new BasicState(numPlayers);
		
		state.setLives(3);
		state.setInformation(8);
		// My cards. I should not actually set the cards this way as I don't know which they are
		state.setCardAt(0, 0, new Card(2, CardColour.BLUE));
		
		
		// Their cards
		state.setCardAt(1, 0, new Card(2, CardColour.RED));

		state.setKnownValue(0, 0, 2, CardColour.RED);
		state.setKnownValue(0, 0, 2, CardColour.ORANGE);

		state.addToDiscard(new Card(3, CardColour.RED));
		
		state.setTableValue(CardColour.RED, 2);
		state.setTableValue(CardColour.BLUE, 3);

		
		
		human.doMove(0, state);
	}

	
	

}
