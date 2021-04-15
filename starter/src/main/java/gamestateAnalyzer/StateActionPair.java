package gamestateAnalyzer;

import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;

public class StateActionPair {

	public GameState state;
	public Action	action;
	
	public StateActionPair(GameState state, Action action) {
		this.state = state;
		this.action = action;
	}
	

	
}
