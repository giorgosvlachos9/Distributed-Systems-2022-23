import java.io.*;
import java.net.*;
import java.util.Random;


public class Client extends Thread{
    private String file;
    private Result final_res;
    private String username;
    private FileInputStream fileIS;

    Client(String f){

            this.file = f;

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
            out.writeUTF(createFileString(file));
            out.flush();



            /*out.writeUTF("client");
            out.flush();
            out.writeObject(this.fileIS);
            out.flush();*/

            while(true) {

                username = in.readUTF();
                String gpx_res = in.readUTF();
                String user_total_res = in.readUTF();
                String server_total_res = in.readUTF();
                int total_user_files = in.readInt();
                int total_gpxs = in.readInt();
                System.out.println("Username : " + username);

                System.out.println(gpx_res);
                System.out.println("--------------------------------");
                System.out.println(user_total_res);
                System.out.println("--------------------------------");
                System.out.println(server_total_res);
                System.out.println("total_user_files" + total_user_files);
                System.out.println("total_gpxs"+total_gpxs);

                break;

            }


        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close(); out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    private String createFileString(String file){
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            br.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException thrown");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException thrown");
            e.printStackTrace();
        }


        return sb.toString();

    }



    public static void main(String [] args) {
        new Client("C:\\Users\\giorg\\OneDrive - aueb.gr\\Έγγραφα\\aueb\\Distributed_Systems\\gpxs\\route1.gpx").start();

    }

}
