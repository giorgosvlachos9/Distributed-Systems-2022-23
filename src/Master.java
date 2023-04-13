import java.io.*;
import java.net.*;

public class Master {

    public static void main(String args[]){ new Master().openServer(); }

        //Socket that receives the requests
        ServerSocket s;
        //Socket that is sued to handle the connection
        Socket socketProvider;
        void openServer(){

            try{
                /* Create Server Socket */
                s= new ServerSocket(4321, 10);

                while(true){
                    /* Accept the connection */
                    socketProvider = s.accept();
                    /* Handle the request */

                }
            }catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                try {
                    socketProvider.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        }

}
