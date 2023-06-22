import java.time.Duration;

public class Compute {

    public double time_diff(Waypoint prev, Waypoint curr){
        Duration totalD = Duration.between(prev.getDate(), curr.getDate());
        long min = totalD.getSeconds() ;
        return min;
    }

    public double distance(Waypoint prev, Waypoint curr) {
        return calculateDist(prev, curr);
    }

    public double up_elevation(Waypoint prev, Waypoint curr){
        double ele_diff;
        if (prev.getElevation() < 0 && curr.getElevation() < 0 && curr.getElevation() > prev.getElevation()){
            ele_diff = Math.abs(Math.abs(curr.getElevation()) - Math.abs(prev.getElevation()));
            return ele_diff;
        }
        ele_diff = curr.getElevation() - prev.getElevation();
        if (ele_diff > 0){
            return ele_diff;
        }
        return 0;
    }

    private double calculateDist(Waypoint prev, Waypoint curr){
        //return Math.sqrt(Math.pow(curr.getLatitude()-prev.getLatitude(),2)+Math.pow(curr.getLongitude()-prev.getLongitude(), 2));
        double earthRadius = 6371;

        double lat1Rad = Math.toRadians(prev.getLatitude());
        double lon1Rad = Math.toRadians(prev.getLongitude());
        double lat2Rad = Math.toRadians(curr.getLatitude());
        double lon2Rad = Math.toRadians(curr.getLongitude());

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double distanceKm = earthRadius * c;
        return distanceKm ;

        /*double dlat = Math.toRadians(curr.getLatitude() - Math.toRadians(prev.getLatitude()));
        double dlon = Math.toRadians(curr.getLongitude() - Math.toRadians(prev.getLongitude()));

        double temp1 = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(Math.toRadians(prev.getLatitude())) * Math.cos(Math.toRadians(curr.getLatitude())) *
                        Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double temp2 = 2 * Math.atan2(Math.sqrt(temp1), Math.sqrt(1-temp1));
        return earthRadius * temp2;*/
    }

}
