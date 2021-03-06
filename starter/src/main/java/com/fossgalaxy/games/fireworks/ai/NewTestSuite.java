package com.fossgalaxy.games.fireworks.ai;

import Evolution.Rulebase;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

//import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewTestSuite {


    public static Vector<Double> ConstantNumberPlayersTest(int numPlayers, int numGames, AgentPlayer yourAgent, AgentPlayer otherAgent, Random random) {
       
    	
    	boolean threaded =false;
        boolean randomizePosition = true;
        
        if (threaded) {
        	//your code test:runevolutionmirror get time to finish
        }
        else {
        	//current code
        }
        Vector<Double> results = new Vector<Double>();
//ask cores available or get as a parameter 
//new constructor for multi thread
        for (int i = 0; i < numGames; i++) {
        		//System.out.println("Running game with number of players " +numPlayers);
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

//            // Adds your agent to the game
//            Player player = new AgentPlayer(yourAgentName, AgentUtils.buildAgent(yourAgentName));
//
//            //add N-1 other agents to the game
//            for (int j=0; j<numPlayers; j++) {
//                // the player class keeps track of our state for us...
//            		if (otherAgentName != "") {
//            			player = new AgentPlayer(yourAgentName, AgentUtils.buildAgent(otherAgentName));
//            		}
//                runner.addPlayer(player);
//            }
            GameStats stats = runner.playGame(random.nextLong());
            results.add((double) stats.score);
            // System.out.println("Game number " + i + " complete");
        }

        return results;
    }


    public static Map<Integer, Vector<Double>> VariableNumberPlayersTest(AgentPlayer yourAgent, AgentPlayer otherAgent, int minNumPlayers, int maxNumPlayers, int numGames, Random random) {
        Map<Integer, Vector<Double>> resultsByGameSize = new HashMap<Integer, Vector<Double>>();

        for (int i = minNumPlayers; i <= maxNumPlayers; i++) {
            resultsByGameSize.put(i,ConstantNumberPlayersTest(i, numGames, yourAgent, otherAgent, random) );
        }
        
        return resultsByGameSize;
    }

    	// Returns a vector where the index is the number of the agent in the population, the key of the map is the number of players in the game, and a list of games is the value
    public static Vector<Map<Integer, Vector<Double>>> mirrorPopulationEvaluation(Vector<AgentPlayer> population, int minNumPlayers, int maxNumPlayers, int numGames) {
        
//        Vector<Map<Integer, Vector<Double>>> populationResults  = new Vector <Map<Integer, Vector<Double>>>();
//
//        Random random = new Random();
//        Long seed = random.nextLong();
//
//        for (AgentPlayer agent : population) {
//            random.setSeed(seed);
//            populationResults.add(VariableNumberPlayersTest(agent, agent, minNumPlayers, maxNumPlayers, numGames, random)); //TODO: Maybe this should be a copy of agent
//
//        }
//
//        return populationResults;
        return mirrorPopulationEvaluation(population, minNumPlayers, maxNumPlayers, numGames, true); 

    }
    
   public static Vector<Map<Integer, Vector<Double>>> mirrorPopulationEvaluation(Vector<AgentPlayer> population, int minNumPlayers, int maxNumPlayers, int numGames, boolean useSameSeed) {
        
        Vector<Map<Integer, Vector<Double>>> populationResults  = new Vector <Map<Integer, Vector<Double>>>();

        Random random = new Random();
        Long seed = random.nextLong();

        for (AgentPlayer agent : population) {
            random.setSeed(seed);
            populationResults.add(VariableNumberPlayersTest(agent, agent, minNumPlayers, maxNumPlayers, numGames, random)); //TODO: Maybe this should be a copy of agent
            
            if (!useSameSeed) {
            		random = new Random();
            		seed = random.nextLong();
            }

        }

        return populationResults;

    }
    
    public static Vector<Map<Integer, Vector<Double>>> crossPopulationEvaluation(Vector<AgentPlayer> population1, Vector<AgentPlayer> population2, int minNumPlayers, int maxNumPlayers, int numGames){
    		Vector<Map<Integer, Vector<Double>>> populationResults  = new Vector <Map<Integer, Vector<Double>>>();
    		
    		Random random = new Random();
        Long seed = random.nextLong();
        
        for (int i=0; i<population1.size(); i++) {
        		AgentPlayer agent1 = population1.get(i);
        		AgentPlayer agent2 = population1.get(i);
        		
        		 random.setSeed(seed);
             populationResults.add(VariableNumberPlayersTest(agent1, agent2, minNumPlayers, maxNumPlayers, numGames, random));

        		
        }

    		
    		return populationResults;

    }
    
    
    // TODO: This should be a map of (integer, integer) because j should be from i to population size
    public static Vector<Map<Integer, Vector<Double>>> intraPopulationEvaluation(Vector<AgentPlayer> population, int minNumPlayers, int maxNumPlayers, int numGames){
		Vector<Map<Integer, Vector<Double>>> populationResults  = new Vector <Map<Integer, Vector<Double>>>();
		
		Random random = new Random();
		Long seed = random.nextLong();
    
		for (int i=0; i<population.size(); i++) {
			for (int j=0; j<population.size(); j++) {
			System.out.println("["+i+","+j+"]");
    			AgentPlayer agent1 = population.get(i);
    			AgentPlayer agent2 = population.get(j);
    		
    			random.setSeed(seed);
    			populationResults.add(VariableNumberPlayersTest(agent1, agent2, minNumPlayers, maxNumPlayers, numGames, random));
			}
		}
    		return populationResults;
    			

    		
    }
    
