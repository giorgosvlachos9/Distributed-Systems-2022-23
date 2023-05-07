import java.io.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;

public class ActionsForClients extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final int CHUNCK_SIZE = 3;
    private HashMap<String, ArrayList<Waypoint>> chuncks ;
    private ArrayList<Result> file_res;
    private HashMap<String, Result> file_results;
    /* Xreiazetai na dilwsoyme eite HashMap eite ArrayList gia ta results poy tha epistrafoyn apo toys workers, ta opoia meta tha pane ston reduce */
    private User current_user;
    private String fileId;
    private String gpx_file;
    private Result final_results;
    private final Object lock = new Object();

    //int[] num = new int[10];

    public ActionsForClients(Socket connection) {
        //for (int i =0;i<10;i++) this.num[i] = i;
        try {
            //this.client = connection;
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try{

            //Thread.sleep(500);        // Sleep in order to set the fileId

            this.gpx_file = in.readUTF();
            //this.setGpx_file(gpx);
            System.out.println(gpx_file);
            synchronized(lock) {
                lock.wait();
            }
            System.out.println("Im back");
            //Master.setFile_name(gpx_file);

            /*Reader file_reader = new Reader();
            current_user = file_reader.readgpx(gpx_file);

            //System.out.println("My name is " + this.fileId);
            //System.out.println("User wpts " + current_user.getWaypoints());

            //create chunks, map them via workers, once returned reduce on the master
            ArrayList<Waypoint> user_wpts = current_user.getWaypoints().get(current_user.getWaypoints().size()-1);
            user_wpts = current_user.getWaypoints().get(current_user.getWaypoints().size()-1);      //Gets the last List of waypoints that the user has
            this.setChuncks(createChuncks(user_wpts, CHUNCK_SIZE));
            HashMap<String, ArrayList<Waypoint>> chuncks_temp = (HashMap<String, ArrayList<Waypoint>>) this.chuncks.clone();
            Master.setUser_chuncks(chuncks_temp);

            synchronized(this){
                this.wait();
            }*/

            System.out.println("Job here finished! Bye");

        }catch (IOException e) {
            System.out.println("System threw IOException!");
            e.printStackTrace();
        }catch(InterruptedException e){
            System.out.println("System threw InterruptedException!");
            e.printStackTrace();

            //} catch (ClassNotFoundException e) {
            //  throw new RuntimeException(e);// need it in readObject
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }



    //public int[] getNum(){ return this.num; }

    // Methods for the chunks
    public synchronized void setChuncks(HashMap<String, ArrayList<Waypoint>> ch) { this.chuncks = ch; }

    public HashMap<String, ArrayList<Waypoint>> getChuncks(){
        HashMap<String, ArrayList<Waypoint>> temp_hmap = (HashMap<String, ArrayList<Waypoint>>) this.chuncks.clone();
        return temp_hmap;
    }

    public synchronized HashMap<String, ArrayList<Waypoint>> createChuncks(ArrayList<Waypoint> wpts, int size)  {

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
            String name = this.fileId + "." + i;
            temp.put(name, chunckies.get(i-1));
        }

        return temp;

    }

    // Methods for Users
    public User getUser(){
        User u = this.current_user;
        return u;
    }


    // For fileId
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getFileId() { return this.fileId; }

    // For file_results
    //public synchronized void setFileResults(HashMap<String, Result> temp) { this.file_results = temp; }

    // For final_results
    public synchronized void setFinal_results(Result final_results) {
        this.final_results = final_results;
        synchronized(lock){
            lock.notify();
        }
    }

    //public Result getFinal_results() { return final_results; }

    //public void setFile_res(ArrayList<Result> file_res) { this.file_res = file_res; }

    public String getGpxFile() {
        //synchronized(lock) {
          //  lock.notify();
        //}
        return this.gpx_file;

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



}

