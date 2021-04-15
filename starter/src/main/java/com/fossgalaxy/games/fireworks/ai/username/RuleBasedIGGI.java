package com.fossgalaxy.games.fireworks.ai.username;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.iggi.LegalRandom;
import com.fossgalaxy.games.fireworks.ai.rule.PlaySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.random.DiscardRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.random.TellRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.*;
import com.fossgalaxy.games.fireworks.ai.osawa.rules.OsawaDiscard;
import com.fossgalaxy.games.fireworks.ai.osawa.rules.TellPlayableCardOuter;
import com.fossgalaxy.games.fireworks.ai.rule.simple.PlayIfCertain;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;

/**
 * This agent is a modification of Cautious. 
 * The alteration to a deterministic Discard function greatly aids the predictability of this player
 */
public class RuleBasedIGGI {

    @AgentBuilderStatic("RuleBasedIGGI")
    public static Agent buildRuleBased() {
        ProductionRuleAgent pra = new ProductionRuleAgent();

        //you can add rules to your agent here
        pra.addRule(new PlayIfCertain());
        pra.addRule(new PlaySafeCard());
        pra.addRule(new TellAnyoneAboutUsefulCard());
        pra.addRule(new OsawaDiscard());
        pra.addRule(new DiscardOldestFirst());
        pra.addRule(new LegalRandom());


        return pra;
    }

}
