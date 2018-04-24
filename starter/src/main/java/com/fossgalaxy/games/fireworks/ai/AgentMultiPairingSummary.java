package com.fossgalaxy.games.fireworks.ai;

import java.util.Vector;

import com.fossgalaxy.stats.StatsSummary;


/*
 * Class containing the summary for the evaluation of a single agent paired with a population of "control" agents.
 */
public class AgentMultiPairingSummary {

	public AgentMultiPairingSummary(boolean mirror, AgentPlayer agent, Vector<AgentPlayer> v) {
		this.mirror = mirror;
		this.agent = agent;
		
		if (mirror) {
			pairings = new Vector<PairingSummary>(1);
		}
		else {
			pairings  = new Vector<PairingSummary>(v.size());
		}

	}
	
	boolean mirror;
	public AgentPlayer agent;
	public Vector<PairingSummary> pairings;
	
	public String toString() {
		String text = "";
		if (mirror) {
			text += "Multi-Pairing summary for agent " + agent.getName() + " on a mirrored pairing:\n";
		}
		else {
			text += "Multi-Pairing summary for agent " + agent.getName() + " paired with " + pairings.size() + " other agents:\n";
		}

		
		// Print in detail all pairings
//		for (PairingSummary p: pairings) {
//			text += p.toString();
//		}
		for (int n = 2; n<=5; n++) {
			text += "	Mean accross all pairings for game size of " +n + " is " + getMeanByGameSize(n) +"\n";
		}
		
		text += "Mean accross all pairings and game sizes for agent " + agent.getName() + " = " + getMean() + "\n";
		return text;
	}
	
	public double getMean() {
		double mean = 0;
		int size = pairings.size();
		
		if (size>0) {
			for (PairingSummary p: pairings) {
				mean += p.getMean();
			}
			mean = mean/(double)size;
		}
		
		return mean;
	}
	
	public double getMeanByGameSize(int n) {
		double mean = 0;
		int size = pairings.size();
		
		if (size>0) {
			for (PairingSummary p: pairings) {
				mean += p.getScoreByGameSize(n);
			}
			mean = mean/(double)size;
		}
		
		return mean;
		
	}
	

}
