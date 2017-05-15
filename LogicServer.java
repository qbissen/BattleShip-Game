import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.net.*;
import java.util.*;
import java.io.*;


public class LogicServer implements ActionListener {
   GuiServer gs;
   JTextArea jta;
   //JButton
   JButton jbStart;
   //JLabel
   JLabel jlIP;
   Vector<ObjectOutputStream> clients = new Vector<ObjectOutputStream>();

   public LogicServer(GuiServer _gs, JTextArea _jta, JButton _jbStart, JLabel _jlIP){
      gs = _gs;
      jta = _jta;
      jbStart = _jbStart;
      jlIP = _jlIP;
   }
   public void actionPerformed(ActionEvent e){
      if(e.getSource().equals(jbStart)){
         Runnable r = 
            new Runnable () {
               public void run() {
                  doStart();
               }
            };
         new Thread(r).start();
      }
      
   }
   
   public void doStart(){
      jbStart.setEnabled(false);
      
      ServerSocket ss = null;
      try {
         //display the ip address 
         InetAddress address = InetAddress.getLocalHost();
         String hostIP = address.getHostAddress();
         jlIP.setText("IP Address: " + hostIP);
      
      
         ss = new ServerSocket(16789);						 
         Socket s = null;
         while(true){ 											
            s = ss.accept(); 	
            jta.append("Connection from " + s.getInetAddress() + "\n");
         					 
            ThreadServer ths = new ThreadServer(s);	 
            ths.start();											 
         }         
      }
      catch( BindException be ) {
         System.out.println("Server already running on this computer, stopping.");
      }
      catch(SocketException se){
         System.out.println("play quit");
      }
      catch( IOException ioe ) {
         System.out.println("IO Error");
         ioe.printStackTrace();
      }
   
   }
   class ThreadServer extends Thread {
      Socket cs;
      
      public ThreadServer( Socket cs) {
         this.cs = cs;
      }
      
      public void run(){
         ObjectOutputStream obs;
         ObjectInputStream ois;
      
         String clientMsg;
         String shift = "";
         try {
            OutputStream out = cs.getOutputStream();
            InputStream in = cs.getInputStream();
         
            obs = new ObjectOutputStream(out);
            ois = new ObjectInputStream(in);
            String com = "";
            clients.add(obs);   
            while(true){
               String command = ois.readUTF();
               System.out.println(command);
                     
               if(command.equals("CHAT")){
                  String username = "user";
                  String message = ois.readUTF();
                  System.out.println(message);
                  sendMessage(message);    
               }
            } 
         }
         catch( IOException e ) {
            System.out.println("Inside catch");
            e.printStackTrace();
         }
      }
      public void sendMessage(String message){
         for(ObjectOutputStream p : clients) {
            try {
               p.writeUTF("CHAT");
               p.flush();
               p.writeUTF(message);
               p.flush();
            }
            catch(Exception e) {
               System.out.println("error");
            }
         }
      
      }
      
   
   }
   
}