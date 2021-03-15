package SPARTA_Communication;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.state.GameState;
import com.fossgalaxy.games.fireworks.state.actions.Action;
import com.fossgalaxy.games.fireworks.state.actions.DiscardCard;
import com.fossgalaxy.games.fireworks.state.actions.PlayCard;
import com.fossgalaxy.games.fireworks.state.actions.TellColour;
import com.fossgalaxy.games.fireworks.state.actions.TellValue;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import com.google.common.primitives.Ints; 


public class humanSpartaAgent implements Agent {

	String pathToStates;
	String pathToActions;
	
	

	public humanSpartaAgent(String pathToStates, String pathToActions) {
		super();
		this.pathToStates = pathToStates;
		this.pathToActions = pathToActions;
	}


	@Override
	public Action doMove(int agentID, GameState state) {
		
		// TODO Auto-generated method stub
		
		Action previousAction = state.getActionHistory().get(state.getActionHistory().size()-1).action;
		
		
		System.out.println("Hands");
		System.out.println(state.getHand(0).getCard(1));
		System.out.println(state.getHand(1).getCard(1));

		
		if (previousAction != null){
			try (FileWriter file = new FileWriter(pathToActions + File.separator + "botaction " + new java.util.Date())) {
				int actionAsInt = actionToInt(previousAction);
				System.out.println("Wrote bot action " + actionAsInt);
				file.write(String.valueOf(actionAsInt));
				file.close();
			 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			System.out.println("No action from partner to report");
		}
				
		// Write state to JSON
		JSONObject json = StateToSpartaConverter.toSpartaJSON(state, agentID);
		
		 try (FileWriter file = new FileWriter(pathToStates + File.separator + "state " + new java.util.Date())) {
			 file.write(json.toJSONString());
			 System.out.println("Wrote state file");
			 file.close();
		 }
		 catch (IOException e) {
	            e.printStackTrace();
	     }
		 
		 
		
		// Wait until file with human action can be found
		boolean found = false;
		int actionID = -1;
		
		while (!found) {
			String[] pathnames;
			File f = new File(pathToActions);
			pathnames = f.list();
			for (String pathname:pathnames) {
				if (pathname.toLowerCase().contains("playeraction")){
					found = true;
					System.out.println("Found action!");
					try {
						File actionFile  = new File(pathToActions + File.separator + pathname);

						Scanner scanner = new Scanner(actionFile);
						actionID = scanner.nextInt();
						System.out.println("Action is " + actionID);
						actionFile.delete();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
					
				}
			}
			
			if (!found) {

				System.out.println("Waiting for human SPARTA action, current state is:");
				System.out.println(json.toJSONString());
				try {
					TimeUnit.MILLISECONDS.sleep(50);;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		Action action = intToAction(agentID,actionID);
			 
			
		
		return action;
	}

	public static Action intToAction(int agentID,int actionID) {
		Action action = null;
		
		if (actionID <5) {
			action = new DiscardCard(actionID);
		}
		else if (actionID <10) {
			action = new PlayCard(actionID-5);
		}
//		TODO: hardcoded numplayers = 2
		else if (actionID <15 ) {
			action = new TellColour( (agentID+1) % 2, StateToSpartaConverter.spartaColorAsString(StateToSpartaConverter.spartaColors[actionID-10]));
		}
		else if (actionID <20) {
			action  = new TellValue((agentID+1) % 2, actionID-14); // Note: here it's 14 not 15 because values are one-based
		}
			
		
		return action;
	}
	
	public int actionToInt(Action action) {
		if (action instanceof DiscardCard) {
			DiscardCard dc = (DiscardCard) action;
			int slot = dc.slot;
			return 0 + slot;
		}
		if (action instanceof PlayCard) {
			PlayCard pc = (PlayCard) action;
			int slot = pc.slot;
			return 5 + slot;
		}
		if (action instanceof TellColour) {
			TellColour tc = (TellColour) action;
			int colorAsInt = StateToSpartaConverter.spartaColorAsInt(StateToSpartaConverter.convertColorToSparta(tc.colour));
			return 10 + colorAsInt;
		}
		if (action instanceof TellValue) {
			TellValue tv = (TellValue) action;
			int value  = tv.value;
			return 14 + value;
		}
		
		return -1;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 int numPlayers = 2;
		 Random random = new Random();
		 String agentName = "RuleBasedPiers";
		 GameRunner runner = new GameRunner("test-game", numPlayers);
		 
		 
		 String pathToStates = "/Users/rodrigocanaan/Dev/SpartaIntegrationTest/states";
		 String pathToActions = "/Users/rodrigocanaan/Dev/SpartaIntegrationTest/actions";
		 
//		 String pathToStates = "/Users/rodrigocanaan/Dev/HanabiUIs/Arshiya_SPARTA/MockDirectoryStructure/SESSIONS/Session000001/Games/Game1/actions";
//		 String pathToActions = "/Users/rodrigocanaan/Dev/HanabiUIs/Arshiya_SPARTA/MockDirectoryStructure/SESSIONS/Session000001/Games/Game1/states";
	 
		 Agent spartaHuman = new humanSpartaAgent(pathToStates, pathToActions);
	     runner.addPlayer(new AgentPlayer("spartaHuman", spartaHuman));
	     Agent rb = AgentUtils.buildAgent(agentName);
		 runner.addPlayer (new AgentPlayer("rb", rb)) ;
		 
		 runner.playGame(random.nextLong());

	}
}
