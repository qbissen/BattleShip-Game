import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;



/**
 * Created by Quinn on 4/7/2017.
 */
public class H7Client implements ActionListener {

    private JTextField jtfPortName = new JTextField(20);
    private JTextField jtfHostName = new JTextField(20);
    private String hostName;
    private int portNumber;

    private JTextArea jtaChat = new JTextArea("Send a message to the client", 15, 40);
    private JTextArea jtaRecive = new JTextArea("WELCOME TO THE CHAT!", 15, 40);
    private JTextField jtfUName = new JTextField("user");


    public H7Client() {
        JFrame defaultFrame = new JFrame();

        JLabel jlPortName = new JLabel("Enter The Port number");
        JLabel jlHostName = new JLabel("Enter the Host name");
        JLabel jlUName = new JLabel("Enter a username");

        JButton jbSetSocketInfo = new JButton("Confirm Port and Host Info");
        JButton jbExit = new JButton("Exit");
        JButton jbSendText = new JButton("Send");

        jbSetSocketInfo.addActionListener(this);
        jbExit.addActionListener(this);
        jbSendText.addActionListener(this);


        JPanel jpNorth = new JPanel();
        JPanel jpCenter = new JPanel();
        JPanel jpLabels = new JPanel();

        defaultFrame.add(jpNorth, BorderLayout.NORTH);
        jpNorth.add(jbSetSocketInfo, BorderLayout.EAST);
        jpNorth.add(jbSendText, BorderLayout.CENTER);
        jpNorth.add(jbExit, BorderLayout.WEST);


        defaultFrame.add(jpCenter, BorderLayout.CENTER);
        jpCenter.add(jtaChat, BorderLayout.SOUTH);
        jpCenter.add(jpLabels, BorderLayout.NORTH);

        jpLabels.setLayout(new GridLayout(2, 3));
        jpLabels.add(jlHostName);
        jpLabels.add(jlPortName);
        jpLabels.add(jlUName);
        jpLabels.add(jtfHostName);
        jpLabels.add(jtfPortName);
        jpLabels.add(jtfUName);

        defaultFrame.add(jtaRecive, BorderLayout.SOUTH);

        defaultFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        defaultFrame.setLocationRelativeTo(null);
        defaultFrame.setSize(800, 800);
        defaultFrame.setVisible(true);

    }

    public void setClientComms(String message) {
        try {
            // open communications to the server
            Socket s = new Socket(hostName, portNumber);
            SendThread ct = new SendThread(s, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ReceiveThread extends Thread {

        private BufferedReader s;

        public ReceiveThread(Socket sock) throws IOException {
            s = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        }

        public void run() {
            while (true) {
                try {
                    String message = s.readLine();

                    jtaRecive.append("\n" + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SendThread extends Thread {

        public SendThread(Socket sock, String msg) {
            while (true) {
                try {

                    // open output stream
                    PrintWriter pout = new PrintWriter(
                            new OutputStreamWriter(sock.getOutputStream()));


                    // write something to the server
                    pout.println(msg);

                    // make sure it went
                    pout.flush();

                    // print the something to the user
                    System.out.println("Message: " + msg);
                    //jtaChat.setText("");
                    jtaRecive.append("\n" + msg);


                    // Send the terminating string to the server
                    pout.println("quit");
                    pout.flush();

                    // close everything
                    pout.close();
                } catch (UnknownHostException uhe) {
                    System.out.println("What host you speak of?");
                } catch (IOException ioe) {
                    System.out.println("Bad IO?");
                    ioe.printStackTrace();
                }
            }
        }

    }

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("Exit")) {
            System.exit(0);
        } else if (event.getActionCommand().equals("Send")) {
            String chatMessage = jtaChat.getText();
            jtaChat.setText("");
            setClientComms(chatMessage);


        }
        else if (event.getActionCommand().equals("Confirm Port and Host Info")) {
            hostName = jtfHostName.getText();
            //NEED TO ADD IN WAY TO HANDLE IP ADDRESSES
            portNumber = Integer.parseInt(jtfPortName.getText());
            try {
                Socket s = new Socket(hostName, portNumber);
                ReceiveThread rc = new ReceiveThread(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {

        new H7Client();


    }
}
