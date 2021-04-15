	package SmartState.Observations;

import java.util.ArrayList;
import java.util.List;

import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.CardColour;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;

import NeuralNetworkControllers.GamestateEvaluator;
import SmartState.SmartStateObservation;
import Utils.ColorToIntConverter;

public class PartnerCardOneHotColorAndValueObservation extends SmartStateObservation{
	
	
	public PartnerCardOneHotColorAndValueObservation() {
		this.length = 0; //state.getPlayerCount()*state.getHandSize(); // TODO: The way the code is currently structured, I will not have a gamestate when the constructor is called
	}

	@Override
	public List<Double> getObservation(GameState state, int playerID) {
		List<Double> partnerCardColorAndValuesOneHot = new ArrayList<Double>();
		
		//Note, here i starts at 1 because we don't want our own hand
		for (int i = 1; i<state.getPlayerCount(); i++){
			int targetID = (playerID+i)%state.getPlayerCount();
			Hand hand = state.getHand(targetID);
			for (int c = 0; c<state.getHandSize(); c++) {
				Card card = hand.getCard(c);
				int colorIndex = ColorToIntConverter.colorToInt(card.colour);
				int value = card.value;
				double[] valueOneHot = new double[25];
				if (value >0 && value <=5 && colorIndex >=0 && colorIndex<5) {
					valueOneHot[(colorIndex*5) + value-1]  = 1;
				}
				for (double v: valueOneHot) {
					partnerCardColorAndValuesOneHot.add(v);
				}
			}
		}
		
		return partnerCardColorAndValuesOneHot;
	}

}
