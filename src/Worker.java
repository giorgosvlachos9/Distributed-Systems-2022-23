import java.io.*;
import java.net.*;
import java.util.*;

import javax.management.RuntimeErrorException;

public class Worker extends Thread{
    private String name;
    private String key;
    private ArrayList<Waypoint> values;
    Compute computer = new Compute();



    public Worker() {}

    public void run(){

        ObjectOutputStream out = null ;
        ObjectInputStream in = null ;
        Socket requestSocket = null ;


        try{

            //--------------------------------------------------------------------------
            // CHECK IF WE NEED TO CONCLUDE THE requestSocket VARIABLE IN THE WHILE LOOP
            //--------------------------------------------------------------------------

            /* Create socket for contacting the server on port 4320*/
            String host = "localhost";
            requestSocket = new Socket(host, 4320);

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF("worker");
            out.flush();

            System.out.println("E");
            while(true) {
                //synchronized (this) {
                //key = in.readUTF();
                //System.out.println(in == null);
                //System.out.println(in.readUTF());
                //synchronized(lock) {
                //String word = in.readUTF();
                Object val = (Object) in.readObject();
                values = (ArrayList<Waypoint>) val;

                //ActionForWorkers worker_thread = new ActionsForWorkers(

                Result temp = this.accumulateStats(values);                // Finds the intermediate results
                out.writeObject(temp);

            }
            //}


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

        //HashMap<String, Result> results = new HashMap<>();
        //Compute computer = new Compute();
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


    public static void main(String args[]) throws InterruptedException {
        new Worker().start();
        new Worker().start();
        new Worker().start();
        new Worker().start();
        new Worker().start();
    }


}

