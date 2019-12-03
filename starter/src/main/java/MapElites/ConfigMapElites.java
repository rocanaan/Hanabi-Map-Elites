package MapElites;

import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.AgentPlayer;

public class ConfigMapElites {

	private static int G = 10000; //number of random individuals
	private static int I = 1000000 - G; // number of mutations after initial random individuals
	private static int d1 = 20; // number of niches in the first dimension
	private static int d2 = 20; // number of niches in the second dimension
	private static double mutationRate = 0.1;
	private static double crossoverRate = 0.5;
	private static int numGames = 100; // Number of games per agent per game size. There are 4 different game sizes, so this number is actually 4 times higher
	private static boolean mirror = true; // If true, will run in mirror mode. If false, will run in mixed mode, which is takes around 7 times as long
	private static int minNumPlayers = 5;
	private static int maxNumPlayers = 5;
	private static boolean rulebaseStandard= false;
	private static int chromosomeLength = 15;
	private static Vector<AgentPlayer> testPool = null;
	private static double[][] map = new double[d1][d2];
	private static int[][][] population = new int[d1][d2][chromosomeLength];

	private static String filePath = "/Users/rodrigocanaan/Dev/HanabiResults/Evolution";
	private static String directory = "/20190801VariablePlayers/5p/";
	public static int getG() {
		return G;
	}
	public static void setG(int g) {
		G = g;
	}
	public static int getI() {
		return I;
	}
	public static void setI(int i) {
		I = i;
	}
	public static int getD1() {
		return d1;
	}
	public static void setD1(int d1) {
		ConfigMapElites.d1 = d1;
	}
	public static int getD2() {
		return d2;
	}
	public static void setD2(int d2) {
		ConfigMapElites.d2 = d2;
	}
	public static double getMutationRate() {
		return mutationRate;
	}
	public static void setMutationRate(double mutationRate) {
		ConfigMapElites.mutationRate = mutationRate;
	}
	public static double getCrossoverRate() {
		return crossoverRate;
	}
	public static void setCrossoverRate(double crossoverRate) {
		ConfigMapElites.crossoverRate = crossoverRate;
	}
	public static int getNumGames() {
		return numGames;
	}
	public static void setNumGames(int numGames) {
		ConfigMapElites.numGames = numGames;
	}
	public static boolean isMirror() {
		return mirror;
	}
	public static void setMirror(boolean mirror) {
		ConfigMapElites.mirror = mirror;
	}
	public static int getMinNumPlayers() {
		return minNumPlayers;
	}
	public static void setMinNumPlayers(int minNumPlayers) {
		ConfigMapElites.minNumPlayers = minNumPlayers;
	}
	public static int getMaxNumPlayers() {
		return maxNumPlayers;
	}
	public static void setMaxNumPlayers(int maxNumPlayers) {
		ConfigMapElites.maxNumPlayers = maxNumPlayers;
	}
	public static boolean isRulebaseStandard() {
		return rulebaseStandard;
	}
	public static void setRulebaseStandard(boolean rulebaseStandard) {
		ConfigMapElites.rulebaseStandard = rulebaseStandard;
	}
	public static int getChromosomeLength() {
		return chromosomeLength;
	}
	public static void setChromosomeLength(int chromosomeLength) {
		ConfigMapElites.chromosomeLength = chromosomeLength;
	}
	public static Vector<AgentPlayer> getTestPool() {
		return testPool;
	}
	public static void setTestPool(Vector<AgentPlayer> testPool) {
		ConfigMapElites.testPool = testPool;
	}
	public static double[][] getMap() {
		return map;
	}
	public static void setMap(double[][] map) {
		ConfigMapElites.map = map;
	}
	public static int[][][] getPopulation() {
		return population;
	}
	public static void setPopulation(int[][][] population) {
		ConfigMapElites.population = population;
	}
	public static String getFilePath() {
		return filePath;
	}
	public static void setFilePath(String filePath) {
		ConfigMapElites.filePath = filePath;
	}
	public static String getDirectory() {
		return directory;
	}
	public static void setDirectory(String directory) {
		ConfigMapElites.directory = directory;
	}

}
