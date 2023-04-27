import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class ActionsForWorkers extends Thread{
    private Socket worker;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private HashMap<String, ArrayList<Waypoint>> chuncks_workload;
    private ArrayList<ArrayList<Waypoint>> workload ;
    private int number ;

    public ActionsForWorkers(Socket connection) {
        try {
            this.worker = connection;
            this.out = new ObjectOutputStream(worker.getOutputStream());
            this.in = new ObjectInputStream(worker.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try{

            while(true) {
                //System.out.println("Mpainei gia epe3ergasia");

                out.write(this.number);
                out.flush();

                //out.writeUTF("Gamw to spiti2");
                //out.flush();
                //System.out.println("G");
            }

        }catch (IOException e) {
            e.printStackTrace();
            //} catch (ClassNotFoundException e) {
            //throw new RuntimeException(e);// need it in readObject
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void setChuncks_workload(HashMap<String, ArrayList<Waypoint>> temp){ this.chuncks_workload = temp; }

    public void setNumber(int num){ this.number = num; }

    public void addWork(ArrayList<Waypoint> chunck){
        this.workload.add(chunck);
    }
}
