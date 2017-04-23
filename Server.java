import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Jacob on 4/23/2017.
 * Server for Main game and ChatClient
 */
public class Server extends JFrame{

    //JLabels
    private JLabel jlIP = new JLabel("IP");
    private JLabel jlPort = new JLabel("Port");

    //JTextFields
    private JTextField jtfIP = new JTextField(20);
    private JTextField jtfPort = new JTextField(5);

    //JTextArea
    private JTextArea jta = new JTextArea(50,40);

    //JButton
    private JButton jbConnect = new JButton("Connect");

    //JPanel
    private JPanel jpInput = new JPanel(new GridLayout(0,2));

    public static void main(String[] args){
        new Server();
    }

    private Server(){
        jpInput.add(jlIP);
        jpInput.add(jtfIP);
        jpInput.add(jlPort);
        jpInput.add(jtfPort);

        add(jpInput,BorderLayout.NORTH);
        add(jta, BorderLayout.CENTER);
        add(jbConnect, BorderLayout.SOUTH);
    }
}
