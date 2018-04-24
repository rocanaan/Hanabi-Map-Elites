package MCTSVariations;

import com.fossgalaxy.games.fireworks.ai.mcts.*;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.events.GameEvent;
import java.util.LinkedList;

/**
 * - Create a variation of PredictorMCTS that uses probabilistic model agent -
 * Modify the last agent with the ability to look at history and update the
 * probabilities of the model agent
 */
public class LearningPredictorMCTS extends MCTSPredictor {

    ProbabilisticModel[] agents;

    //constructors
    public LearningPredictorMCTS(ProbabilisticModel[] others) {
        super(others);
        this.agents = others;
    }
    public LearningPredictorMCTS(ProbabilisticModel[] others, int roundLength) {
        super(others, roundLength);
        this.agents = others;
    }
    public LearningPredictorMCTS(ProbabilisticModel[] others, int roundLength, int rolloutDepth, int treeDepthMul) {
        super(others, roundLength, rolloutDepth, treeDepthMul);
        this.agents = others;
    }

    @Override
    public Action doMove(int agentID, GameState state) {
        LinkedList<GameEvent> history = state.getHistory();
        for (int i = 0; i < agents.length; i++) {
            if (agents[i] != null) {
                // update the probabilities

            }
        }
        return super.doMove(agentID, state);
    }
}
