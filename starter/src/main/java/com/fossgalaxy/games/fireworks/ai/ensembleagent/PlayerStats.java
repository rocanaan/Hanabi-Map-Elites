package com.fossgalaxy.games.fireworks.ai.ensembleagent;

public class PlayerStats {
		
	public double numPlays;
	public double totalPlayability;
	public double numHints;
	public double numPossibleHints;
	public int totalInteractions;
	public PlayerStats(double numPlays, double totalPlayability, double numHints, double numPossibleHints, int totalInteractions) {
		super();
		this.numPlays = numPlays;
		this.totalPlayability = totalPlayability;
		this.numHints = numHints;
		this.numPossibleHints = numPossibleHints;
		this.totalInteractions = totalInteractions;
	}
		
	public double getCommunicativeness(){
		double communicativeness = 0.0;
		if (numPossibleHints >0) {
			return numHints/numPossibleHints;
		}
		return communicativeness;
	}
	
	public double getRiskAversion() {
		double riskAversion = 0.0;
		if (numPlays >0) {
			return totalPlayability/numPlays;
		}
		return riskAversion;
	}
}
