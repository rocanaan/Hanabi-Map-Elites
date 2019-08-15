package com.fossgalaxy.games.fireworks.ai;

import java.util.Vector;

import com.fossgalaxy.stats.StatsSummary;


/*
 * Class containing the summary for a single "pairing" of agents, that is agent agentName paired with copies of agent otherAgent
 * The variable maxNumberPlayers controls the number of game sizes that will be recorded.
 * For example, a value of 2 will mean only 2-player games will be recorded. A value of 5 means games with 2, 3, 4 and 5 games will be recorded.
 * In all cases, the pairing is made with 1 copy of agentName and (n-1) copy of otherAgent.
 */
public class PairingSummary {

	public PairingSummary(AgentPlayer name, AgentPlayer other, int minPlayers, int maxPlayers, int games) {
		agent = name;
		otherAgent = other;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		gamesPlayed = games;
		results = new Vector<StatsSummary>(maxPlayers - minPlayers +1);	
	}
	public AgentPlayer agent;
	public AgentPlayer otherAgent;
	public int minPlayers;
	public int maxPlayers;
	public int gamesPlayed;
	public Vector<StatsSummary> results;
	
	public String toString() {
		String text = "	Pairing summary for agent " + agent.getName() + " paired with agent " + otherAgent.getName() + " :";
		text += "\n";
		
		int n = minPlayers;
		for (StatsSummary s: results) {
			text += ("		Stats for agent " +  agent.getName() + " playing " + gamesPlayed + " games with " + (n-1) + " copies of  " + otherAgent.getName() + ":\n");
			text += ("			Mean: " + s.getMean() +"\n");
			text += ("			Max: " + s.getMax() +"\n");
			text += ("			Min: " + s.getMin() + "\n");
			n++;
		}
		
		text += "	Mean accross all game sizes for this pairing is " + getMean() + "\n";
		
		return text;
	}

	public double getMean() {
		double mean = 0;
		for (int n = minPlayers; n<= maxPlayers; n++) {
			int index = n-minPlayers;
			StatsSummary s = results.get(index);
			mean += s.getMean();
		}
		mean  = mean/(double) (maxPlayers-minPlayers+1);
	
		
		return mean; 
	}
	
	public double getScoreByGameSize(int n) {
		if (n <= maxPlayers && n >= minPlayers) {
			int index = n-minPlayers;
			return results.get(index).getMean();
		}
		else {
			return -1;
		}
		

	}
	

}
