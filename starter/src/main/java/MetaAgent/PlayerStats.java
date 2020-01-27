package MetaAgent;

public class PlayerStats {
		
	public double numPlays;
	public double totalPlayability;
	public double totalInformationPlays;
	public double numHints;
	public double numPossibleHints;
	public int totalInteractions;
	public PlayerStats(double numPlays, double totalPlayability, double totalInformationPlays, double numHints, double numPossibleHints, int totalInteractions) {
		super();
		this.numPlays = numPlays;
		this.totalPlayability = totalPlayability;
		this.totalInformationPlays = totalInformationPlays;
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
	
	public double getInformationPlays() {
		double riskAversion = 0.0;
		if (numPlays >0) {
			return totalInformationPlays/(2*numPlays);
		}
		return riskAversion;
	}
}
