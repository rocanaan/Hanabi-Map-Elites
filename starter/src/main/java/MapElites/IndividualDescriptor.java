package MapElites;

import java.util.List;

public class IndividualDescriptor {
	public double fitness;
	public int numDimensions;
	public List<Double> coordinates;
	
	public IndividualDescriptor(double fitness, List<Double> coordinates) {
		this.fitness = fitness;
		this.numDimensions = coordinates.size();
		this.coordinates = coordinates;
	}
}
