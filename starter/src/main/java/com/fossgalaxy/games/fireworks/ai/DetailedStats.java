package com.fossgalaxy.games.fireworks.ai;

import java.util.Vector;

import com.fossgalaxy.stats.StatsSummary;

public class DetailedStats  implements StatsSummary{
	private int n;
	private double min;
	private double max;
	private double sum;
	private Vector<Double> individualResults;
	
    public DetailedStats() {
        this.min = Double.MAX_VALUE;
        this.max = -Double.MAX_VALUE;
        this.n = 0;
        this.sum = 0;
        individualResults = new Vector<Double>();
    }

    @Override
    public void add(double number) {
        this.min = Math.min(number, min);
        this.max = Math.max(number, max);
        this.sum += number;
        this.n++;
	    individualResults.add(number);
    }

    @Override
    public int getN() {
        return n;
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getMin() {
        return min;
    }

    @Override
    public double getRange() {
        return max-min;
    }

    @Override
    public double getMean() {
        return sum/n;
    }
    
    public Vector<Double> getIndividualResults(){
    		return individualResults;
    }
    
}
