package MCTSVariations;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import java.util.ArrayList;
import java.util.Random;

public class ProbabilisticModel implements Agent {

    // Indices of these three ArrayLists should match
    public ArrayList<Agent> agents;
    public ArrayList<Float> prob; // probability of being the actual agent
    public ArrayList<Integer> nRight; //number of times it made the same move as the actual agent
    
    Random rand;
    
    public ProbabilisticModel(Agent[] possibleAgents) {
        agents = new ArrayList<Agent>();
        prob = new ArrayList<Float>();
        rand = new Random();
        for (Agent a : possibleAgents) {
            agents.add(a);
            prob.add(0f);
            nRight.add(10);  // starting value of nRight
        }

        // all agents are equally likely at the beginning
        float p = 1f / agents.size();
        for (int i = 0; i < prob.size(); i++) {
            prob.set(i, p);
        }
    }

    @Override
    public Action doMove(int agentID, GameState state) {
        int n = rand.nextInt(101);
        float p = n / 100f;
        int index = prob.size() - 1;
        float current = 0;
        for (int i = 0; i < prob.size(); i++) {
            current += prob.get(i);
            if (current >= p) {
                index = i;
                break;
            }
        }
        
        return agents.get(index).doMove(agentID, state);
    }
    
    public void updateProbability(GameState state, Action action){
        //checks what Action each possible agent would do given the GameState, then updates nRight accordingly
        
        
        float sum = 0f;
        for (int i : nRight) sum+=i;
        for (int j = 0; j < prob.size(); j++) {
            prob.set(j, nRight.get(j)/sum);
        }
    }
}
