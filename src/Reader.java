import java.util.ArrayList;
import java.io.*;
import java.time.*;

public class Reader{

    public User readgpx(String filename) throws IOException{
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        StringReader gpx = new StringReader(filename);
        BufferedReader gpx_handler = new BufferedReader(gpx);
        String online;
        online=gpx_handler.readLine();
        Waypoint wpt=new Waypoint();
        double latitude=0.0, longitude=0.0, elevation=0.0;
        String user="";
        Instant date= null;
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
                date = Instant.parse(online.substring(online.indexOf(">")+1, online.indexOf("</")));
            }
            else if(online.trim().contains("</wpt")){
                wpt.setLatitude(latitude);
                wpt.setLongitude(longitude);
                wpt.setDate(date);
                wpt.setElevation(elevation);
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

