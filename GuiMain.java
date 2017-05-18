import com.sun.xml.internal.bind.v2.TODO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Quinn on 2/21/2017.
 * guiMain creates and adds listeners for the GUI, it also sets up the actions that the user can input into the game.
 * It can communicate with BattleshipLogic to place fleets.
 */
public class GuiMain extends JFrame{
   private JButton[][] bottomBoardArray; // 2D board of JLabels that allows a player to place and view their ships.
   private JButton[][] topBoardArray; //2D board of JLabels that allows a player to target the other players ships.
   private final int columns = 10; //Columns in both JLabel boards.
   private final int rows = 10; //Rows in both JLabel boards.
   private JPanel bottomBoardPanel; //JPanel for the top board
   private JPanel topBoardPanel; //JPanel for the bottom board
   private String greenName; //Green player's name label
   private String orangeName; //Orange player's name label
   private ImageIcon battleshipImage = new ImageIcon("resources/battleship.png");
   private ImageIcon cruiserImage = new ImageIcon("resources/cruiser.png");
   private ImageIcon destroyerImage = new ImageIcon("resources/destroyer.png");
   private ImageIcon subImage = new ImageIcon("resources/sub.png");
   private JLabel battleshipCheck = new JLabel(battleshipImage);
   private JLabel cruiserCheck = new JLabel(cruiserImage);
   private JLabel destroyerCheck = new JLabel(destroyerImage);
   private JLabel subCheck = new JLabel(subImage);
   private JLabel battleshipCheck1 = new JLabel(battleshipImage);
   private JLabel cruiserCheck1 = new JLabel(cruiserImage);
   private JLabel destroyerCheck1 = new JLabel(destroyerImage);
   private JLabel subCheck1 = new JLabel(subImage);
   private JLabel turnLabel;
   private ImageIcon smallGreen = new ImageIcon("resources/green.JPG"); //img used for player name joptionpane
   private ImageIcon smallOrange = new ImageIcon("resources/orange.jpg"); //img used for player name joptionpane
   private int whoTurn;



   //Chat client variables
   final int PORT = 16789;
   private JTextArea jtaMessages;
   private JTextField jtfSendMessage;
   private ObjectInputStream ois;
   private ObjectOutputStream oout;
   private Socket socket;

   private ChatClient chatClient;
   
   private int im=0;

   public static void main(String []args){
   
      new GuiMain();
   
   }
   private BattleshipLogic logicClass = new BattleshipLogic();
   /*
      *This is the constructor for the GUI and action listeners.
      * It also calls calls the method to place the fleets on the board.
    */
   private GuiMain(){
   
      createNewGame();
      logicClass.setFleetFormation();
      showFriendlyFleet();
   }
   /*
       *createNewGame gathers user input for their name, then sets the board. It also calls the method to see what player's turn it is.
    */
   private void createNewGame(){
       String IP_ADDR;
      try{
         JOptionPane.showMessageDialog(null,"This game consists of two players, one being green and the other being orange.\n The goal of the game is to sink the enemy ships by firing at positions in the grid. \n " +
                "The game will place each players fleet for them so that neither player will be able to view the\n" +
                " other person placing ships and gain an unfair advantage. Once in the game, players can fire at ANY position, take care not to \n" +
                "hit your own ships! Have fun and we hope that you enjoy the game.");
         JFrame pane = new JFrame();
         //Prompt for orange name
         orangeName = (String) JOptionPane.showInputDialog(pane,"Enter your user name.","Orange Player's Name",JOptionPane.QUESTION_MESSAGE, smallOrange,null,null);
         while(orangeName.isEmpty())
         {
            JOptionPane.showMessageDialog(pane,"Please enter a user name!","Orange Player Error",JOptionPane.WARNING_MESSAGE);
            orangeName = (String) JOptionPane.showInputDialog(pane,"Enter a user name.","Orange Player's Name",JOptionPane.QUESTION_MESSAGE, smallOrange,null,null);
         }
         IP_ADDR = (String) JOptionPane.showInputDialog(pane, "Enter the IP Address of the server", JOptionPane.QUESTION_MESSAGE );

         String player = (String) JOptionPane.showInputDialog(pane, "Are you a player, Enter either 'true' or 'false'", JOptionPane.QUESTION_MESSAGE);
         chatClient = new ChatClient(IP_ADDR);
         chatClient.isPlayer(player);
         buildGameBoard();
         //randomizeTurn();
      }
      catch(Exception nameError)
      {
         System.exit(0);
      }
   }

