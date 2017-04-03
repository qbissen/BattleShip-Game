import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by Quinn on 3/21/2017.
 */
public class BattleshipLogic {
    public int[][] logicTopBoard = new int[10][10];
    public int[][] logicBottomBoard = new int[10][10];

    private static int[] shipLengths = {2, 3, 3, 4};


    public BattleshipLogic() {

    }

    public void setFleetFormation() {
        checkDirection();
        findFleetStart(logicTopBoard);
        findFleetStart(logicBottomBoard);
    }

    private static boolean checkDirection() {
        return Math.random() < 0.5;
    }

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


    public void findFleetStart(int[][] _logicBoard) {
        int[][] logicBoard = _logicBoard;
        for (int ship : shipLengths) {
            System.out.println("Adding ship " + ship);
            boolean added = false;
            while (!added) {
                int x = (int) (logicBoard.length * Math.random());
                int y = (int) (logicBoard[0].length * Math.random());
                boolean horizontal = ((int) (10 * Math.random())) % 2 == 0;
                if (horizontal) {
                    // Check for vertical space
                    boolean hasSpace = true;
                    for (int i = 0; i < ship; i++) {
                        if (y + i >= logicBoard[0].length) {
                            hasSpace = false;
                            break;
                        }
                        if (logicBoard[x][y + i] != 0) {
                            hasSpace = false;
                            break;
                        }
                    }
                    if (!hasSpace) {
                        // No room there, check again
                        continue;
                    }
                    for (int i = 0; i < ship; i++) {
                        logicBoard[x][y + i] = 2;
                    }
                    added = true;
                } else {
                    // Check for horizontal space
                    boolean hasSpace = true;
                    for (int i = 0; i < ship; i++) {
                        if (x + i >= logicBoard.length) {
                            hasSpace = false;
                            break;
                        }
                        if (logicBoard[x + i][y] != 0) {
                            hasSpace = false;
                            break;
                        }
                    }
                    if (!hasSpace) {
                        // No room there, check again
                        continue;
                    }
                    for (int i = 0; i < ship; i++) {
                        logicBoard[x + i][y] = 2;
                    }
                    added = true;
                }
            }
        }

    }
}
