import java.util.ArrayList;
import java.io.*;

public class Reader{

    public ArrayList<Waypoint> readgpx(String filename) throws IOException{
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        FileReader gpx = new FileReader(filename);
        BufferedReader gpx_handler = new BufferedReader(gpx);
        String online;
        online=gpx_handler.readLine();
        Waypoint wpt=new Waypoint();
        long latitude=0;
        long longitude=0;
        String time="";
        String date="";
        long elevation=0;
        String user="";
        while(online!=null){
            if(online.trim().contains("<gpx")){
                //user=online.substring(online.indexOf("createor="+9,online.lastIndexOf(">")));
            }
            if(online.trim().contains("<wpt")){
                wpt=new Waypoint();
                latitude=Long.parseLong(online.trim().substring(online.indexOf("lat")+5, online.indexOf("lon")-4));
                longitude=Long.parseLong(online.trim().substring(online.indexOf("lon")+5, online.lastIndexOf(">")-3));
            }
            else if(online.trim().contains("<ele")){
                elevation=Long.parseLong(online.substring(online.indexOf(">")+1),online.lastIndexOf("<"));
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
        
        gpx_handler.close();
        return waypoints;
    }


    public static void main(String[] args) {
        try{
            Reader r = new Reader();
            ArrayList<Waypoint> waypoints = new ArrayList<>();
            waypoints=r.readgpx("test.xml");
            for(Waypoint w : waypoints){
                System.out.println("We got");
            }
        }catch(IOException e){

        }
        

    }
}
