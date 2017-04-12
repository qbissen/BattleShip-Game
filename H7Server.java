import java.io.*;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by Quinn on 4/7/2017.
 */
public class H7Server{

    public Vector<Socket> clients = new Vector<Socket>();

    public static void main(String[] args){new H7Server();}
    public H7Server()
    {



        ServerSocket ss = null;

        try {
            System.out.println("getLocalHost: "+ InetAddress.getLocalHost() );
            System.out.println("getByName:    "+InetAddress.getByName("localhost") );

            Socket cs = null;
            while(true){ 		// run forever once up
                //try{
                ss = new ServerSocket(16789);
                cs = ss.accept(); 				// wait for connection
                clients.add(cs);
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
               String clientMsg;
               while(true) {
                   try {
                       br = new BufferedReader(
                               new InputStreamReader(
                                       cs.getInputStream()));


                       clientMsg = br.readLine();                // from client
                       System.out.println("Server read: " + clientMsg);
                       while (clientMsg != null) {
                           sendMessage(clientMsg);
                       }

                   } catch (IOException e) {
                       System.out.println("Inside catch");
                       e.printStackTrace();
                   }

               }
        }

        public synchronized void sendMessage(String s){
            try{
                for(Socket sock: clients) {
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));

                    pw.println(s);
                    pw.flush();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }


        }

    } // end class ThreadServer



}
