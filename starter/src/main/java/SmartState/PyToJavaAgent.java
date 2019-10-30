package SmartState;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

public class PyToJavaAgent implements Agent{
	

/*	 This class assumes that you have a Python process that is running N Hanabi games on the hanabi learning environment
 The python code will be responsible for generating the game states, applying actions to the generated state and keeping track of the average score of the agent
 Python will also return, with every action it receives, an indication of whether the simulation is over (if we've reached the end of the Nth game).
 Python will also compute the average score for the agent in the end.\
 I am assuming all inter-process communication and synchronization issues are already taken care of
 */
	
//	 Read whatever communication channel you're using to communicate with Python to get a new int[] representation of the game state
	private  int[] getPythonState() {
		int[] array = {0};
		return array;
	}
	
	
// Send an action to python and read a message telling whether the game is over
	private boolean sendActionToPython(Action a){
		if (true) {
			 return false;
		}
		return true;
	}

// Constructor that takes a Java agent that ultimately provides the actions
	public PyToJavaAgent(Agent policy) {
		super();
		this.policy = policy;
	}


	private Agent policy;
	
// Wraps the underlying Java agent's doMove() method with the code for communicating with Python
	public Action doMoveFromPython(int[] array) {
		
		int agentID = PyToJavaGameState.getAgentIDFromPy(array);
		GameState state = PyToJavaGameState.convertPyToJava(array);
		return doMove(agentID,state);
	}


// The original Java agent's move
	@Override
	public Action doMove(int agentID, GameState state) {
		// TODO Auto-generated method stub
		return policy.doMove(agentID, state);
	}
	
// Simple template of a program that, when the communication is implemented, would simply return the action for each state until it is told to stop
	public static void main(String[] args) {
		String agentName = "RuleBasedPiers";
		Agent agent = AgentUtils.buildAgent(agentName);
		
		PyToJavaAgent py_agent = new PyToJavaAgent(agent);
		
		boolean is_over = false;
		while (!is_over) {
			int[] array = py_agent.getPythonState();
			Action action = py_agent.doMoveFromPython(array);
			is_over = py_agent.sendActionToPython(action) ;
			
		}
	}

}
