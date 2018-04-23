package MCTSVariations;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.mcts.MCTSPredictor;
import com.fossgalaxy.games.fireworks.ai.username.*;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

import java.util.Random;

/**
 * Game runner for testing.
 *
 * This will run a bunch of games with your agent so you can see how it does.
 */
public class PredictorMctsTest {

    public static void main(String[] args) {
        int numPlayers = 2;
        int numGames = 10;

        String agentName = "pmctsND";
        String otherAgentName = "RuleBasedPiers";

        // building the ProbabilisticModel agent
        Agent[] testPool = {RuleBasedIGGI.buildRuleBased(), RuleBasedInternal.buildRuleBased(), RuleBasedOuter.buildRuleBased(),
                            new SampleLegalRandom(), RuleBasedVanDeBergh.buildRuleBased(),
                            RuleBasedFlawed.buildRuleBased(), RuleBasedPiers.buildRuleBased()};
        ProbabilisticModel PMAgent = new ProbabilisticModel(testPool);
        // building the Predictor MCTS using the ProbabilisticModel
        Agent[] agents = new Agent[numPlayers];
        agents[0] = null;
        for (int j = 1; j < numPlayers; j++) {
            agents[j] = PMAgent;
        }
        Agent mctsAgent = new MCTSPredictor(agents);
        Player player = new AgentPlayer(agentName, mctsAgent);

        Random random = new Random();
        StatsSummary statsSummary = new BasicStats();
        for (int i = 0; i < numGames; i++) {
            GameRunner runner = new GameRunner("test-game", numPlayers);

            runner.addPlayer(player);
            for (int j = 1; j < numPlayers; j++) {
                player = new AgentPlayer(otherAgentName, AgentUtils.buildAgent(otherAgentName));
                runner.addPlayer(player);
            }

            GameStats stats = runner.playGame(random.nextLong());
            statsSummary.add(stats.score);
            System.out.println("Game number " + i + " complete");
        }

        //print out the stats
        System.out.println("Stats for agent " + agentName);
        System.out.println(String.format("Our agent: Avg: %f, min: %f, max: %f",
                statsSummary.getMean(),
                statsSummary.getMin(),
                statsSummary.getMax()));
    }
}
