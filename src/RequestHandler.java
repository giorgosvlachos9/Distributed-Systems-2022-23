import java.net.*;
import java.util.*;
import java.io.*;

public class RequestHandler extends Thread{
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private HashMap<String, ArrayList<Waypoint>> chuncks;
    private Result final_result;
    private ArrayList<Result> my_results = new ArrayList<>();
    private String CLIENT = "client";



    public RequestHandler(Socket connection){
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try{

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
                            //Master.client_counter++;
                            Master.incrementClientCounter();
                            Master.total_gpx_files++;
                            total_master_files = Master.total_gpx_files;
                            // Creates the chuncks
                            this.chuncks = this.createChuncks(client, cur_user.getWaypoints().get(0), 6);
                        }


                        while (true) {

                            synchronized (Master.user_intermediates) {
                                if (Master.user_intermediates.size() < chuncks.size()) continue;
                                while (Master.user_intermediates.size() != 0) { //&& iter.hasNext()) {
                                    // Gets the intermediate results
                                    Result res = Master.user_intermediates.get(0);
                                    Master.user_intermediates.remove(res);
                                    this.my_results.add(res);
                                    if (this.my_results.size() == this.chuncks.size()) {
                                        // if the results the client gets is equal to the size of his chuncks HashMap
                                        // then we have all the results we need
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
                                } else {
                                    Master.users.add(cur_user);
                                }
                                // Sets the total values for the user
                                Master.users.get(Master.users.indexOf(cur_user)).setTotals();

                                total_user_files = Master.users.get(Master.users.indexOf(cur_user)).getNumberOfFiles();

                                gpx_results = Master.users.get(Master.users.indexOf(cur_user)).createResultsString(final_result);

                                user_total_results = Master.users.get(Master.users.indexOf(cur_user)).createTotalsString();

                                User temp_user = getServerTotalResults();

                                server_total_results = temp_user.createTotalsString();

                            }

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

                } else {
                    String worker, checker;
                    synchronized (Master.worker_lock) {
                        // Gives the worker its name/id
                        worker = "worker" + Master.worker_counter;
                        Master.incrementWorkerCounter();

                    }
                    while(true){

                        synchronized(Master.user_chuncks) {
                            if (Master.user_chuncks.size() != 0) {

                                //Checking for Round Robin
                                synchronized (Master.worker_lock) {
                                    checker = "worker"+Master.rr_counter;
                                    if (worker.equals(checker)) {

                                        // Remove chunck
                                        ArrayList<Waypoint> ch = Master.user_chuncks.remove(0);
                                        // write value to worker
                                        out.writeObject(ch);
                                        out.flush();

                                        synchronized (Master.user_intermediates) {
                                            try {
                                                // Returning result from worker
                                                Object obj = in.readObject();
                                                Result interm_res = (Result) obj;
                                                // Adding result to the Master's list
                                                Master.user_intermediates.add(interm_res);
                                            } catch (ClassNotFoundException classNotFoundException) {
                                                classNotFoundException.printStackTrace();
                                            }
                                        }

                                        //String temp = "worker" + Master.worker_counter;
                                        if (Master.rr_counter < Master.worker_counter-1){
                                            // Increments the counter for round robin
                                            Master.incrementRRCounter();
                                        }
                                        if (Master.rr_counter == Master.worker_counter-1 && Master.worker_counter-1 < Master.NUM_WORKERS){
                                            // Here we reset the Round Robin counter if the number of workers is lesser
                                            // than the number of workers we can have

                                            Master.rr_counter = 1;
                                        }
                                        if (Master.rr_counter > Master.NUM_WORKERS) {
                                            // Here we reset the Round Robin counter if the Round Robin counter is bigger
                                            // than the number of workers we have to use
                                            Master.rr_counter = 1;
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

        double dist = 0.0, up_ele = 0.0, avg_sp = 0.0;
        double time = 0;

        for (int i = 0; i < worker_results.size(); i++){
            dist += worker_results.get(i).getTotal_distance();
            up_ele += worker_results.get(i).getTotal_ascent();
            time += worker_results.get(i).getTotal_time();
        }
        // Calculated in meters per minute
        avg_sp = (dist * 1000) / (time / 60);


        Result final_result = new Result();
        final_result.setTotal_time(time/60);
        final_result.setTotal_distance(dist);
        final_result.setTotal_ascent(up_ele);
        final_result.setAvg_speed(avg_sp);

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
