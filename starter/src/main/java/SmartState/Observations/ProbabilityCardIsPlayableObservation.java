package SmartState.Observations;

import java.util.ArrayList;
import java.util.List;

import com.fossgalaxy.games.fireworks.state.GameState;

import NeuralNetworkControllers.GamestateEvaluator;
import SmartState.SmartStateObservation;

public class ProbabilityCardIsPlayableObservation extends SmartStateObservation{
	
	
	public ProbabilityCardIsPlayableObservation() {
		this.length = 0; //state.getPlayerCount()*state.getHandSize(); // TODO: The way the code is currently structured, I will not have a gamestate when the constructor is called
	}

	@Override
	public List<Double> getObservation(GameState state, int playerID) {
		List<Double> playableProbabilities = new ArrayList<Double>();
		
		for (int i = 0; i<state.getPlayerCount(); i++){
			int targetID = (playerID+i)%state.getPlayerCount();
			GamestateEvaluator gse = new GamestateEvaluator(playerID, state);
			playableProbabilities.addAll(gse.getProbabiltyPlayable(targetID));
		}
		
		return playableProbabilities;
	}

}
