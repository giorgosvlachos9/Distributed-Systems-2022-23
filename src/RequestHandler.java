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
                // To see what the request wants

                if (word.equals("client")) {


                    //if (process.equals("upload")) {
                        String file = in.readUTF();
                        Reader gpx_reader = new Reader();
                        User cur_user = gpx_reader.readgpx(file);
                        System.out.println(cur_user.getId());

                        String client;
                        int total_master_files;
                        synchronized (Master.client_lock) {
                            client = "Client" + Master.client_counter;
                            System.out.println(client);
                            Master.client_counter++;
                            Master.total_gpx_files++;
                            total_master_files = Master.total_gpx_files;
                            // Creates the chuncks
                            this.chuncks = this.createChuncks(client, cur_user.getWaypoints().get(0), 6);
                            System.out.println(this.chuncks.size());
                        }


                        while (true) {

                            synchronized (Master.user_intermediates) {
                                if (Master.user_intermediates.size() < chuncks.size()) continue;
                                while (Master.user_intermediates.size() != 0) { //&& iter.hasNext()) {
                                    System.out.println("got a result to add to my list");
                                    // Gets the intermediate results
                                    Result res = Master.user_intermediates.get(0);
                                    System.out.println("Removing from user_intermed to add to the client' list and print user interm size " + Master.user_intermediates.size());
                                    res.printEndResults(false);
                                    System.out.println("-----------------------------------------------------");
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

                            String gpx_results, user_total_results, server_total_results;
                            int total_user_files;
                            synchronized (Master.users) {
                                cur_user.addResults(this.final_result);
                                if (Master.users.contains(cur_user)) {
                                    Master.users.get(Master.users.indexOf(cur_user)).addResults(final_result);
                                    // Set the totals from all the files
                                    System.out.println("User has been registered to the server. Adding results its list!");
                                } else {
                                    Master.users.add(cur_user);
                                    System.out.println("New user added!");
                                }
                                // Sets the total values for the user
                                Master.users.get(Master.users.indexOf(cur_user)).setTotals();

                                total_user_files = Master.users.get(Master.users.indexOf(cur_user)).getNumberOfFiles();

                                gpx_results = Master.users.get(Master.users.indexOf(cur_user)).createResultsString(final_result);

                                user_total_results = Master.users.get(Master.users.indexOf(cur_user)).createTotalsString();

                                User temp_user = getServerTotalResults();
                                server_total_results = temp_user.createTotalsString();

                                System.out.println("Size of users array = " + Master.users.size());
                            }
                            //String username = cur_user.

                            out.writeUTF(cur_user.getId());
                            out.flush();
                            out.writeUTF(gpx_results);
                            out.flush();
                            out.writeUTF(user_total_results);
                            out.flush();
                            out.writeUTF(server_total_results);
                            out.flush();
                            out.writeInt(total_user_files);
                            out.flush();
                            out.writeInt(total_master_files);
                            out.flush();
                            break;
                        }
                    //}
                    /*else{

                        String search_name = in.readUTF();
                        User cur_user = new User(search_name);

                        while (true) {

                            synchronized (Master.users) {
                                while (Master.users.size() == 0) { }
                                total_master_files = Master.total_gpx_files;
                                synchronized (Master.client_lock){

                                }

                                if (Master.users.contains(cur_user)) {
                                    total_user_files = Master.users.get(Master.users.indexOf(cur_user)).getNumberOfFiles();

                                    gpx_results = Master.users.get(Master.users.indexOf(cur_user)).createResultsString(final_result);

                                    user_total_results = Master.users.get(Master.users.indexOf(cur_user)).createTotalsString();

                                    User temp_user = getServerTotalResults();
                                    server_total_results = temp_user.createTotalsString();
                                }
                            }
                            break;
                        }


                    }*/




                } else {
                    String worker, checker;
                    synchronized (Master.worker_lock) {
                        worker = "worker" + Master.worker_counter;
                        Master.incrementWorkerCounter();
                        System.out.println(worker);

                    }
                    while(true){


                        synchronized(Master.user_chuncks) {
                            if (Master.user_chuncks.size() != 0) {

                                //val = Master.user_chuncks.get(0);

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
                                        System.out.println("Removes chunck from chunck list and prints chuncks list size ");
                                        System.out.println(Master.user_chuncks.size());
                                        ArrayList<Waypoint> ch = Master.user_chuncks.remove(0);
                                        System.out.println("User chuncks size after remove : " + Master.user_chuncks.size());
                                        // write value to worker
                                        System.out.println("Sending chunck to the worker");
                                        System.out.println("I am " + worker);
                                        System.out.println("I am checker" + checker);
                                        out.writeObject(ch);
                                        out.flush();

                                        synchronized (Master.user_intermediates) {
                                            try {
                                                System.out.println("Got an intermediate");
                                                // Returning result from worker
                                                Object obj = in.readObject();
                                                Result interm_res = (Result) obj;
                                                Master.user_intermediates.add(interm_res);
                                                //Master.user_intermediates.put(key, interm_res);
                                                System.out.println(worker);
                                                interm_res.printEndResults(true);
                                                System.out.println("\nGot result back");
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


        } finally{
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
                    ArrayList<Waypoint> temp_chunck = new ArrayList<>(chunck);
                    chunckies.add(temp_chunck);
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

        double dist = 0.0, up_ele = 0.0;
        double time = 0;

        for (int i = 0; i < worker_results.size(); i++){
            dist += worker_results.get(i).getTotal_distance();
            up_ele += worker_results.get(i).getTotal_ascent();
            time += worker_results.get(i).getTotal_time();
        }
        double avg_speed = dist / time;

        Result final_result = new Result();
        final_result.setTotal_time(time/60);
        final_result.setTotal_distance(dist);
        final_result.setTotal_ascent(up_ele);
        final_result.setAvg_speed(avg_speed);

        return final_result;

    }

    private synchronized User getServerTotalResults(){
        User temp_user = new User("temp");
        double t = 0.0, d = 0.0, e = 0.0;
        synchronized (Master.users){
            for (User u : Master.users){
                Result t_r = u.getTotal_res();
                t += t_r.getTotal_time();
                d += t_r.getTotal_distance();
                e += t_r.getTotal_ascent();
            }
            temp_user.setTotalResultWithParameters(t, d, e);
        }
        return temp_user;
    }

}
