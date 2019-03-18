package MapElites;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.actions.PlayCard;
import com.fossgalaxy.games.fireworks.state.actions.TellColour;
import com.fossgalaxy.games.fireworks.state.actions.TellValue;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ReportAgent implements Agent{
	private Agent agent;
	public int possibleHints;
	public int hintsGiven;
	public double totalPlayability;
	public int countPlays;
    
    public ReportAgent(Agent a){
        agent = a;
        possibleHints = 0;
        hintsGiven = 0;
        totalPlayability = 0.0;
        countPlays = 0;
        
    }
    
    @Override
    public Action doMove(int agentID, GameState state) {
    		Action action = agent.doMove(agentID, state);
        
        
    		
//        System.out.println(action.getClass().getName());
        if (action instanceof PlayCard) {
        		updatePlayability(state, agentID, (PlayCard)action);
        }
        updateHints(state, action);
        
        
        return action;
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
//        System.out.println("Probability is " + probability);
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
 
    
   
}