   /*
       *BuildGameBoard constructs the GUI but relies on the createPanels method to build the two grids that hold the fleets.
    */
   private void buildGameBoard(){
       JLabel greenLabel=null;
      JPanel backgroundBoard = new JPanel(new BorderLayout());
      JPanel optionsBoard = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JPanel shipMonitorBoard = new JPanel(new BorderLayout());
      JPanel friendlyMonitor = new JPanel(new BorderLayout());
      JPanel enemyMonitor = new JPanel(new BorderLayout());
   
      JPanel topBoardGlassPanel = new JPanel();
      topBoardGlassPanel.setOpaque(false);
      topBoardGlassPanel.setBackground(Color.gray);
      topBoardPanel = new JPanel(new GridLayout(10,10));
   
      JPanel bottomBoardGlassPanel = new JPanel();
      bottomBoardGlassPanel.setOpaque(false);
      bottomBoardGlassPanel.setBackground(Color.gray);
      bottomBoardPanel = new JPanel(new GridLayout(10,10));
   
      JPanel friendlyShipCheck = new JPanel(new GridLayout(2,2));
      JPanel enemyShipCheck = new JPanel(new GridLayout(2,2));
   
      bottomBoardArray = new JButton[rows][columns];
      topBoardArray = new JButton[rows][columns];
   
      JButton exitButton = new JButton("Exit");
      JButton resetButton = new JButton("Reset Game");
   
       greenLabel = new JLabel();
      JLabel orangeLabel = new JLabel();
      turnLabel = new JLabel("");
   
   
      createPanels();
   
      setLayout(new BorderLayout());
      setVisible(true);
      setSize(1200,1000);
      setLocation(500,0);
      setTitle("Quinn and Joe's Battleship Game");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   
      add(backgroundBoard,BorderLayout.CENTER);
      add(optionsBoard,BorderLayout.NORTH);
      add(shipMonitorBoard,BorderLayout.WEST);
      add(chatClient, BorderLayout.EAST);
   
      backgroundBoard.add(topBoardPanel,BorderLayout.NORTH);
      backgroundBoard.add(bottomBoardPanel,BorderLayout.SOUTH);
   
      shipMonitorBoard.add(turnLabel,BorderLayout.CENTER);
      shipMonitorBoard.add(friendlyMonitor,BorderLayout.NORTH);
      shipMonitorBoard.add(enemyMonitor,BorderLayout.SOUTH);
   
      friendlyMonitor.setBackground(Color.green);
      friendlyMonitor.add(greenLabel,BorderLayout.NORTH);
      friendlyMonitor.add(friendlyShipCheck,BorderLayout.CENTER);
      friendlyShipCheck.add(battleshipCheck);
      friendlyShipCheck.add(cruiserCheck);
      friendlyShipCheck.add(destroyerCheck);
      friendlyShipCheck.add(subCheck);
   
      enemyMonitor.setBackground(Color.orange);
      enemyMonitor.add(orangeLabel,BorderLayout.NORTH);
      enemyMonitor.add(enemyShipCheck,BorderLayout.CENTER);
      enemyShipCheck.add(battleshipCheck1);
      enemyShipCheck.add(cruiserCheck1);
      enemyShipCheck.add(destroyerCheck1);
      enemyShipCheck.add(subCheck1);
      optionsBoard.add(exitButton);
      optionsBoard.add(resetButton);
      greenLabel.setText(greenName+"'s Fleet");
      orangeLabel.setText(orangeName+"'s Fleet");
      exitButton.addActionListener(listenerTop);
      resetButton.addActionListener(listenerTop);
   }
   /*
       *createPanels uses a double for loop to iteratively build the two grids that hold the ships
       * It will also set the icon images and add action listeners to the grids
    */
   private void createPanels(){
       JButton button;
       JButton button1;
      for(int c = 0;c<rows;c++)
      {
         for(int g = 0;g<columns;g++) {
            button = new JButton();
            button.setPreferredSize(new Dimension(40, 40));
            try {
               Image img = ImageIO.read(getClass().getResource("resources/tileImage.jpg"));
               button.setIcon(new ImageIcon(img));
            } 
            catch (Exception ex) {
               System.out.println("Failed to create an icon");
            }
            bottomBoardArray[c][g] = button;
            bottomBoardArray[c][g].putClientProperty("column", g);
            bottomBoardArray[c][g].putClientProperty("row", c);
            bottomBoardArray[c][g].addActionListener(listenerBottom);
            bottomBoardPanel.add(bottomBoardArray[c][g]);
            button1 = new JButton();
            button1.setPreferredSize(new Dimension(40, 40));
            try {
               Image img = ImageIO.read(getClass().getResource("resources/tileImage.jpg"));
               button1.setIcon(new ImageIcon(img));
            } 
            catch (Exception ex) {
               System.out.println("Failed to create an icon");
            }
            topBoardArray[c][g] = button1;
            topBoardArray[c][g].putClientProperty("column", g);
            topBoardArray[c][g].putClientProperty("row", c);
            topBoardArray[c][g].addActionListener(listenerTop);
            topBoardPanel.add(topBoardArray[c][g]);
         }
      }
   }
   /*
       *randomizeTurn randomizes the players turn and sets this equal to whoTurn
    */

