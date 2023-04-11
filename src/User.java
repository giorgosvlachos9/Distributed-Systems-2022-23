import java.util.ArrayList;

public class User {
    private String id;
    private ArrayList<ArrayList<Waypoint>> waypoints = new ArrayList<>();

    public User(String id){
        this.id = id;
    }

    public void addWaypoints(ArrayList<Waypoint> wpts){
        this.waypoints.add(wpts);
        //System.out.println("We in this.");
    }

    public String getId(){ return this.id; }

    public ArrayList<ArrayList<Waypoint>> getWaypoints(){ return this.waypoints; }
}
