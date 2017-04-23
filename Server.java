import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Created by Jacob on 4/23/2017.
 * Server for Main game and ChatClient
 */
public class Server extends JFrame{

    //JLabels
    private JLabel jlIP = new JLabel("IP  ");
    private JLabel jlPort = new JLabel("Port  ");

    //JTextFields
    private JTextField jtfIP = new JTextField(20);
    private JTextField jtfPort = new JTextField(5);

    //JTextArea
    private JTextArea jta = new JTextArea(20,40);

    //JButton
    private JButton jbConnect = new JButton("Connect");

    //JPanels
    private JPanel jpInput = new JPanel();
    private JPanel jpButton = new JPanel();
    private JPanel jpTextArea = new JPanel();

    //Border for JTextArea
    private Border border = BorderFactory.createLineBorder(Color.BLACK);
    public static void main(String[] args){
        new Server();
    }

    private Server(){
        jta.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        jta.setEnabled(false);

        jpInput.add(jlIP);
        jpInput.add(jtfIP);
        jpInput.add(jlPort);
        jpInput.add(jtfPort);

        jpTextArea.add(jta);

        jpButton.add(jbConnect);

        jlIP.setHorizontalAlignment(JLabel.RIGHT);
        jlPort.setHorizontalAlignment(JLabel.RIGHT);

        add(jpInput,BorderLayout.NORTH);
        add(jpTextArea, BorderLayout.CENTER);
        add(jpButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setSize(500,500);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
