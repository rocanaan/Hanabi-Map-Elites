package Evolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.BiFunction;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
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
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import newrules.PlayJustHinted;
import newrules.TellAtLeastNUseless;
import newrules.TellHighProbabilityMistake;
import newrules.TellUnambiguous;
import newrules.TellUnambiguous2;
import newrules.TellUnambiguous3;

public class Rulebase {

    private static final BiFunction<Integer, GameState, Boolean> hasMoreThanOneLife = (i, state) -> {
        return state.getLives() > 1;
    };

    private static final BiFunction<Integer, GameState, Boolean> hailMary = (i, state) -> {
        return (state.getLives() > 1 && !state.getDeck().hasCardsLeft());
    };

    private static final BiFunction<Integer, GameState, Boolean> informationLessThan4 = (i, state) -> {
        return (state.getInfomation() < 4);
    };

    private Rule[] ruleset;
    
      
    
    private static  Rule[] completeRuleset = {
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
            new CompleteTellUsefulCard(), //rule no 10
            new TellAboutOnes(),
            new TellAnyoneAboutOldestUsefulCard(),
            new TellAnyoneAboutUsefulCard(),
            new TellAnyoneAboutUselessCard(),
            new TellDispensable(),
            new TellFinesse(),
            new TellFives(),
            new TellIllInformed(),
            new TellMostInformation(),
            new TellPlayableCard(),  //20
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
            new DiscardProbablyUselessCard(0.2), // Parametrized rule  //30
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
            new TryToUnBlock(), //40
            // Conditional rules
            new IfRule(hailMary, new PlayProbablySafeCard(0.0)), //hail mary
            new IfRule(hailMary, new PlayProbablySafeCard(0.1)), // slightly smarter hail mary

            new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.0)),
            new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.2)),
            new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.4)),
            new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.6)),
            new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.8)), // Used by Piers

            new IfRule(informationLessThan4, new TellDispensable()), //Used by Piers

            new PlayProbablySafeCard(0.25), // Used by Flawed

            // New, more complex rules
            new PlayJustHinted(), //50
            new PlayJustHinted(true, true, 2, 0),
            new PlayJustHinted(true, false, 2, 0),
            new PlayJustHinted(false, true, 2, 0),
            new PlayJustHinted(false, false, 2, 0),
            new PlayJustHinted(true, true, 2, 0.6),
            new PlayJustHinted(true, false, 2, 0.6),
            new PlayJustHinted(false, true, 2, 0.6),
            new PlayJustHinted(false, false, 2, 0.6),
            new PlayJustHinted(true, true, 2, 0.8),
            new PlayJustHinted(true, false, 2, 0.8), //60
            new PlayJustHinted(false, true, 2, 0.8),
            new PlayJustHinted(false, false, 2, 0.8),
            new PlayJustHinted(true, true, 2, 1),
            new PlayJustHinted(true, false, 2, 1),
            new PlayJustHinted(false, true, 2, 1),
            new PlayJustHinted(false, false, 2, 1),
            new TellUnambiguous(true),
            new TellUnambiguous(false),
            new TellUnambiguous2(1, 0),
            new TellUnambiguous2(10, -1), //70
            new TellUnambiguous2(2, -1),
            new TellUnambiguous3(1,0),
            new TellUnambiguous3(0,-1),
            new TellUnambiguous3(2,-1),
            new TellUnambiguous3(1,-2),
            new TellUnambiguous3(10,-1),
            new TellUnambiguous3(1,-10),
            new TellAtLeastNUseless(1),
            new TellAtLeastNUseless(2),
            new TellAtLeastNUseless(3),
            new TellAtLeastNUseless(4),
            new TellAtLeastNUseless(5),
            new TellHighProbabilityMistake(true, 0),
            new TellHighProbabilityMistake(true, 0.1),
            new TellHighProbabilityMistake(true, 0.2),
            new TellHighProbabilityMistake(true, 0.3),
            new TellHighProbabilityMistake(true, 0.4),
            new TellHighProbabilityMistake(true, 0.5),
            new TellHighProbabilityMistake(true, 0.6),
            new TellHighProbabilityMistake(true, 0.7),
            new TellHighProbabilityMistake(true, 0.8),
            new TellHighProbabilityMistake(true, 0.9),
            new TellHighProbabilityMistake(true, 1),
            new TellHighProbabilityMistake(false, 0),
            new TellHighProbabilityMistake(false, 0.1),
            new TellHighProbabilityMistake(false, 0.2),
            new TellHighProbabilityMistake(false, 0.3),
            new TellHighProbabilityMistake(false, 0.4),
            new TellHighProbabilityMistake(false, 0.5),
            new TellHighProbabilityMistake(false, 0.6),
            new TellHighProbabilityMistake(false, 0.7),
            new TellHighProbabilityMistake(false, 0.8),
            new TellHighProbabilityMistake(false, 0.9),
            new TellHighProbabilityMistake(false, 1),

            
     };
    
    private static Rule[] originalRuleset = {
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
            new IfRule(hailMary, new PlayProbablySafeCard(0.0)), //hail mary
            new IfRule(hailMary, new PlayProbablySafeCard(0.1)), // slightly smarter hail mary

            new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.0)),
            new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.2)),
            new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.4)),
            new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.6)),
            new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.8)), // Used by Piers

            new IfRule(informationLessThan4, new TellDispensable()), //Used by Piers

            new PlayProbablySafeCard(0.25), // Used by Flawed
                    
    };
    
    public Rulebase() {
    	this.ruleset = completeRuleset;
    }
    

    //TODO: reimplement this using the static variables or, more generally, defining a mask
    public Rulebase(boolean standardRulebase) {
        if (standardRulebase) {
            Rule[] ruleset = {
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
                new IfRule(hailMary, new PlayProbablySafeCard(0.0)), //hail mary
                new IfRule(hailMary, new PlayProbablySafeCard(0.1)), // slightly smarter hail mary

                new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.0)),
                new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.2)),
                new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.4)),
                new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.6)),
                new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.8)), // Used by Piers

                new IfRule(informationLessThan4, new TellDispensable()), //Used by Piers

                new PlayProbablySafeCard(0.25), // Used by Flawed
            };
            this.ruleset = ruleset;
        } else {
            Rule[] rulesset = {
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
                new CompleteTellUsefulCard(), //rule no 10
                new TellAboutOnes(),
                new TellAnyoneAboutOldestUsefulCard(),
                new TellAnyoneAboutUsefulCard(),
                new TellAnyoneAboutUselessCard(),
                new TellDispensable(),
                new TellFinesse(),
                new TellFives(),
                new TellIllInformed(),
                new TellMostInformation(),
                new TellPlayableCard(),  //20
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
                new DiscardProbablyUselessCard(0.2), // Parametrized rule  //30
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
                new TryToUnBlock(), //40
                // Conditional rules
                new IfRule(hailMary, new PlayProbablySafeCard(0.0)), //hail mary
                new IfRule(hailMary, new PlayProbablySafeCard(0.1)), // slightly smarter hail mary

                new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.0)),
                new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.2)),
                new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.4)),
                new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.6)),
                new IfRule(hasMoreThanOneLife, new PlayProbablySafeCard(0.8)), // Used by Piers

                new IfRule(informationLessThan4, new TellDispensable()), //Used by Piers

                new PlayProbablySafeCard(0.25), // Used by Flawed

                // New, more complex rules
                new PlayJustHinted(), //50
                new PlayJustHinted(true, true, 2, 0),
                new PlayJustHinted(true, false, 2, 0),
                new PlayJustHinted(false, true, 2, 0),
                new PlayJustHinted(false, false, 2, 0),
                new PlayJustHinted(true, true, 2, 0.6),
                new PlayJustHinted(true, false, 2, 0.6),
                new PlayJustHinted(false, true, 2, 0.6),
                new PlayJustHinted(false, false, 2, 0.6),
                new PlayJustHinted(true, true, 2, 0.8),
                new PlayJustHinted(true, false, 2, 0.8), //60
                new PlayJustHinted(false, true, 2, 0.8),
                new PlayJustHinted(false, false, 2, 0.8),
                new PlayJustHinted(true, true, 2, 1),
                new PlayJustHinted(true, false, 2, 1),
                new PlayJustHinted(false, true, 2, 1),
                new PlayJustHinted(false, false, 2, 1),
                new TellUnambiguous(true),
                new TellUnambiguous(false),
                new TellUnambiguous2(1, 0),
                new TellUnambiguous2(10, -1), //70
                new TellUnambiguous2(2, -1),
                new TellUnambiguous3(1,0),
                new TellUnambiguous3(0,-1),
                new TellUnambiguous3(2,-1),
                new TellUnambiguous3(1,-2),
                new TellUnambiguous3(10,-1),
                new TellUnambiguous3(1,-10),
                new TellAtLeastNUseless(1),
                new TellAtLeastNUseless(2),
                new TellAtLeastNUseless(3),
                new TellAtLeastNUseless(4),
                new TellAtLeastNUseless(5),
                new TellHighProbabilityMistake(true, 0),
                new TellHighProbabilityMistake(true, 0.1),
                new TellHighProbabilityMistake(true, 0.2),
                new TellHighProbabilityMistake(true, 0.3),
                new TellHighProbabilityMistake(true, 0.4),
                new TellHighProbabilityMistake(true, 0.5),
                new TellHighProbabilityMistake(true, 0.6),
                new TellHighProbabilityMistake(true, 0.7),
                new TellHighProbabilityMistake(true, 0.8),
                new TellHighProbabilityMistake(true, 0.9),
                new TellHighProbabilityMistake(true, 1),
                new TellHighProbabilityMistake(false, 0),
                new TellHighProbabilityMistake(false, 0.1),
                new TellHighProbabilityMistake(false, 0.2),
                new TellHighProbabilityMistake(false, 0.3),
                new TellHighProbabilityMistake(false, 0.4),
                new TellHighProbabilityMistake(false, 0.5),
                new TellHighProbabilityMistake(false, 0.6),
                new TellHighProbabilityMistake(false, 0.7),
                new TellHighProbabilityMistake(false, 0.8),
                new TellHighProbabilityMistake(false, 0.9),
                new TellHighProbabilityMistake(false, 1),

                
            };
            		
            this.ruleset = rulesset;
        };
    }

    public void printRulesFromChromossome(int[] chromossome) {
        for (int i = 0; i < chromossome.length; i++) {
            String[] str = (ruleMapping(chromossome[i]).toString()).split("\\.");
            if (str[str.length - 1].length() < 8) {
                System.out.println(str[str.length - 2] +"."+ str[str.length - 1]);
            } else {
                System.out.println(str[str.length - 1]);
            }
        }
    }

    public Rule[] getRuleset() {
        return Arrays.copyOf(ruleset, ruleset.length);
    }

    public Rule ruleMapping(int index) {
        return ruleset[index];
    }

    public HistogramAgent makeAgent(String name, Rule[] agentRules) {
        ProductionRuleAgent pra = new ProductionRuleAgent();

        for (Rule r : agentRules) {
            pra.addRule(r);
        }
        HistogramAgent agent = new HistogramAgent(pra);
        //AgentPlayer agent = new AgentPlayer(name, hAgent);

        return agent;

    }
    
	public HistogramAgent makeAgent(int[] chromosome) {
		// TODO Auto-generated method stub
		Rule[] rules = new Rule[chromosome.length];
		for (int i = 0; i<chromosome.length; ++i) {
			rules[i] = ruleMapping(chromosome[i]);
		}
		 
		return makeAgent("Agent", rules);
	}

    public HistogramAgent makeAgent(Rule[] agentRules) {
        ProductionRuleAgent pra = new ProductionRuleAgent();

        for (Rule r : agentRules) {
            pra.addRule(r);
        }
        HistogramAgent agent = new HistogramAgent(pra);
        //AgentPlayer agent = new AgentPlayer("makeAgent", hAgent);

        return agent;

    }

    public int getChromossomeLength() {
        return ruleset.length;
    }

    public ArrayList<int[]> getBaselineAgentChromossomes() {
        ArrayList<int[]> baselinePrefixes = BaselinePrefixPool.getBaselinePrefixes();

        ArrayList<int[]> baselineChromossomes = new ArrayList<int[]>();

        for (int[] prefix : baselinePrefixes) {
            baselineChromossomes.add(randomFillPrefix(prefix));
        }

        return baselineChromossomes;
    }

    public ArrayList<int[]> getGoodAgentChromossomes() {
        ArrayList<int[]> baselinePrefixes = BaselinePrefixPool.getGoodPrefixes();

        ArrayList<int[]> baselineChromossomes = new ArrayList<int[]>();

        for (int[] prefix : baselinePrefixes) {
            baselineChromossomes.add(randomFillPrefix(prefix));
        }

        return baselineChromossomes;
    }

    public int[] getRandomChromossome() {
        return getRandomChromossome(getChromossomeLength());
    }

    public int[] getRandomChromossome(int length) {
        List<Integer> rules = new ArrayList<Integer>();

        for (int i = 0; i < getChromossomeLength(); i++) {
            rules.add(i);
        }
        Collections.shuffle(rules);
        int[] chromossome = new int[length];
        for (int i = 0; i < length; i++) {
            chromossome[i] = rules.get(i);
        }
        return chromossome;
    }

    private int[] randomFillPrefix(int[] prefix) {
        return randomFillPrefix(prefix, getChromossomeLength());
    }

    private static int[] randomFillPrefix(int[] prefix, int chromossomeLength) {
        List<Integer> rules = new ArrayList<Integer>();

        //create a list from 0 to chromossomeLength-1
        for (int i = 0; i < chromossomeLength; i++) {
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
                if (i == j) {
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
//        ArrayList<int[]> baseline = getBaselineAgentChromossomes();
//        for (int[] chromossome : baseline) {
//            System.out.printf("[");
//            for (int i : chromossome) {
//                System.out.printf(i + ",");
//            }
//            System.out.println("]");
//        }
//        System.out.println("good");
//        ArrayList<int[]> good = getGoodAgentChromossomes();
//        for (int[] chromossome : good) {
//            System.out.printf("[");
//            for (int i : chromossome) {
//                System.out.printf(i + ",");
//            }
//            System.out.println("]");
//        }
    }

    private static class BaselinePrefixPool {

        //baselineChromossomes
        private static int[] cautiousChromossome = {2, 8, 13, 38, 34};
        private static int[] flawedChromossome = {8, 49, 22, 38, 27, 34};
        private static int[] iggiChromossome = {2, 8, 13, 38, 27};
        private static int[] internalChromossome = {8, 38, 20, 22, 34};
        private static int[] outerChromossome = {8, 38, 21, 22, 34};
        private static int[] piersChromossome = {41, 8, 46, 13, 48, 38, 27, 22, 34};
        private static int[] vandeberghChromossome = {46, 37, 13, 14, 19, 29}; //Note: actual Van de Bergh uses DiscardProbablyUselessCard(1.0) -not on rulebase- rather than DiscardUselessCard -27

        //evolved or modified agents
        private static int[] firstEvolved = {8, 13, 28, 6, 33, 35, 20, 38, 37, 23, 30, 26, 9, 0, 14, 17, 10, 21, 25, 4, 15, 16, 11, 31, 3, 5, 29, 36, 7, 27, 34, 12, 2, 39, 19, 40, 18, 24, 1, 32, 22};
        private static int[] variation1 = {8, 13, 38, 28, 6, 33, 35, 20, 37, 23, 30, 26, 9, 0, 14, 17, 10, 21, 25, 4, 15, 16, 11, 31, 3, 5, 29, 36, 7, 27, 34, 12, 2, 39, 19, 40, 18, 24, 1, 32, 22};
        private static int[] variation2 = {42, 8, 13, 38, 28, 6, 33, 35, 20, 37, 23, 30, 26, 9, 0, 14, 17, 10, 21, 25, 4, 15, 16, 11, 31, 3, 5, 29, 36, 7, 27, 34, 12, 2, 39, 19, 40, 18, 24, 1, 32, 22};

        public static ArrayList<int[]> getBaselinePrefixes() {
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

        public static ArrayList<int[]> getGoodPrefixes() {
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

    public static Vector<AgentPlayer> GetBaselineAgentPlayers() {
        String[] testPoolNames = {"RuleBasedIGGI", "RuleBasedInternal", "RuleBasedOuter", "SampleLegalRandom", "RuleBasedVanDeBergh", "RuleBasedFlawed", "RuleBasedPiers"};

        Vector<AgentPlayer> testPool = new Vector<AgentPlayer>();

        for (String other : testPoolNames) {
            AgentPlayer otherAgent = new AgentPlayer(other, AgentUtils.buildAgent(other));
            testPool.add(otherAgent);
        }

        return testPool;
    }



}
