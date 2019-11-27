package MapElites;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.TimedHand;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.actions.DiscardCard;
import com.fossgalaxy.games.fireworks.state.actions.PlayCard;
import com.fossgalaxy.games.fireworks.state.actions.TellColour;
import com.fossgalaxy.games.fireworks.state.actions.TellValue;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import PostSimulationAnalyses.StateActionPair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ReportAgent implements Agent{
	private Agent agent;
	public int possibleHints;
	public int hintsGiven;
	public double totalPlayability;
	public int countPlays;
	public int totalInformationPlays;
	public boolean recordPlays;
	
	public double totalDiscardSlot;
	public int countDiscards;
	private ArrayList<StateActionPair> stateActionArchive;
	

    
    public ReportAgent(Agent a){
        agent = a;
        possibleHints = 0;
        hintsGiven = 0;
        totalPlayability = 0.0;
        countPlays = 0;
        totalInformationPlays = 0;
    	totalDiscardSlot = 0.0;
    	countDiscards = 0;
        recordPlays = false;
        stateActionArchive = null;
    }
    public ReportAgent(Agent a, boolean recordPlays){
        agent = a;
        possibleHints = 0;
        hintsGiven = 0;
        totalPlayability = 0.0;
        countPlays = 0;
        totalInformationPlays = 0;
        this.recordPlays = recordPlays;
        if (recordPlays) {
        		stateActionArchive = new ArrayList<StateActionPair>();
        }
        
    }
    
    @Override
    public Action doMove(int agentID, GameState state) {
    		Action action = agent.doMove(agentID, state);
        
        
    		
//        System.out.println(action.getClass().getName());
        if (action instanceof PlayCard) {
        		updatePlayability(state, agentID, (PlayCard)action);

        }
        updateHints(state, action);
        if (action instanceof DiscardCard) {
        	DiscardCard dAction = (DiscardCard)action;
        	totalDiscardSlot += (double)dAction.slot/((double)state.getHandSize()-1);
        	countDiscards++;
        }
        
        if (recordPlays) {
        		stateActionArchive.add(new StateActionPair(state,action, agentID));
        }
        
        
        return action;
    }
    
    public ArrayList<StateActionPair> getStateActionArchive(){
    		return stateActionArchive;
    }
    
    private void updatePlayability(GameState state, int playerID, PlayCard action) {
    		int slot = action.slot;
    		double probability = 0;
    		Map<Integer, List<Card>> possibleCards = DeckUtils.bindBlindCard(playerID, state.getHand(playerID), state.getDeck().toList());
    		for (Map.Entry<Integer, List<Card>> entry : possibleCards.entrySet()) {
        		if (entry.getKey() == slot) {
        			probability = DeckUtils.getProbablity(entry.getValue(), x -> isPlayable(x, state));
        		}
    		}
//        	System.out.println("Probability is " + probability);
    		int knownInformation = 0;
    		if (state.getHand(playerID).getKnownColour(slot) != null) {
    			knownInformation ++;
    		}
    		if (state.getHand(playerID).getKnownValue(slot)!=null) {
    			knownInformation ++;
    		}
    		totalInformationPlays += knownInformation;
        	totalPlayability += probability;
        	countPlays++;
//        	System.out.println("Total playability: " + totalPlayability);
//        	System.out.println("Count Plays: " + countPlays);
        	
    		
    }
    
    private void updateHints (GameState state, Action action) {
    		if (state.getInfomation() > 0) {
    			possibleHints++;
    			if (action instanceof TellColour || action instanceof TellValue) {
    				hintsGiven++;
    			}
    		}
//        	System.out.println("Possible Hints: " + possibleHints);
//        	System.out.println("Hints Given: " + hintsGiven);
    }
    
    public boolean isPlayable(Card card, GameState state) {
        return state.getTableValue(card.colour) + 1 == card.value;
    }
 
    public double getRiskAversion() {
    	return totalPlayability / (double) countPlays;
    }
    
    public double getCommunicativeness() {
    	return (double) hintsGiven / (double) possibleHints;
    }
    
    public double getInformationPlays() {
    	return (double) totalInformationPlays / (2 * (double) countPlays);
    }
    
    public double getAverageDiscardSlot() {
    	return totalDiscardSlot / (double) countDiscards;
    }
   
}
