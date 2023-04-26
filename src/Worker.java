import java.io.*;
import java.net.*;

import javax.management.RuntimeErrorException;

public class Worker extends Thread{
    private String name;
    private int port;

    public Worker(String name, int port_no){
        this.name = name;
        this.port = port_no;
    }

    public void run(){

        ObjectOutputStream out= null ;
        ObjectInputStream in = null ;
        Socket requestSocket= null ;

        try{

            System.out.println("E");
            String host = "localhost";
            /* Create socket for contacting the server on port 4321*/
            requestSocket = new Socket(host, 4320);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            System.out.println("F");

            String word =  in.readUTF();
            System.out.println("Server eipe " + word);

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


    public static void main(String args[]){
        new Worker("Worker1", 81).start();
        new Worker("Worker2", 82).start();
        new Worker("Worker3", 83).start();
        new Worker("Worker4", 84).start();
        new Worker("Worker5", 85).start();
    }


}
