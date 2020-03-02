package PostSimulationAnalyses;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.fossgalaxy.games.fireworks.ai.rule.Rule;

public class printChromosomes {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String fileName = "Pop3";
		
		try {
			// Reading the object from a file
			FileInputStream file = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(file);
			// Method for deserialization of object
			int[][][] chromosomes = (int[][][]) in.readObject();
			for (int[][] v : chromosomes) {
				for (int[] c : v) {
					Rule[] rules1 = new Rule[c.length];
					for (int i = 0; i < c.length; i++) {
//						rules1[i] = rb.ruleMapping(c[i]);
						System.out.print(c[i]);
						if (i < c.length-1) {
							System.out.print(",");
						}
					}
//					agents.add(rb.makeAgent(rules1));
					System.out.println("");
				}
			}
			System.out.println(chromosomes);
			in.close();
			file.close();
			System.out.println("Object has been deserialized ");
//			return true;
		} catch (IOException ex) {
			System.out.println(ex);
		} catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException is caught");
		}
	}

}
