import java.io.*;
import java.net.*;
import java.util.*;


public class Worker extends Thread{
    private String server_ip;
    private int server_port;
    private ArrayList<Waypoint> values;
    Compute computer = new Compute();


    public Worker(String ip, int port) {
        this.server_ip = ip;
        this.server_port = port;
    }

    public void run(){

        ObjectOutputStream out = null ;
        ObjectInputStream in = null ;
        Socket requestSocket = null ;


        try{


            /* Create socket for contacting the server on port 4320*/
            requestSocket = new Socket(server_ip, server_port);

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF("worker");
            out.flush();

            while(true) {

                Object val = (Object) in.readObject();
                values = (ArrayList<Waypoint>) val;


                Result temp = this.accumulateStats(values);                // Finds the intermediate results
                out.writeObject(temp);
                out.flush();

            }



        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
       } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close(); out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }



    public synchronized Result accumulateStats(ArrayList<Waypoint> working){

        //call computer to calculate intermidiate result

        Waypoint w1, w2;
        double distance = 0.0,  up_elevasion = 0.0, average_speed = 0.0;
        double time_diff = 0;


        for(int i = 1; i < working.size(); i++){
            w1 = working.get(i-1);
            w2 = working.get(i);
            distance += computer.distance(w1, w2);
            up_elevasion += computer.up_elevation(w1, w2);
            time_diff += computer.time_diff(w1, w2);
        }

        Result final_result = new Result(time_diff, distance, up_elevasion);

        return final_result;

    }


    public static void main(String args[]) throws InterruptedException {
        new Worker(args[0], Integer.parseInt(args[1])).start();
        new Worker(args[0], Integer.parseInt(args[1])).start();
        new Worker(args[0], Integer.parseInt(args[1])).start();
        new Worker(args[0], Integer.parseInt(args[1])).start();
        new Worker(args[0], Integer.parseInt(args[1])).start();
    }


}

