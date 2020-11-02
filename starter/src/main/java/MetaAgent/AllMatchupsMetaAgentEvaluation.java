package MetaAgent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

public class AllMatchupsMetaAgentEvaluation {

	/**
	 * This class allows for the evaluation of the meta agent in a variety of scenarios:
	 * Adaptive: agent attempts to adapt after a certain number of interactions TODO set threshold as parameter
	 * Generalist: agent always plays the generalist strategy according TODO: set threshold as parameter or create generatlist flag
	 * Forced Match-up: higher level controller forces agent to play a particular strategy
	 * Oracle: higher level controller forces agent to play as if facing a particular strategy
	 * Misleading Oracle: like Oracle, but controler passes misleading identity of partner
	 * All matchups: forcing all numAgents*numAgents matchups 
	 */
	

	/** TODO 
	 * Consolidate chromosome and matchup selection?
	 * @return
	 */
	
	public static Player[] buildPool() {
		
        final ArrayList<Agent>  agents = AgentLoaderFromFile.makeAgentsFromFile("5by5", 20, 20, false);
        Player[] pool = new Player[agents.size()];
        int i = 0;
        for (Agent a:agents) {
            pool[i] = new AgentPlayer(String.valueOf(i), a);
            i+=1;
        }
        
		// the player pool size is not revealed to the agents.
        /*
		Player[] pool = new Player[] {
				new AgentPlayer("0", AgentUtils.buildAgent("iggi")),
				new AgentPlayer("1", AgentUtils.buildAgent("piers")),
				new AgentPlayer("2", AgentUtils.buildAgent("internal")),
				new AgentPlayer("3", AgentUtils.buildAgent("outer")),
				
				// weak players
				new AgentPlayer("4", AgentUtils.buildAgent("legal_random")),
				new AgentPlayer("5", AgentUtils.buildAgent("flawed")),
				
				// there may be multiple agents using the same technique
				new AgentPlayer("6", AgentUtils.buildAgent("iggi")),
				new AgentPlayer("7", AgentUtils.buildAgent("piers")),
				
//				new AgentPlayer("8", AgentUtils.buildAgent("mctsND"))
		};
		*/
		return pool;
	}
	

	private static boolean isValidPartner(int dim1, int dim2) {
		
		if (MatchupTables.getMatchups(2)[dim1][dim2][0] !=0 || MatchupTables.getMatchups(2)[dim1][dim2][1] !=0){
			return true;
		}
		return false;
	}
	
