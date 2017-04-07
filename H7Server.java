import java.io.*;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;

/**
 * Created by Quinn on 4/7/2017.
 */
public class H7Server{

    public static void main(String[] args){new H7Server();}
    public H7Server()
    {



        ServerSocket ss = null;

        try {
            System.out.println("getLocalHost: "+ InetAddress.getLocalHost() );
            System.out.println("getByName:    "+InetAddress.getByName("localhost") );

            ss = new ServerSocket(16789);
            Socket cs = null;
            while(true){ 		// run forever once up
                //try{
                cs = ss.accept(); 				// wait for connection
                ThreadServer ths = new ThreadServer( cs );
                ths.start();
            } // end while
        }
        catch( BindException be ) {
            System.out.println("Server already running on this computer, stopping.");
        }
        catch( IOException ioe ) {
            System.out.println("IO Error");
            ioe.printStackTrace();
        }

    } // end main

    class ThreadServer extends Thread {
        Socket cs;

        public ThreadServer( Socket cs ) {
            this.cs = cs;
        }

        public void run() {

               BufferedReader br;
               PrintWriter opw;
               String clientMsg;
               try {
                   br = new BufferedReader(
                           new InputStreamReader(
                                   cs.getInputStream()));
                   opw = new PrintWriter(
                           new OutputStreamWriter(
                                   cs.getOutputStream()));

                   clientMsg = br.readLine();                // from client
                   System.out.println("Server read: " + clientMsg);
                   opw.println(clientMsg);    //to client
                   opw.flush();
               } catch (IOException e) {
                   System.out.println("Inside catch");
                   e.printStackTrace();
               }

             
        }
    } // end class ThreadServer


}
