import java.io.*;
import java.net.*;
import java.util.*;


public class Worker extends Thread{
    private ArrayList<Waypoint> values;
    Compute computer = new Compute();


    public Worker() {}

    public void run(){

        ObjectOutputStream out = null ;
        ObjectInputStream in = null ;
        Socket requestSocket = null ;


        try{


            /* Create socket for contacting the server on port 4320*/
            String host = "192.168.56.1";
            requestSocket = new Socket(host, 4320);

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeUTF("worker");
            out.flush();

            System.out.println("E");
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
        Result final_result = new Result();

        for(int i = 1; i < working.size(); i++){
            w1 = working.get(i-1);
            w2 = working.get(i);
            distance += computer.distance(w1, w2);
            up_elevasion += computer.up_elevation(w1, w2);
            time_diff += computer.time_diff(w1, w2);
        }

        average_speed = distance / time_diff;

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

