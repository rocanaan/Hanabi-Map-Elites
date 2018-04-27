package MCTSVariations;

import com.fossgalaxy.games.fireworks.ai.mcts.*;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.events.GameEvent;
import java.util.LinkedList;

/**
 * This agent keeps a copy of the game state that is updated until the last time this MCTS agent made a move.
 * When it updates the probability of the agents before making a move, the copy will be used to determine what 
 * could have happened at that point of time, and compare with what actually happened. The copy will then get updated
 * by one turn.
 */
public class LearningPredictorMCTS extends MCTSPredictor {
    GameState stateCopy; 
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
                // get the corresponding GameEvent from history
                
                // turn the GameEvent into an Action
                
                // call updateProbability(stateCopy, action); see comments at ProbabilisticModel
                
                // update stateCopy with the actual event so it is ready for the next agent

            }
        }
        // update stateCopy with the Action this MCTS agent will do this turn
        return super.doMove(agentID, state);
    }
}
