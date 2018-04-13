package Evolution;

import java.util.Arrays;

import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.PairingSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.iggi.LegalRandom;
import com.fossgalaxy.games.fireworks.ai.osawa.rules.OsawaDiscard;
import com.fossgalaxy.games.fireworks.ai.osawa.rules.TellPlayableCardOuter;
import com.fossgalaxy.games.fireworks.ai.rule.CompleteTellUsefulCard;
import com.fossgalaxy.games.fireworks.ai.rule.DiscardHighest;
import com.fossgalaxy.games.fireworks.ai.rule.DiscardOldestFirst;
import com.fossgalaxy.games.fireworks.ai.rule.DiscardOldestNoInfoFirst;
import com.fossgalaxy.games.fireworks.ai.rule.DiscardSafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.DiscardUnidentifiedCard;
import com.fossgalaxy.games.fireworks.ai.rule.DiscardUselessCard;
import com.fossgalaxy.games.fireworks.ai.rule.PlaySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.PlayUniquePossibleCard;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.ai.rule.TellAboutOnes;
import com.fossgalaxy.games.fireworks.ai.rule.TellAnyoneAboutOldestUsefulCard;
import com.fossgalaxy.games.fireworks.ai.rule.TellAnyoneAboutUsefulCard;
import com.fossgalaxy.games.fireworks.ai.rule.TellAnyoneAboutUselessCard;
import com.fossgalaxy.games.fireworks.ai.rule.TellDispensable;
import com.fossgalaxy.games.fireworks.ai.rule.TellFives;
import com.fossgalaxy.games.fireworks.ai.rule.TellIllInformed;
import com.fossgalaxy.games.fireworks.ai.rule.TellMostInformation;
import com.fossgalaxy.games.fireworks.ai.rule.TellUnknown;
import com.fossgalaxy.games.fireworks.ai.rule.TryToUnBlock;
import com.fossgalaxy.games.fireworks.ai.rule.finesse.PlayFinesse;
import com.fossgalaxy.games.fireworks.ai.rule.finesse.PlayFinesseTold;
import com.fossgalaxy.games.fireworks.ai.rule.finesse.TellFinesse;
import com.fossgalaxy.games.fireworks.ai.rule.random.DiscardLeastLikelyToBeNecessary;
import com.fossgalaxy.games.fireworks.ai.rule.random.DiscardProbablyUselessCard;
import com.fossgalaxy.games.fireworks.ai.rule.random.DiscardRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.random.PlayProbablySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.random.TellPlayableCard;
import com.fossgalaxy.games.fireworks.ai.rule.random.TellRandomly;
import com.fossgalaxy.games.fireworks.ai.rule.simple.DiscardIfCertain;
import com.fossgalaxy.games.fireworks.ai.rule.simple.PlayIfCertain;

import com.fossgalaxy.games.fireworks.ai.TestSuite;

public class Rulebase {

	private static Rule[] ruleset = {
			// Play rules
			new PlayFinesse(),
			new PlayFinesseTold(),
			new PlayIfCertain(), 
			new PlayProbablySafeCard(0.0),
			new PlayProbablySafeCard(0.2),
			new PlayProbablySafeCard(0.4),
			new PlayProbablySafeCard(0.6),
			new PlayProbablySafeCard(0.8),
			//new PlayProbablySafeCard(1.0),
			new PlaySafeCard(), 
			new PlayUniquePossibleCard(),

			
			// Tell rules
			new CompleteTellUsefulCard(),
			new TellAboutOnes(),
			new TellAnyoneAboutOldestUsefulCard(),
			new TellAnyoneAboutUsefulCard(),
			new TellAnyoneAboutUselessCard(),
			new TellDispensable(),
			new TellFinesse(),
			new TellFives(),
			new TellIllInformed(),
			new TellMostInformation(),
			new TellPlayableCard(),
			new TellPlayableCardOuter(),
			new TellRandomly(), // Random rule
			new TellUnknown(),
			
			// Discard rules
			new DiscardHighest(),
			new DiscardIfCertain(),
			new DiscardLeastLikelyToBeNecessary(),
			new DiscardOldestFirst(),
			new DiscardOldestNoInfoFirst(),
			new DiscardProbablyUselessCard(0.0), // Parametrized rule
			new DiscardProbablyUselessCard(0.2), // Parametrized rule
			new DiscardProbablyUselessCard(0.4), // Parametrized rule
			new DiscardProbablyUselessCard(0.6), // Parametrized rule
			new DiscardProbablyUselessCard(0.8), // Parametrized rule
			//new DiscardProbablyUselessCard(1.0), // Parametrized rule
			new DiscardRandomly(),
			new DiscardSafeCard(),
			new DiscardUnidentifiedCard(),
			new DiscardUselessCard(),
			new OsawaDiscard(),
			
			// Other rules
			new LegalRandom(), // Bad rule
			new TryToUnBlock()
			
	};

	
	public static Rule[] getRuleset() {
		return Arrays.copyOf(ruleset, ruleset.length);
	}
	
	public static Rule ruleMapping(int index) {
		return ruleset[index];
	}
	
	public static AgentPlayer makeAgent (String name, Rule[] agentRules) {
		ProductionRuleAgent pra = new ProductionRuleAgent();
		
		for (Rule r: agentRules) {
			pra.addRule(r);
		}
		
		AgentPlayer agent = new AgentPlayer(name, pra);
		
		return agent;
		
	}
	
	public static AgentPlayer makeAgent ( Rule[] agentRules) {
		ProductionRuleAgent pra = new ProductionRuleAgent();
		
		for (Rule r: agentRules) {
			pra.addRule(r);
		}
		
		AgentPlayer agent = new AgentPlayer("makeAgent", pra);
		
		return agent;
		
	}
	
//	public static void main( String[] args ) {
//		 ProductionRuleAgent pra = new ProductionRuleAgent();
//		 
//		 Rule[] rules = getRuleset();
//
//		 AgentPlayer agent = makeAgent("test", rules);
//		 
//		 PairingSummary stats = TestSuite.VariableNumberPlayersTest( agent,  agent,  5,  100);
//
//		 System.out.println(stats);
//	}

}
