import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ActionsForWorkers extends Thread{
    ObjectInputStream in;
    ObjectOutputStream out;
    ArrayList<ArrayList<Waypoint>> workload ;

    public ActionsForWorkers(Socket connection) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try{

            System.out.println("Z");

            out.writeUTF("Gamw to spiti2");
            out.flush();
            System.out.println("G");



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

    public void addWork(ArrayList<Waypoint> chunck){
        this.workload.add(chunck);
    }
}
