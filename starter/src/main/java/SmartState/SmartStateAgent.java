package SmartState;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;

public class SmartStateAgent implements Agent {
	
	Agent policy;
	SmartGameState smartState;
	
	public SmartStateAgent(SmartGameState smartState, Agent policy) {
		this.smartState = smartState;
		this.policy = policy;
	}
	

	@Override
	public Action doMove(int agentID, GameState state) {
		// TODO Auto-generated method stub
		
		double smartStateArray[] = smartState.getObservation(state, agentID);
		System.out.println("Printing Smart State of length " + smartStateArray.length);
		System.out.print("{ ");
		for (int i = 0; i<smartStateArray.length;i++) {
			System.out.print(String.format("%.2f",smartStateArray[i]) + " ");
		}
		System.out.println("}");

		
		return policy.doMove(agentID, state);
	}
	
	
	
}



