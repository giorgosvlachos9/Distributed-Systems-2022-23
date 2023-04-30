import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class ActionsForWorkers extends Thread{
    private Socket worker;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private HashMap<String, ArrayList<Waypoint>> workload;
    //private ArrayList<ArrayList<Waypoint>> workload ;
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

    public void run(){
        try{

            this.sleep(500);             // Sleeping for seting the worker id

            while(true) {
                //System.out.println("Mpainei gia epe3ergasia");

                out.writeUTF("Koko");
                out.flush();

                //out.writeUTF("Gamw to spiti2");
                //out.flush();
                //System.out.println("G");
            }

        }catch (IOException e) {
            e.printStackTrace();
            //} catch (ClassNotFoundException e) {
            //throw new RuntimeException(e);// need it in readObject
        }catch(InterruptedException e){
            System.out.println("System threw InterruptedException!");
            e.printStackTrace();
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

    /* Method used to add chunck to the worker's workload
    * key is the fileId and val is the chunck with the waypoints
    * */
    public synchronized void addWorkload(String key , ArrayList<Waypoint> val){ this.workload.put(key, val); }

    public void notifyThread(){
        synchronized(lock){
            lock.notify();
        }
    }

}
