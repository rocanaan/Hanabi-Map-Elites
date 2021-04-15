package MetaAgent;

import java.util.ArrayList;
import java.util.HashMap;

import com.fossgalaxy.stats.BasicStats;

public class DetailedStats extends BasicStats {
	private ArrayList<Double> gameScores;
//	private HashMap<String,ArrayList<Double>> behaviorScores;
	
	
    public DetailedStats() {  //ArrayList<String> BCs
        super();
        this.gameScores = new ArrayList<Double>();
        
//        for (String bc: BCs) {
//        	behaviorScores.put(bc, new ArrayList<Double>());
//        }
    }
	
    @Override
    public void add(double number) {
        super.add(number);
        gameScores.add(number);
    }
    
    
    public ArrayList<Double> getGameScores(){
    	return this.gameScores;
    }
	
//    public HashMap<String,ArrayList<Double>> getBehaviorScores(){
//    	return this.behaviorScores;
//    }
	

}
