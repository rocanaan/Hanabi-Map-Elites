package com.fossgalaxy.games.fireworks.ai.username;

import java.util.function.BiFunction;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.rule.PlaySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.random.DiscardRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.random.TellRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.*;
import com.fossgalaxy.games.fireworks.ai.osawa.rules.OsawaDiscard;
import com.fossgalaxy.games.fireworks.ai.rule.simple.PlayIfCertain;
import com.fossgalaxy.games.fireworks.annotations.AgentBuilderStatic;
import com.fossgalaxy.games.fireworks.ai.rule.random.DiscardProbablyUselessCard;
import com.fossgalaxy.games.fireworks.state.BasicState;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.ai.rule.wrapper.IfRule;
import com.fossgalaxy.games.fireworks.ai.rule.random.PlayProbablySafeCard;

/**
 * This is an agent designed to be intelligent but with some flaws:
 * it does not possess intelligent Tell rules, and has a risky Play rule as well. 
 * Understanding this agent is the key to playing well with it, 
 * because other agents can give it the information it needs to prevent it from playing poorly.
 */
public class RuleBasedFlawed {

    private static final BiFunction<Integer, GameState, Boolean> hasLives =  (i,state) ->{
    		return state.getLives()>1;
    };

	@AgentBuilderStatic("RuleBasedFlawed")
    public static Agent buildRuleBased(){
        ProductionRuleAgent pra = new ProductionRuleAgent();

        //you can add rules to your agent here
        
        pra.addRule(new PlaySafeCard());
        pra.addRule(new PlayProbablySafeCard(0.25));
        pra.addRule(new TellRandomly());
        pra.addRule(new OsawaDiscard());
        pra.addRule(new DiscardOldestFirst());
        pra.addRule(new DiscardRandomly());

        return pra;
    }
    
//    public static  boolean hasLives(int lives, BasicState state ) {
//    		return state.getLives()>1;
//    }

}
