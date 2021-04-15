package MetaAgent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.configuration2.XMLConfiguration;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.iggi.Utils;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.ai.rule.logic.DeckUtils;
import com.fossgalaxy.games.fireworks.state.Card;
import com.fossgalaxy.games.fireworks.state.CardColour;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.Hand;
import com.fossgalaxy.games.fireworks.state.HistoryEntry;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.actions.DiscardCard;
import com.fossgalaxy.games.fireworks.state.actions.PlayCard;
import com.fossgalaxy.games.fireworks.state.actions.TellColour;
import com.fossgalaxy.games.fireworks.state.actions.TellValue;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

import Evolution.Rulebase;
import MetaAgent.HistogramAgent;
import MetaAgent.MatchupTables;
import MetaAgent.PlayerStats;


import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * A sample agent for the learning track.
 * 
 * This agent demonstrates how to get the player IDs for the paired agents.
 * 
 * 
 * You can see more agents online at:
 * https://git.fossgalaxy.com/iggi/hanabi/tree/master/src/main/java/com/fossgalaxy/games/fireworks/ai
 */
public class BayesAdaptiveAgent implements Agent {

	// Precomputed strategies, matchup information and training partner names. These all relate to training partners
	private HashMap<String, Agent> myStrategyPool;
	private Set<String> trainingPoolCandidates;
	private MultiKeyMap<String, MatchupInformation> precomputedMatchupInfo;
	
	// These relate to evaluation partners, which are not known in advance
	private MultiKeyMap<String, Double> beliefDistribution;   // One entry would look like { ("anonymous1", "13 7"), 1/400}
	
//	private MultiKeyMap<String, PlayerStats> evaluationMatchupInfo; // Note: Currently we're not storing each match-up information in the long term
	
	private HashMap<String, PlayerStats> rollingMatchupInfo;   // This is the information on the current match-up for a given partner.
	private HashMap<String, Integer> rollingGamesWithPartner;
	private HashMap<String, Integer> rollingTurnsWithPartner;	
	
	
//	The following variables keep track of game state changes to compute BCs. They are assigned on the call to receiveID()
//	TODO: Perhaps they should be consolidated in a separate class
	private int myTurnCount; //This keeps track of the number of turns in the current game. Currently this is only used to do some set-up at game start. TODO: check if can be ommited in favor of GameState variable.
	private int hintsAvailable; //This keeps track of hints available over the tracked turns. Not the same as the hints available on the actual current turn.
	private int myID; // This maintains our turn order index throughout the current game. Not sure if needed as doMove() also receives the current ID 
	private String currentResponseID; // This maintains the ID of the strategy in strategy pool that is currently being used.
	private String[] currentPlayers;
	
	private Card[][] playerHands;
    private double[][] playabilityMask;
    private boolean[][] knowsColorMask;
    private boolean [][] knowsRankMask;
	
	// Hyperparameters TODO: Decide whether these will be passed by constructor, read from a config file etc
	private int turnsAdaptationThreshold;
	private int gamesAdaptationThreshold;
	private double assumedBehaviorVariance; // This is the knob that lets us determine by how much to update a given match-up's behavior information. In the general case, this could vary per match-up and depend on the threshold of games/turns. We are simplifying it with a single number.
	
	public BufferedWriter agentLogWriter;
	public BufferedWriter responseLogWriter;
	public BufferedWriter beliefLogWriter;


	
	//TODO: XIANBO: Create a new constructor that can also receive a belief distribution as an instance of MultiKeyMap<String, Double> and rolling matchup info as an instance of MultiKeyMap<String, MatchupInformation>
	//TODO: XIANBO: Create a new method that's publicly accessible to output belief distribution and rollingMatchupInfo to file



	public void loadRollingMatchupInfoFromFile(String filename) {

		JSONParser jsonParser = new JSONParser();
        
        try (FileReader reader = new FileReader(filename))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONObject belief = (JSONObject) obj;
            rollingMatchupInfo.clear();
            Set keys = 	belief.keySet();
            ;
            //Iterate over employee arra
            Iterator<String> it = keys.iterator();
            while(it.hasNext()){
            	String k = it.next();
            	Double ps0 = Double.parseDouble(k.split(",")[0]);
            	Double ps1 = Double.parseDouble(k.split(",")[1]);
            	Double ps2 = Double.parseDouble(k.split(",")[2]);
            	Double ps3 = Double.parseDouble(k.split(",")[3]);
            	Double ps4 = Double.parseDouble(k.split(",")[4]);
            	int ps5 = (int)Double.parseDouble(k.split(",")[5]);
        		PlayerStats ps =  new PlayerStats(ps0,ps1,ps2,ps3,ps4,ps5);
        		rollingMatchupInfo.put(k, ps);
            }
            
            

    		
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		
	}

    public void rollingMatchupInfoToFile(String filename) {
        JSONObject obj = new JSONObject();
        for(Entry<String, PlayerStats> entry: rollingMatchupInfo.entrySet()){
        	obj.put(entry.getKey(),entry.getValue().numPlays+","+entry.getValue().totalPlayability+","+entry.getValue().totalInformationPlays+","+entry.getValue().numHints+","+entry.getValue().numPossibleHints+","+entry.getValue().totalInteractions);
	    }
        FileWriter file;
        try {
            file = new FileWriter(filename);
            file.write(obj.toJSONString());
            file.flush();
            file.close();
        }
        catch(IOException e) {
        }finally {
        }
}	

	
	
