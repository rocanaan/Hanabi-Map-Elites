package SmartState;

import java.util.ArrayList;
import java.util.List;

import com.fossgalaxy.games.fireworks.state.GameState;

public class SmartGameState {
	
	public SmartStateObservation[] observations;
	
	public SmartGameState(SmartStateObservation[] observations) {
		this.observations = observations;

	}
	
	public double[] getObservation(GameState state, int playerID) {
		List<Double> listFeatures = new ArrayList<Double>();
		// Run getObservation for each observation
		for (SmartStateObservation obs:observations) {
			listFeatures.addAll(obs.getObservation(state, playerID));
		}
		// Convert from list to array
		double arrayFeatures[] = new double[listFeatures.size()];
		for (int i = 0; i<listFeatures.size(); i++) {
			arrayFeatures[i] = listFeatures.get(i);
		}
		return arrayFeatures;
	}

}
