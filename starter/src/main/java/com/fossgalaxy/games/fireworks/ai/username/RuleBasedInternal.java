package com.fossgalaxy.games.fireworks.ai.username;

import com.fossgalaxy.games.fireworks.ai.Agent;
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
 * This is a clone of the agent presented by Osawa that shares the same name. 
 * It features memory of the information it has been told about its own hand but 
 * does not remember information about what other players have been told.
 */
public class RuleBasedInternal {

    @AgentBuilderStatic("RuleBasedInternal")
    public static Agent buildRuleBased() {
        ProductionRuleAgent pra = new ProductionRuleAgent();

        //you can add rules to your agent here
        pra.addRule(new PlaySafeCard());
        pra.addRule(new OsawaDiscard());
        pra.addRule(new TellPlayableCard());
        pra.addRule(new TellRandomly());
        pra.addRule(new DiscardRandomly());



        return pra;
    }

}
