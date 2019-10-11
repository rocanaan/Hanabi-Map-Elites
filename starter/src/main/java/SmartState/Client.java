package SmartState;

import java.net.*;
import java.io.*;

public class Client {
    static Thread sent;
    static Thread receive;
    static Socket socket;
    //TODO game state object--> agent(Python) -->action(Java->Python)
    public static void main(String args[]){
            try {
                socket = new Socket("localhost",9999);
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            sent = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        BufferedReader stdIn =new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        while(true){
//                            System.out.println("Trying to read...");
                                String in = stdIn.readLine();
                                if(in!=null) {
                                	System.out.println(in);
                                }
                                out.print("Try from Java"+"\r\n");
                                out.flush();
//                                System.out.println("Message sent");
                            }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
            });
        sent.start();
        try {
            sent.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}