import java.net.*;
import java.util.*;
import java.io.*;

public class RequestHandler extends Thread{
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket active_connection;
    private int worker_counter = 1;

    public RequestHandler(Socket connection){
        try {
            this.active_connection = connection;
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try{


                System.out.println("Mpikame");
                // To see from whom the request comes from
                String word = in.readUTF();
                if (word.equals("client")) {

                    System.out.println("client");
                    String file = in.readUTF();         // Got the file
                    Reader gpx_reader = new Reader();
                    User cur_user = gpx_reader.readgpx(file);
                    String client = "Client" + Master.client_counter;
                    Master.incrementClientCounter();
                    // Creates the chuncks
                    HashMap<String, ArrayList<Waypoint>> chuncks = this.createChuncks(client, cur_user.getWaypoints().get(0), 6);
                    System.out.println(client);
                    System.out.println(chuncks.size() == Master.user_chuncks.size());
                    System.out.println("Chuncks created");

                } else {
                    String worker;
                    synchronized (Master.worker_lock) {
                        worker = "worker" + Master.worker_counter;
                        Master.incrementWorkerCounter();
                        System.out.println(worker);
                    }
                    while(true){
                        synchronized(Master.user_chuncks){
                            Iterator<Map.Entry<String, ArrayList<Waypoint>>> iterator = Master.user_chuncks.entrySet().iterator();
                            while(Master.user_chuncks != null){
                                Map.Entry<String, ArrayList<Waypoint>> entry = iterator.next();
                                String key = entry.getKey();
                                ArrayList<Waypoint> val = entry.getValue();
                                // Round Robin
                                synchronized (Master.worker_lock) {
                                    if (worker.equals("worker"+Master.rr_counter)){
                                        // write value to worker
                                        out.writeObject(val);
                                        out.flush();
                                        Master.incrementRRCounter();
                                        if (Master.rr_counter > Master.NUM_WORKERS) Master.rr_counter = 1;
                                    }
                                }

                            }
                        }
                    }
                    //System.out.println(worker);

                }


        }catch (IOException e) {
            System.out.println("System threw IOException!");
            e.printStackTrace();
            //}catch(InterruptedException e){
            //  System.out.println("System threw InterruptedException!");
            //e.printStackTrace();

            //} catch (ClassNotFoundException e) {
            //  throw new RuntimeException(e);// need it in readObject
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public synchronized HashMap<String, ArrayList<Waypoint>> createChuncks(String n, ArrayList<Waypoint> wpts, int size)  {

        System.out.println("Eimaste mesa");
        HashMap<String, ArrayList<Waypoint>> temp = new HashMap<>();
        ArrayList<ArrayList<Waypoint>> chunckies = new ArrayList<>();
        int chuncksize = size;
        int helper = 1;                 // counter to input the data for each chunk
        ArrayList<Waypoint> chunck = new ArrayList<>();  //first chunck
        for (int i = 0; i < wpts.size(); i++) {
            chunck.add(wpts.get(i));
            if (helper == chuncksize){
                if (i+1<wpts.size()) {
                    chunck.add(wpts.get(i + 1));
                    helper = 1;
                    chunckies.add(chunck);
                    chunck.clear();
                    continue;
                }
            }
            helper++;

        }
        System.out.println("Ola kala");

        for (int i=1; i<=chunckies.size(); i++){
            String name = n + "." + i;
            synchronized(Master.user_chuncks){
                Master.user_chuncks.put(name, chunckies.get(i-1));
            }
            temp.put(name, chunckies.get(i-1));
        }

        return temp;

    }

}
