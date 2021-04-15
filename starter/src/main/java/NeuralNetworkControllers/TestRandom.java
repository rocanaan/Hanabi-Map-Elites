package NeuralNetworkControllers;

import java.util.Random;

public class TestRandom {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Random random=new Random();
		Long seed = random.nextLong();
		
		
		for (int i = 0; i<5; i++) {
			random.setSeed(seed);
			printTenRandomNumbers(random, i);

		}

	}
	
	public  static void printTenRandomNumbers(Random random, int ID) {
		for (int i = 0; i<10; i++) {
			System.out.println("ID " + ID + " random " + random.nextLong());
		}
	}

}
