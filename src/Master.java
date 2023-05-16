import java.io.*;
import java.net.*;
import java.util.*;

public class Master{

    private static int serverport = 4320;
    private static int workerserverport = 4321;
    private static final int CHUNCK_SIZE = 5;
    static int NUM_WORKERS;
    static int client_counter = 1;
    static int worker_counter = 1;
    static int rr_counter = 1;
    //Socket that receives the requests
    private static ServerSocket serversocket = null;
    private static ServerSocket workerserversocket = null;
    //Socket that is sued to handle the connection
    private static Socket socketprovider;
    private static User cur_user;
    private static String file_name;

    //private static ArrayList<User> users;
    private static ArrayList<Socket> client_connections = new ArrayList<>();
    //private static ArrayList<ActionsForWorkers> workers = new ArrayList<>();
    private static ArrayList<Socket> workers = new ArrayList<>();
    //private static ArrayList<ArrayList<Waypoint>> chuncks ;
    static HashMap<String, ArrayList<Waypoint>> user_chuncks = new HashMap<>();
    static HashMap<String, Result> user_intermediates = new HashMap<>();
    private static ArrayList<Result> file_results = new ArrayList<>();

    private final Object client_lock = new Object();            // Might be an exaggeration
    static Object worker_lock = new Object();


    public static void main(String args[]) {
        int NUM_WORKER = Integer.parseInt(args[0]);
        new Master().openServer(NUM_WORKER);
    }

    void openServer(int w) {

        try {
            this.NUM_WORKERS = w;
            /* Create Server Socket */
            serversocket = new ServerSocket(4320);
            rr_counter = 0;



            // Connection with clients;
            while (true) {

                /* Accept the connection */
                socketprovider = serversocket.accept();
                System.out.println("we in");

                RequestHandler req_handler = new RequestHandler(socketprovider);
                req_handler.start();




            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        //} catch (ClassNotFoundException classNotFoundException){
          //  classNotFoundException.printStackTrace();
        } finally {
            try {
                socketprovider.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    public static synchronized void incrementClientCounter() { client_counter++; }

    public static synchronized void incrementWorkerCounter() { worker_counter++; }

    public static synchronized void incrementRRCounter() { rr_counter++; }

    public static synchronized int getNumWorkers() { return NUM_WORKERS; }




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
    }

    public static synchronized void setChuncks(HashMap<String, ArrayList<Waypoint>> h){ user_chuncks = h; }

    private static synchronized HashMap<String, ArrayList<Waypoint>> getMasterChuncks(){ return user_chuncks; }

    public static synchronized void setFile_name(String file_name) {
        Master.file_name = file_name;
    }

    public static synchronized void setUser_chuncks(HashMap<String, ArrayList<Waypoint>> user_chuncks) {
        user_chuncks = user_chuncks;
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

    */





}