   /*
       *checkTurn sets the JLabels to determine what players turn it is.
    */
   private void checkTurn()
   {
      switch(whoTurn)
      {
         case 1:
            turnLabel.setText(greenName +"'s turn");
            System.out.println(whoTurn);
            break;
         case 2:
            turnLabel.setText(orangeName +"'s turn");
            System.out.println(whoTurn);
            break;
      }
   }
   /*
       *changeTurn changes the players turn after a player clicks on a grid
    */
   private void changeTurn()
   {
      if(whoTurn == 1)
      {
         turnLabel.setText(greenName+"'s Turn");
         whoTurn = 2;
         //bottomBoardArray[c][g].setEnabled();
         //topBoardArray[][].removeActionListener(listener);
         //button.setEnabled(false);
         System.out.println("Changed turn");
         for(int i=0; i< 10; i++){
            for(int j=0; j<10; j++){
               topBoardArray[i][j].setEnabled(false);
               bottomBoardArray[i][j].setEnabled(false);
            }
         }
      }
      else if(whoTurn == 2)
      {
         turnLabel.setText(orangeName+"'s Turn");
         whoTurn = 1;
         //bottomBoardArray[][].addActionListener(listener);
         //topBoardArray[][].removeActionListener(listener);
         System.out.println("Changed turn");
         for(int i=0; i< 10; i++){
            for(int j=0; j<10; j++){
               bottomBoardArray[i][j].setEnabled(true);
               topBoardArray[i][j].setEnabled(false);
            }
         }
      }
   }
   /*
       *Check win determines which player won the game. It adds up the sum of the array list. Once the sum hits
       * 36 the game is over. winLogic is then called.
    */
   private void checkWin(int [][] _logic, String _whoWon){
      int sum = 0;
      for (int i = 0; i < _logic.length; i++)
         for (int j = 0; j < _logic[i].length; j++)
            sum += _logic[i][j];
      if(sum == 36){
         if(_whoWon == "Top" ){
            winLogic(greenName);
         }
         else if(_whoWon == "Bottom"){
            winLogic(greenName);
         }
      }
   }
   /*
       *winLogic creates a new JFrame that tells the players who won the game. It also offers the players a chance to play a new game.
    */
   private void winLogic(String _name){
      System.out.println(_name + " WINS!");
      setVisible(false);
      dispose();
      newGameQuestion(_name);
   }
   /*
       *getThatButton simply gets the source of a JButton when it is clicked and changes the icon.
    */
   private void getThatButton(ActionEvent _event){
      if(_event.getSource()instanceof JButton){
         try {
            Image img = ImageIO.read(getClass().getResource("resources/miss.jpg"));
            ((JButton)_event.getSource()).setIcon(new ImageIcon(img));
         } 
         catch (Exception ex) {
            System.out.println("Failed to create an icon");
         }
      }
      else{
         System.out.println("Not Exit");
      }
   }
   /*
       *newGameQuestion creates the JFrame for the new game option.
    */
   private void newGameQuestion(String _winner){
      JFrame newGameFrame = new JFrame();
      JPanel bottomPanel = new JPanel();
      JLabel winnerWinnerChickenDinner = new JLabel(_winner + " WINS!!!");
      winnerWinnerChickenDinner.setHorizontalAlignment(SwingConstants.CENTER);
      JButton newGame = new JButton("Start A New Game?");
      JButton exit = new JButton("Exit");
      newGameFrame.setLayout(new BorderLayout());
      newGameFrame.setVisible(true);
      newGameFrame.setSize(250,250);
      newGameFrame.setLocation(500,0);
      newGameFrame.setTitle("New Game?");
      newGameFrame.setDefaultCloseOperation(newGameFrame.EXIT_ON_CLOSE);
      newGameFrame.add(winnerWinnerChickenDinner,BorderLayout.CENTER);
      newGameFrame.add(bottomPanel,BorderLayout.SOUTH);
      bottomPanel.add(newGame, BorderLayout.EAST);
      bottomPanel.add(exit, BorderLayout.WEST);
      exit.addActionListener(listenerTop);
      newGame.addActionListener(listenerTop);
   }
   private void showFriendlyFleet(){
      int friendlyFleetArray[][] = logicClass.getLogicTopBoard();
   
      for(int i = 0; i < friendlyFleetArray.length; i++){
         for(int j = 0; j< friendlyFleetArray.length; j++){
            if(friendlyFleetArray[i][j]==2){
               try {
                  Image img = ImageIO.read(getClass().getResource("resources/ship.png"));
                  bottomBoardArray[i][j].setIcon(new ImageIcon(img));
               } 
               catch (Exception ex) {
                  System.out.println("Failed to create an icon in showFriendlyFleet method");
               }
            }
         }
      }
   }
   /*
       *The two ActionListener classes listen for actions. Once a button is clicked they perform the action specified.
       * There is a anonymous inner classes for each grid of JButtons.
    */
   ActionListener listenerTop = 
      new ActionListener(){
         @Override
         //Uses Bottom Board on GUI
         public void actionPerformed(ActionEvent eventTop){
            if (eventTop.getActionCommand().equals("Exit")){
               System.exit(0);
            }
            else if(eventTop.getActionCommand().equals("Reset Game")){
            
            }
            else if(eventTop.getActionCommand().equals("Start A New Game?")){
               new GuiMain();
            }
            else if(eventTop.getSource()instanceof JButton){
            /*
            *If a JButton in the grid is hit, the game will check the logic array to see if there is either a 0, 2, or 3
            * If there is a 0, the icon will be changed to be a miss.
            * If there is a 2, the icon will be changed to be a hit marker. That position in the logic array will also become a 3.
            * If there is a 3, there is a hit ship.
            */
               changeTurn();
               JButton btn = (JButton) eventTop.getSource();
               int arrRow = (int)(btn.getClientProperty("row"));
               int arrCol = (int)(btn.getClientProperty("column"));
               int arrPos[][] = logicClass.getLogicTopBoard();
               chatClient.sendFireLocation(arrRow,arrCol);
               if(arrPos[arrRow][arrCol]== 0){
                  getThatButton(eventTop);
               }
               else if(arrPos[arrRow][arrCol]== 1){
                  System.out.println("Got a One");
               }
               else if(arrPos[arrRow][arrCol]== 2){
                  if(eventTop.getSource()instanceof JButton){
                     try {
                        Image img = ImageIO.read(getClass().getResource("resources/hit.jpg"));
                        ((JButton)eventTop.getSource()).setIcon(new ImageIcon(img));
                        arrPos[arrRow][arrCol] = 3;
                        logicClass.setLogicTopBoard(arrPos);
                     } 
                     catch (Exception ex) {
                        System.out.println("Failed to create an icon");
                     }
                  }
                  else{
                     System.out.println("Not Exit");
                  }
               }
               else {
                  System.out.println("3");
               }
            }
            else{
               System.out.println("Not Exit");
            }
            checkWin(logicClass.getLogicTopBoard(), "Bottom");
         }
      };
   private ActionListener listenerBottom = 
      new ActionListener(){
         @Override
         //Uses Bottom Board on GUI
         public void actionPerformed(ActionEvent eventBottom){
            if (eventBottom.getActionCommand().equals("Exit")){
               System.exit(0);
            }
            else if(eventBottom.getSource()instanceof JButton){
               changeTurn();
            
               JButton btn = (JButton) eventBottom.getSource();
               int arrRow = (int)(btn.getClientProperty("row"));
               int arrCol = (int)(btn.getClientProperty("column"));
               int arrPos[][] = logicClass.getLogicBottomBoard();
               chatClient.sendFireLocation(arrRow,arrCol);
               if(arrPos[arrRow][arrCol]== 0){
                  getThatButton(eventBottom);
               }
               else if(arrPos[arrRow][arrCol]== 1){
                  System.out.println("Got a One");
               }
               else if(arrPos[arrRow][arrCol]== 2){
                  if(eventBottom.getSource()instanceof JButton){
                     try {
                        Image img = ImageIO.read(getClass().getResource("resources/hit.jpg"));
                        ((JButton)eventBottom.getSource()).setIcon(new ImageIcon(img));
                        arrPos[arrRow][arrCol] = 3;
                        logicClass.setLogicBottomBoard(arrPos);
                     } 
                     catch (Exception ex) {
                        System.out.println("Failed to create an icon");
                     }
                  }
                  else{
                     System.out.println("Not Exit");
                  }
               }
               else {
                  System.out.println("3");
               }
            }
            else{
               System.out.println("Not Exit");
            }
            checkWin(logicClass.getLogicBottomBoard(), "Top");
         }
      };

