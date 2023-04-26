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
    private static boolean isStopped = false;
    private static ArrayList<User> users;
    private static ArrayList<ActionsForClients> clients = new ArrayList<>();
    private static ArrayList<ActionsForWorkers> workers = new ArrayList<>();
    private static ArrayList<ArrayList<Waypoint>> chuncks ;


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
            int counter = 1;

            while (true) {
                /* Accept the connection */
                clientsocketprovider = clientserversocket.accept();
                workersocketprovider = workerserversocket.accept();
                /* Handle the request */
                ActionsForClients tc = new ActionsForClients(clientsocketprovider);
                tc.start();
                ActionsForWorkers tw = new ActionsForWorkers(workersocketprovider);
                tw.start();

                //System.out.println("Client try for getting numbers");
                //.out.println(tc.getNum().length);
                //System.out.println("Successfull");

                clients.add(tc);
                System.out.println("New client");

                // Mallon thelei synch
                synchronized(this) {
                    if (counter <= NUM_WORKERS) {
                        workers.add(tw);
                        System.out.println("New worker");
                        counter++;
                        System.out.println(workers.size());
                    }
                }

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

    void removeClient(ActionsForClients client){ clients.remove(client); }

}


