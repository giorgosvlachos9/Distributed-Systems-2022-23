import java.io.*;
import java.net.*;
import java.util.*;

public class Master{

    private static int clientserverport = 4321;
    private static int workerserverport = 4320;
    private static final int CHUNCK_SIZE = 5;
    //Socket that receives the requests
    private static ServerSocket clientserversocket = null;
    private static ServerSocket workerserversocket = null;
    private static ServerSocket serversocket = null;
    //Socket that is sued to handle the connection
    private static Socket clientsocketprovider;
    private static Socket workersocketprovider;
    private static User cur_user;
    private static String file_name;

    //private static ArrayList<User> users;
    private static ArrayList<Socket> client_connections = new ArrayList<>();
    //private static ArrayList<ActionsForWorkers> workers = new ArrayList<>();
    private static ArrayList<Socket> workers = new ArrayList<>();
    //private static ArrayList<ArrayList<Waypoint>> chuncks ;
    static HashMap<String, ArrayList<Waypoint>> user_chuncks = new HashMap<>();
    private static ArrayList<Result> file_results = new ArrayList<>();
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
            worker_counter = 0;
            client_counter = 0;
            rr_counter = 0;

            // Connection and initilization of workers
            while(workers.size() < NUM_WORKERS ){

                workersocketprovider = workerserversocket.accept();
                //ActionsForWorkers tw = new ActionsForWorkers(workersocketprovider);
                //tw.start();

                if (workers.size() <= NUM_WORKERS ){

                    if (workersocketprovider != null) workers.add(workersocketprovider);
                    System.out.println("New worker " + workers.size());
                }
            }

            // Connection with clients;
            while (true) {

                /* Accept the connection */
                clientsocketprovider = clientserversocket.accept();
                this.client_connections.add(clientsocketprovider);
                Thread tc = new ActionsForClients(clientsocketprovider);
                tc.start();
                synchronized(tc){
                    client_counter++;
                    ((ActionsForClients) tc).setFileId("Client" + client_counter);
                }
                //while(tc.isAlive()){}
                synchronized(tc){
                    Reader file_reader = new Reader();
                    file_name = ((ActionsForClients) tc).getGpxFile();
                    cur_user = file_reader.readgpx(file_name);
                    ArrayList<Waypoint> user_wpts = cur_user.getWaypoints().get(cur_user.getWaypoints().size()-1);
                    user_wpts = cur_user.getWaypoints().get(cur_user.getWaypoints().size()-1);
                    HashMap<String, ArrayList<Waypoint>> chuncks = createChuncks(((ActionsForClients) tc).getFileId(), user_wpts, CHUNCK_SIZE);
                    //cur_user.setChuncks(chuncks);
                    System.out.println("Chunks size = " + user_chuncks.size());
                    //System.out.println("Size " + this.user_chuncks);
                }
                synchronized(tc){
                    HashMap<String, ArrayList<Waypoint>> temp = ;
                    Iterator<String> iterator = temp.keySet().iterator();
                    ArrayList<Result> f_results = new ArrayList<>();

                    while(iterator.hasNext()){
                        String key = iterator.next();
                        //while(workers.size() == 0) {
                         //   System.out.println("Stopped");
                        //}

                        Socket worker = workers.get(rr_counter);
                        ObjectOutputStream worker_out = new ObjectOutputStream(worker.getOutputStream());
                        ObjectInputStream worker_in = new ObjectInputStream(worker.getInputStream());

                        worker_out.writeObject(temp.get(key));
                        worker_out.flush();

                        Result res = (Result) worker_in.readObject();
                        f_results.add(res);
                        //rr_counter++;
                        //if (rr_counter == 3) rr_counter = 0;
                        //workers.remove(0);
                        //new Worker().start();
                    }
                    Result final_res = reduce(f_results);
                    printResults(final_res);

                }
                /*synchronized (this){
                    Reader gpx_reader = new Reader();
                    User cur_user = gpx_reader.readgpx(this.file_name);
                }*/
                /*//synchronized(this){
                    HashMap<String, ArrayList<Waypoint>> chuncks = getMasterChuncks();
                    if (chuncks != null) {
                        System.out.println("User's chuncks = " + chuncks.size());
                    }

                    //System.out.println("Welcome new user");
                    //System.out.println("User's waypoints = " + cur_user.getWaypoints().size());
                //}*/


            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException classNotFoundException){
            classNotFoundException.printStackTrace();
        } finally {
            try {
                clientsocketprovider.close();
                workersocketprovider.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    public static synchronized void setFile_name(String file_name) {
        Master.file_name = file_name;
    }

    private static synchronized HashMap<String, ArrayList<Waypoint>> getMasterChuncks(){ return user_chuncks; }

    public static synchronized void setUser_chuncks(HashMap<String, ArrayList<Waypoint>> user_chuncks) {
        user_chuncks = user_chuncks;
    }

    //public static synchronized void setChuncks(HashMap<String, ArrayList<Waypoint>> h){ user_chuncks = h; }

    /*public static void openWorkerServer(int num_wor){
        try {
            while(true){

                workersocketprovider = workerserversocket.accept();

                if (workers.size() <= num_wor ){
                    ActionsForWorkers tw = new ActionsForWorkers(workersocketprovider);
                    tw.start();
                    workers.add(tw);
                    System.out.println("New worker " + workers.size());
                }
                if (workers.size() == num_wor) break;
            }
            return ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/




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


    public synchronized HashMap<String, ArrayList<Waypoint>> createChuncks(String filename, ArrayList<Waypoint> wpts, int size)  {

        System.out.println("Eimaste mesa");
        HashMap<String, ArrayList<Waypoint>> temp = new HashMap<>();
        ArrayList<ArrayList<Waypoint>> chunckies = new ArrayList<>();
        int chuncksize = size;
        int helper = 1;                 // counter to input the data for each chunk
        ArrayList<Waypoint> chunck = new ArrayList<>();  //first chunck
        for (int i = 0; i < wpts.size(); i++) {
            chunck.add(wpts.get(i));
            if (helper == chuncksize){
                if (i+1<wpts.size()) {
                    chunck.add(wpts.get(i + 1));
                    helper = 1;
                    chunckies.add(chunck);
                    chunck.clear();
                    continue;
                }
            }
            helper++;

        }
        System.out.println("Ola kala");

        for (int i=1; i<=chunckies.size(); i++){
            String name = filename + "." + i;
            temp.put(name, chunckies.get(i-1));
        }

        return temp;

    }

    public Result reduce(ArrayList<Result> worker_results) {

        Result final_result = new Result();
        final_result = worker_results.get(0);

        if(worker_results.size() > 1) {
            for (int i = 1; i < worker_results.size(); i++) {
                final_result.addResults(worker_results.get(i));
            }
        }
        return final_result;
    }

    private synchronized void printResults(Result my_file_results){
        System.out.println("Total Time = " + my_file_results.getTotal_time() + "\nTotal Distance = " + my_file_results.getTotal_distance() +
                      "\nTotal Ascent = " + my_file_results.getTotal_ascent() + "\nAverage Speed = " + my_file_results.getAvg_speed());
    }
}