   public class ChatClient extends JPanel implements Runnable{
      public ChatClient(String _IPADDR) {
         try{
            socket = new Socket(_IPADDR, 16789);
         }
         catch(IOException ioe){
         
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
      
         jpSend.addActionListener(
            new ActionListener() {
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
      
      
         Thread cc = new Thread(this);//readMessage();
         cc.start();
      }
      public void createStreams(){
         try {
            // open input stream
           oout = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
         
            // open output stream
         
            System.out.println("Created input and output streams");
         } 
         catch(IOException ioe){
         
         }
      }
   
      public void sendMessage(){
         try{
            oout.writeUTF("CHAT");
            oout.writeUTF(jtfSendMessage.getText());
            oout.flush();
         }
         catch(IOException ioe){
         
         }
         jtfSendMessage.setText("");
      }
      public void sendFireLocation(int _arrRow,int _arrCol){
         try{
            oout.writeUTF("DATA");
            oout.writeInt(_arrRow);
            oout.writeInt(_arrCol);
            oout.flush();
         }
         catch(IOException ioe){
         
         }
      }
      public void sendResult(int _isHit) {
      
      }
   
      public void isPlayer(String _isPlayer){
         if(_isPlayer.equals("true")){
            try{
               System.out.println("Hit isPlayer");
               oout.writeUTF("PLAYER");
               oout.writeUTF("true");
               oout.flush();
            }
            catch (IOException ioe){
            
            }
         }
         else{
            System.out.println("Player == false");
         }
      }
   
      public void run(){
         String mes = "";
         int targetRow;
         int targetCol;
         int fireSpot[][] = new int[10][10];

         try {
            while(true){
               //read in the first line to determine what type of information is being sent in
               String command = ois.readUTF();
               System.out.println(command);
            
               //If a message is being sent from the chat
               if(command.equals("CHAT")){
                  //                        String username = ois.readUTF();
                  mes = ois.readUTF();
                  System.out.println(mes);
                  jtaMessages.append(mes + " \n");
               }
               else if(command.equals("SPECTATOR-CHAT")){
               
               }
               else if(command.equals("DATA")){
                  targetRow = ois.readInt();
                  targetCol = ois.readInt();
                  System.out.println(targetRow + " " + targetCol);
                  //This may produce a null value ~~~~Check here if you are getting errors reguarding the reading in of fire locations.
                   fireSpot[targetRow][targetCol] = 3;
                  logicClass.setLogicBottomBoard(fireSpot);
                   System.out.println(fireSpot[targetRow][targetCol] + " array location for incoming fire.");
               }
               else if(command.equals("RESULT")) {
               
               }
               else if(command.equals("DECLARE-WINNER")){
               
               }
               else if(command.equals("WHOTURN")){
                  whoTurn = ois.readInt();
                  System.out.println(whoTurn);
                  checkTurn();
               }
               else if(command.equals("YOUARE")){
                   im = ois.readInt();
                  
                  System.out.println("im player " + im);
                  
               }
               else if(command.equals("READY")){
                  oout.writeUTF("IMP" + im);
                  oout.writeUTF(orangeName);
                  oout.flush();
                  
               }
               else if(command.equals("ENAME")){
                  System.out.println(ois.readUTF());
               }
            
            
            
            
            }
         }
         catch (IOException ioe){
         
         }
      
      }
   }
}
