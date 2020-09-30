package SPARTA_Communication;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fossgalaxy.games.fireworks.state.BasicState;
import com.fossgalaxy.games.fireworks.state.GameState;

public class HLEStateConverter {
	
	private String[] keys = {"discard_pile", "complete_card_knowledge", "current_player_offset", 
							"num_players", "fireworks", "observed_hands", "card_knowledge", 
							"current_player", "deck_size", "life_tokens", "legal_moves_as_int", 
							"information_tokens", "legal_moves"};
	

	public static BasicState convert(Map<String, Object> map) {
		
		BasicState state = new BasicState ( (int) map.get("num_players") );
		
		
		ArrayList discardPile = (ArrayList) map.get("discard_pile");
		
		for (Object objCardOnDiscard : discardPile) {
			LinkedHashMap cardOnDiscard = (LinkedHashMap) objCardOnDiscard;
			System.out.println(cardOnDiscard);
		}
		
//		System.out.println(discardPile.get(0));
//		System.out.println(discardPile.get(0).getClass());
//		LinkedHashMap cardOnDiscard = 
		
		
		
		return null;
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String path = "/Users/rodrigocanaan/Dev/HLE_AIIDE/hanabi-ad-hoc-learning/Experiments/SPARTA_Integration/states";
		String infix = "state";
		
		StateJSONReader reader = new StateJSONReader(path,infix);
		boolean found = false;
		Map<String, Object> map = null;
		while (!found){
			map = reader.read();
			if (map == null){
				System.out.println("Waiting for state");
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				found = true;
			}
		}
		BasicState state = convert(map);

	}

}
