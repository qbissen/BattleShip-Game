import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by Quinn on 3/21/2017.
 * This class deals with placing the ships on the board. It deals with most of the underlying logic in the game.
 */
public class BattleshipLogic {
    public int[][] logicTopBoard;
    public int[][] logicBottomBoard;

    /**
     * Default constructor
     */
    public BattleshipLogic() {

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

}
