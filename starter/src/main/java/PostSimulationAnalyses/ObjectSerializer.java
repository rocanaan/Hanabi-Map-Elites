package PostSimulationAnalyses;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectSerializer {
	
	
	public static boolean serializeObject(Object obj, String outputFileName, String identifier) {
		try {
			FileOutputStream file = new FileOutputStream(outputFileName);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(obj);
			out.close();
			file.close();
            System.out.println(identifier + " been serialized"); 
            return true;

		}
	    catch(IOException ex) 
        { 
            System.err.println("Failed to serialize " + identifier);
            System.err.println(ex);
            return false;
        } 
	}
}
