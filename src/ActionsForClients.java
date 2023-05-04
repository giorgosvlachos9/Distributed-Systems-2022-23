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

            String gpx_file = in.readUTF();
            Reader file_reader = new Reader();
            current_user = file_reader.readgpx(gpx_file);

            System.out.println("My name is " + this.fileId);

            //create chunks, map them via workers, once returned reduce on the master
            //ArrayList<Waypoint> user_wpts = current_user.getWaypoints().get(current_user.getWaypoints().size()-1);
            //user_wpts = current_user.getWaypoints().get(current_user.getWaypoints().size()-1);      //Gets the last List of waypoints that the user has

            //System.out.println(user_wpts.size());
            //HashMap<String, ArrayList<Waypoint>> chuncks_temp = createChuncks(user_wpts, CHUNCK_SIZE);
            //this.setChuncks(chuncks_temp);
            //System.out.println(this.chuncks.size());
            System.out.println("We here4");

            //this.waitThread();
            //System.out.println("Thread resumed");
            Thread.sleep(1000);
            //while (this.final_results != null){ }



            //final_results = this.reduce(this.file_res);
            //out.writeObject(final_results);
            //out.flush();

            System.out.println("Epistrefw arxeio oeo!");
            //out.writeObject(final_results);
            //out.flush();



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

    private void waitThread() throws InterruptedException {
        synchronized(this){
            this.wait();
        }
    }

    //public int[] getNum(){ return this.num; }

    // Methods for the chunks
    public synchronized void setChuncks(HashMap<String, ArrayList<Waypoint>> ch) { this.chuncks = ch; }

    public HashMap<String, ArrayList<Waypoint>> getChuncks(){
        HashMap<String, ArrayList<Waypoint>> temp_hmap = this.chuncks;
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

            if (helper > chuncksize){
                helper = 1;
                chunckies.add(chunck);
                chunck.clear();
                continue;
            }
            chunck.add(wpts.get(i));
            helper++;


            /*
            if (helper <= chuncksize) {
                chunck.add(wpts.get(i));
                helper++;
                if (i == wpts.size() && wpts.size() < chuncksize){          // if the size of waypoints is smaller than the chunck
                    chuncks.add(chunck);
                    break;
                }
                if (helper > chuncksize) {
                    helper = 1;
                    chunks.add(chunck);
                    chunck.clear();   //Empties out the current chunk
                }
            }*/
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

    public String getFileId() { return fileId; }

    // For file_results
    //public synchronized void setFileResults(HashMap<String, Result> temp) { this.file_results = temp; }

    // For final_results
    public void setFinal_results(Result final_results) { this.final_results = final_results; }

    //public Result getFinal_results() { return final_results; }

    //public void setFile_res(ArrayList<Result> file_res) { this.file_res = file_res; }

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

    public synchronized void notifyThread(){
        synchronized(this){
            this.notify();
        }
    }


    /*private User readgpx(String filename) throws IOException{
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        FileReader gpx = new FileReader(filename);
        BufferedReader gpx_handler = new BufferedReader(gpx);
        String online;
        online=gpx_handler.readLine();
        Waypoint wpt=new Waypoint();
        double latitude=0.0, longitude=0.0, elevation=0.0;
        String time="", date="", user="";
        while(online!=null){
            if(online.trim().contains("<gpx")){
                user = online.substring(online.indexOf("creator=")+9, online.indexOf(">")-1);
                //user=online.substring(online.indexOf("createor="+9,online.lastIndexOf(">")));
            }
            if(online.trim().contains("<wpt")){
                wpt=new Waypoint();
                latitude=Double.parseDouble(online.trim().substring(online.indexOf("lat")+5, online.indexOf("lon")-4));
                longitude=Double.parseDouble(online.trim().substring(online.indexOf("lon")+5, online.lastIndexOf(">")-3));
                //latitude=Long.parseLong(online.trim().substring(online.indexOf("lat")+5, online.indexOf("lon")-4));
                //longitude=Long.parseLong(online.trim().substring(online.indexOf("lon")+5, online.lastIndexOf(">")-3));
            }
            else if(online.trim().contains("<ele")){
                elevation=Double.parseDouble(online.substring(online.indexOf(">")+1, online.indexOf("</")));
            }else if(online.trim().contains("<time")){
                date=online.substring(online.indexOf(">"),online.indexOf("T"));
                time=online.substring(online.indexOf("T")+1,online.indexOf("Z"));
            }
            else if(online.trim().contains("</wpt")){
                wpt.setLatitude(latitude);
                wpt.setLongitude(longitude);
                wpt.setDate(date);
                wpt.setElevation(elevation);
                wpt.setTime(time);
                waypoints.add(wpt);
            }
            online =gpx_handler.readLine();
        }

        User new_user = new User(user);                 //User creation
        new_user.addWaypoints(waypoints);

        gpx_handler.close();
        return new_user;
    }*/
}

