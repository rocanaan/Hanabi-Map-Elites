package MapElites;

import java.io.Serializable;
import java.util.ArrayList;

import com.fossgalaxy.games.fireworks.ai.Agent;

import PostSimulationAnalyses.StateActionPair;

public class ActionDatabaseEntry implements Serializable{
	//public Agent agent; //TODO: Can't do this otherwize can't serialize
	public int dim1;
	public int dim2;
	public ArrayList<StateActionPair> stateActionArchive;
	
	public ActionDatabaseEntry(int dim1, int dim2, ArrayList<StateActionPair> stateActionArchive) {
		//this.agent = agent;
		this.dim1 = dim1;
		this.dim2 = dim2;
		this.stateActionArchive = stateActionArchive;
	}
}