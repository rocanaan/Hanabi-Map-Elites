package SmartState;


import java.util.List;

import com.fossgalaxy.games.fireworks.state.GameState;

public abstract class  SmartStateObservation {
	public int length;	

	public abstract List<Double> getObservation(GameState state, int playerID);

}
