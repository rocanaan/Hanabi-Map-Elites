	package SmartState.Observations;

import java.util.ArrayList;
import java.util.List;

import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;

import NeuralNetworkControllers.GamestateEvaluator;
import SmartState.SmartStateObservation;

public class PartnerKnowsCardValueObservation extends SmartStateObservation{
	
	
	public PartnerKnowsCardValueObservation() {
		this.length = 0; //state.getPlayerCount()*state.getHandSize(); // TODO: The way the code is currently structured, I will not have a gamestate when the constructor is called
	}

	@Override
	public List<Double> getObservation(GameState state, int playerID) {
		List<Double> partnerKnowsCardColor = new ArrayList<Double>();
		
		for (int i = 1; i<state.getPlayerCount(); i++){
			int targetID = (playerID+i)%state.getPlayerCount();
			Hand hand = state.getHand(targetID);
			for (int c = 0; c<state.getHandSize(); c++) {
				if (hand.getKnownValue(c) == null) {
					partnerKnowsCardColor.add(0.0);
				}
				else {
					partnerKnowsCardColor.add(1.0);
				}
			}
		}
		return partnerKnowsCardColor;
	}

}
