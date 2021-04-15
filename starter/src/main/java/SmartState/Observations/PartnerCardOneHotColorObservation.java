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

public class PartnerCardOneHotColorObservation extends SmartStateObservation{
	
	
	public PartnerCardOneHotColorObservation() {
		this.length = 0; //state.getPlayerCount()*state.getHandSize(); // TODO: The way the code is currently structured, I will not have a gamestate when the constructor is called
	}

	@Override
	public List<Double> getObservation(GameState state, int playerID) {
		List<Double> partnerCardColorsOneHot = new ArrayList<Double>();
		
		//Note, here i starts at 1 because we don't want our own hand
		for (int i = 1; i<state.getPlayerCount(); i++){
			int targetID = (playerID+i)%state.getPlayerCount();
			Hand hand = state.getHand(targetID);
			for (int c = 0; c<state.getHandSize(); c++) {
				Card card = hand.getCard(c);
				CardColour color = card.colour;
				int colorIndex = ColorToIntConverter.colorToInt(color);
				double[] colorOneHot = new double[5];
				if (colorIndex >=0 && colorIndex <5) {
					colorOneHot[colorIndex]  = 1;
				}
				for (double v: colorOneHot) {
					partnerCardColorsOneHot.add(v);
				}
			}
		}
		
		return partnerCardColorsOneHot;
	}

}
