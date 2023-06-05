import java.io.*;
import java.net.*;



public class Client extends Thread{
    private String file;
    private Result final_res;
    private String username;

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
            /* Create socket for contacting the server on port 4320*/
            requestSocket = new Socket(host, 4320);
            System.out.println("A");

            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
            System.out.println("B");

            out.writeUTF("client");
            out.flush();
            out.writeUTF(this.file);
            out.flush();

            while(true) {

                username = in.readUTF();
                Object val = in.readObject();
                Result res = (Result) val;
                System.out.println("Username : " + username);
                res.printEndResults();
                break;

            }

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
        } finally {
            try {
                in.close(); out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private String formatFilePath(String filePath){

        return null;
    }



    public static void main(String [] args) {
        new Client(args[0]).start();

    }

}
