import java.io.*;
import java.net.*;
import java.util.*;

import javax.management.RuntimeErrorException;

public class Worker extends Thread{
    private String name;
    private String key;
    private ArrayList<Waypoint> values;
    private ArrayList<Result> worker_results;
    Compute computer = new Compute();

    /*public Worker(String name){
        this.name = name;
    }*/
    public Worker() {}

    public void run(){

        ObjectOutputStream out = null ;
        ObjectInputStream in = null ;
        Socket requestSocket = null ;


        try{

            String host = "localhost";
            /* Create socket for contacting the server on port 4320*/

            requestSocket = new Socket(host, 4320);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            //while(true) {
               System.out.println("E");
                //synchronized (this) {
                //key = in.readUTF();
                values = (ArrayList<Waypoint>) in.readObject();
                Result temp = this.accumulateStats(values);                // Finds the intermediate results
                out.writeObject(temp);                                                  // Write them to the corresponding socket
                out.flush();

                new Worker().start();

            /*if (key.equals("")) {
                //System.out.println("I dont know what im doing");
                //System.out.println("Im awake");
                // Here the worker gets the chunk and the key to find the results
                key = in.readUTF();
                values = (ArrayList<Waypoint>) in.readObject();


            }*/
                //}
            //}

                //get the waypoint list
                // steile sto action for workers to intermidiate result
                //apo action for workers sto master gia reduce


        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);// need it in readObject
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

        //HashMap<String, Result> results = new HashMap<>();
        Waypoint w1, w2;
        double distance = 0,  up_elevasion = 0,  time_diff = 0, average_speed = 0;
        Result final_result = new Result();

        for(int i = 0; i < working.size()-1; i++){
            w1 = working.get(i);
            w2 = working.get(i + 1);
            distance += computer.distance(w1, w2);
            up_elevasion += computer.up_elevasion(w1, w2);
            time_diff += computer.time_diff(w1, w2);
            average_speed += computer.average_speed(w1, w2);
        }
        final_result.setTotal_ascent(up_elevasion);
        final_result.setTotal_distance(distance);
        final_result.setTotal_time(time_diff);
        final_result.setAvg_speed(average_speed);

        return final_result;
    }

    /*public HashMap<String, Result> accumulateStats(String key, ArrayList<Waypoint> working){

        //call computer to calculate intermidiate result

        HashMap<String, Result> results = new HashMap<>();
        Waypoint w1, w2;
        double distance = 0,  up_elevasion = 0,  time_diff = 0, average_speed = 0;
        Result final_result = new Result();

        for(int i = 0; i < working.size()-1; i++){
            w1 = working.get(i);
            w2 = working.get(i + 1);
            distance += computer.distance(w1, w2);
            up_elevasion += computer.up_elevasion(w1, w2);
            time_diff += computer.time_diff(w1, w2);
            average_speed += computer.average_speed(w1, w2);
        }
        final_result.setTotal_ascent(up_elevasion);
        final_result.setTotal_distance(distance);
        final_result.setTotal_time(time_diff);
        final_result.setAvg_speed(average_speed);

        results.put(key, final_result);
        return results;
    }*/


    public static void main(String args[]){
        new Worker().start();
        new Worker().start();
        new Worker().start();
        new Worker().start();
        new Worker().start();
        /*new Worker("Worker1").start();
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

