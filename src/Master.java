import java.io.*;
import java.net.*;
import java.util.*;

public class Master{

    private static int clientserverport = 4321;
    private static int workerserverport = 4320;
    //Socket that receives the requests
    private static ServerSocket clientserversocket = null;
    private static ServerSocket workerserversocket = null;
    private static ServerSocket serversocket = null;
    //Socket that is sued to handle the connection
    private static Socket clientsocketprovider;
    private static Socket workersocketprovider;
    private static Socket socketprovider;

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
            serversocket = new ServerSocket(4321);
            //clientserversocket = new ServerSocket(4321);
            workerserversocket = new ServerSocket(4320);
            worker_counter = 1;
            client_counter = 0;
            rr_counter = 1;

            while (true) {
                /* Accept the connection */
                socketprovider = serversocket.accept();
                //workersocketprovider = workerserversocket.accept();
                //clientsocketprovider = clientserversocket.accept();

                /* Handle the request */
                Thread tc = new ActionsForClients(socketprovider);
                tc.start();

                this.openWorkers(NUM_WORKERS);

                // Mallon thelei synch


                /* Giving IDs to the new ClientThreads */
                synchronized(this){
                    client_counter++;
                    ((ActionsForClients) tc).setFileId("file"+this.client_counter);
                    //tc.notifyThread();
                }

                //while(tc.isAlive()) {
                // runs the thread method
                //}


                //this.worker_counter++;


                //System.out.println("Client try for getting numbers");
                //.out.println(tc.getNum().length);
                //System.out.println("Successfull");

                //this.worker_counter = 1;
                //System.out.println("New client");





                // Gia to RoundRobin
                 // Mono gia ton diamoirasmo twn chuncks stoys workers (?)
                 // Uses the rr_counter to assign the workload to the corresponding worker
                 //
                /*synchronized(this) {
                    while (tc.isAlive()) {
                        User current_user = tc.getUser();
                        ArrayList<Waypoint> user_wpts = current_user.getWaypoints().get(current_user.getWaypoints().size() - 1);
                        HashMap<String, ArrayList<Waypoint>> chuncks_temp = tc.createChuncks(user_wpts, 3);


                        //HashMap<String, ArrayList<Waypoint>> temp_chuncks = tc.getChuncks();
                        for (Map.Entry<String, ArrayList<Waypoint>> entry : chuncks_temp.entrySet()) {
                            // Add workload to the workers with the corresponding to rr_counter Id
                            //workers.get(rr_counter-1).notifyThread();
                            workers.get(rr_counter - 1).addWorkload(entry.getKey(), entry.getValue());
                            this.rr_counter++;
                            // If we reach the 3rd worker we set the counter back to 1, to got to the first one
                            if (this.rr_counter > NUM_WORKERS) this.rr_counter = 1;
                        }
                    }
                }

                synchronized(this){
                    int file_size = tc.getChuncks().size();
                    int worker_chuncks;
                    // Checks if the file contains chuncks less than the amount of workers
                    if (file_size < NUM_WORKERS) worker_chuncks = 1;
                    else worker_chuncks = (int) Math.floor(file_size / NUM_WORKERS);

                    int worker = 0;
                    ArrayList<ArrayList<Result>> worker_res_together = new ArrayList<>();
                    ArrayList<Result> worker_res;

                    while (file_size != 0){                         // Oso yparxoyn akoma Results na vrethoyne
                        if (file_size % (NUM_WORKERS-worker) != 0){
                            worker_chuncks += 1;
                            worker_res = workers.get(worker).findClientResults(tc.getFileId(), worker_chuncks);
                        }
                        else{
                            worker_chuncks = (int) file_size / NUM_WORKERS;
                            worker_res = workers.get(worker).findClientResults(tc.getFileId(), worker_chuncks);
                        }
                        worker++;
                        if (worker == 3) worker = 0;
                        file_size = file_size - worker_chuncks;
                        worker_res_together.add(worker_res);
                    }
                    ArrayList<Result> file_res = this.orderResults(worker_res_together);
                    Result fin_res = tc.reduce(file_res);
                    tc.setFinal_results(fin_res);
                    // Setaroyme ta dedomena sto ActionsForClients thread

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

    /* Orders results to one ArrayList to send to the client thread */
    private ArrayList<Result> orderResults(ArrayList<ArrayList<Result>> nested_res){
        ArrayList<Result> fin_res = new ArrayList<>();
        for (int i=0; i<nested_res.size(); i++){
            for (int j=0; j<nested_res.get(i).size(); j++){
                fin_res.add(nested_res.get(i).get(j));
            }
        }

        return fin_res;
    }

    private synchronized void openWorkers(int num_wor){

        try {
            while(true){
                workersocketprovider = workerserversocket.accept();
                synchronized(this) {
                    Thread tw = new ActionsForWorkers(workersocketprovider);
                    tw.start();
                    if (worker_counter <= num_wor) {
                        ((ActionsForWorkers) tw).setWorker_id("worker"+this.worker_counter);
                        workers.add((ActionsForWorkers)tw);
                        System.out.println("New worker");
                        worker_counter++;
                        System.out.println(workers.size());
                    }
                }
                System.out.println("Number of workers = " + this.workers.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


