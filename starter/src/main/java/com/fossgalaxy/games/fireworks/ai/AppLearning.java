package com.fossgalaxy.games.fireworks.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

public class AppLearning {

	/**
	 * The pool of other players the player can play against.
	 * 
	 * These are not actually the ones used for the competition, they are here for testing :).
	 * 
	 * @return all of the possible players the player can play against.
	 */
	public static Player[] buildPool() {
		
		// the player pool size is not revealed to the agents.
		Player[] pool = new Player[] {
				new AgentPlayer("A", AgentUtils.buildAgent("iggi")),
				new AgentPlayer("B", AgentUtils.buildAgent("piers")),
				new AgentPlayer("C", AgentUtils.buildAgent("internal")),
				new AgentPlayer("D", AgentUtils.buildAgent("outer")),
				
				// weak players
				new AgentPlayer("E", AgentUtils.buildAgent("legal_random")),
				new AgentPlayer("F", AgentUtils.buildAgent("flawed")),
				
				// there may be multiple agents using the same technique
				new AgentPlayer("G", AgentUtils.buildAgent("iggi")),
				new AgentPlayer("H", AgentUtils.buildAgent("piers")),
				
				new AgentPlayer("I", AgentUtils.buildAgent("mctsND"))
		};
		
		return pool;
	}

	
    public static void main( String[] args )
    {
    	// the parameters for the test
        int numPlayers = 5;
        int numGames = 100;
        String agentName = "Rodrigocanaan";
        Player[] pool = buildPool();

        Random random = new Random();
        StatsSummary statsSummary = new BasicStats();
        
        //build players (persistent across games)
        Player you = new AgentPlayer("you", AgentUtils.buildAgent(agentName));

        // run the test games
        for (int i=0; i<numGames; i++) {
            GameRunner runner = new GameRunner("test-game", numPlayers);
            
            // add the players to the game
            List<Player> others = getRandomPlayers(pool, numPlayers - 1, random);
            addPlayers(runner, others, you, random.nextInt(numPlayers));
            
            //play the game
            long deckOrderingSeed = random.nextLong();
            GameStats stats = runner.playGame(deckOrderingSeed);
            statsSummary.add(stats.score);
        }

        //print out the stats
        System.out.println(String.format("Our agent: Avg: %f, min: %f, max: %f",
                statsSummary.getMean(),
                statsSummary.getMin(),
                statsSummary.getMax()));
    }
    
    // UTILITY METHODS PAST THIS POINT
    
    /**
     * Get numPlayers players from the pool at random.
     * 
     * @param pool all players the player could play against
     * @param numPlayers the number of players to select
     * @return a randomly ordered list containing n players
     */
	public static List<Player> getRandomPlayers(Player[] pool, int numPlayers, Random random) {

		List<Player> order = new ArrayList<>();
		for (int i=0; i<pool.length; i++) {
			order.add(pool[i]);
		}
		Collections.shuffle(order, random);
		
		return order.subList(0, numPlayers);
	}
    
	/**
	 * Add the players to the game
	 * 
	 * @param runner the game runner for the game
	 * @param others the other players in the game
	 * @param you your agent
	 * @param yourPos the position your agent should be in
	 */
	public static void addPlayers(GameRunner runner, List<Player> others, Player you, int yourPos) {
		
        // sort out player assignments
		int numPlayers = others.size() + 1;
        int lastOther = 0;
        
        for (int seat=0; seat<numPlayers; seat++) {
        	
        	// if you are in this position, add you, else one of the other players
        	if (seat == yourPos) {
        		runner.addNamedPlayer("you", you);
        	} else {
        		Player other = others.get(lastOther++);
        		runner.addNamedPlayer(other.getName(), other);
        	}
        	
        }
	}
	
}
