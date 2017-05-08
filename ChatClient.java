import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ChatClient extends JPanel{
   final int PORT = 16789;
   private JTextArea jtaMessages;
   private JTextField jtfSendMessage;
   private ObjectInputStream ois;
   private ObjectOutputStream oout;
   private Socket socket;

   public ChatClient(String _IPADDR) {
       try{
           socket = new Socket(_IPADDR, 16789);
       }catch(IOException ioe){

       }
       createStreams();
       System.out.println("Created the ChatClientGUI constructor");
       setLayout(new BorderLayout());
       JLabel jlTitle =
			new JLabel("In Game Chat Client",JLabel.CENTER );
			
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

      jpSend.addActionListener(new ActionListener() {
             /**
              * Responds to pressing the enter key in the textfield by sending
              * the contents of the text field to the server.    Then clear
              * the text area in preparation for the next message.
              */
             public void actionPerformed(ActionEvent e) {
                 sendMessage();
             }
         });
      jpSendingInfo.add(jpSend);
      add( jpSendingInfo, BorderLayout.SOUTH);
      setVisible(true);
      jtfSendMessage.requestFocus();
      System.out.println("Just before createStreams");


          readMessage();

   }
   public void createStreams(){
       try {
           // open input stream
           oout = new ObjectOutputStream(socket.getOutputStream());
           ois = new ObjectInputStream(socket.getInputStream());

           // open output stream

           System.out.println("Created input and output streams");
       } catch(IOException ioe){

       }
   }
   public void readMessage() {

       while (true) {
           try {
               jtaMessages.append(ois.readUTF() + " \n");
           } catch (IOException ioe) {

           }
       }
   }
   public void sendMessage(){
       try{
           oout.writeUTF(jtfSendMessage.getText());
           oout.flush();
       }catch(IOException ioe){

       }
       jtfSendMessage.setText("");
   }
   public class ThreadedServer{
       
   }
}
