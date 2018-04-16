package Evolution;

import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

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
import com.fossgalaxy.games.fireworks.ai.rule.wrapper.IfRule;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.ai.TestSuite;

public class Rulebase {
	
	private static final BiFunction<Integer, GameState, Boolean> hasMoreThanOneLife =  (i,state) ->{
		return state.getLives()>1;
	};

	private static final BiFunction<Integer, GameState, Boolean> hailMary =  (i,state) ->{
		return (state.getLives()>1 && !state.getDeck().hasCardsLeft());
	};

	private static final BiFunction<Integer, GameState, Boolean> informationLessThan4 =  (i,state) ->{
		return (state.getInfomation() < 4);
	};

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
			new TryToUnBlock(),
			
			
			// Conditional rules
			new IfRule(hailMary, new  PlayProbablySafeCard(0.0)), //hail mary
			new IfRule(hailMary, new  PlayProbablySafeCard(0.1)), // slightly smarter hail mary
			
			new IfRule(hasMoreThanOneLife, new  PlayProbablySafeCard(0.0)),
			new IfRule(hasMoreThanOneLife, new  PlayProbablySafeCard(0.2)),
			new IfRule(hasMoreThanOneLife, new  PlayProbablySafeCard(0.4)),
			new IfRule(hasMoreThanOneLife, new  PlayProbablySafeCard(0.6)),
			new IfRule(hasMoreThanOneLife, new  PlayProbablySafeCard(0.8)), // Used by Piers
			
			new IfRule(informationLessThan4, new TellDispensable()), //Used by Piers
			
			new PlayProbablySafeCard(0.25) // Used by Flawed
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
	
	public static int getChromossomeLength ()
	{
		return ruleset.length;
	}
	
	public static ArrayList<int[]> getBaselineAgentChromossomes(){
		ArrayList<int[]> baselinePrefixes = BaselinePrefixPool.getBaselinePrefixes();
		
		ArrayList<int[]> baselineChromossomes = new ArrayList<int[]>();
		
		for (int[] prefix : baselinePrefixes) {
			baselineChromossomes.add(randomFillPrefix(prefix));
		}
		
		return baselineChromossomes;
	}
	
	public static ArrayList<int[]> getGoodAgentChromossomes(){
		ArrayList<int[]> baselinePrefixes = BaselinePrefixPool.getGoodPrefixes();
		
		ArrayList<int[]> baselineChromossomes = new ArrayList<int[]>();
		
		for (int[] prefix : baselinePrefixes) {
			baselineChromossomes.add(randomFillPrefix(prefix));
		}
		
		return baselineChromossomes;
	}
	
	private static int[] randomFillPrefix(int[] prefix) {
		List<Integer> rules = new ArrayList<Integer>();
		
		//create a list from 0 to chromossomeLength-1
		int chromossomeLength = getChromossomeLength();
		for (int i = 0; i< chromossomeLength; i++) {
			rules.add(i);
		}
		
		Collections.shuffle(rules);
		
		int chromossome[] = new int[chromossomeLength];
		
		int currentIndex = 0;
		
		for (int j : prefix) {
			chromossome[currentIndex] = j;
			currentIndex++;
		}

		
		for (Integer i : rules) {
			boolean duplicate = false;
			for (int j : prefix) {
				if (i == j ) {
					duplicate = true;
					break;
				}
			}
			if (!duplicate) {
				chromossome[currentIndex] = i;
				currentIndex++;
			}
		}
		
		
		return chromossome;
		
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
	public static void main(String[] args) {
		ArrayList<int[]> baseline = getBaselineAgentChromossomes();
		for (int[] chromossome : baseline) {
			System.out.printf("[");
			for (int i : chromossome) {
				System.out.printf(i+",");
			}
			System.out.println("]");
		}
		System.out.println("good");
		ArrayList<int[]> good = getGoodAgentChromossomes();
		for (int[] chromossome : good) {
			System.out.printf("[");
			for (int i : chromossome) {
				System.out.printf(i+",");
			}
			System.out.println("]");
		}
	}
	
	private static class BaselinePrefixPool{
		
		
		//baselineChromossomes
		private static int[] cautiousChromossome = {2,8,13,38,34};
		private static int[] flawedChromossome = {8,49,22,38,27,34};
		private static int[] iggiChromossome = {2,8,13,38,27};
		private static int[] internalChromossome = {8,38,20,22,34};
		private static int[] outerChromossome = {8,38,21,22,34};
		private static int[] piersChromossome = {41,8,46,13,48,38,27,22,34};
		private static int[] vandeberghChromossome = {46,37,13,14,19,29}; //Note: actual Van de Bergh uses DiscardProbablyUselessCard(1.0) -not on rulebase- rather than DiscardUselessCard -27
		
		//evolved or modified agents
		private static int[] firstEvolved = {8,13,28,6,33,35,20,38,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
		private static int[] variation1 = {8,13,38,28,6,33,35,20,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
		private static int[] variation2 = {42,8,13,38,28,6,33,35,20,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
		

		public static ArrayList<int[]> getBaselinePrefixes(){
			ArrayList<int[]> baselineChromossomes = new ArrayList<int[]>();
			
			baselineChromossomes.add(cautiousChromossome);
			baselineChromossomes.add(flawedChromossome);
			baselineChromossomes.add(iggiChromossome);
			baselineChromossomes.add(internalChromossome);
			baselineChromossomes.add(outerChromossome);
			baselineChromossomes.add(piersChromossome);
			baselineChromossomes.add(vandeberghChromossome);
			
			return baselineChromossomes;
		}
		
		public static ArrayList<int[]> getGoodPrefixes(){
			ArrayList<int[]> baselineChromossomes = new ArrayList<int[]>();
			
			baselineChromossomes.add(cautiousChromossome);
			baselineChromossomes.add(iggiChromossome);
			baselineChromossomes.add(internalChromossome);
			baselineChromossomes.add(outerChromossome);
			baselineChromossomes.add(piersChromossome);
			baselineChromossomes.add(vandeberghChromossome);
			baselineChromossomes.add(firstEvolved);
			baselineChromossomes.add(variation1);
			baselineChromossomes.add(variation2);
			
			return baselineChromossomes;
		}


	}
	
	
	

}
