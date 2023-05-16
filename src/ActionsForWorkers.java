import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class ActionsForWorkers extends Thread{
    private Socket worker;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private HashMap<String, ArrayList<Waypoint>> workload;
    //private ArrayList<ArrayList<Waypoint>> workload ;
    //private ArrayList<Result> temp_res = new ArrayList<>();         // To store each Result object coming from the worker
    private HashMap<String, Result> results;
    private ArrayList<Waypoint> chunck;
    private Result worker_result;
    private String worker_id;
    private final Object lock = new Object();




    public ActionsForWorkers(Socket connection, ArrayList<Waypoint> ch) {
        try {
            this.worker = connection;
            this.out = new ObjectOutputStream(worker.getOutputStream());
            this.in = new ObjectInputStream(worker.getInputStream());
            this.chunck = ch;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {

            Result res = this.accumulateStats(this.chunck);
            out.writeObject(res);
            out.flush();

        }catch (IOException e) {
            System.out.println("System threw IOException!");
            e.printStackTrace();
        //}catch(InterruptedException e){
            //System.out.println("System threw InterruptedException!");
            //e.printStackTrace();
        //} catch (ClassNotFoundException e) {
          //throw new RuntimeException(e);// need it in readObject
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public synchronized Result accumulateStats(ArrayList<Waypoint> working){

        //call computer to calculate intermidiate result

        //HashMap<String, Result> results = new HashMap<>();
        Compute computer = new Compute();
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



    /*public synchronized void setChunck(ArrayList<Waypoint> ch){
        this.chunck = ch;
        synchronized(lock){
            lock.notify();
        }
    }

    public synchronized Result getWorker_result() {
        synchronized(lock){
            lock.notify();
        }
        //new Worker().start();
        return this.worker_result;
    }


    // Gia to correct_size tha pername to megethos tis ArrayList me ta Waypoint wste na vevaiwthoyme pws exoyme apotelesma gia kathe chunck
    public synchronized ArrayList<Result> findClientResults(String file_name, int correct_size){
        ArrayList<Result> arr_temp = new ArrayList<>();

        for (Map.Entry<String, Result> entry : this.results.entrySet()){
            String key = entry.getKey();
            Result value = entry.getValue();
            // If the key and the file name are equal, add them to the temp HashMap to be returned and remove them from the local HashMap
             // that stores the results from all chuncks accumulated by the corresponding worker .
             //
            if (key.equals(file_name+".")){
                arr_temp.add(value);
                this.results.remove(key, value);
            }
            // If we have found the results from all chuncks
            if (!key.equals(file_name) && arr_temp.size() == correct_size) break;
        }
        return arr_temp;
    }

    public synchronized HashMap<String, Result> getResults() { return this.results ; }

    // Method used to add chunck to the worker's workload
     // key is the fileId and val is the chunck with the waypoints
     //
    public synchronized void addWorkload(String key , ArrayList<Waypoint> val){ this.workload.put(key, val); }*/


}

