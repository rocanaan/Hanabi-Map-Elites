package MCTSVariations;

import Evolution.Rulebase;
import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.SpecializedAgent;
import com.fossgalaxy.games.fireworks.ai.mcts.MCTSPredictor;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
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
        boolean mixed = true;

        String agentName = "pmctsND";
        String otherAgentName = "ourAgent";

        int[][] agentChromossomesMixed = {
            {68, 7, 52, 35, 36, 33, 54, 16, 48, 69, 19, 4, 27},
            {63, 50, 7, 67, 29, 39}};

        int[][] agentChromossomesMirror = {
            {67, 69, 62, 57, 46, 8, 33, 36, 19, 44, 54, 26},
            {46, 10, 7, 67, 45, 29, 4, 71, 17, 43, 19}};

        int[][] agentChromossomes = (mixed) ? agentChromossomesMixed : agentChromossomesMirror;
        
        //// building the ProbabilisticModel agent
//        Agent[] testPool = {RuleBasedIGGI.buildRuleBased(), RuleBasedInternal.buildRuleBased(), RuleBasedOuter.buildRuleBased(),
//            new SampleLegalRandom(), RuleBasedVanDeBergh.buildRuleBased(),
//            RuleBasedFlawed.buildRuleBased(), RuleBasedPiers.buildRuleBased()};
        //ProbabilisticModel PMAgent = new ProbabilisticModel(testPool);
        Rulebase rb = new Rulebase(false);
        Agent[] sAgents = new Agent[2];
        for (int i = 0; i < agentChromossomes.length; i++) {
            int[] chromossome = agentChromossomes[i];
            Rule[] rules = new Rule[chromossome.length];
            for (int j = 0; j < chromossome.length; j++) {
                rules[j] = rb.ruleMapping(chromossome[j]);
            }
            HistogramAgent agent = rb.makeAgent(rules);
            sAgents[i] = agent;
        }
        SpecializedAgent sa = new SpecializedAgent(sAgents);

        // building the Predictor MCTS using the ProbabilisticModel
        Agent[] agents = new Agent[numPlayers];
        agents[0] = null;
        for (int j = 1;
                j < numPlayers;
                j++) {
            agents[j] = sa;
        }
        Agent mctsAgent = new MCTSPredictor(agents);
        Player player = new AgentPlayer(agentName, mctsAgent);

        Random random = new Random();
        StatsSummary statsSummary = new BasicStats();
        for (int i = 0; i < numGames; i++) {
            GameRunner runner = new GameRunner("test-game", numPlayers);

            runner.addPlayer(player);
            for (int j = 1; j < numPlayers; j++) {
                player = new AgentPlayer(otherAgentName, sa);
                runner.addPlayer(player);
            }

            GameStats stats = runner.playGame(random.nextLong());
            statsSummary.add(stats.score);
            System.out.println("Game number " + i + " complete");
        }

        //print out the stats
        System.out.println(
                "Stats for agent " + agentName);
        System.out.println(String.format("Our agent: Avg: %f, min: %f, max: %f",
                statsSummary.getMean(),
                statsSummary.getMin(),
                statsSummary.getMax()));
    }
}
