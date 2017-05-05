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
    private Vector<Socket> clients = new Vector<Socket>();

    public static void main(String[] args){
        new Server();
    }

    private Server(){

        jpConnectionInfo.add(jlIP);
        jpConnectionInfo.add(jlPort);
        add(jpConnectionInfo, BorderLayout.NORTH);


        jta.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        jta.setEnabled(false);

        jpTextArea.add(jsp);

        add(jpTextArea, BorderLayout.CENTER);

        jpButton.add(jbStart);
        add(jpButton,BorderLayout.SOUTH);

        jbStart.addActionListener(this);

        setLocationRelativeTo(null);
        setSize(500,500);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource().equals(jbStart)){

            Runnable r = new Runnable () {
                public void run() {
                    doStart();
                }
            };
            new Thread(r).start();
        }
    }

    public void doStart(){

        jbStart.setEnabled(false);

        ServerSocket ss;

        try{
           InetAddress address = InetAddress.getLocalHost();
           String hostIP = address.getHostAddress();

           jlIP.setText("IP Address: " + hostIP);

            ss = new ServerSocket(16789);
            while(true){
                Socket s = ss.accept();
                jta.append("Connection from " + s.getInetAddress());
                clients.add(s);
                ServerThread st = new ServerThread(s);
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



    class ServerThread extends Thread{
        Socket sock;
//        BufferedReader br;
        ObjectInputStream obr;

//        private InetAddress address = sock.getInetAddress();
//        private String netAdress = address.getHostAddress();

        String uName;

        public ServerThread(Socket _s){
            sock = _s;
            try{
//                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                obr = new ObjectInputStream(sock.getInputStream());
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public void run(){
            try{

                doStartGame();

                while(true){
                    //read in the first line to determine what type of information is being sent in
                    String command = obr.readUTF();

                    //If a message is being sent from the chat
                    if(command == "CHAT"){
                        String username = obr.readUTF();
                        String message = obr.readUTF();
                        sendMessage(message, username);
                        uName = username;
                    }
                    else if(command == "SPECTATOR-CHAT"){
                        String username = obr.readUTF();
                        String message = obr.readUTF();
                        sendSpectatorMessage(message, username);
                        uName = username;
                    }
                    else if(command == "DATA"){
                        String player = obr.readUTF();
                        int row = obr.readInt();
                        int column = obr.readInt();
                        sendButtonNumber(row, column, player);
                    }
                    else if(command == "RESULT"){
                        String player = obr.readUTF();;
                        boolean isHit = obr.readBoolean();
                        sendResult(isHit, player);
                    }
                    else if(command == "DECLARE-WINNER"){
                        String player = obr.readUTF();
                        declareWinner(player);
                    }
                }
            }
            catch(SocketException e){
                e.printStackTrace();
            }
            catch(UnknownHostException e){
                try {
                    sock.close();
                    jta.append(uName + " disconnected");
                }
                catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void declareWinner(String player){
            try{
                for(Socket s: clients){
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                    pw.println(player);
                    pw.flush();
                    pw.close();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void doStartGame(){
            try{
                for(Socket s: clients){
                    ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                    oos.writeDouble(1 + (int)(Math.random() * 2));
                    oos.flush();
                    oos.close();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void sendMessage(String msg, String username){
            try{
                for(Socket s: clients){
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                    pw.println("MESSAGE");
                    pw.flush();
                    pw.println(username);
                    pw.flush();
                    pw.println(msg);
                    pw.flush();
                    pw.close();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void sendSpectatorMessage(String msg, String username){
            try{
                for(Socket s: clients){
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                    pw.println("SPECTATOR-MESSAGE");
                    pw.flush();
                    pw.println(username);
                    pw.flush();
                    pw.println(msg);
                    pw.flush();
                    pw.close();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void sendButtonNumber(int row, int column, String s){
            try{
                for(Socket so: clients) {
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(so.getOutputStream()));
                    pw.println("DATA");
                    pw.flush();
                    pw.println(s);
                    pw.flush();
                    pw.close();
                    ObjectOutputStream oos = new ObjectOutputStream(so.getOutputStream());
                    oos.writeInt(row);
                    oos.flush();
                    oos.writeInt(column);
                    oos.close();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void sendResult(Boolean b, String s){
            try{
                for(Socket so: clients) {
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(so.getOutputStream()));
                    pw.println("RESULT");
                    pw.flush();
                    pw.println(s);
                    pw.flush();
                    pw.close();
                    ObjectOutputStream oos = new ObjectOutputStream(so.getOutputStream());
                    oos.writeBoolean(b);
                    oos.flush();
                    oos.close();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
