package SmartState;

import java.util.ArrayList;
import java.util.List;

import com.fossgalaxy.games.fireworks.state.GameState;

public class SmartGameState {
	
	public List<SmartStateObservation> observations;
	
	public SmartGameState(List<SmartStateObservation> observations) {
		this.observations = observations;
	}
	
	public double[] getObservation(GameState state, int playerID) {
		List<Double> features = new ArrayList<Double>();
		// Run getObservation for each observation
		for (SmartStateObservation obs:observations) {
			features.addAll(obs.getObservation(state, playerID));
		}
		// Create an array with all the observations
		double r[] = new double[features.size()];
		for (int i = 0; i<features.size(); i++) {
			r[i] = features.get(i);
		}
		return r;
	}

}
