package gamestateAnalyzer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fossgalaxy.games.fireworks.state.BasicState;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.actions.DiscardCard;
import com.fossgalaxy.games.fireworks.state.actions.PlayCard;
import com.fossgalaxy.games.fireworks.state.actions.TellColour;
import com.fossgalaxy.games.fireworks.state.actions.TellValue;
import com.fossgalaxy.games.fireworks.state.events.CardDiscarded;
import com.fossgalaxy.games.fireworks.state.events.CardInfoColour;
import com.fossgalaxy.games.fireworks.state.events.CardInfoValue;
import com.fossgalaxy.games.fireworks.state.events.CardPlayed;
import com.fossgalaxy.games.fireworks.state.events.GameEvent;

public class HistoryAnalyzer {
	
	private List<GameEvent> history;
	private List<StateActionPair> gameSequence;
	private int historyIndex;
	private int handSize;
	private int playerCount;
	private int playerID;
	
	public HistoryAnalyzer (List<GameEvent> history, int handSize, int playerCount, int playerID) {
		this.history = history;
		this.playerID = playerID;
		this.handSize = handSize;
		this.playerCount = playerCount;
		gameSequence = new ArrayList<StateActionPair>();
		historyIndex  = 0;
		
	}
	
	public void getFullGameSequence() {
		getStartingState(handSize,playerCount,history,playerID);
		
		
		
		while (historyIndex < history.size()) {
			readNextEvent();
		}
		System.out.println("Finished getting game Sequence");
	}

	public void getStartingState(int handSize,int playerCount, List<GameEvent> history, int playerID) {
		GameState startState = new BasicState(handSize, playerCount);
		
		if (history.size() > 0) {
			int i = 1;
			for(; i <= playerCount*handSize; i++) {
				GameEvent event = history.get(i);
				event.apply(startState, playerID);
			}
			historyIndex = i;
			
		}
		
		StateActionPair sap = new StateActionPair(startState, null);
		gameSequence.add(sap);
		
		return;
	}
	
	
	
	public List<StateActionPair> getGameSequence (){
		return gameSequence;
	}
	
	
	void readNextEvent () {
		StateActionPair sap = gameSequence.get(gameSequence.size()-1);
		GameState currentState = sap.state;
		GameEvent nextEvent = history.get(historyIndex);
		
		if (nextEvent!= null) {
		
			Action action  = eventToAction(nextEvent);
			if(action != null) {		
				sap.action = action;
				historyIndex ++;
				GameState nextState = currentState.getCopy();
				action.apply(playerID, nextState);
				gameSequence.add(new StateActionPair (nextState, null));
			}
			else {
				nextEvent.apply(currentState, playerID);
				historyIndex ++;
			}
		}
		

		//GameEvent event = gameHistory.get(index)
	}
		
	Action eventToAction(GameEvent e) {
		Action action = null;
		if (e instanceof CardDiscarded) {
			String s = e.toString();
			int indexSlot = s.indexOf("slot") + 5; // "slot x"
			int slot = (int)s.charAt(indexSlot);
			
			System.out.println("Discarded card at slot  " + slot);

			
			action = new DiscardCard(slot);		
		}
		else if (e instanceof CardPlayed) {
			
			String s = e.toString();
			int indexSlot = s.indexOf("slot") + 5; // "slot x"
			int slot = (int)s.charAt(indexSlot);
			
			action = new PlayCard(slot);
			
			System.out.println("Played card at slot ");
			
		}
		else if (e instanceof CardInfoColour) {
			CardInfoColour colorHint = (CardInfoColour) e;
			
			System.out.println("Hint Color " + colorHint.getColour() + " to player " + colorHint.getPlayerTold() );

			action = new TellColour(colorHint.getPlayerTold(),colorHint.getColour());
			
		}
		else if (e instanceof CardInfoValue) {
			CardInfoValue valueHint = (CardInfoValue) e;
			action = new TellValue(valueHint.getPlayerTold(),valueHint.getValue());
			
			System.out.println("Hint Value " + valueHint.getValue() + " to player " +valueHint.getPlayerTold() );
		}
		return action;
	}
	
//	List<GameState> getStateHistory(GameState state, LinkedList<GameEvent> history, int playerID){
//		return getStateHistory(state, history, 0);
//	}
//
//	
//	List<GameState> getStateHistory(GameState state, LinkedList<GameEvent> history, int playerID, int firstIndex){
//		List<GameState> stateHistory = new ArrayList<GameState> ();
//		
//		if (firstIndex == 0) {
//			GameState start = new BasicState(state.getHandSize(), state.getPlayerCount()); // possible problem if I might want the variable state to be empty
//		}
//		
//		return stateHistory;
//	}
//	
	
	
}
