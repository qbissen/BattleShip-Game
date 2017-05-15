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
public class GuiServer extends JFrame{

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
 
   private int numberOfPlayers = 0;

   public static void main(String[] args){
      new GuiServer();
   }

   private GuiServer(){
   
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
   
      //jbStart.addActionListener(this);
      
      LogicServer ls = new LogicServer(this, jta, jbStart, jlIP);
            jbStart.addActionListener(ls);

      
      setLocationRelativeTo(null);
      setSize(500,500);
      setVisible(true);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
   
   }
   }