//    public static Vector<Map<Integer, Vector<Double>>> intraPopulationEvaluationThreaded(Vector<AgentPlayer> population, int minNumPlayers, int maxNumPlayers, int numGames){
//		Vector<Map<Integer, Vector<Double>>> populationResults  = new Vector <Map<Integer, Vector<Double>>>();
//		
//		int numWorkers = 4;
//		
//		Random random = new Random();
//		Long seed = random.nextLong();	
//		
//		RunnableWorker[] workers = new RunnableWorker[numWorkers];
//		
//		int index=0;
//		while (index < population.size()*population.size()) {
//			for (int w =0; w<numWorkers; w++) {
//				RunnableWorker worker = workers[w];
//				if (worker==null) {
//					int i = index/population.size();
//					int j = index%population.size();
//					AgentPlayer agent1 = population.get(i);
//	    				AgentPlayer agent2 = population.get(j);
//					worker = new RunnableWorker(i+","+j, agent1, agent2, i, j, minNumPlayers, maxNumPlayers, numGames, random);
//					Future<Map<Integer,Vector<Double>>> result = worker.call();
//					index++;
//					random.setSeed(seed);
//
//
//				}
//				else if (worker.done) {
//					System.out.println(worker.threadName + " has finished running");
//					populationResults.add(worker.results);
//					worker.terminate = true;
//					int i = index/population.size();
//					int j = index%population.size();
//					System.out.println("["+i+","+j+"]");
//					AgentPlayer agent1 = population.get(i);
//	    				AgentPlayer agent2 = population.get(j);
//					worker = new RunnableWorker(i+","+j, agent1, agent2, i, j, minNumPlayers, maxNumPlayers, numGames, random);
//					worker.start();
//					index++;
//					random.setSeed(seed);
//
//				}
//			}
////			try {
////				Thread.sleep(50);
////			} catch (InterruptedException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//		}
//    
//
//    		return populationResults;
//    		
//    }
//    
//    public static class RunnableWorker implements Callable<Map<Integer, Vector<Double>>>{
//    		private Thread t;
//		private String threadName;
//		AgentPlayer agent1;
//		AgentPlayer agent2;
//		int i;
//		int j;
//		int minNumPlayers;
//		int maxNumPlayers;
//		int numGames;
//		Random random;
//		Map<Integer, Vector<Double>> results;
//		boolean done;
//		boolean terminate;
//		
//		public RunnableWorker(String threadName, AgentPlayer agent1, AgentPlayer agent2, int i, int j, int minNumPlayers,
//				int maxNumPlayers, int numGames, Random random) {
//			super();
//			this.threadName = threadName;
//			this.agent1 = agent1;
//			this.agent2 = agent2;
//			this.minNumPlayers = minNumPlayers;
//			this.maxNumPlayers = maxNumPlayers;
//			this.i = i;
//			this.j = j;
//			this.numGames = numGames;
//			this.random = random;
//			done = false;
//			terminate = false;
//		    System.out.println("Creating thread " +  threadName );
//
//		}
//
////		@Override
////		public void run() {
////			// TODO Auto-generated method stub
////
////		
////		public void start() {
////			if (t==null) {
////				t=new Thread (this,threadName);
////				t.start();
////			}
////		}
////		
////		public Map<Integer, Vector<Double>> getResults(){
////			return results;
////		}
//
//		@Override
//		public Map<Integer, Vector<Double>> call() throws Exception {
//		    System.out.println("Running " +  threadName );
//
//			results = VariableNumberPlayersTest(agent1, agent2, minNumPlayers, maxNumPlayers, numGames, random);
//		    System.out.println("Got results for " +  threadName + (results == null));
//		    return (results);
//	
//		}
//    }

		

    	// First index is the number of the player in the population, second is the number of the partner in the test pool
    public static Vector<Vector <Map<Integer, Vector<Double>>>> mixedPopulationEvaluation(Vector<AgentPlayer> population, Vector<AgentPlayer> testPool, int minNumPlayers, int maxNumPlayers, int numGames) {
        Vector<Vector<Map<Integer, Vector<Double>>>> populationResults  = new Vector<Vector<Map<Integer, Vector<Double>>>>();

        Random random = new Random();
        Long seed = random.nextLong();
        for (AgentPlayer agent : population) {
            random.setSeed(seed);
            Vector<Map<Integer, Vector<Double>>> playerResults = new Vector<Map<Integer, Vector<Double>>> ();

            for (AgentPlayer other : testPool) {
            		playerResults.add (VariableNumberPlayersTest(agent, other, minNumPlayers, maxNumPlayers, numGames, random));
            }
            populationResults.add(playerResults);
        }

        return populationResults;

    }
    
    

    // fileName is the path to the file, rulebaseStandard determines if the standard rulebase or the extended rulebase is being used
    public static ArrayList<HistogramAgent> makeAgentsFromFile(String fileName, boolean rulebaseStandard) {
        Rulebase rb = new Rulebase(rulebaseStandard);
        String thisLine;
        ArrayList<HistogramAgent> agents = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((thisLine = br.readLine()) != null) {
                thisLine = thisLine.substring(1, thisLine.length() - 2);
                String[] c = thisLine.split(",");
                int[] chromossome = new int[c.length];
                for (int i = 0; i < c.length; i++) {
                    chromossome[i] = Integer.parseInt(c[i]);
                }
                Rule[] rules1 = new Rule[chromossome.length];
                for (int i = 0; i < chromossome.length; i++) {
                    if (rulebaseStandard) {
                        rules1[i] = rb.ruleMapping(chromossome[i]);
                    } else {
                        rules1[i] = rb.ruleMapping(chromossome[i]);
                    }
                }
                if (rulebaseStandard) {
                    agents.add(rb.makeAgent(rules1));
                } else {
                    agents.add(rb.makeAgent(rules1));
                }
            }
        } catch (Exception e) {
        		System.err.println(e);;
        }
        return agents;
    }

}
