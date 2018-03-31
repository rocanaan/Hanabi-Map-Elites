package com.fossgalaxy.games.fireworks.ai.username;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.rule.PlaySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.random.DiscardRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.random.TellRandomly;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;

/**
 * Example showing how to build a production rule agent.
 *
 * These agents execute a series of rules in order.
 */
public class SampleRuleBased {

    @AgentBuilderStatic("SampleRuleBased")
    public static Agent buildRuleBased() {
        ProductionRuleAgent pra = new ProductionRuleAgent();

        //you can add rules to your agent here
        pra.addRule(new PlaySafeCard());
        pra.addRule(new TellRandomly());
        pra.addRule(new DiscardRandomly());


        return pra;
    }

}
