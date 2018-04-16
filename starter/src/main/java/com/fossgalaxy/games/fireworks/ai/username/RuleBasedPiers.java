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
 *This is an agent designed to use IfRules to
 *improve the overall score. Otherwise, it is similar to IGGI.
 */
public class RuleBasedPiers {

	    private static final BiFunction<Integer, GameState, Boolean> hasMoreThanOneLife =  (i,state) ->{
	    		return state.getLives()>1;
	    };
	    
	    /*
	     * This IfRule is designed as a hail Mary in the endgame: 
	     * if there is nothing left to lose, try to gain a point.
	     * This derives from human play, when typically during the end game we make random plays
	     *  if we know there is a playable card somewhere in our hand. 
	     *  This rule is more accurate, as it uses all the information it has gathered to calculate probabilities.
	     */
	    private static final BiFunction<Integer, GameState, Boolean> hailMary =  (i,state) ->{
			return (state.getLives()>1 && !state.getDeck().hasCardsLeft());
	    };
	    
	    private static final BiFunction<Integer, GameState, Boolean> informationLessThan4 =  (i,state) ->{
			return (state.getInfomation() < 4);
	    };

	@AgentBuilderStatic("RuleBasedPiers")
    public static Agent buildRuleBased(){
        ProductionRuleAgent pra = new ProductionRuleAgent();

        //you can add rules to your agent here
        pra.addRule(new IfRule(hailMary, new  PlayProbablySafeCard(0.0)));
        pra.addRule(new PlaySafeCard());
        pra.addRule(new IfRule(hasMoreThanOneLife, new  PlayProbablySafeCard(0.6)));
        pra.addRule(new TellAnyoneAboutUsefulCard());
        pra.addRule(new IfRule(informationLessThan4, new  TellDispensable()));
        pra.addRule(new OsawaDiscard());
        pra.addRule(new DiscardOldestFirst());
        pra.addRule(new TellRandomly());
        pra.addRule(new DiscardRandomly());

        return pra;
    }
    
//    public static  boolean hasLives(int lives, BasicState state ) {
//    		return state.getLives()>1;
//    }

}
