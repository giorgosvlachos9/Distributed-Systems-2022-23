import java.util.ArrayList;

public class Waypoint {
    private double latitude;
    private double longitude;
    private double elevation;
    private String time;
    private String date;

    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public double getElevation(){
        return elevation;
    }
    public String getTime(){
        return time;
    }

    public String getDate(){
        return date;
    }

    public void setLatitude(double l){
        latitude=l;
    }
    public void setLongitude(double l){
        longitude=l;
    }
    public void setElevation(double l){
        elevation=l;
    }
    public void setTime(String l){
        time=l;
    }
    public void setDate(String l){
        date=l;
    }
}

/*class User{
    String id;
    ArrayList<ArrayList<Waypoint>> wpt;
}*/
