import java.net.*;
import java.util.*;
import java.io.*;

public class RequestHandler extends Thread{
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket active_connection;
    private int worker_counter = 1;
    private HashMap<String, ArrayList<Waypoint>> chuncks;
    private Result final_result;
    private ArrayList<Result> my_results = new ArrayList<>();
    private ArrayList<Waypoint> val = new ArrayList<>();

    public RequestHandler(Socket connection){
        try {
            this.active_connection = connection;
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try{


                System.out.println("Mpikame");
                // To see from whom the request comes from
                String word = in.readUTF();
                //  Checks if the requests comes from a client or a worker
                if (word.equals("client")) {

                    String file = in.readUTF();         // Got the file
                    Reader gpx_reader = new Reader();
                    User cur_user = gpx_reader.readgpx(file);
                    String client;
                    synchronized (Master.client_lock) {
                        client = "Client" + Master.client_counter;
                        System.out.println(client);
                        Master.client_counter++;
                        // Creates the chuncks
                        this.chuncks = this.createChuncks(client, cur_user.getWaypoints().get(0), 6);
                        System.out.println(this.chuncks.size());
                    }

                    while(true) {

                        synchronized (Master.user_intermediates) {
                            //Iterator<Map.Entry<String, Result>> iter = Master.user_intermediates.entrySet().iterator();
                            //System.out.println("Im in the synch block for User_Intermediates and this is their size = " + Master.user_intermediates.size());
                            if (Master.user_intermediates.size() < chuncks.size()) continue;
                            while (Master.user_intermediates.size() != 0) { //&& iter.hasNext()) {
                                System.out.println("got a result to add to my list");
                                // Gets the intermediate results
                                Result res = Master.user_intermediates.get(0);
                                Master.user_intermediates.remove(res);
                                this.my_results.add(res);
                                if (this.my_results.size() == this.chuncks.size()) {
                                    System.out.println("I ju got my results!");
                                    System.out.println("This them size = " + this.my_results.size());
                                    break;
                                }
                            }
                        }
                        this.final_result = this.reduce(this.my_results);
                        synchronized (Master.users){
                            if (Master.users.contains(cur_user)){
                                Master.users.get(Master.users.indexOf(cur_user)).addResults(final_result);
                                System.out.println("User has been registered to the server. Adding results its list!");
                            }else{
                                cur_user.addResults(this.final_result);
                                Master.users.add(cur_user);
                                System.out.println("New user added!");
                            }
                            System.out.println("Size of users array = " + Master.users.size());
                        }
                        out.writeUTF(cur_user.getId());
                        out.flush();
                        out.writeObject(this.final_result);
                        out.flush();
                        break;
                    }
                    synchronized(Master.user_intermediates){
                        System.out.println("User_intermed size = " + Master.user_intermediates.size());
                    }



                } else {
                    String worker, checker;
                    synchronized (Master.worker_lock) {
                        //if (Master.worker_counter > Master.NUM_WORKERS) active_connection.close();
                        worker = "worker" + Master.worker_counter;
                        Master.incrementWorkerCounter();
                        System.out.println(worker);
                        //out.writeUTF("HELLO");
                        //out.flush();
                    }
                    while(true){

                        synchronized(Master.user_chuncks) {
                            if (Master.user_chuncks.size() != 0) { //&& iterator.hasNext()){

                                val = Master.user_chuncks.get(0);

                                //Checking for Round Robin
                                synchronized (Master.worker_lock) {
                                    checker = "worker"+Master.rr_counter;
                                    if (worker.equals(checker)) {
                                        // Increments the counter for round robin
                                        Master.rr_counter++;
                                        if (Master.rr_counter > Master.NUM_WORKERS) {
                                            System.out.println("Reset rr counter");
                                            Master.rr_counter = 1;
                                        }
                                        // Remove chunck
                                        System.out.println("Removes chunck from chunck list");
                                        Master.user_chuncks.remove(val);
                                        // write value to worker
                                        System.out.println("Sending chunck to the worker");
                                        System.out.println("I am " + worker);
                                        System.out.println("I am checker" + checker);
                                        out.writeObject(val);
                                        out.flush();

                                        synchronized (Master.user_intermediates) {
                                            try {
                                                System.out.println("Got an intermediate");
                                                // Returning result from worker
                                                Object obj = in.readObject();
                                                Result interm_res = (Result) obj;
                                                Master.user_intermediates.add(interm_res);
                                                //Master.user_intermediates.put(key, interm_res);
                                                System.out.println("Got result back");
                                                System.out.println("Intermidiates List size = " + Master.user_intermediates.size());
                                            } catch (ClassNotFoundException classNotFoundException) {
                                                classNotFoundException.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }       // For the synchronization of the user chuncks

                    }       // For the infinite while loop
                }


        }catch (IOException e) {
            System.out.println("System threw IOException!");
            e.printStackTrace();

        } finally {
            try {
                System.out.println("closing");
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private synchronized HashMap<String, ArrayList<Waypoint>> createChuncks(String n, ArrayList<Waypoint> wpts, int size) {

        System.out.println("Eimaste mesa");
        HashMap<String, ArrayList<Waypoint>> temp = new HashMap<>();
        ArrayList<ArrayList<Waypoint>> chunckies = new ArrayList<>();
        int chuncksize = size;
        int helper = 1;                 // counter to input the data for each chunk
        ArrayList<Waypoint> chunck = new ArrayList<>();  //first chunck
        for (int i = 0; i < wpts.size(); i++) {
            chunck.add(wpts.get(i));
            if (helper == chuncksize) {
                if (i + 1 < wpts.size()) {
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

        synchronized (Master.user_chuncks) {
            for (int i = 1; i <= chunckies.size(); i++) {
                String name = n + "." + i;

                Master.user_chuncks.add(chunckies.get(i - 1));

                temp.put(name, chunckies.get(i - 1));
            }
        }


        return temp;

    }

    private Result reduce(ArrayList<Result> worker_results) {

        Result final_result = new Result();
        final_result = worker_results.get(0);

        if(worker_results.size() > 1) {
            for (int i = 1; i < worker_results.size(); i++) {
                final_result.addResults(worker_results.get(i));
            }
        }
        return final_result;
    }

}
