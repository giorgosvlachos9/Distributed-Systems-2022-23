import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
    private String id;
    private ArrayList<ArrayList<Waypoint>> waypoints = new ArrayList<>();
    private ArrayList<Result> results = new ArrayList<>();

    public User(String id){
        this.id = id;
    }

    public void addWaypoints(ArrayList<Waypoint> wpts){
        this.waypoints.add(wpts);
        //System.out.println("We in this.");
    }

    public String getId(){ return this.id; }

    public void setResults(ArrayList<Result> results) { this.results = results; }

    public ArrayList<Result> getResult(){ return results; }

    public ArrayList<ArrayList<Waypoint>> getWaypoints(){
        ArrayList<ArrayList<Waypoint>> temp_wayp = this.waypoints;

        return temp_wayp;
    }

    // Used to check if a user is already contained in an ArrayList
    @Override
    public boolean equals(Object u){
        if (this == u) return true;
        if (u == null || this.getClass() != u.getClass()) return false;
        User user = (User) u;
        return this.id.equals(user.getId());
    }

}

