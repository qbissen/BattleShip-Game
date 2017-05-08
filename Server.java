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
                jta.append("Connection from " + s.getInetAddress() + "\n");
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
        ObjectInputStream ois;
        ObjectOutputStream oos;


//        private InetAddress address = sock.getInetAddress();
//        private String netAdress = address.getHostAddress();

        String uName;

        public ServerThread(Socket _s){
            sock = _s;
            try{
//                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                oos = new ObjectOutputStream(sock.getOutputStream());
                ois = new ObjectInputStream(sock.getInputStream());
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
                    String command = ois.readUTF();

                    //If a message is being sent from the chat
                    if(command == "CHAT"){
//                        String username = ois.readUTF();
                        String username = "user";
                        String message = ois.readUTF();
                        System.out.println(message);
                        sendMessage(message, username);
                        uName = username;
                    }
                    else if(command == "SPECTATOR-CHAT"){
                        String username = ois.readUTF();
                        String message = ois.readUTF();
                        sendSpectatorMessage(message, username);
                        uName = username;
                    }
                    else if(command == "DATA"){
                        String player = ois.readUTF();
                        int row = ois.readInt();
                        int column = ois.readInt();
                        sendButtonNumber(row, column, player);
                    }
                    else if(command == "RESULT"){
                        String player = ois.readUTF();;
                        boolean isHit = ois.readBoolean();
                        sendResult(isHit, player);
                    }
                    else if(command == "DECLARE-WINNER"){
                        String player = ois.readUTF();
                        declareWinner(player);
                    }

                }
            }
            catch(SocketException e){
                try {
                    oos.close();
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
        }

        public synchronized void declareWinner(String player){
            try{
                for(Socket s: clients){
                    oos.writeUTF(player);
                    oos.flush();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void doStartGame(){
            try{
                for(Socket s: clients){
                    oos.writeDouble(1 + (int)(Math.random() * 2));
                    oos.flush();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void sendMessage(String msg, String username){
            try{
                for(Socket s: clients){
                    oos.writeUTF("CHAT");
                    oos.flush();
                    oos.writeUTF(username);
                    oos.flush();
                    oos.writeUTF(msg);
                    oos.flush();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void sendSpectatorMessage(String msg, String username){
            try{
                for(Socket s: clients){
                    oos.writeUTF("SPECTATOR-MESSAGE");
                    oos.flush();
                    oos.writeUTF(username);
                    oos.flush();
                    oos.writeUTF(msg);
                    oos.flush();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void sendButtonNumber(int row, int column, String s){
            try{
                for(Socket so: clients) {
                    oos.writeUTF("DATA");
                    oos.flush();
                    oos.writeUTF(s);
                    oos.flush();
                    oos.writeInt(row);
                    oos.flush();
                    oos.writeInt(column);
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        public synchronized void sendResult(Boolean b, String s){
            try{
                for(Socket so: clients) {
                    oos.writeUTF("RESULT");
                    oos.flush();
                    oos.writeUTF(s);
                    oos.flush();
                    oos.writeBoolean(b);
                    oos.flush();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
