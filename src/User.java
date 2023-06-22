import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
    private String id;
    private ArrayList<ArrayList<Waypoint>> waypoints = new ArrayList<>();
    private ArrayList<Result> final_results = new ArrayList<>();
    private Result total_res ;


    public User(String id){
        this.id = id;
    }

    public void addWaypoints(ArrayList<Waypoint> wpts){
        this.waypoints.add(wpts);
    }

    public String getId(){ return this.id; }

    public ArrayList<ArrayList<Waypoint>> getWaypoints(){
        ArrayList<ArrayList<Waypoint>> temp_wayp = this.waypoints;

        return temp_wayp;
    }

    public void addResults(Result res) { this.final_results.add(res); }

    public void setTotals(){
        double t_time = 0.0, t_dist = 0.0, t_ele = 0.0;
        for (Result res: final_results){
            t_time += res.getTotal_time();
            t_dist += res.getTotal_distance();
            t_ele += res.getTotal_ascent();
        }
        Result r = new Result(t_time, t_dist, t_ele);
        total_res = r;
    }

    public int getNumberOfFiles() { return this.final_results.size(); }

    // This method is only used when creating a temporary user that stores the total values of all users in the server
    // we basically create this user to easily create the string to return using its createTotalString message
    public void setTotalResultWithParameters(double t, double d, double e){
        this.total_res = new Result(t, d, e);
    }

    public Result getTotal_res() {
        return total_res;
    }

    // Method that creates a String variable that has all the values of a result
    private String resultStringBuilder(Result res, boolean avg_speed_check){
        StringBuilder sb = new StringBuilder();
        sb.append("Total Time (in Minutes) : " + Math.round(res.getTotal_time() * 100) / 100.0 + "\n");
        sb.append("Total Distance (in Kilometers) : " + Math.round(res.getTotal_distance() * 100) / 100.0 + "\n");
        sb.append("Total Elevation (in Meters) : " + Math.round(res.getTotal_ascent() * 100) / 100.0 + "\n");
        if (avg_speed_check) sb.append("Average Speed (in Meters / minute) : " + Math.round(res.getAvg_speed() * 100) / 100.0 + "\n");

        return sb.toString();

    }

    public String createResultsString(Result res){
        return resultStringBuilder(res, true);
    }

    public String createTotalsString(){
        // Sets the total results
        //setTotalResults();

        return resultStringBuilder(total_res, false);
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

