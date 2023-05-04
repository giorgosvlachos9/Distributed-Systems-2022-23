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
    private Result worker_result;
    private String worker_id;
    private final Object lock = new Object();




    public ActionsForWorkers(Socket connection) {
        try {
            this.worker = connection;
            this.out = new ObjectOutputStream(worker.getOutputStream());
            this.in = new ObjectInputStream(worker.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {

            Thread.sleep(500);             // Sleeping for seting the worker id

            System.out.println("Mpainei gia epe3ergasia");
            while (true) {
                //for all chunks send based on round robin workload

                String key_temp = "";
                if (this.workload != null) {

                    /*Iterator<String> iter = this.workload.keySet().iterator();

                    while (iter.hasNext()) {
                        key_temp = iter.next();
                        ArrayList<Waypoint> val_temp = this.workload.get(key_temp);
                        this.workload.remove(key_temp, val_temp);
                        System.out.println("Sending data to my worker!");
                        out.writeUTF(key_temp);
                        out.flush();
                        out.writeObject(val_temp);
                        out.flush();
                        System.out.println("Receiving data from my worker!");

                        worker_result = (Result) in.readObject();
                        results.put(key_temp, worker_result);

                    }*/
                }

                    /*for (Map.Entry<String, ArrayList<Waypoint>> entry : this.workload.entrySet()) {
                        key_temp = entry.getKey();
                        ArrayList<Waypoint> val_temp = entry.getValue();
                        // Successfully removes the chunck from the workload
                        this.workload.remove(key_temp, val_temp);
                        // Starts sending data to the responding worker
                        System.out.println("Sending data to my worker!");
                        //out.writeUTF(key_temp);
                        //out.flush();
                        out.writeObject(val_temp);
                        out.flush();

                        System.out.println("Receiving data from my worker!");
                        worker_result = (Result) in.readObject();
                        results.put(key_temp, worker_result);
                    }*/
                //}
                //out.writeUTF("Koko");
                //out.flush();

                //out.writeUTF("Gamw to spiti2");
                //out.flush();
                //System.out.println("G");
                //}
                //System.out.println("Vgainei");

            }
        //}catch (IOException e) {
          //  System.out.println("System threw IOException!");
           // e.printStackTrace();
        }catch(InterruptedException e){
            System.out.println("System threw InterruptedException!");
            e.printStackTrace();

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

    public void setWorker_id(String worker_id) { this.worker_id = worker_id; }

    public String getWorker_id(){ return this.worker_id; }

    /* Gia to correct_size tha pername to megethos tis ArrayList me ta Waypoint wste na vevaiwthoyme pws exoyme apotelesma gia kathe chunck*/
    public synchronized ArrayList<Result> findClientResults(String file_name, int correct_size){
        ArrayList<Result> arr_temp = new ArrayList<>();

        for (Map.Entry<String, Result> entry : this.results.entrySet()){
            String key = entry.getKey();
            Result value = entry.getValue();
            /* If the key and the file name are equal, add them to the temp HashMap to be returned and remove them from the local HashMap
             * that stores the results from all chuncks accumulated by the corresponding worker .
             */
            if (key.equals(file_name+".")){
                arr_temp.add(value);
                this.results.remove(key, value);
            }
            /* If we have found the results from all chuncks */
            if (!key.equals(file_name) && arr_temp.size() == correct_size) break;
        }
        return arr_temp;
    }

    public synchronized HashMap<String, Result> getResults() { return this.results ; }

    /* Method used to add chunck to the worker's workload
     * key is the fileId and val is the chunck with the waypoints
     * */
    public synchronized void addWorkload(String key , ArrayList<Waypoint> val){ this.workload.put(key, val); }

    public synchronized void notifyThread(){
        synchronized(lock){
            lock.notify();
        }
    }

}

