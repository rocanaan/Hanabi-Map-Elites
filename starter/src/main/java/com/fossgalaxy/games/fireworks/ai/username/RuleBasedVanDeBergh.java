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
 *Van den Bergh et al used a Genetic Algorithm (GA) to evolve the best options
 *for each section, resulting in the following rules as an implementation:
 */
public class RuleBasedVanDeBergh {

    private static final BiFunction<Integer, GameState, Boolean> hasLives =  (i,state) ->{
    		return state.getLives()>1;
    };

	@AgentBuilderStatic("RuleBasedVanDeBergh")
    public static ProductionRuleAgent buildRuleBased(){
        ProductionRuleAgent pra = new ProductionRuleAgent();

        //you can add rules to your agent here
        pra.addRule(new IfRule(hasLives, new  PlayProbablySafeCard(0.6), new PlaySafeCard()));
        pra.addRule(new DiscardProbablyUselessCard(0.99));
        pra.addRule(new TellAnyoneAboutUsefulCard());
        pra.addRule(new TellAnyoneAboutUselessCard());
        pra.addRule(new TellMostInformation());
        pra.addRule(new DiscardProbablyUselessCard(0.0));


        return pra;
    }
    
//    public static  boolean hasLives(int lives, BasicState state ) {
//    		return state.getLives()>1;
//    }

}