	/*
	 * 		this.numPlays = numPlays;
		this.totalPlayability = totalPlayability;
		this.totalInformationPlays = totalInformationPlays;
		this.numHints = numHints;
		this.numPossibleHints = numPossibleHints;
		this.totalInteractions = totalInteractions;
	 */
	public void loadBeliefFromFile(String filename) {

		JSONParser jsonParser = new JSONParser();
        
        try (FileReader reader = new FileReader(filename))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONObject belief = (JSONObject) obj;
            beliefDistribution.clear();
            Set keys = 	belief.keySet();
            ;
            //Iterate over employee arra
            Iterator<String> it = keys.iterator();
            while(it.hasNext()){
            	String k = it.next();
        		MultiKey<String> key = new MultiKey<String>(k.split(",")[0]+k.split(",")[1],k.split(",")[2]);
        		beliefDistribution.put(key,(Double) belief.get(k));
            }
            
            

    		
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		
	}
	
    public void beliefInfoToFile(String filename) {
        JSONObject obj = new JSONObject();
        for(Entry<MultiKey<? extends String>, Double> entry: beliefDistribution.entrySet()){
        	obj.put(entry.getKey().getKey(0)+","+entry.getKey().getKey(1), entry.getValue());
	    }
        FileWriter file;
        try {
            file = new FileWriter(filename);
            file.write(obj.toJSONString());
            file.flush();
            file.close();
        }
        catch(IOException e) {
        }finally {
        }
}	
	
