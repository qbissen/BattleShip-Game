import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient extends JPanel
{
   final int PORT = 16789;
   private JTextArea jtaMessages;
   private JTextField jtfSendMessage;
   private BufferedReader brInput;
   private PrintWriter pwOutput;
   private ObjectOutputStream oos;
   private ObjectInputStream ois;

   public ChatClient(String _hostName) {
      setLayout(new BorderLayout());
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
      setVisible(true);
		jtfSendMessage.requestFocus();
      
      try {
         Socket sock = new Socket(_hostName, 16789);

         OutputStream out = sock.getOutputStream();
         oos = new ObjectOutputStream(out);

         InputStream in = sock.getInputStream();
         ois = new ObjectInputStream(in);
         
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
            try {
               oos.writeUTF("CHAT");
               oos.writeUTF(data);
               oos.flush();
            }catch(IOException io){

            }
            
            jtfSendMessage.setText("");
         }
      }
   }
   
   class ReceiveMessage extends Thread {
      public void run() {
         String message = "";
         try {
               jtaMessages.append(message + "\n");
            }
         catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
} 