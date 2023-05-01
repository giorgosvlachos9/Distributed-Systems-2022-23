import java.io.Serializable;
import java.time.*;

public class Waypoint implements Serializable{
    private double latitude;
    private double longitude;
    private double elevation;
    private Instant date;

    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public double getElevation(){
        return elevation;
    }
    public Instant getDate(){
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
    public void setDate(Instant l){
        date=l;
    }
}

/*class User{
    String id;
    ArrayList<ArrayList<Waypoint>> wpt;
}*/

