import java.io.*;
import java.net.*;

import javax.management.RuntimeErrorException;

public class Worker extends Thread{
    private String name;

    public Worker(String name){
        this.name = name;
    }

    public void run(){

        ObjectOutputStream out= null ;
        ObjectInputStream in = null ;
        Socket requestSocket= null ;

        try{

            while(true) {

                System.out.println("E");
                String host = "localhost";
                /* Create socket for contacting the server on port 4320*/
                requestSocket = new Socket(host, 4320);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                //System.out.println("F");

                int number = in.read();
                System.out.println("My number equals to " + number);

                //String word = in.readUTF();
                //System.out.println("Server eipe " + word);
            }

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close(); out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    public void accumulateStats(){

    }


    public static void main(String args[]){
        new Worker("Worker1").start();
        new Worker("Worker2").start();
        new Worker("Worker3").start();
        new Worker("Worker4").start();
        new Worker("Worker5").start();
        /*int i = 1;
        while (true){
            new Worker("Worker"+i).start();
            i++;
        }*/
    }


}