	public BayesAdaptiveAgent(HashMap<String, Agent> myStrategyPool, Set<String> trainingPoolCandidates,
			MultiKeyMap<String, MatchupInformation> precomputedMatchupInfo, String logPath, String postfix, int turnsAdaptationThreshold, int gamesAdaptationThreshold, double assumedBehaviorVariance ) {
		super();
		this.myStrategyPool = myStrategyPool;
		this.trainingPoolCandidates = trainingPoolCandidates;
		this.precomputedMatchupInfo = precomputedMatchupInfo;
		this.turnsAdaptationThreshold = turnsAdaptationThreshold;
		this.gamesAdaptationThreshold = gamesAdaptationThreshold;
		this.assumedBehaviorVariance = assumedBehaviorVariance;

		
		beliefDistribution = new MultiKeyMap<String, Double>();
		
		rollingMatchupInfo = new HashMap<String, PlayerStats>();  // This is the information on the current match-up for a given partner.
		rollingGamesWithPartner = new HashMap<String, Integer>();
		rollingTurnsWithPartner = new HashMap<String, Integer>();	
		
		try {
			agentLogWriter = new BufferedWriter(new FileWriter(logPath + "Full" + postfix, true));
			responseLogWriter = new BufferedWriter(new FileWriter(logPath + "Responses " + postfix, true));
			beliefLogWriter = new BufferedWriter(new FileWriter(logPath + "Beliefs" + postfix, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to open Bayes agent internal log files");
			e.printStackTrace();
		}
		String preamble = String.format("Test Starting agent with turns = %d games = %d variance = %f\n", turnsAdaptationThreshold, gamesAdaptationThreshold, assumedBehaviorVariance);
		
		
		writeLog(preamble,agentLogWriter);
		writeLog(preamble,responseLogWriter);
		writeLog(preamble,beliefLogWriter);

//		evaluationMatchupInfo = new MultiKeyMap<String, PlayerStats>();
	} //TODO: Write a function that initializes this class by generating all the precomputed object from our files


	// GameRunner should call this at the start of each game.
	public void receiveID(int agentID, String[] names) {
		this.myID = agentID;
		this.currentPlayers = names;
		myTurnCount = 0;
		hintsAvailable = 8;
		
		for (String name:currentPlayers) {
			if (name != "Bayes") {
				boolean first = false;
				if (!rollingGamesWithPartner.containsKey(name)) {
					first = true;
					rollingGamesWithPartner.put(name, 0);
					rollingTurnsWithPartner.put(name, 0);
					rollingMatchupInfo.put(name, new PlayerStats(0, 0, 0, 0, 0, 0));
					for (String trainingPartner:trainingPoolCandidates) {
						MultiKey<String> key = new MultiKey<String>(name, trainingPartner);
						beliefDistribution.put(key, 1.0/trainingPoolCandidates.size());
						
					    for(Entry<MultiKey<? extends String>, Double> entry: beliefDistribution.entrySet()){
					        System.out.println(entry.getKey().getKey(0)+" " +entry.getKey().getKey(1)+ " value: "+entry.getValue());
					    }
						
						
						//beliefInfoToFile("BayesInfo");	
						//loadBeliefFromFile("BayesTest");
					    //rollingMatchupInfoToFile("rollingmatchupinfo");
						//System.out.println("belief read and write test done");
					}	
				}
				else {
					rollingGamesWithPartner.put(name, rollingGamesWithPartner.get(name)+1); //TODO: This assumes each agent identifier is unique and would not generalize for multiplayer games where more than one player has the same identifier
				}
				currentResponseID = getResponse(name);
				
				if (first) {
					writeLog(name + " initial response: " + currentResponseID +"\n",responseLogWriter);
					writeLog("Response: " + currentResponseID +"\n","Failed writing to agent logger when starting new game",agentLogWriter);
					writeLog("First game with " + name + "\n",beliefLogWriter);
					writeLog("Response: " + currentResponseID +"\n","Failed writing to agent logger when starting new game",beliefLogWriter);
				}
				writeLog("First game with " + name + "\n",beliefLogWriter);
				
				// Logging initial responses for every game takes a lot of space TODO: Encapsulate, create flag
//				try {
//					agentLogWriter.append("Game number " + rollingGamesWithPartner.get(name)+ " with agent " + name +"\n");
//					agentLogWriter.append("Initial response: " + currentResponseID +"\n");
//
//					
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					System.err.println("Failed writing to agent logger when starting new game");
//					e.printStackTrace();
//				}
			}
			
		}

	}


	@Override
	public Action doMove(int agentID, GameState state) {
		// TODO Auto-generated method stub
		String theirID = currentPlayers[(agentID+1)%state.getPlayerCount()]; //TODO: get their ID
		
		
		
		// Some start of game set-up
		if (myTurnCount == 0) {
			int numPlayers = state.getPlayerCount();
			int numCards = state.getHandSize();
			//System.out.println("Setup " + numPlayers +  "  " + numCards + " Turn count " + myTurnCount);
			playerHands = new Card[numPlayers][numCards];
			playabilityMask = new double[numPlayers][numCards];
			knowsColorMask = new boolean[numPlayers][numCards];
			knowsRankMask = new boolean[numPlayers][numCards];

		
			
			for (int playerIndex = 0; playerIndex<numPlayers; ++playerIndex) {
				//System.out.format("Player %d's hand", playerIndex);
				if (playerIndex!=agentID) {
					for (int slotIndex = 0; slotIndex<numCards; ++slotIndex) {
						playerHands[playerIndex][slotIndex] = state.getCardAt(playerIndex, slotIndex);
						playabilityMask [playerIndex] = getPlayabilityMask(playerIndex,state);
						//System.out.print(" " + playerHands[playerIndex][slotIndex].colour + " " +  playerHands[playerIndex][slotIndex].value);
						//System.out.print(" " + playabilityMask[playerIndex][slotIndex]);

					}
										
				}
				
			}
		}
		
//		writeLog(String.format("Stats before update: %f %f\n", rollingMatchupInfo.get(theirID).getCommunicativeness(),rollingMatchupInfo.get(theirID).getInformationPlays()),"Error updating stats",agentLogWriter);
		PlayerStats theirStats =updateHistory(agentID, state); // new values for IPP, Comm, etc TODO Xianbo
//		writeLog(String.format("Updated their stats to %f %f\n", theirStats.getCommunicativeness(), theirStats.getInformationPlays()),"Error updating stats",agentLogWriter);
		rollingMatchupInfo.put(theirID,theirStats);
		
		// if (adaptationCondition(){
		if (rollingGamesWithPartner.get(theirID) >= gamesAdaptationThreshold || rollingTurnsWithPartner.get(theirID) >= turnsAdaptationThreshold) {
			updateBeliefs(currentResponseID,theirID); // update belief distribution using Bayes theorem, if either number of games or turns is > thresholdTODO
			rollingGamesWithPartner.put(theirID, 0);
			rollingTurnsWithPartner.put(theirID, 0);
			currentResponseID = getResponse(theirID); 
			
			// Logging responses every game takes a lot of space. TODO: find a better solution
			writeLog(theirID + " response: " + currentResponseID +"\n",responseLogWriter);
//			writeLog("Response: " + currentResponseID +"\n","Failed writing to agent logger when starting new game",agentLogWriter);
			writeLog("Response: " + currentResponseID +"\n","Failed writing to agent logger when starting new game",beliefLogWriter);
		}
			
			
//		currentResponseID = getResponse(theirID); //TODO: check if this can be ommited here as long as it is inside the adaptation check
		Agent responsePolicy = myStrategyPool.get(currentResponseID);
		
		rollingTurnsWithPartner.put(theirID, rollingTurnsWithPartner.get(theirID)+1);
		
		try {
			Action action = responsePolicy.doMove(agentID, state);
			return action;
		}
		catch (NullPointerException e){
			System.err.println("Null action returned");
	        List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(agentID, state));
	        //choose a  xdom item from that list and return it
	        int moveToMake = new Random().nextInt(possibleMoves.size());
	        return possibleMoves.get(moveToMake);
		}
	}
	
	
	String getResponse(String theirID) {
		
		double maxExpectedScore = Integer.MIN_VALUE;
		String bestExpectedResponse = "";
		
		for (String possibleResponse: myStrategyPool.keySet()) {
			
			double weightedScore = 0.0;
			
			for (String possiblePartner:trainingPoolCandidates) {
				double matchupScore = precomputedMatchupInfo.get(possibleResponse, possiblePartner).scoreMean;
				double matchupWeight = beliefDistribution.get(theirID, possiblePartner);
				weightedScore += matchupScore*matchupWeight;
			}
			
			if (weightedScore > maxExpectedScore){
				maxExpectedScore = weightedScore;
				bestExpectedResponse = possibleResponse;
			}
			
		}
		
		
		return bestExpectedResponse;
		
	}
	
	//TODO: Currently hardcodes the names of dimensions
	void updateBeliefs(String ourID, String theirID) {
		
		double epsilon = 0.001;
		
		// Too much space. TODO
		writeLog("-=-=-=-=-= Starting Probability Update -==-=-=-=\n",agentLogWriter);
		writeLog("New belief update with " + theirID + "\n",beliefLogWriter);
		
		double totalProbability = 0;
		double observedD1 = rollingMatchupInfo.get(theirID).getCommunicativeness();
		double observedD2 = rollingMatchupInfo.get(theirID).getInformationPlays();
		
		
		for (String possiblePartner:trainingPoolCandidates) {
			
			double expectedD1 = precomputedMatchupInfo.get(ourID, possiblePartner).EstimatedBCValues.get("communicativeness");
			double expectedD2 = precomputedMatchupInfo.get(ourID, possiblePartner).EstimatedBCValues.get("IPP");
			
			double diff1 = expectedD1 - observedD1;
			double diff2 = expectedD2 - observedD2;
			

			
			NormalDistribution n = new NormalDistribution(0,assumedBehaviorVariance);
			writeLog("Old belief for " + possiblePartner + " is " + beliefDistribution.get(theirID,possiblePartner) + "\n",beliefLogWriter);	
			double p1 = n.density(diff1);
			double p2 = n.density(diff2);
			double joint_probability = p1*p2;
			if (Double.isNaN(joint_probability)) {
				joint_probability = 0;
			}
//			if (joint_probability < epsilon){
//				joint_probability = epsilon;
//			}
			// Too much space. TODO
//			writeLog(String.format("Expected c = %f, ipp = %f with partner %s, observed %f %f\n", expectedD1, expectedD2, possiblePartner, observedD1, observedD2),agentLogWriter);
//			writeLog(String.format("Expected c = %f, ipp = %f with partner %s, observed %f %f\n", expectedD1, expectedD2, possiblePartner, observedD1, observedD2),beliefLogWriter);
//			writeLog(String.format("p1  = %f, p2 = %f joint = %f\n", p1, p2, joint_probability),agentLogWriter);


			beliefDistribution.put(theirID, possiblePartner, joint_probability*beliefDistribution.get(theirID,possiblePartner)) ;
			totalProbability += beliefDistribution.get(theirID, possiblePartner);
			
			// Too much space. TODO

//			writeLog(String.format("New belief for (%s %s) is %f \n", theirID, possiblePartner, beliefDistribution.get(theirID, possiblePartner)),agentLogWriter);
	

		}
		// Too much space. TODO

		writeLog("total probability is " + totalProbability,agentLogWriter);
		
		//Normalizing all beliefs at the end
		for (String possiblePartner:trainingPoolCandidates) {
			beliefDistribution.put(theirID, possiblePartner, beliefDistribution.get(theirID,possiblePartner)/totalProbability);
			// Too much space. TODO

//			writeLog(String.format("Final belief for (%s %s) is  %f\n", theirID, possiblePartner, beliefDistribution.get(theirID, possiblePartner)), agentLogWriter);
			writeLog(String.format("%s %s %f\n", theirID, possiblePartner, beliefDistribution.get(theirID, possiblePartner)), beliefLogWriter);
		}
	}
		
		
		
		
//		for possible_x2 in range(dimsize):
//            for possible_y2 in range(dimsize):
//                expected_matchup = matchups[chosen_x1][chosen_y1][possible_x2][possible_y2]
//                expected_d1 = expected_matchup.est_d1
//                expected_d2 = expected_matchup.est_d2
//                
//                diff_1 = expected_d1-observed_d1
//                diff_2 = expected_d2-observed_d2
//                
//                # Here we assume that diff_1 and diff_2 are independently and normally distributed ~N(0,std**2)
//                
//                n = norm(0,variance)
//                p1 = n.pdf(diff_1) # This is the probability of observing a behavior diff_1 from the expected value.
//                p2 = n.pdf(diff_2)
//                joint_probability = p1*p2 
//                
//                #Bayes
//                belief_distribution[possible_x2][possible_y2] = p1*p2*belief_distribution[possible_x2][possible_y2]
//                total_probability +=p1*p2*belief_distribution[possible_x2][possible_y2]
//    
//        
//        belief_distribution = belief_distribution/total_probability # Normalizing so that it sums to 1
//        bayes_adaptive_score[x2][y2] = score # Right now I'm always updating to the score of the most recent episode, not taking into account previous episodes and not doing early stopping based on convergence.
//        i+=1
//        print(score)

	
	
	
	// TODO: This method is duplicated from MetaAgent with slight variations. Needs to be consolidated. Alternatively, some of its functions could be moved to the game runner.
	public PlayerStats updateHistory(int agentID, GameState state) {
	    
		   /*
		    * The following variables in comment are global, which are used in this function.
	        private int myTurnCount;
	        private int hintsAvailable;
	        int numPlayers;
	        int numCards;
	        private Card[][] playerHands;
	        private double[][] playabilityMask;
	        private boolean[][] knowsColorMask;
	        private boolean [][] knowsRankMask;
		    */
	        GameState workingState = state.getCopy();
	        
//	      TODO: BUG FIX: There seems to be an instance where the agent records a hint as being given without having hints available
	        
	        List<HistoryEntry> lastMoves = getLastMoves(state);
	        int numMoves = lastMoves.size();
	        //System.out.println("My ID is " + agentID + " There were " + numMoves + " moves since last state on my turn count " + myTurnCount);
	        if (myTurnCount >0 ) { //check if needed, skipping a possibly incomplete first round in order to not have to deal with edge cases
	            Collections.reverse(lastMoves);
	            for(HistoryEntry move:lastMoves) {
	                

	                int activePlayerID = move.playerID;
	                if (activePlayerID!=agentID) {
	                    // Update player specific information;
	                    PlayerStats currentPlayerStats = rollingMatchupInfo.get(currentPlayers[activePlayerID]); // Note: getting the match-up information independently for each history entry should cover the 3+ player case, but is untested in the current version
	                    
	                    
	                    if (move.action instanceof TellColour) {
	                        currentPlayerStats.numHints = currentPlayerStats.numHints+1;
	                        currentPlayerStats.numPossibleHints = currentPlayerStats.numPossibleHints+1;  
	                    }
	                    else if (move.action instanceof TellValue) {
	                        currentPlayerStats.numHints = currentPlayerStats.numHints+1;
	                        currentPlayerStats.numPossibleHints = currentPlayerStats.numPossibleHints+1;   
	                    }
	                    else if (hintsAvailable>0) {
	                        currentPlayerStats.numPossibleHints = currentPlayerStats.numPossibleHints+1;
	                    }
	                    if (move.action instanceof PlayCard) {
	                        PlayCard play = (PlayCard)move.action;
	                        currentPlayerStats.numPlays = currentPlayerStats.numPlays+1;
	                        currentPlayerStats.totalPlayability = currentPlayerStats.totalPlayability + playabilityMask[activePlayerID][play.slot];
	                        
	                        
	                        int informationPlay = 0;
	                        if (knowsColorMask[activePlayerID][play.slot]) {
	                            informationPlay +=1;
	                        }
	                        if (knowsRankMask[activePlayerID][play.slot]) {
	                            informationPlay +=1;
	                        }
	                        currentPlayerStats.totalInformationPlays+=informationPlay;
	                    }
	                    currentPlayerStats.totalInteractions+=1;
	                    //System.out.println("Total Interactions with player " + currentPlayers[activePlayerID] + " is "  + playerStatsRecord.get(currentPlayers[activePlayerID]).totalInteractions);
	                } // end if

	                
	                
	                // Update general state information - that is: number of hints availabble after this move
	                if (move.action instanceof PlayCard) {
	                    PlayCard playAction = (PlayCard)move.action;
	                    //System.out.println("Card slot " + playAction.slot);
	                    //System.out.println("Active PLayer ID " + activePlayerID);

	                    Card playedCard = playerHands[activePlayerID][playAction.slot];
	                    //System.out.println("Played card was " + playerHands[activePlayerID][playAction.slot]);// + " color " + playerHands[activePlayerID][playAction.slot].colour + " value " + playerHands[activePlayerID][playAction.slot].value);
	                    if (playedCard!=null && playedCard.value == 5) {
	                        if (state.getTableValue(playedCard.colour)==5) {
	                            ++hintsAvailable;
	                        }
	                    }
	                    
	                }
	                if (move.action instanceof DiscardCard) {
	                    ++hintsAvailable;
	                }
	                if (move.action instanceof TellColour){
	                	TellColour tc = (TellColour)move.action;
                        CardColour color = tc.colour;
                        int targetPlayerID = tc.player;
                        if (targetPlayerID != agentID) {
                            int cardIndex = 0;
                            for (Card card:playerHands[targetPlayerID]) {
                                if (card.colour == color) {
                                    knowsColorMask[targetPlayerID][cardIndex] = true;
                                }
                                ++cardIndex;
                            }
                        }
	                	--hintsAvailable;
                        
	                }
	                if (move.action instanceof TellValue) {
	                	TellValue tv = (TellValue)move.action;
                        int rank = tv.value;
                        int targetPlayerID = tv.player;
                        if (targetPlayerID != agentID) {
                            int cardIndex = 0;
                            for (Card card:playerHands[targetPlayerID]) {
                                if (card.value == rank) {
                                    knowsRankMask[targetPlayerID][cardIndex] = true;
                                }
                                ++cardIndex;
                            }
                        }
	                	--hintsAvailable;
	                }
	            }
	            
	            
	        
	        }
	        
	        // Update what each player knows about their hand
	        for (int playerID = 0; playerID < state.getPlayerCount(); ++playerID) {
	            Hand hand = state.getHand(playerID);
	            for (int cardIndex = 0; cardIndex<hand.getSize(); ++cardIndex) {
	                playerHands[playerID][cardIndex] = hand.getCard(cardIndex);
	                if (hand.getKnownColour(cardIndex)!=null) {
	                    knowsColorMask[playerID][cardIndex] = true;
	                }
	                else {
	                    knowsColorMask[playerID][cardIndex] = false;
	                }
	                if (hand.getKnownValue(cardIndex)!=null) {
	                    knowsRankMask[playerID][cardIndex] = true;
	                }
	                else {
	                    knowsRankMask[playerID][cardIndex] = false;
	                }
	            }
	        }
	        
	        
//	      //System.out.println("Hints available after this action: "  + hintsAvailable);
	        
//	      for (int playerIndex = 0; playerIndex<numPlayers; ++playerIndex) {
//	          //System.out.format("Player %d's hand", playerIndex);
//	          if (playerIndex!=agentID) {
//	              for (int slotIndex = 0; slotIndex<state.getHandSize(); ++slotIndex) {
//	                  playerHands[playerIndex][slotIndex] = state.getCardAt(playerIndex, slotIndex);
//	                  //System.out.print(" " + playerHands[playerIndex][slotIndex].colour + " " +  playerHands[playerIndex][slotIndex].value);
//	              }
//	              
//	          }
//	      }
	        
	        if (hintsAvailable != state.getInformation()) {
	            //System.out.println("-------- WARNING: hints were miscalculated ----- ");
	        }
	        hintsAvailable = state.getInformation();
	        ++myTurnCount;
	        
	        playerHands = new Card[state.getPlayerCount()][state.getHandSize()];
	        playabilityMask = new double[state.getPlayerCount()][state.getHandSize()];
	        
	        for (int playerIndex = 0; playerIndex<state.getPlayerCount(); ++playerIndex) {
	            //System.out.format("Player %d's hand", playerIndex);
	            if (playerIndex!=agentID) {
	                for (int slotIndex = 0; slotIndex<state.getHandSize(); ++slotIndex) {
	                    playerHands[playerIndex][slotIndex] = state.getCardAt(playerIndex, slotIndex);
	                    playabilityMask [playerIndex] = getPlayabilityMask(playerIndex,state);
	                    //System.out.print(" " + playerHands[playerIndex][slotIndex].colour + " " +  playerHands[playerIndex][slotIndex].value);
	                    //System.out.print(" " + playabilityMask[playerIndex][slotIndex]);

	                }
	                                    
	            }
	            
	        }
	        
	        
	        // calculate possible moves
	        //List<Action> possibleMoves = new ArrayList<>(Utils.generateActions(agentID, state));

	        PlayerStats partnerStats = rollingMatchupInfo.get(currentPlayers[(agentID+1)%state.getPlayerCount()]); //TODO: This assumes I'm always going to adapt to the stats displayed by the next player in the turn order;
	        double communicativeness = partnerStats.getCommunicativeness();
	        double riskAversion = partnerStats.getRiskAversion();
	        double informationPlays = partnerStats.getInformationPlays();
//	        writeLog("Communicativeness " + communicativeness,agentLogWriter);
//	        writeLog(" Risk Aversion " + riskAversion,agentLogWriter);
//	        writeLog(" Average Information per play " + informationPlays + "\n",agentLogWriter);
	        
	        return partnerStats;

		}
	
	// TODO: This method is duplicated from MetaAgent. Needs to be consolidated.
	private List<HistoryEntry> getLastMoves(GameState state) {
		List<HistoryEntry> allHistory = state.getActionHistory();
		int historySize = allHistory.size();
		
		List<HistoryEntry> movesSinceLastGo = new ArrayList<>();
		
		//we want the last n moves, if possible
		int movesToGet = Math.min(state.getPlayerCount(), historySize);
		
		for (int i = 0; i < movesToGet; i++) {
			int moveIndex = (historySize - (1+i));
			
			//negative game events are events that don't belong to any player, eg. game setup
			HistoryEntry entry = allHistory.get(moveIndex);
			if (entry.playerID < 0) {
				continue;
			}
			
			movesSinceLastGo.add(allHistory.get(moveIndex));
		}
		
		return movesSinceLastGo;
	}
	
	// TODO: This method is duplicated from MetaAgent. Needs to be consolidated.
	//TODO This currently only works in 2-player games, as in games with more players both
	//the playability status of each card might have changed (if some cards were played) 
	//and the information a player has might also have changed (if they received hints)
	// To fix this, I need to either generate new GameState objects for each move, or keep track of which cards would be  playable and what information a player has after each move separatly
	 public static double[] getPlayabilityMask(int targetID, GameState gameState){
			List<Double> probabilities = new ArrayList<Double>();
			
			if (targetID < gameState.getPlayerCount()) {
				
				List<Card> deck = gameState.getDeck().toList();
				
				Hand hand = gameState.getHand(targetID);
				
				// Adding target player's hand to the deck, as they don't know about it
				for(int slot=0;slot<hand.getSize();slot++) {
					Card c = hand.getCard(slot);
					if (c != null) {
						deck.add(c);
					}
					
				}
			
				Map<Integer, List<Card>> possibleCards = DeckUtils.bindBlindCard(targetID, gameState.getHand(targetID), deck);
				
				for (Map.Entry<Integer, List<Card>> entry : possibleCards.entrySet()) {
		            double probability = DeckUtils.getProbablity(entry.getValue(), x -> isPlayable(x, gameState));
		            probabilities.add(probability);
		        }
				
				int size = probabilities.size();
				if (size < 5) {
					for (int i = 0; i < (5-size); i++) {
						probabilities.add(0.0);
					}
				}
				
			}
			
			else {
				for (int i = 0; i<5; i++) {
					probabilities.add(0.0);
				}
			}
			double[] probabilityArray = {0,0,0,0,0};
			
			int i = 0;
			for (Double d: probabilities) {
				probabilityArray[i] = d;
				i++;
			}
			return probabilityArray;
		}
	 
	    public static boolean isPlayable(Card card, GameState state) {
	        return state.getTableValue(card.colour) + 1 == card.value;
	    }
	
	    public static HashMap<String,Agent> getGauntlet(){
	    	HashMap<String, Agent> gauntlet = new HashMap<String,Agent>();
			String[] gauntletNames = {"RuleBasedIGGI", "RuleBasedInternal","RuleBasedOuter","SampleLegalRandom","RuleBasedVanDeBergh","RuleBasedFlawed","RuleBasedPiers"};
	    	for (String name : gauntletNames) {
	    		Agent a = AgentUtils.buildAgent(name);
	    		gauntlet.put(name, a);
	    	}
	    	return gauntlet;
	    }
	    
	    // A small main function to play the Bayes agent using the same training pool and strategy pool
	    public static void main(String[] args){
			String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
			
			// Experiment parameters
			int numTrainingGames = 400; //400 in the paper
			int numEvaluationGames = 10;
			int numEvalRepetitions = 200;
			// Agent parameters
			int turnsAdaptationThreshold = Integer.MAX_VALUE;
			int gamesAdaptationThreshold = Integer.MAX_VALUE;
			double assumedBehaviorVariance = 0.1;
			boolean usePrecomputed = false;
			String precomputedMatchupFile =  System.getProperty("user.dir")+ File.separator + "FinalTOG5by5" + File.separator + "Bayes3Matchups"; 
//			String precomputedMatchupFile =  System.getProperty("user.dir")+ File.separator + "5by5tests" + File.separator + "20201130185201MatchupInfo"; 
			String experimentName = "FinalTOG5by5" + File.separator + "GauntletGeneralistE3"; 



	    	
//	    	If reading from plain text chromosome files
//	    	String strategyChromosomeFile = "2P3";
//	    	String trainingChromosomeFile = strategyChromosomeFile;
//	    	String evaluationChromosomeFile = "2P3";
//			HashMap<String, Agent> strategyPool = AgentLoaderFromFile.makeAgentMapFromFile(strategyChromosomeFile, false, true);
//			HashMap<String, Agent> trainingPool = AgentLoaderFromFile.makeAgentMapFromFile(trainingChromosomeFile, false, true); //TODO: If precomputed, training pool has to be the set of partner agents in the matchup file
//			HashMap<String, Agent> evaluationPool = AgentLoaderFromFile.makeAgentMapFromFile(evaluationChromosomeFile, false, true);

//	  		If reading from JSON iteration files  			
	    	String strategyChromosomeFile = "5by5final3/1iteration999999.json";
	    	String trainingChromosomeFile = strategyChromosomeFile;
	    	String evaluationChromosomeFile = "5by5final3/1iteration999999.json";
			HashMap<String, Agent> strategyPool = AgentLoaderFromFile.makeAgentMapFromJSON(strategyChromosomeFile, false, true);
			HashMap<String, Agent> trainingPool = AgentLoaderFromFile.makeAgentMapFromJSON(trainingChromosomeFile, false, true); //TODO: If precomputed, training pool has to be the set of partner agents in the matchup file
			HashMap<String, Agent> evaluationPool = AgentLoaderFromFile.makeAgentMapFromJSON(evaluationChromosomeFile, false, true);
			
			strategyPool = getGauntlet();
			trainingPool = getGauntlet();
//			evaluationPool = getGauntlet();
			
			
			Set<String> trainingPoolCandidates = strategyPool.keySet();
			Set<String> evaluationPoolCandidates = evaluationPool.keySet();

			int numPlayers = 2;
			
//			TODO: if there is no specified path to load precomputed information, play games and output json else read from the JSON
//			String precomputedPath = "";
//			if (precomputedPath == "") {
//				int numTrainingGames = 100;
//				MultiKeyMap<String, MatchupInformation> MUs = PrecomputeMatchupInfo.precomputeMatchups(strategyPool, trainingPool, numPlayers, numTrainingGames);
//				TODO: write this to JSON instead of raw text
//	    	}
//	    	else {
//	    		TODO: read from the JSON specified in the path
//	   		}
			
			
			
			MultiKeyMap<String, MatchupInformation> MUs = null;
			
			
			
			
			
			
			
			// A very messy parser for the matchup file. TODO: Output the matchups in JSON instead, write a converter from current format to JSON for compatibiliy
			if (usePrecomputed) {
				MUs = parsePrecomputedMUFile(precomputedMatchupFile); 
			}
			
			else {
				// TODO: This block has too many responsibilities: actually precomputing the matchups with the provided method and logging a bunch of stuff
				String matchupInfoFile = experimentName + File.separator +  date+ "MatchupInfo";
				
				BufferedWriter matchupWriter = null;
				try {
					matchupWriter = new BufferedWriter(new FileWriter(matchupInfoFile, true));
					matchupWriter.append(String.format("Training games = %d chromosome file = %s\n", numTrainingGames,strategyChromosomeFile));
					matchupWriter.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
				// TODO: Rewrite this method into  precomputeSingleMatchup, which lets us get the results matchup per matchup and add them to file one by one
				// Also I do not want detailed score information on the file.
				MUs = PrecomputeMatchupInfo.precomputeMatchups(strategyPool, trainingPool, numPlayers, numTrainingGames);
	
				System.out.println("Finished precomputing matchups");
				// TODO: replace the next 4 lines where you print to the file with outputting a JSON
				
	
	//			String overview = "";
	//			String detailed = "Detailed Scores\n";
				for (MultiKey key: MUs.keySet()) {
					//System.out.println(key.getKey(0));
					//System.out.println(key.getKey(1));
					MatchupInformation MU = MUs.get(key);
					

					writeLog(String.format("%s;%s: %f %f %f\n", key.getKey(0), key.getKey(1), MU.EstimatedBCValues.get("communicativeness"), MU.EstimatedBCValues.get("IPP"), MU.scoreMean),matchupWriter);
					System.out.println(String.format("%s;%s: %f %f %f\n", key.getKey(0), key.getKey(1), MU.EstimatedBCValues.get("communicativeness"), MU.EstimatedBCValues.get("IPP"), MU.scoreMean));
					//				detailed+= String.format("%s %s:",  key.getKey(0), key.getKey(1));
	//				for (Double score:MU.gameScores) {
	//					detailed+=(String.format(" %f",score));
	//				}
	//				detailed+="\n";
				}
	//			writeLog(overview,matchupWriter);
	//			writeLog(detailed,matchupWriter);
				//xianbocomment
				/*
				try {
					matchupWriter.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
				*/
			}
			
			
			
			
			
			
			double overallAverage = 0;
			for (int i=0; i<numEvalRepetitions;i++) {
				BayesAdaptiveAgent ba = new BayesAdaptiveAgent(strategyPool, trainingPoolCandidates, MUs, experimentName + File.separator + date+ "bayesLogger", String.valueOf(i),turnsAdaptationThreshold, gamesAdaptationThreshold, assumedBehaviorVariance );
				HashMap<String, StatsSummary> results = new HashMap<String, StatsSummary>();
				HashMap<String, ArrayList<Integer>> detailedResults = new HashMap<String, ArrayList<Integer>>();
	            Random random = new Random();
	            long seed = random.nextLong();
				for (String theirID : evaluationPoolCandidates) {
					random = new Random(seed);
					StatsSummary statsSummary = new BasicStats();
					
					ArrayList<Integer> gameScores = new ArrayList<Integer>();
	
		               
			        // run the test games
			        for (int gameCount=0; gameCount<numEvaluationGames; gameCount++) {
			        	
			            GameRunner runner = new GameRunner("test-game", numPlayers);
			            
			            //runner.addNamedPlayer("Bayes", new AgentPlayer("Bayes", ba));
			            
			            int nextPlayer = 0;
			            for (nextPlayer = 0; nextPlayer< numPlayers; nextPlayer++);{
			            	Agent evaluationPartner = evaluationPool.get(theirID);
			            	runner.addNamedPlayer("test"+theirID+String.valueOf(nextPlayer), new AgentPlayer(theirID, evaluationPartner));
			            }
			           
			    		
			    		System.out.println(String.format("Starting to play game %d with %s", gameCount, theirID));
			            GameStats stats = runner.playGame(random.nextLong());
			            System.out.println("Finished game");
			            statsSummary.add(stats.score);
			            gameScores.add(stats.score);
			            System.out.println(String.format("Played game %d with %s, score %d", gameCount, theirID, stats.score));
			            
			        }
			        
			        results.put(theirID,statsSummary);
			        detailedResults.put(theirID,gameScores);
			        
			        System.out.println(String.format("Playing Bayes with %s got average score %f" , theirID, statsSummary.getMean()));
	
				}
	//			ba.agentLogWriter.close();
				BufferedWriter resultsWriter = null;
				try {
					resultsWriter = new BufferedWriter(new FileWriter(experimentName + File.separator +  date+ "Results_" + String.valueOf(i), true));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("Failed to open Bayes agent internal log file");
					e.printStackTrace();
				}
				writeLog(String.format("Strategy and Training: %s Evaluation: %s Training games: %d Evaluation games: %d Turn adaptation: %d Game adaptation: %d Assumed variance: %f\n", 
						strategyChromosomeFile, evaluationChromosomeFile, numTrainingGames, numEvaluationGames, turnsAdaptationThreshold, gamesAdaptationThreshold, assumedBehaviorVariance), resultsWriter);
				System.out.println(String.format("Strategy and Training: %s Evaluation: %s Training games: %d Evaluation games: %d Turn adaptation: %d Game adaptation: %d Assumed variance: %f\n", 
						strategyChromosomeFile, evaluationChromosomeFile, numTrainingGames, numEvaluationGames, turnsAdaptationThreshold, gamesAdaptationThreshold, assumedBehaviorVariance));
				System.out.println("=-=-=-=-Printing Final Results=-=-=-=-=-=-");
				String individualGameScores = "";
				double runAverage = 0;
				for (String theirID:results.keySet()) {
					StatsSummary MU = results.get(theirID);
			        System.out.println(String.format("Playing Bayes with %s got average score %f" , theirID, MU.getMean()));
			        writeLog(String.format("Playing Bayes with %s got average score %f\n" , theirID, MU.getMean()), resultsWriter);
			        runAverage+=MU.getMean();
			        for (int s: detailedResults.get(theirID)) {
			        	individualGameScores += (s + " ");
			        }
		        	individualGameScores += "\n";
				}
	//			System.out.println(individualGameScores);
				runAverage = runAverage/results.keySet().size();
				overallAverage += (runAverage/numEvalRepetitions);
				System.out.println(String.valueOf(runAverage));
				writeLog(String.valueOf(runAverage),resultsWriter);
			}
			System.out.println(String.format("Overall average is %f", overallAverage));
	    }
	
	private static void writeLog(String log, String error, BufferedWriter writer) {
		try {
			writer.append(log);
			writer.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(error);
			e.printStackTrace();

		}
	}
	
	private static void writeLog(String log, BufferedWriter writer) {
		//writeLog(log, "", writer);
	}
	
	// A very messy parser for the matchup file. TODO: Output the matchups in JSON instead, write a converter from current format to JSON for compatibiliy
	public static MultiKeyMap<String, MatchupInformation> parsePrecomputedMUFile(String precomputedMatchupFile){
		MultiKeyMap<String, MatchupInformation> MUs = new MultiKeyMap<String, MatchupInformation>();
		System.out.println("Reading Matchups from file " + precomputedMatchupFile);
		try (BufferedReader br = new BufferedReader(new FileReader(precomputedMatchupFile))) {
		    String line;
		    int lineIndex=0;
		    while ((line = br.readLine()) != null) {
		       if (lineIndex==0) {
		    	   System.out.println("Read initial line");
		    	   lineIndex++;
		    	   continue;
		       }
		       else {
		    	   System.out.println("Reading line " + String.valueOf(lineIndex) + " " + line);
		    	   lineIndex++;
//		    	   String[] tokens = line.split(" "); //old way
		    	   try {
		    		   String playerIDs = line.split(":")[0].strip();
		    		   String matchupMetrics = line.split(":")[1].strip();
		    		   String ourID = playerIDs.split(";")[0];     
		    		   String theirID = playerIDs.split(";")[1];   // Old versions of the precomputing script use a colon instead of semicolon here
		    		   
		    		   
		    		   double theirComm = Double.valueOf(matchupMetrics.split(" ")[0]);
			    	   double theirIPP = Double.valueOf(matchupMetrics.split(" ")[1]);
		    		   
//			    	   String ourID =  String.valueOf(Integer.valueOf(tokens[0].split(",|:")[0]));  //old way
//			    	   String theirID =  String.valueOf(Integer.valueOf(tokens[0].split(",|:")[1]));  //old way
//
//
//			    	   double theirComm = Double.valueOf(tokens[1]);
//			    	   double theirIPP = Double.valueOf(tokens[2]);
			    	   
			    	   Map<String,Double> estimatedBCValues = new HashMap<String,Double>();
			    	   estimatedBCValues.put("communicativeness", theirComm);
			    	   estimatedBCValues.put("IPP", theirIPP);
			    	   
			    	   double matchupScore = Double.valueOf(matchupMetrics.split(" ")[2]);;
			    	   
//			    	   double matchupScore = Double.valueOf(tokens[3]); //old way

			    	   MatchupInformation matchup = new MatchupInformation(ourID, theirID, estimatedBCValues, null, matchupScore, 0, 0, null);
			    	   
			    	   MUs.put(ourID,theirID,matchup);
			    	   
			    	   matchup = MUs.get(ourID,theirID);
			    	   
			    	   System.out.println(String.format("Read matchup: Our ID %s their ID %s %f %f %f", 
			    			   ourID,theirID, matchup.EstimatedBCValues.get("communicativeness"),matchup.EstimatedBCValues.get("IPP"),matchup.scoreMean  ));
		    	   }
		    	   catch (NumberFormatException e) {
		    		   System.err.println("Error reading precomputed matchup values");
		    		   e.printStackTrace();
		    		   System.exit(1);
		    	   }
		       }
		    }
		    System.out.println("Finished reading matchups");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println(String.format("File %s not found", precomputedMatchupFile));
			e.printStackTrace();
    		 System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    		System.exit(1);

		}
		return MUs;
	}
		
}
