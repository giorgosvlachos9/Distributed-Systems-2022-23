import java.io.Serializable;

public class Result implements Serializable{

    private double total_time;
    private double total_distance;
    private double total_ascent;
    private double avg_speed;

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

    public Result addResults(Result r2) {
        Result r_final = new Result();

        r_final.setTotal_time(this.getTotal_time() + r2.getTotal_time());
        r_final.setTotal_distance(this.getTotal_distance() + r2.getTotal_distance());
        r_final.setTotal_ascent(this.getTotal_ascent() + r2.getTotal_ascent());
        r_final.setAvg_speed(this.getAvg_speed() + r2.getAvg_speed());

        return r_final;
    }
}
