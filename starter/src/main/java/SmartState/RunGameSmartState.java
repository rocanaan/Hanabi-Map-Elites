package SmartState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

import SmartState.Observations.BoardStateObservation;
import SmartState.Observations.PartnerCardIsNecessaryObservation;
import SmartState.Observations.PartnerCardIsPlayableObservation;
import SmartState.Observations.PartnerCardIsUselessObservation;
import SmartState.Observations.PartnerCardOneHotColorAndValueObservation;
import SmartState.Observations.PartnerCardOneHotColorObservation;
import SmartState.Observations.PartnerCardOneHotValueObservation;
import SmartState.Observations.PartnerKnowsCardColorObservation;
import SmartState.Observations.PartnerKnowsCardValueObservation;
import SmartState.Observations.ProbabilityCardIsNecessaryObservation;
import SmartState.Observations.ProbabilityCardIsPlayableObservation;
import SmartState.Observations.ProbabilityCardIsUselessObservation;

public class RunGameSmartState {
	
	public static void main(String[] args) {
		int numPlayers = 2;
	    int numGames = 1;
	 
	     
	    Random random = new Random();
	    StatsSummary statsSummary = new BasicStats();
     
	    GameRunner runner = new GameRunner("test-game", numPlayers);
         
	    Agent human = AgentUtils.buildAgent("HumanControlledAgent");
     
    
	    SmartStateObservation[] observations = {
	    		new BoardStateObservation(),
	    		new ProbabilityCardIsPlayableObservation(),
	    		new ProbabilityCardIsUselessObservation(),
	    		new ProbabilityCardIsNecessaryObservation(),
	    		new PartnerCardIsPlayableObservation(),
	    		new PartnerCardIsUselessObservation(),
	    		new PartnerCardIsNecessaryObservation(),  		
	    		new PartnerKnowsCardColorObservation(),
	    		new PartnerKnowsCardValueObservation(),
	    		
//	    		new PartnerCardOneHotColorObservation(),
//	    		new PartnerCardOneHotValueObservation(),
//	    		new PartnerCardOneHotColorAndValueObservation(),


	    };
	    SmartStateAgent ssa = new SmartStateAgent(new SmartGameState(observations), human);
	    
        Player player1 = new AgentPlayer("P1", ssa);
        Player player2 = new AgentPlayer("P2", ssa);

        runner.addPlayer(player1);
        runner.addPlayer(player2);
        
        runner.playGame(random.nextLong());
	     
	     
	}
	 
     
   

     
     
     
}
