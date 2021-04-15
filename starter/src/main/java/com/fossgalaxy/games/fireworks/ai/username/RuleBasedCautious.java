package com.fossgalaxy.games.fireworks.ai.username;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.rule.PlaySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.random.DiscardRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.random.TellRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.*;
import com.fossgalaxy.games.fireworks.ai.osawa.rules.OsawaDiscard;
import com.fossgalaxy.games.fireworks.ai.rule.simple.PlayIfCertain;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;

/**
This is an agent derived from human game-play. 
The agent plays cautiously, never losing a life. 
 */
public class RuleBasedCautious {

    @AgentBuilderStatic("RuleBasedCautious")
    public static Agent buildRuleBased() {
        ProductionRuleAgent pra = new ProductionRuleAgent();

        //you can add rules to your agent here
        pra.addRule(new PlayIfCertain());
        pra.addRule(new PlaySafeCard());
        pra.addRule(new TellAnyoneAboutUsefulCard());
        pra.addRule(new OsawaDiscard());
        pra.addRule(new DiscardRandomly());


        return pra;
    }

}
