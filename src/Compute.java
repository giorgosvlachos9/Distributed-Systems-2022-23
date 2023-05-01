import java.time.Duration;

public class Compute {
    public double distance(Waypoint w1, Waypoint w2){
        return Math.sqrt(Math.pow(w2.getLatitude()-w1.getLatitude(),2)+Math.pow(w2.getLongitude()-w1.getLongitude(), 2));
    }

    public double up_elevasion(Waypoint w1, Waypoint w2){
        double diff = w2.getElevation()-w1.getElevation();
        if(diff>0){
            return diff;
        }
        return 0;
    }

    public double time_diff(Waypoint w1, Waypoint w2){
        Duration dur = Duration.between(w1.getDate(), w2.getDate());
        double hours = (dur.getSeconds());
        return hours;
    }

    public double average_speed(Waypoint w1 ,Waypoint w2){
        return (distance(w1, w2) / time_diff(w1, w2));
    }

}
