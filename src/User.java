import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
    private String id;
    private ArrayList<ArrayList<Waypoint>> waypoints = new ArrayList<>();
    private HashMap<String, ArrayList<Waypoint>> chuncks = new HashMap<>();
    private ArrayList<ArrayList<Result>> results = new ArrayList<>();

    public User(String id){
        this.id = id;
    }

    public void addWaypoints(ArrayList<Waypoint> wpts){
        this.waypoints.add(wpts);
        //System.out.println("We in this.");
    }

    public String getId(){ return this.id; }

    public ArrayList<ArrayList<Result>> getResult(){return results;}

    public ArrayList<ArrayList<Waypoint>> getWaypoints(){
        ArrayList<ArrayList<Waypoint>> temp_wayp = this.waypoints;

        return temp_wayp;
    }

    public void setChuncks(HashMap<String, ArrayList<Waypoint>> ch){ this.chuncks = ch; }

    public HashMap<String, ArrayList<Waypoint>> getChuncks() { return this.chuncks; }

}

