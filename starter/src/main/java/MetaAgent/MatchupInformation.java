package MetaAgent;

import java.util.Collection;
import java.util.Map;

public class MatchupInformation {
	
	public MatchupInformation(String myID, String partnerID, Map<String, Double> estimatedBCValues, Map<String, Double> estimatedBCStd,
			double scoreMean, double scoreStd, int numGamesPlayed, Collection<Double> gameScores) {
		super();
		this.myID = myID;
		this.partnerID = partnerID;
		EstimatedBCValues = estimatedBCValues;
		EstimatedBCStd = estimatedBCStd;
		this.scoreMean = scoreMean;
		this.scoreStd = scoreStd;
		this.numGamesPlayed = numGamesPlayed;
		this.gameScores = gameScores;
	}

	String myID;
	String partnerID;
	
//	Map<String,Double> RealBCValues; // one entry of this map looks like {"Communicativeness", 0.5}
//	Map<String,Double> RealBCStd;
	
	public Map<String,Double> EstimatedBCValues;
	public Map<String,Double> EstimatedBCStd;
	
	
	
	public double scoreMean;
	public double scoreStd;
	
	int numGamesPlayed;
	Collection<Double> gameScores;
	
	
}


//  0 15 0 0 0.000000 0.298611 11.000000 -> new MatchupInformation("0 15","0 0", {"Communicativeness":0.00,"IPP":0.298}, null, 11, null, null}
