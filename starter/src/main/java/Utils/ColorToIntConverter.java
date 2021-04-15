package Utils;

import com.fossgalaxy.games.fireworks.state.CardColour;

public class ColorToIntConverter {
	public static int colorToInt(CardColour color) {
		switch (color){
		case RED:
			return 0;
		case ORANGE:
			return 1;
		case GREEN:
			return 2;
		case WHITE:
			return 3;
		case BLUE:
			return 4;
		default:
			return -1;
		}
	}
	
	public static CardColour intToColor (int colorIndex) {
		switch (colorIndex) {
		case 0:
			return CardColour.RED;
		case 1:
			return CardColour.ORANGE;
		case 2:
			return CardColour.GREEN;
		case 3:
			return CardColour.WHITE;
		case 4:
			return CardColour.BLUE;
		default:
			return null;
		}
	}
	
	

}
