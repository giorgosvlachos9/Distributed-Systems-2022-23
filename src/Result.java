import java.io.Serializable;

public class Result implements Serializable{

    private double total_time;
    private double total_distance;
    private double total_ascent;
    private double avg_speed;

    public Result(double t, double d, double a){
        this.total_time = t;
        this.total_distance = d;
        this.total_ascent = a;
        this.avg_speed = 0.0;
    }

    public Result() {
        this.total_time = 0;
        this.total_distance = 0;
        this.total_ascent = 0;
        this.avg_speed = 0;
    }

    public double getTotal_time() {
        return total_time;
    }

    public void setTotal_time(double total_time) {
        this.total_time = total_time;
    }

    public double getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(double total_distance) {
        this.total_distance = total_distance;
    }

    public double getTotal_ascent() {
        return total_ascent;
    }

    public void setTotal_ascent(double total_ascent) {
        this.total_ascent = total_ascent;
    }

    public double getAvg_speed() {
        return avg_speed;
    }

    public void setAvg_speed(double avg_speed) {
        this.avg_speed = avg_speed;
    }

    public void printEndResults(boolean isTotals){
        System.out.print("Total Time = " + this.getTotal_time() + "\nTotal Distance = " + this.getTotal_distance() +
                "\nTotal Ascent = " + this.getTotal_ascent());
        if (!isTotals) System.out.println("\nAverage Speed = " + this.getAvg_speed());
    }
}
