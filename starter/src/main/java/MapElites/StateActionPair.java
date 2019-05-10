package MapElites;

import java.io.Serializable;

import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;

public class StateActionPair implements Serializable{

	GameState state;
	Action action;
	
	public StateActionPair(GameState state, Action action) {
		this.state = state;
		this.action = action;
	}
}