package com.fossgalaxy.games.fireworks.ai;

import java.util.Random;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.stats.StatsSummary;

public class RunnableGame implements Runnable {
	


	RunnableGame(int numPlayers, int numGames, AgentPlayer yourAgent, AgentPlayer otherAgent, Random random, boolean randomizePosition){
		this.numPlayers = numPlayers;
		this.numGames = numGames;
		this.yourAgent = yourAgent;
		this.otherAgent = otherAgent;
		this.random = random;
		this.randomizePosition = randomizePosition;
		threadName = "Thread";
	}

	public Thread t;
	public String threadName;
	
	public int numPlayers, numGames;
	public AgentPlayer yourAgent;
	public AgentPlayer otherAgent;
	public int score;
	public GameRunner runner;
	public Random random;
	public boolean randomizePosition;
	
	public StatsSummary statsSummary;

	
	private
	
	@Override
	public void run() {
		GameRunner runner = new GameRunner("test-game", numPlayers);

        int playerPosition = 0;
        if (randomizePosition) {
            playerPosition = random.nextInt(numPlayers);
        }

        for (int pos = 0; pos < numPlayers; pos++) {
            if (pos == playerPosition) {
                // Adds your agent to the game
                runner.addPlayer(yourAgent);
            } else {
                // Adds the other 
                runner.addPlayer(new AgentPlayer(otherAgent.getName(), otherAgent.policy));
            }
        }
        // Adds your agent to the game

//        // Adds your agent to the game
//        Player player = new AgentPlayer(yourAgentName, AgentUtils.buildAgent(yourAgentName));
//
//        //add N-1 other agents to the game
//        for (int j=0; j<numPlayers; j++) {
//            // the player class keeps track of our state for us...
//        		if (otherAgentName != "") {
//        			player = new AgentPlayer(yourAgentName, AgentUtils.buildAgent(otherAgentName));
//        		}
//            runner.addPlayer(player);
//        }
        GameStats stats = runner.playGame(random.nextLong());
        statsSummary.add(stats.score);
        // System.out.println("Game number " + i + " complete");
    }

		
	
	public StatsSummary start() {
		if (t==null) {
			t = new Thread(this, threadName);
			t.start();
		}
		return statsSummary;
	}

}
