import java.util.ArrayList;

public class Waypoint {
    private long latitude;
    private long longitude;
    private long elevation;
    private String time;
    private String date;

    public long getLatitude(){
        return latitude;
    }
    public long getLongitude(){
        return longitude;
    }
    public long getElevation(){
        return elevation;
    }
    public String getTime(){
        return time;
    }

    public String getDate(){
        return date;
    }

    public void setLatitude(long l){
        latitude=l;
    }
    public void setLongitude(long l){
        longitude=l;
    }
    public void setElevation(long l){
        elevation=l;
    }
    public void setTime(String l){
        time=l;
    }
    public void setDate(String l){
        date=l;
    }
}

class User{
    String id;
    ArrayList<ArrayList<Waypoint>> wpt;
}
