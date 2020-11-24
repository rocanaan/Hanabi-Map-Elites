package MetaAgent;

import java.util.ArrayList;

import com.fossgalaxy.stats.BasicStats;

public class DetailedStats extends BasicStats {
	private ArrayList<Double> gameScores;
	
	
    public DetailedStats() {
        super();
        this.gameScores = new ArrayList<Double>();
    }
	
    @Override
    public void add(double number) {
        super.add(number);
        gameScores.add(number);
    }
    
    
    public ArrayList<Double> getGameScores(){
    	return this.gameScores;
    }
	
	

}
