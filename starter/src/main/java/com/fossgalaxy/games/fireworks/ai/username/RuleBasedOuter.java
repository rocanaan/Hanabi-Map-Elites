package com.fossgalaxy.games.fireworks.ai.username;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.osawa.rules.TellPlayableCardOuter;
import com.fossgalaxy.games.fireworks.ai.rule.random.TellPlayableCard;
import com.fossgalaxy.games.fireworks.ai.rule.PlaySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.random.DiscardRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.random.TellRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.*;
import com.fossgalaxy.games.fireworks.ai.osawa.rules.OsawaDiscard;
import com.fossgalaxy.games.fireworks.ai.rule.simple.PlayIfCertain;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;

/**
 *This is a clone of the agent presented by Osawa with the same name.
 *It features knowledge of what the other agents have been told already, to avoid repeating Tell actions.
 */
public class RuleBasedOuter {

    @AgentBuilderStatic("RuleBasedOuter")
    public static Agent buildRuleBased() {
        ProductionRuleAgent pra = new ProductionRuleAgent();

        //you can add rules to your agent here
        pra.addRule(new PlaySafeCard());
        pra.addRule(new OsawaDiscard());
        pra.addRule(new TellPlayableCardOuter());
        pra.addRule(new TellUnknown());
        pra.addRule(new DiscardRandomly());



        return pra;
    }

}
