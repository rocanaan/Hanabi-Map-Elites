package com.fossgalaxy.games.fireworks.ai;

import java.util.Vector;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

public class MirrorTrackTest {
	public static void main( String[] args ) {
		int maxNumPlayers = 5;
		int numGames = 100;
		
		String agentName = "RuleBasedVanDeBergh";
		String otherAgent = "RuleBasedVanDeBergh";
		
		Vector<StatsSummary> stats = new Vector<StatsSummary>(maxNumPlayers-1);
		stats = TestSuite.VariableNumberPlayersTest(maxNumPlayers, numGames, agentName, otherAgent);
		
		int n = 2;
		for (StatsSummary s: stats) {
			System.out.println("Stats for agent " +  agentName + " paired with " + (n-1) + " copies of  " + otherAgent);
			System.out.println("Mean: " + s.getMean());
			System.out.println("Max: " + s.getMax());
			System.out.println("Min: " + s.getMin());
			
			n++;
		}
				
	}
}