    public static void main( String[] args )
    {
    	//The arraylist and iterator is used for output only.
    	//After we disable logger we could remove these.
    	ArrayList<StatsSummary> statsSummary = new ArrayList<StatsSummary>();
        MetaAgent agent = new MetaAgent();
        String results = "";    
        
  
        int dim = 5;
        
    	for(int i = 0; i<dim*dim; i++) {
    		
    		int theirDim1 = i/dim;
    		int theirDim2 = i%dim;
    		
    		for (int j = 0; j<dim*dim; j++) {
        		int myDim1 = j/dim;
        		int myDim2 = j%dim;
        		
        		agent.setGivenDimensions(-1, -1, myDim1, myDim2);
        		
        		double score = runTestGames(String.valueOf(i), agent).getMean();
        		
        		double estimatedDim1 = agent.getEstimatedDimensions(String.valueOf(i), MetaAgent.BehaviorCharacteristic.COMMUNICATIVENESS);
        		double estimatedDim2 = agent.getEstimatedDimensions(String.valueOf(i), MetaAgent.BehaviorCharacteristic.IPP);
        		
        		String r = String.format("%d %d %d %d %f %f %f\n",myDim1,myDim2,theirDim1,theirDim2,estimatedDim1,estimatedDim2,score);
        		System.out.println(r);
        		results += r;
//        		try {
//					fw.write(r);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

    		}
    		
    	}
    	System.out.print(results);

        FileWriter fw = null;
        try {
			fw = new FileWriter("matchup_estimations_and_scores2p3_20_games.txt");
			fw.write(results);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static StatsSummary runTestGames(String name, Agent agent) {
    	// the parameters for the st
        int numPlayers = 2;
        int numGames = 100;
        String agentName = "MetaAgent";
        Player[] pool = buildPool();

        Random random = new Random();
        StatsSummary statsSummary = new BasicStats();
        
        //build players (persistent across games)
        numPlayers = 2;
        Player you = new AgentPlayer("you", agent);
        
        // run the test games
        for (int i=0; i<numGames; i++) {
            GameRunner runner = new GameRunner("test-game", numPlayers);
            
            // add the players to the game
            List<Player> others;
            if(name == "random"){
                throw new IllegalArgumentException("Didn't expect to be random player");
            	//others = getRandomPlayers(pool, numPlayers - 1, random);
            }
            else {
            	others = getSpecificPlayers(pool, numPlayers - 1, name);
            }          
            
            addPlayers(runner, others, you, random.nextInt(numPlayers));
            
            //play the game
            long deckOrderingSeed = random.nextLong();
            GameStats stats = runner.playGame(deckOrderingSeed);
            statsSummary.add(stats.score);
            
        }

        //print out the stats
        /*
        System.out.println(String.format("Our agent with agent %s : Avg: %f, min: %f, max: %f",
        		name,
                statsSummary.getMean(),
                statsSummary.getMin(),
                statsSummary.getMax()));
        */
        return statsSummary;
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
	
	
	public static List<Player> getSpecificPlayers(Player[] pool, int numPlayers, String name){
		List<Player> order = new ArrayList<>();
		for (int i=0; i<numPlayers; i++) {
		    if(pool[Integer.parseInt(name)]==null) {
		        
		        System.out.println(pool.length);
		        Player test = pool[1];
		        System.out.println(Integer.parseInt(name));
		        throw new IllegalArgumentException("Player "+name +" not found");
		    }
			order.add(pool[Integer.parseInt(name)]);
		}
		order = order.subList(0, numPlayers);
		return order.subList(0, numPlayers);
	}
	
	/*
	
	public static Player buildSpecificAgent(String name) {
		switch(name) {
			case "0" :{
				return new AgentPlayer("0", AgentUtils.buildAgent("iggi"));
			}
			case "1" :{
				return new AgentPlayer("1", AgentUtils.buildAgent("piers"));
			}
			case "2" :{
				return new AgentPlayer("2", AgentUtils.buildAgent("internal"));
			}
			case "3" :{
				return new AgentPlayer("3", AgentUtils.buildAgent("outer"));
			}
			case "4" :{
				return new AgentPlayer("4", AgentUtils.buildAgent("legal_random"));
			}
			case "5" :{
				return new AgentPlayer("5", AgentUtils.buildAgent("flawed"));
			}
			case "6" :{
				return new AgentPlayer("6", AgentUtils.buildAgent("RuleBasedVanDeBergh"));
			}
			case "7" :{
				return new AgentPlayer("7", AgentUtils.buildAgent("piers"));
			}
			case "8" :{
				return new AgentPlayer("8", AgentUtils.buildAgent("mctsND"));
			}
			default:
				return null;
	
		}
		
	}
	*/
    
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
		if(others.size() <= 0||others.get(0)== null) {
		    throw new IllegalArgumentException("List of players is empty");
		}
		if(yourPos >= numPlayers) {
		    throw new IllegalArgumentException("yourPos out of range");
		}
        int lastOther = 0;
        for (int seat=0; seat<numPlayers; seat++) {
        	
        	// if you are in this position, add you, else one of the other players
        	if (seat == yourPos) {
        		runner.addNamedPlayer("you", you);
        	} else {
        		Player other = others.get(lastOther);
        		lastOther = lastOther++;
        		runner.addNamedPlayer(other.getName(), other);
        	}
        	
        }
	}
	
}
