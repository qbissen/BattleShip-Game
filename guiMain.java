//package miniproject.src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Scanner;


/**
 * Created by Quinn on 2/21/2017.
 */
public class guiMain extends JFrame{
    private JButton[][] placementBoardArray; // 2D board of JLabels that allows a player to place and view their ships.
    private JButton[][] targetBoardArray; //2D board of JLabels that allows a player to target the other players ships.

    public final int columns = 10; //Columns in both JLabel boards.
    public final int rows = 10; //Rows in both JLabel boards.

    private JPanel backgroundBoard;
    private JPanel placementBoard;
    private JPanel targetBoard;
    private JPanel optionsBoard;
    private JPanel shipMonitorBoard;
    private JPanel friendlyMonitor;
    private JPanel enemyMonitor;
    private JPanel friendlyShipCheck;
    private JPanel enemyShipCheck;

    private JButton resetGame;
    private JButton nextTurn;
    private JButton exitButton;

    private ActionReciever action;

    private String greenName;
    private String orangeName;

    private JLabel greenLabel;
    private JLabel orangeLabel;

    ImageIcon battleshipImage = new ImageIcon("resources/battleship.png");
    ImageIcon cruiserImage = new ImageIcon("resources/cruiser.png");
    ImageIcon destroyerImage = new ImageIcon("resources/destroyer.png");
    ImageIcon subImage = new ImageIcon("resources/sub.png");

    private JLabel battleshipCheck = new JLabel(battleshipImage);
    private JLabel cruiserCheck = new JLabel(cruiserImage);
    private JLabel destroyerCheck = new JLabel(destroyerImage);
    private JLabel subCheck = new JLabel(subImage);

    private JLabel battleshipCheck1 = new JLabel(battleshipImage);
    private JLabel cruiserCheck1 = new JLabel(cruiserImage);
    private JLabel destroyerCheck1 = new JLabel(destroyerImage);
    private JLabel subCheck1 = new JLabel(subImage);

    private JLabel turnLabel;

    private ImageIcon smallBlack = new ImageIcon("resources/green.JPG"); //img used for player name joptionpane
    private ImageIcon smallRed = new ImageIcon("resources/orange.jpg"); //img used for player name joptionpane

    private int turnDirtyBit;

    private JButton button;

    BattleshipLogic logicClass = new BattleshipLogic();

    public guiMain(){
        createNewGame();
        logicClass.setFleetFormation();
        checkWin();
    }

    public void createNewGame(){
        try{
            JOptionPane.showMessageDialog(null,"This game consists of two players, one being green and the other being orange.\n The goal of the game is to sink the enemy ships by firing at positions in the grid. \n To start the game" +
                    "allow player one to place their ships, then hit the 'next turn' button and allow player two to place their fleet.\n Next, player one will fire at at position on player two's grid. \n" +
                    "this will continue until either player one or two have completely sunk the enemy fleet. ");
            JFrame pane = new JFrame();
            //Prompt for red name
            orangeName = (String) JOptionPane.showInputDialog(pane,"Enter player one's name:","Orange Player's Name",JOptionPane.QUESTION_MESSAGE,smallRed,null,null);
            while(orangeName.isEmpty())
            {
                JOptionPane.showMessageDialog(pane,"Please enter a name for Player one!","Orange Player Error",JOptionPane.WARNING_MESSAGE);
                orangeName = (String) JOptionPane.showInputDialog(pane,"Enter player one's name:","Orange Player's Name",JOptionPane.QUESTION_MESSAGE,smallRed,null,null);
            }

            //Prompt for black name
            greenName = (String) JOptionPane.showInputDialog(pane,"Enter player two's name:","Green Player Name",JOptionPane.QUESTION_MESSAGE,smallBlack,null,null);
            while(greenName.isEmpty())
            {
                JOptionPane.showMessageDialog(pane,"Please enter a name for Player two!","Green Player Error",JOptionPane.WARNING_MESSAGE);
                greenName = (String) JOptionPane.showInputDialog(pane,"Enter player two's name:","Green Player Name",JOptionPane.QUESTION_MESSAGE,smallBlack,null,null);
            }

            buildGameBoard();
            randomizeTurn();

        }
        catch(Exception nameError)
        {
            System.exit(0);
        }
    }

