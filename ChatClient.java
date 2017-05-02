import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient extends JFrame
{
   static String IP_ADDR;
   final int PORT = 16789;

   Socket sock;

   JTextArea jtaMessages;
   JTextField jtfSendMessage;
   BufferedReader brInput;
   PrintWriter pwOutput;

   public static void main(String[] args) 
   {
		if( args.length == 1){
			IP_ADDR = args[0];
		}
		else	
		{
			System.out.println("No IP address on command line, using localhost.");
			System.out.println("Usage: java ChatClient <ChatServerIPAddress>");
			IP_ADDR = "localhost";
		}
      new ChatClient();
   }

   public ChatClient() {
   
  
      setTitle("Chat Client");
  
      JLabel jlTitle = 
			new JLabel("<html><b>Easy Chat Client</b></html>",JLabel.CENTER );
			
      add( jlTitle, BorderLayout.NORTH );
  
      jtaMessages = new JTextArea(20,30);
         jtaMessages.setLineWrap(true);
         jtaMessages.setWrapStyleWord(true);
        	jtaMessages.setEditable(false);
       
         JScrollPane jspText = new JScrollPane(jtaMessages);
			
			add(jspText, BorderLayout.CENTER );

      // Add a field for message entry and a SEND button to send it
      JPanel jpSendingInfo = new JPanel();
         jtfSendMessage = new JTextField(25);
         jpSendingInfo.add(jtfSendMessage);
         JButton jpSend = new JButton("Send");
         jpSend.addActionListener(new SendButtonListener());
         jpSendingInfo.add(jpSend);
      add( jpSendingInfo, BorderLayout.SOUTH);
   
        // GUI has been built, display it centered
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
      setVisible(true);
		jtfSendMessage.requestFocus();
      
      try {
         sock = new Socket("localhost", 16789);
         brInput = new BufferedReader(new InputStreamReader(sock.getInputStream()));
         pwOutput = new PrintWriter(sock.getOutputStream());
         
         new ReceiveMessage().start();
         }
      catch(Exception e) {
         e.printStackTrace();
      }
         
   }


   public class SendButtonListener implements ActionListener
   {
      public void actionPerformed(ActionEvent evt) 
      {
         String data = jtfSendMessage.getText();
         if(data != null && !data.isEmpty()) {
            pwOutput.println(data);
            pwOutput.flush();
            
            jtfSendMessage.setText("");
         }
      }
   }
   
   class ReceiveMessage extends Thread {
      public void run() {
         String message = "";
         try {
            while((message = brInput.readLine()) != null) {
               jtaMessages.append(message + "\n");
            } 
            }
         catch(Exception e) {
            e.printStackTrace();
         }
      }

   }

} 