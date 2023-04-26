import java.io.*;
import java.net.*;

import javax.management.RuntimeErrorException;

public class Client extends Thread{
    private String file;
    //private String path = "C:\\Users\\giorg\\OneDrive - aueb.gr\\Desktop\\gpxs";

    Client(String file){
        this.file = file;
    }

    public String getFile(){ return this.file; }

    public void run(){
        ObjectOutputStream out= null ;
        ObjectInputStream in = null ;
        Socket requestSocket= null ;

        try{

            String host = "localhost";
            /* Create socket for contacting the server on port 4321*/
            requestSocket = new Socket(host, 4321);
            System.out.println("A");

            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
            System.out.println("B");

            out.writeUTF(this.file);
            out.flush();

            String s = in.readUTF();
            System.out.println("Server eipe " + s);

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        //}catch (ClassNotFoundException e) {
            //throw new RuntimeException(e);
        } finally {
            try {
                in.close(); out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String [] args) {
        new Client("C:\\Users\\giorg\\OneDrive - aueb.gr\\Επιφάνεια εργασίας\\gpxs\\route1.gpx").start();
        new Client("C:\\Users\\giorg\\OneDrive - aueb.gr\\Επιφάνεια εργασίας\\gpxs\\route2.gpx").start();
        new Client("C:\\Users\\giorg\\OneDrive - aueb.gr\\Επιφάνεια εργασίας\\gpxs\\route3.gpx").start();
        new Client("C:\\Users\\giorg\\OneDrive - aueb.gr\\Επιφάνεια εργασίας\\gpxs\\route4.gpx").start();
        //Hello
    }

}
