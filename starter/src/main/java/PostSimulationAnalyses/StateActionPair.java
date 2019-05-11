package PostSimulationAnalyses;

import java.io.Serializable;

import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;

public class StateActionPair implements Serializable{

	public GameState state;
	public Action action;
	public int agentID;
	
	public StateActionPair(GameState state, Action action, int agentID) {
		this.state = state;
		this.action = action;
		this.agentID = agentID;
	}
}