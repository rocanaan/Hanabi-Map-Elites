package NeuralNetworkControllers;

import java.io.*;
import java.util.*;

// Example code from https://stackoverflow.com/questions/4112470/java-how-to-both-read-and-write-to-from-process-thru-pipe-stdin-stdout
// see also https://jj09.net/interprocess-communication-python-java/
public class PythonPipeTesting {

    public static BufferedReader inp;
    public static BufferedWriter out;

    public static void print(String s) {
    System.out.println(s);
    }

    public static String pipe(String msg) {
    String ret;

    try {
        out.write( msg + "\n" );
        print("Sent message " + msg +"\n");
        out.flush();
        ret = inp.readLine();
        return ret;
    }
    catch (Exception err) {
    	print(err.toString());
    }
    return "";
    }



    public static void main(String[] args) {

    String s;
    //String cmd = "c:\\programs\\python\\python.exe d:\\a.py";
//    String cmd = "python /Users/rodrigocanaan/Dev/HanabiMapElites/Hanabi-Map-Elites/starter/src/main/java/NeuralNetworkControllers/pythonpipe.py";

    try {


        String dir = System.getProperty("user.dir");
        String cmd = "python ".concat(dir.concat("/src/main/java/NeuralNetworkControllers/ffnn.py"));
        print(cmd);
        Process p = Runtime.getRuntime().exec(cmd);

        inp = new BufferedReader( new InputStreamReader(p.getInputStream()) );
        out = new BufferedWriter( new OutputStreamWriter(p.getOutputStream()) );

        print( pipe("start 1 2 3") );
        print( pipe("RoteM") );

        pipe("quit");
        inp.close();
        out.close();
    }

    catch (Exception err) {
        err.printStackTrace();
    }
	    
	}
}
