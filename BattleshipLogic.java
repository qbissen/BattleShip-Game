import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by Quinn on 3/21/2017.
 * This class deals with placing the ships on the board. It deals with most of the underlying logic in the game.
 */
public class BattleshipLogic {
    public int[][] logicTopBoard = new int[10][10];
    public int[][] logicBottomBoard = new int[10][10];
    private static int[] shipLengths = {2, 3, 3, 4};

    public BattleshipLogic() {
    }
    /*
        *setFleetFormation calls the two methods that place the fleets
     */
    public void setFleetFormation() {

        findFleetStart(logicTopBoard);
        findFleetStart(logicBottomBoard);
    }

    /*
        *The following methods get and set the logic board. This is used for fleet placement.
     */
    public int[][] getLogicTopBoard() {
        return logicTopBoard;
    }

    public int[][] getLogicBottomBoard(){
        return logicBottomBoard;
    }

    public void setLogicTopBoard(int[][] _logicTopBoard){
        logicTopBoard = _logicTopBoard;
    }

    public void setLogicBottomBoard(int[][] _logicBottomBoard){
        logicBottomBoard = _logicBottomBoard;
    }

    /*
        *findFleetStart places all of the fleets for the top and bottom board.
        * It starts by entering into a for loop that iterates for the the length of the ship. If a ship is 3 long this will be done three times.
        * Next, it checks to see if a ship segment has been placed in the location it is checking. Once this is completed it
        * generates a random row and column position to see where the fleet will be placed. Next is check to see if that ship will be placed
        * vertically or horizontally. After this it checks to see if the ship will go out of bounds, if this happens it will start the process over.
        * Once this is completed it will place the ship segments.
     */
    public void findFleetStart(int[][] _logicBoard) {
        int[][] logicBoard = _logicBoard;
        for (int ship : shipLengths) {
            boolean placed = false;
            while (!placed) {
                int row = (int) (logicBoard.length * Math.random());
                int col = (int) (logicBoard[0].length * Math.random());
                boolean horizontal = ((int) (10 * Math.random())) % 2 == 0;
                if (horizontal) {
                    // Check for vertical space
                    boolean hasSpace = true;
                    for (int i = 0; i < ship; i++){
                        if (col + i >= logicBoard[0].length){
                            hasSpace = false;
                            break;
                        }
                        if (logicBoard[row][col + i] != 0) {
                            hasSpace = false;
                            break;
                        }
                    }
                    if (!hasSpace) {
                        // Not enough room, find a new spo
                        continue;
                    }
                    for (int i = 0; i < ship; i++){
                        logicBoard[row][col + i] = 2;
                    }
                    placed = true;
                } else {
                    // Check for horizontal space
                    boolean enoughRoom = true;
                    for (int i = 0; i < ship; i++){
                        if (row + i >= logicBoard.length){
                            enoughRoom = false;
                            break;
                        }
                        if (logicBoard[row + i][col] != 0){
                            enoughRoom = false;
                            break;
                        }
                    }
                    if (!enoughRoom) {
                        // Not enough room, find a new spot
                        continue;
                    }
                    for (int i = 0; i < ship; i++){
                        logicBoard[row + i][col] = 2;
                    }
                    placed = true;
                }
            }
        }
    }
}
