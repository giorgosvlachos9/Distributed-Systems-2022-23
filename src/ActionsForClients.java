import java.io.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;

public class ActionsForClients extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    final int CHUNCK_SIZE = 3;
    ArrayList<ArrayList<Waypoint>> chuncks ;
    //int[] num = new int[10];

    public ActionsForClients(Socket connection) {
        //for (int i =0;i<10;i++) this.num[i] = i;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try{

            String gpx_file = in.readUTF();
            User current_user = readgpx(gpx_file);

            System.out.println("Ola good");


            //create chunks, map them via workers, once returned reduce on the master
            ArrayList<Waypoint> user_wpts = new ArrayList<>();
            user_wpts = current_user.getWaypoints().get(current_user.getWaypoints().size()-1);      //Gets the last List of waypoints that the user has
            synchronized(this){
                System.out.println(user_wpts.size());
                chuncks = createChuncks(user_wpts, CHUNCK_SIZE);
                System.out.println("We here4");
            }

            out.writeUTF("Skase");
            out.flush();



        }catch (IOException e) {
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

    private synchronized ArrayList<ArrayList<Waypoint>> createChuncks(ArrayList<Waypoint> wpts, int size)  {

        System.out.println("Eimaste mesa");
        ArrayList<ArrayList<Waypoint>> chunks = new ArrayList<>();
        int chuncksize = size;
        int helper = 1;                 // counter to input the data for each chunk
        ArrayList<Waypoint> chunck = new ArrayList<>();  //first chunck
        for (int i = 0; i < wpts.size(); i++) {
            while (helper <= chuncksize) {
                chunck.add(wpts.get(i));
                helper++;
                if (helper > chuncksize) {
                    helper = 1;
                    chunks.add(chunck);
                    chunck = new ArrayList<>();   //Empties out the current chunk
                }
            }
        }
        System.out.println("Ola kala");

        return chunks;

    }

    public ArrayList<ArrayList<Waypoint>> getChuncks(){ return this.chuncks; }

    private User readgpx(String filename) throws IOException{
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
    }
}