    public void buildGameBoard(){
        backgroundBoard = new JPanel(new BorderLayout());

        optionsBoard = new JPanel(new FlowLayout());

        shipMonitorBoard = new JPanel(new BorderLayout());
        friendlyMonitor = new JPanel(new BorderLayout());
        enemyMonitor = new JPanel(new BorderLayout());

        targetBoard = new JPanel(new GridLayout(10,10));
        placementBoard = new JPanel(new GridLayout(10,10));

        friendlyShipCheck = new JPanel(new GridLayout(1,4));
        enemyShipCheck = new JPanel(new GridLayout(1,4));


        placementBoardArray = new JButton[rows][columns];
        targetBoardArray = new JButton[rows][columns];

        resetGame = new JButton("Reset Game");
        nextTurn = new JButton("Next Turn");
        exitButton = new JButton("Exit");
        greenLabel = new JLabel();
        orangeLabel = new JLabel();
        turnLabel = new JLabel("");

        createPanels();

        setLayout(new BorderLayout());
        setVisible(true);
        setSize(1000,1000);
        setLocation(500,0);
        setTitle("Quinn and Joe's Battleship Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(backgroundBoard,BorderLayout.CENTER);
        add(optionsBoard,BorderLayout.NORTH);
        add(shipMonitorBoard,BorderLayout.WEST);

        backgroundBoard.add(targetBoard,BorderLayout.NORTH);
        backgroundBoard.add(placementBoard,BorderLayout.SOUTH);

        shipMonitorBoard.add(turnLabel,BorderLayout.CENTER);

        shipMonitorBoard.add(friendlyMonitor,BorderLayout.NORTH);
        friendlyMonitor.setBackground(Color.green);
        friendlyMonitor.add(greenLabel,BorderLayout.NORTH);
        friendlyMonitor.add(friendlyShipCheck,BorderLayout.CENTER);
        friendlyShipCheck.add(battleshipCheck);
        friendlyShipCheck.add(cruiserCheck);
        friendlyShipCheck.add(destroyerCheck);
        friendlyShipCheck.add(subCheck);

        shipMonitorBoard.add(enemyMonitor,BorderLayout.SOUTH);
        enemyMonitor.setBackground(Color.orange);
        enemyMonitor.add(orangeLabel,BorderLayout.NORTH);
        enemyMonitor.add(enemyShipCheck,BorderLayout.CENTER);
        enemyShipCheck.add(battleshipCheck1);
        enemyShipCheck.add(cruiserCheck1);
        enemyShipCheck.add(destroyerCheck1);
        enemyShipCheck.add(subCheck1);

        optionsBoard.add(resetGame);
        optionsBoard.add(nextTurn);
        optionsBoard.add(exitButton);

        greenLabel.setText(greenName+"'s Fleet Status");
        orangeLabel.setText(orangeName+"'s Fleet Status");

        exitButton.addActionListener(listenerTop);

    }

    public void createPanels(){
        for(int c = 0;c<rows;c++)
        {
            for(int g = 0;g<columns;g++) {
                button = new JButton();
                button.setPreferredSize(new Dimension(40, 40));
                try {
                    Image img = ImageIO.read(getClass().getResource("resources/tileImage.jpg"));
                    button.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                placementBoardArray[c][g] = button;
                placementBoardArray[c][g].putClientProperty("column", g);
                placementBoardArray[c][g].putClientProperty("row", c);
                placementBoardArray[c][g].addActionListener(listenerBottom);
                placementBoard.add(placementBoardArray[c][g]);

                JButton button1 = new JButton();
                button1.setPreferredSize(new Dimension(40, 40));

                try {
                    Image img = ImageIO.read(getClass().getResource("resources/tileImage.jpg"));
                    button1.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                targetBoardArray[c][g] = button1;
                targetBoardArray[c][g].putClientProperty("column", g);
                targetBoardArray[c][g].putClientProperty("row", c);
                targetBoardArray[c][g].addActionListener(listenerTop);
                targetBoard.add(targetBoardArray[c][g]);
            }
        }

    }

    public void randomizeTurn()
    {
        double rand = Math.random();
        turnDirtyBit = (1+(int)(rand * 2));
        checkTurn(); //calls method to check the turn bit

    }

    public void checkTurn()
    {
        switch(turnDirtyBit)
        {
            case 1:
                turnLabel.setText(greenName +"'s turn");
                System.out.println(turnDirtyBit);
                break;
            case 2:
                turnLabel.setText(orangeName +"'s turn");
                System.out.println(turnDirtyBit);
                break;
        }
    }

    public void changeTurn()
    {
        if(turnDirtyBit == 1)
        {
            turnLabel.setText(greenName+"'s Turn");
            turnDirtyBit = 2;
            //placementBoardArray[c][g].setEnabled();
            //targetBoardArray[][].removeActionListener(listener);
            //button.setEnabled(false);
            System.out.println("Changed turn");
            checkWin();
        }
        else if(turnDirtyBit == 2)
        {
            turnLabel.setText(orangeName+"'s Turn");
            turnDirtyBit = 1;
            //placementBoardArray[][].addActionListener(listener);
            //targetBoardArray[][].removeActionListener(listener);
            System.out.println("Changed turn");
            checkWin();
        }
    }

    public void checkWin(){
        int sum = 0;
        int[][]arrLeng = logicClass.getLogicTopBoard();

        for (int i = 0; i < arrLeng.length; i++)
            for (int j = 0; j < arrLeng[i].length; j++)
                sum += arrLeng[i][j];
        System.out.println(sum);
    }

    ActionListener listenerTop = new ActionListener(){
        @Override
        //Uses Bottom Board on GUI

        public void actionPerformed(ActionEvent eventTop){
            Object source = eventTop.getSource();
            if (eventTop.getActionCommand().equals("Exit")){
                System.exit(0);
            }
            else if(eventTop.getActionCommand().equals("Next")){

            }
            else if(eventTop.getActionCommand().equals("Reset")){

            }
            else if(eventTop.getSource()instanceof JButton){
                    changeTurn();
                    JButton btn = (JButton) eventTop.getSource();
                    int arrRow = (int)(btn.getClientProperty("row"));
                    int arrCol = (int)(btn.getClientProperty("column"));
                    int arrPos[][] = logicClass.getLogicTopBoard();

                    if(arrPos[arrRow][arrCol]== 0){
                        if(eventTop.getSource()instanceof JButton){
                            try {
                                Image img = ImageIO.read(getClass().getResource("resources/miss.jpg"));
                                ((JButton)eventTop.getSource()).setIcon(new ImageIcon(img));

                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        }
                        else{
                            System.out.println("Not Exit");
                        }
                    }
                    else if(arrPos[arrRow][arrCol]== 1){
                        System.out.println("Got a One");

                    }
                    else if(arrPos[arrRow][arrCol]== 2){
                        System.out.println("Got a two");
                        if(eventTop.getSource()instanceof JButton){
                            try {
                                System.out.println("test");
                                Image img = ImageIO.read(getClass().getResource("resources/hit.jpg"));
                                ((JButton)eventTop.getSource()).setIcon(new ImageIcon(img));
                                arrPos[arrRow][arrCol] = 3;

                                logicClass.setLogicBottomBoard(arrPos);
                                System.out.println("setting 2 equal to 3");

                            } catch (Exception ex) {
                                System.out.println(ex);
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
        }
    };
    ActionListener listenerBottom = new ActionListener(){
        @Override
        //Uses Bottom Board on GUI
        public void actionPerformed(ActionEvent eventBottom){
            Object source = eventBottom.getSource();
            if (eventBottom.getActionCommand().equals("Exit")){
                System.exit(0);
            }
            else if(eventBottom.getActionCommand().equals("Next")){

            }
            else if(eventBottom.getActionCommand().equals("Reset")){

            }
            else if(eventBottom.getSource()instanceof JButton){
                changeTurn();
                JButton btn = (JButton) eventBottom.getSource();
                int arrRow = (int)(btn.getClientProperty("row"));
                int arrCol = (int)(btn.getClientProperty("column"));
                int arrPos[][] = logicClass.getLogicBottomBoard();

                if(arrPos[arrRow][arrCol]== 0){
                    if(eventBottom.getSource()instanceof JButton){
                        try {
                            Image img = ImageIO.read(getClass().getResource("resources/miss.jpg"));
                            ((JButton)eventBottom.getSource()).setIcon(new ImageIcon(img));

                            arrPos[arrRow][arrCol] = 3;
                            logicClass.setLogicTopBoard(arrPos);
                            System.out.println("setting 2 equal to 3 fuq");

                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }
                    else{
                        System.out.println("Not Exit");
                    }
                }
                else if(arrPos[arrRow][arrCol]== 1){
                    System.out.println("Got a One");

                }
                else if(arrPos[arrRow][arrCol]== 2){
                    if(eventBottom.getSource()instanceof JButton){
                        try {

                            Image img = ImageIO.read(getClass().getResource("resources/hit.jpg"));
                            ((JButton)eventBottom.getSource()).setIcon(new ImageIcon(img));

                        } catch (Exception ex) {
                            System.out.println(ex);
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
        }

    };

    public static void main(String []args){
        new guiMain();
    }
}
