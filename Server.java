import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Created by Jacob on 4/23/2017.
 * Server for Main game and ChatClient
 */
public class Server extends JFrame implements ActionListener{

   //JTextArea + JScrollPane
   private JTextArea jta = new JTextArea(20,40);
   private JScrollPane jsp = new JScrollPane(jta);

   //JPanels
   private JPanel jpTextArea = new JPanel();
   private JPanel jpConnectionInfo = new JPanel(new GridLayout(0,2));
   private JPanel jpButton = new JPanel();

   //JButton
   private JButton jbStart = new JButton("Start");

   //JLabel
   private JLabel jlIP = new JLabel("IP Address: ");
   private JLabel jlPort = new JLabel("Port: 16789");

   //Border for JTextArea
   private Border border = BorderFactory.createLineBorder(Color.BLACK);

   //ArrayList of Clients
   private Vector<ObjectOutputStream> clients = new Vector<ObjectOutputStream>();

   // number of players connected
   private int numberOfPlayers = 0;

   // main method which creates a new instance of Server
   public static void main(String[] args){
      new Server();
   }

   private Server(){

      // add the JLabels to JPConnectionInfo
      jpConnectionInfo.add(jlIP);
      jpConnectionInfo.add(jlPort);

      //add the JPanel jpConnectionInfo
      add(jpConnectionInfo, BorderLayout.NORTH);
   
      //Set a border around the jtextarea
      jta.setBorder(BorderFactory.createCompoundBorder(border,
              BorderFactory.createEmptyBorder(10, 10, 10, 10)));
      //disable the jtextarea
      jta.setEnabled(false);

      //add a JScrollPane
      jpTextArea.add(jsp);

      //add the scrollpane to the jpTextArea JPanel located in the center of the GUI
      add(jpTextArea, BorderLayout.CENTER);

      //add the start button to the GUI
      jpButton.add(jbStart);
      add(jpButton,BorderLayout.SOUTH);

      //add an action listener to jbStart
      jbStart.addActionListener(this);

      //setup the JFrame
      setLocationRelativeTo(null);
      setSize(500,500);
      setVisible(true);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
   
   }

   //ActionListener for jbStart
   public void actionPerformed(ActionEvent e){
      if(e.getSource().equals(jbStart)){
         //add text to the JTextArea saying the server started
         jta.append("Server Start\n");

         //Create a runnable to start the Server and run it.
         Runnable r =
                 new Runnable () {
                    public void run() {
                       doStart();
                    }
                 };
         new Thread(r).start();
      }
   }

   private void randomizeTurn()
   {
      double rand = Math.random();
      int turnDirtyBit = (1+(int)(rand * 2));
      System.out.println("hit randomize turn");
      sendRandomizeTurn(turnDirtyBit);
   }



   public void doStart(){

      //disables the jbStart JButton
      jbStart.setEnabled(false);
      
      ServerSocket ss;


      try{
         //Gets the IP of the machine the Server is being run on
         InetAddress address = InetAddress.getLocalHost();
         String hostIP = address.getHostAddress();

         //displays the IP in the Server GUI
         jlIP.setText("IP Address: " + hostIP);
      
      
         ss = new ServerSocket(16789);
      
         DirtyBitListener db = new DirtyBitListener();
         db.start();

         //Listens for a connection
         while(true){
            Socket s = ss.accept();
            jta.append("Connection from " + s.getInetAddress() + "\n");
            
            // Creates a new ServerThread for each connection
            ServerThread st = new ServerThread(s);
            st.setName(String.valueOf(s.getInetAddress()));
            st.start();
          
         
         }
      
      }
      catch(SocketException e){
         e.printStackTrace();
      }
      catch(IOException e){
         e.printStackTrace();
      }
   
   }

   class DirtyBitListener extends Thread{
      public void run(){
      
         System.out.println("Hit DirtyBit Listener");
         if (numberOfPlayers == 1) {
            sendDirtBitPlayerOne(1);
            whoAreYou(1);
         } 
         else if (numberOfPlayers == 2) {
            sendDirtyBitPlayerTwo(2);
            whoAreYou(2);
         } 
         else {
            System.out.println("More than two players?????");
         }
      
      }
   }

   class ServerThread extends Thread{
      Socket sock;
      //        BufferedReader br;
      ObjectOutputStream obs;
      ObjectInputStream ois;
   
      //        private InetAddress address = sock.getInetAddress();
      //        private String netAdress = address.getHostAddress();
   
      String uName;
   
      public ServerThread(Socket _s){
         this.sock = _s;
      
      }
   
