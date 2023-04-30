import java.io.*;
import java.net.*;
import java.util.*;

public class Master{

    private static int clientserverport = 4321;
    private static int workerserverport = 4320;
    //Socket that receives the requests
    private static ServerSocket clientserversocket = null;
    private static ServerSocket workerserversocket = null;
    //Socket that is sued to handle the connection
    private static Socket clientsocketprovider;
    private static Socket workersocketprovider;

    //private static ArrayList<User> users;
    private static ArrayList<ActionsForWorkers> workers = new ArrayList<>();
    private static ArrayList<ArrayList<Waypoint>> chuncks ;
    private static int client_counter;
    private int worker_counter;
    private int rr_counter;

    private final Object client_lock = new Object();            // Might be an exaggeration
    private final Object worker_lock = new Object();


    public static void main(String args[]) {
        int NUM_WORKER = Integer.parseInt(args[0]);
        new Master().openServer(NUM_WORKER);
    }

    void openServer(int w) {

        try {
            int NUM_WORKERS = w;
            /* Create Server Socket */
            clientserversocket = new ServerSocket(4321);
            workerserversocket = new ServerSocket(4320);
            worker_counter = 1;
            client_counter = 0;
            rr_counter = 1;

            while (true) {
                /* Accept the connection */
                clientsocketprovider = clientserversocket.accept();
                workersocketprovider = workerserversocket.accept();
                /* Handle the request */
                ActionsForClients tc = new ActionsForClients(clientsocketprovider);
                tc.start();
                /* Giving IDs to the new ClientThreads */
                synchronized(client_lock){
                    this.client_counter++;
                    tc.setFileId("file"+this.client_counter);
                }
                ActionsForWorkers tw = new ActionsForWorkers(workersocketprovider);
                tw.start();

                //this.worker_counter++;


                //System.out.println("Client try for getting numbers");
                //.out.println(tc.getNum().length);
                //System.out.println("Successfull");

                //this.worker_counter = 1;
                //System.out.println("New client");

                // Mallon thelei synch
                synchronized(worker_lock) {
                    if (worker_counter <= NUM_WORKERS) {
                        tw.setWorker_id("worker"+this.worker_counter);
                        workers.add(tw);
                        System.out.println("New worker");
                        worker_counter++;
                        System.out.println(workers.size());
                    }
                }

                /* Gia to RoundRobin
                * Mono gia ton diamoirasmo twn chuncks stoys workers (?)
                * Uses the rr_counter to assign the workload to the corresponding worker
                * */
                synchronized(this) {
                    HashMap<String, ArrayList<Waypoint>> temp_chuncks = tc.getChuncks();
                    for (Map.Entry<String, ArrayList<Waypoint>> entry : temp_chuncks.entrySet()){
                        /* Add workload to the workers with the corresponding to rr_counter Id */
                        workers.get(rr_counter-1).addWorkload(entry.getKey(), entry.getValue());
                        this.rr_counter++;
                        /* If we reach the 3rd worker we set the counter back to 1, to got to the first one */
                        if (this.rr_counter > NUM_WORKERS) this.rr_counter = 1;
                    }


                }

                /*synchronized(this){
                    // returns the results i guess
                    // Ta setarei sto actionsforclients thread
                    // Kanei notify

                    // tc.notifyThread();
                    // Epistrefei ta results sto client mesw toy socket kai ola teleiwnoyn

                }*/


                /*synchronized (this) {                           // diamoirasmos twn chuncks sta threads twn workers
                    for (int i=0; i<clients.size();i++){
                        users.add(clients.get(i).getUser_thread());

                    }
                }*/

                /*synchronized(this) {                                // Diamoirasmos twn chunks (??????)
                    for (int i = 0; i < clients.size(); i++) {
                        chuncks = clients.get(i).getChuncks();
                        for (int j=0; j<NUM_WORKERS; j++) {
                            while (!chuncks.isEmpty()) {
                                
                            }
                        }
                    }
                }*/

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                clientsocketprovider.close();
                workersocketprovider.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    Result reduce(String fileId, ArrayList<Result> results){


        return null;
    }

}


