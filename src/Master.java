import java.io.*;
import java.net.*;
import java.util.*;

public class Master{

    private static int serverport = 4320;
    static int NUM_WORKERS;
    static int client_counter = 1;
    static int worker_counter = 1;
    static int rr_counter = 1;
    //Socket that receives the requests
    private static ServerSocket serversocket = null;
    private static ServerSocket workerserversocket = null;
    //Socket that is sued to handle the connection
    private static Socket socketprovider;

    static ArrayList<User> users;
    static ArrayList<ArrayList<Waypoint>> user_chuncks = new ArrayList<>() ;
    static ArrayList<Result> user_intermediates = new ArrayList<>() ;
    //static HashMap<String, ArrayList<Waypoint>> user_chuncks = new HashMap<>();
    //static HashMap<String, Result> user_intermediates = new HashMap<>();
    // For synch
    static Object client_lock = new Object();
    static Object worker_lock = new Object();


    public static void main(String args[]) {
        int NUM_WORKER = Integer.parseInt(args[0]);
        new Master().openServer(NUM_WORKER);
    }

    void openServer(int w) {

        try {
            this.NUM_WORKERS = w;
            /* Create Server Socket */
            serversocket = new ServerSocket(serverport);
            rr_counter = 1;

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




    /*

    public static synchronized void setChuncks(HashMap<String, ArrayList<Waypoint>> h){ user_chuncks = h; }

    private static synchronized HashMap<String, ArrayList<Waypoint>> getMasterChuncks(){ return user_chuncks; }

    public static synchronized void setFile_name(String file_name) {
        Master.file_name = file_name;
    }

    public static synchronized void setUser_chuncks(HashMap<String, ArrayList<Waypoint>> user_chuncks) {
        user_chuncks = user_chuncks;
    }



    */





}


