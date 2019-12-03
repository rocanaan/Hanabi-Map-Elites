package Evolution;

public class evoConfig {
	public static int getMaxGenerations() {
		return maxGenerations;
	}
	public static void setMaxGenerations(int maxGenerations) {
		evoConfig.maxGenerations = maxGenerations;
	}
	public static int getPopulationSize() {
		return populationSize;
	}
	public static void setPopulationSize(int populationSize) {
		evoConfig.populationSize = populationSize;
	}
	public static double getMutationRate() {
		return mutationRate;
	}
	public static void setMutationRate(double mutationRate) {
		evoConfig.mutationRate = mutationRate;
	}
	public static double getCrossoverRate() {
		return crossoverRate;
	}
	public static void setCrossoverRate(double crossoverRate) {
		evoConfig.crossoverRate = crossoverRate;
	}
	public static int getElitismCount() {
		return elitismCount;
	}
	public static void setElitismCount(int elitismCount) {
		evoConfig.elitismCount = elitismCount;
	}
	public static int getTournamentSize() {
		return tournamentSize;
	}
	public static void setTournamentSize(int tournamentSize) {
		evoConfig.tournamentSize = tournamentSize;
	}
	public static int getNumGames() {
		return numGames;
	}
	public static void setNumGames(int numGames) {
		evoConfig.numGames = numGames;
	}
	public static boolean isMirror() {
		return mirror;
	}
	public static void setMirror(boolean mirror) {
		evoConfig.mirror = mirror;
	}
	public static int getMinNumPlayers() {
		return minNumPlayers;
	}
	public static void setMinNumPlayers(int minNumPlayers) {
		evoConfig.minNumPlayers = minNumPlayers;
	}
	public static int getMaxNumPlayers() {
		return maxNumPlayers;
	}
	public static void setMaxNumPlayers(int maxNumPlayers) {
		evoConfig.maxNumPlayers = maxNumPlayers;
	}
	public static boolean isRulebaseStandard() {
		return rulebaseStandard;
	}
	public static void setRulebaseStandard(boolean rulebaseStandard) {
		evoConfig.rulebaseStandard = rulebaseStandard;
	}
	public static int maxGenerations = 5; // note : generation count starts at zero, so this is actually 1000

	public static int populationSize = 10;
	public static double mutationRate = 0.1;
	public static double crossoverRate = 0.9;
    public static int elitismCount = 20;
	public static  int tournamentSize = 5;
	public static int numGames = 10; // Number of games per agent per game size. There are 4 different game sizes, so this number is actually 4 times higher
	public static boolean mirror = true; // If true, will run in mirror mode. If false, will run in mixed mode, which is takes around 7 times as long
	static int minNumPlayers = 2;
	static int maxNumPlayers = 5;
	static boolean rulebaseStandard = false;

}
