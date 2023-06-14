import java.io.*;
import java.net.*;
import java.util.*;

public class Master{

    private static int serverport = 4320;
    static int NUM_WORKERS;
    static int client_counter = 1;
    static int worker_counter = 1;
    static int rr_counter = 1;
    static int total_gpx_files = 0;
    //Socket that receives the requests
    private static ServerSocket serversocket = null;
    private static ServerSocket workerserversocket = null;
    //Socket that is sued to handle the connection
    private static Socket socketprovider;

    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<ArrayList<Waypoint>> user_chuncks = new ArrayList<>() ;
    static ArrayList<Result> user_intermediates = new ArrayList<>() ;

    // For synch
    static Object client_lock = new Object();
    static Object worker_lock = new Object();


    public static void main(String args[]) {
        int NUM_WORKER = Integer.parseInt(args[args.length-1]);
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



}


