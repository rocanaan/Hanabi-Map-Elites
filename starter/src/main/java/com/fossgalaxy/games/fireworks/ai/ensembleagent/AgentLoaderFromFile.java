package com.fossgalaxy.games.fireworks.ai.ensembleagent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import com.fossgalaxy.games.fireworks.ai.rule.Rule;

import Evolution.Rulebase;

public class AgentLoaderFromFile {
	


	//TODO: Extract into a class that gets the integers from file, then one that builds the agents. See chromosome similarity analyzer
	public static ArrayList<HistogramAgent> makeAgentsFromFile(String fileName, int sizeDim1, int sizeDim2, boolean rulebaseStandard) {
		fileName = System.getProperty("user.dir")+File.separator+fileName;
        Rulebase rb = new Rulebase(rulebaseStandard);
        String thisLine;
        ArrayList<HistogramAgent> agents = new ArrayList<HistogramAgent>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((thisLine = br.readLine()) != null) {
                //thisLine = thisLine.substring(1, thisLine.length() - 2);
            		thisLine = thisLine.replaceAll(" ", "");
            		thisLine = thisLine.replaceAll("\t", "");
            		String[] c = thisLine.split(",");
            		int[] chromossome = new int[c.length];
                for (int i = 0; i < c.length; i++) {
                    chromossome[i] = Integer.parseInt(c[i]);   
                }
               
                Rule[] rules1 = new Rule[chromossome.length];
                for (int i = 0; i < chromossome.length; i++) {

                    if (rulebaseStandard) {
                        rules1[i] = rb.ruleMapping(chromossome[i]);
                    } else {
                        rules1[i] = rb.ruleMapping(chromossome[i]);

                    }

                }
                agents.add(new HistogramAgent(rb.makeAgent(rules1)));

            }
            br.close();
        } catch (Exception e) {
        		System.err.println(e);;
        }
      
//        return agents;
//        ArrayList<HistogramAgent> agents = makeAgentsFromFile(fileName, rulebaseStandard);
		if(agents !=null) {
			//System.out.println("not null" + agents.size());
		}
		else {
			//System.out.println("null");
		}
		
//		Vector<AgentPlayer> agentPlayers = new Vector<AgentPlayer>();
//		for (int i = 0; i<sizeDim1; i++) {
//			for (int j = 0; j<sizeDim2; j++) {
//				
//				HistogramAgent ha = agents.get(j + sizeDim1*i);
//				agentPlayers.add(new AgentPlayer("report agent [ " + i + " , " + j + "]", ha));
//			}
//		}
		return agents;
    }
}
