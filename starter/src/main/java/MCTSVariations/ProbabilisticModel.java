package MCTSVariations;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import java.util.ArrayList;
import java.util.Random;

public class ProbabilisticModel implements Agent {

    public ArrayList<Agent> agents;
    public ArrayList<Float> prob;
    Random rand;
    
    public ProbabilisticModel(Agent[] possibleAgents) {
        agents = new ArrayList<Agent>();
        prob = new ArrayList<Float>();
        rand = new Random();
        for (Agent a : possibleAgents) {
            agents.add(a);
            prob.add(0f);
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
}
