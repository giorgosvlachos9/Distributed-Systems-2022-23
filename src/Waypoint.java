import java.time.LocalDateTime;

public class Waypoint {
    private double latitude;
    private double longitude;
    private double elevation;
    private LocalDateTime date;

    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public double getElevation(){
        return elevation;
    }
    public LocalDateTime getDate(){
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
    public void setDate(LocalDateTime l){
        date=l;
    }
}

/*class User{
    String id;
    ArrayList<ArrayList<Waypoint>> wpt;
}*/