      public void run(){
      
         //doStartGame();
      
         String clientMsg;
         String shift = "";
         String eName = null;
         try {
            //OutputStream
            OutputStream out = sock.getOutputStream();
            //InpuStream
            InputStream in = sock.getInputStream();

            //ObjectOutputStream
            obs = new ObjectOutputStream(out);
            //ObejctInputStream
            ois = new ObjectInputStream(in);
            String com = "";
            // adds the ObjectOutpuStream to a vector of clients
            clients.add(obs);
            while(true){
               //read in the first line to determine what type of information is being sent in
               String command = ois.readUTF();
               System.out.println(command);
               
               
               //If a message is being sent from the chat
               if(command.equals("CHAT")){
                  String username = "user";
                  String message = ois.readUTF();
                  System.out.println(message);
                  sendMessage(message);
                  uName = username;
               
               }
               else if(command.equals("SPECTATOR-CHAT")){
                  String username = ois.readUTF();
                  String message = ois.readUTF();
                  sendSpectatorMessage(message, username);
                  uName = username;
               }
               //If the button data is being sent
               else if(command.equals("DATA")){
                  //String player = ois.readUTF();
                  String player = "";
                  int row = ois.readInt();
                  int column = ois.readInt();
                  System.out.println(row);
                  System.out.println(column);
                  sendButtonNumber(row, column, player);
               }
               //if the result is being sent
               else if(command.equals("RESULT")){
                  String player = ois.readUTF();
                  boolean isHit = ois.readBoolean();
                  sendResult(isHit, player);
               }
               //if the winner is being sent
               else if(command.equals("DECLARE-WINNER")){
                  String player = ois.readUTF();
                  declareWinner(player);
               }
               //if the player is being sent
               else if(command.equals("PLAYER")){
               
                  String isPlayer = ois.readUTF();
                  System.out.println(isPlayer);
                  if(isPlayer.equals("true")){
                     System.out.println("just before numberOfPlayers");
                     
                     numberOfPlayers = numberOfPlayers + 1;
                     whoAreYou(numberOfPlayers);
                     
                     System.out.println(numberOfPlayers);
                     if(numberOfPlayers == 2){
                        System.out.println("got inside of numberOfPlayers for loop");
                        randomizeTurn();
                        whoAreYou(numberOfPlayers);
                     
                        weAreReady();
                     }
                  }
               }
               else if(command.equals("IMP1")){
                    eName = ois.readUTF();
               
                  sendEname(1, eName);
               }
               else if(command.equals("IMP2")){
                    eName = ois.readUTF();
               
                  sendEname(0, eName);
               
               }
               
            
            
            }
         }
         catch(SocketException e){
            try {
               //close the connection
               obs.close();
               ois.close();
               sock.close();
               jta.append(uName + " disconnected\n");
            }
            catch(IOException ioe){
               ioe.printStackTrace();
            }
         
         }
         catch(IOException e){
            e.printStackTrace();
         }
      }}
   //send the player who won
   public synchronized void declareWinner(String player){
      try{
         for(ObjectOutputStream o: clients){
            o.writeUTF(player);
            o.flush();
         }
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   public synchronized void doStartGame(){
      try{
         for(ObjectOutputStream o: clients){
            o.writeDouble(1 + (int)(Math.random() * 2));
            o.flush();
         }
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }
   //send the message to the clients
   public void sendMessage(String msg){
      try{
         System.out.println("got to sendMessage method");
         for(ObjectOutputStream o: clients){
            o.writeUTF("CHAT");
            o.flush();
            o.writeUTF(msg);
            o.flush();
         }
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   public synchronized void sendSpectatorMessage(String msg, String username){
      try{
         for(ObjectOutputStream o: clients){
         
            o.writeUTF("SPECTATOR-MESSAGE");
            o.flush();
            o.writeUTF(username);
            o.flush();
            o.writeUTF(msg);
            o.flush();
         }
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }
   //send the button data to the clients
   public synchronized void sendButtonNumber(int row, int column, String s){
      try{
         for(ObjectOutputStream o: clients) {
            o.writeUTF("DATA");
            //o.writeUTF(s);
            //o.flush();
            o.writeInt(row);
            //o.flush();
            o.writeInt(column);
            o.flush();
         }
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   //send the result to the clients
   public synchronized void sendResult(Boolean b, String s){
      try{
         for(ObjectOutputStream o: clients) {
            o.writeUTF("RESULT");
            o.flush();
            o.writeUTF(s);
            o.flush();
            o.writeBoolean(b);
            o.flush();
         }
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   public void sendDirtBitPlayerOne(int _dirtyBit){
      try{
         for(ObjectOutputStream o: clients) {
            o.writeUTF("START");
            o.writeInt(_dirtyBit);
            o.flush();
         }
      }
      catch (IOException ioe){
      
      }
   }

   public void sendDirtyBitPlayerTwo(int _dirtyBit){
      try{
         for(ObjectOutputStream o: clients) {
            o.writeUTF("START");
            o.writeInt(_dirtyBit);
            o.flush();
         }
      }
      catch (IOException ioe){
      
      }
   }

   public void sendRandomizeTurn(int _turnDirtyBit){
      try{
         for(ObjectOutputStream o: clients) {
            o.writeUTF("WHOTURN");
            o.writeInt(_turnDirtyBit);
            o.flush();
            System.out.println(_turnDirtyBit);
         }
      }
      catch (IOException ioe){
      
      }
   }
   
   public void whoAreYou(int numberOfPlayers){
      try{
         clients.get(numberOfPlayers -1).writeUTF("YOUARE");
                  
         clients.get(numberOfPlayers -1).writeInt(numberOfPlayers);
         clients.get(numberOfPlayers -1).flush();
      }
      catch(IOException ioe){}
   }

   //tell both clients that there are two clients connected and the game is ready to start
   public void weAreReady(){
      try{
         for(ObjectOutputStream o: clients) {
            o.writeUTF("READY");
            o.flush();
         }
      }
      catch (IOException ioe){
      
      }
   }
   public void sendEname(int who, String eName){
      try{
         clients.get(who).writeUTF("ENAME");
                  
         clients.get(who).writeUTF(eName);
         clients.get(who).flush();
      }
      catch(IOException ioe){}
   }
}