package com.fossgalaxy.games.fireworks.ai;

import java.util.Vector;

import com.fossgalaxy.stats.StatsSummary;


/*
 * Class containing the summary for the evaluation of a single agent paired with a pool of test agents.
 */
public class PopulationEvaluationSummary implements java.io.Serializable{

	public PopulationEvaluationSummary(boolean mirror, Vector<AgentPlayer> population, Vector<AgentPlayer> testPool) {
		this.mirror = mirror;
		this.population = population;
		this.testPool = testPool;
		

		pairings = new Vector<AgentMultiPairingSummary>(population.size());


	}
	public boolean mirror;
	public Vector<AgentPlayer> population;
	public Vector<AgentPlayer> testPool;
	public Vector<AgentMultiPairingSummary> pairings;
	
	public String toString() {
//		String text = "";
//		
//		if (mirror) {
//			text += "Results of evaluating a population of  " + population.size() + " agents in mirror mode:\n";
//		}
//		else {
//			text += "Results of evaluating a population of  " + population.size() + " agents with a test pool of " +testPool.size() + " test agents:\n";
//		}

//		for (AgentMultiPairingSummary p: pairings) {
//			text += p.toString();
//		}
		
//		text += "Fittest agent is " + getBestAgent() + " with a mean score of " + getBestScore();
		
		return "";
	}
	
	
	public String getBestAgent() {
		String bestAgentName = "";
		double bestScore = 0;
		for (AgentMultiPairingSummary p: pairings) {
			double score = p.getMean();
			if(score > bestScore) {
				bestScore = score;
				bestAgentName = p.agent.getName();
			}
		}
		return bestAgentName;
		
	}
	
	public double getBestScore() {
		String bestAgentName = getBestAgent();
		for (AgentMultiPairingSummary p: pairings) {
			if (p.agent.getName() == bestAgentName)
			{
				return p.getMean();
			}
		}
		return 0;
	}
	
	public double getScoreIndividualAgent(int index) {
		double score =  pairings.get(index).getMean();
		return score;
	}
	

}
