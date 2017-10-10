package sample;

/**
 * Created by kristine B. Skjellestad on 22.02.2017.
 */
//@Deprecated
public class Board extends AbstractBoard {
    private Rules r = new Rules();
    private int h = 100;
    private int w = 110;
    private int board[][] = new int[h][w];

    /**
     * Get board.
     * @return board.
     */
    public int[][] getBoard() {
        return board;
    }

    /**
     * Set the board.
     * @param board replace existing board with the input board.
     */
    public void setBoard(int[][] board) {
        this.board = board;
    }

    /**
     * Counts neighbours for each cell 3x3.
     * @return an array with each cells numbers of neighbours.
     */
    private int[][] countNeihbors() {
        int[][] count = new int[board.length][board[0].length];

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                int neighbors = 0;

                //Defines the area of cells to be counted
                int top = y-1;
                int bottom = y+1;
                int right = x+1;
                int left = x-1;

                //Prevents array outofbounds
                if(top < 0) top = 0;
                if(bottom > board[x].length-1) bottom = board[x].length-1;
                if(right > board.length-1) right = board.length-1;
                if(left < 0) left = 0;

                //Checking all cells 3x3
                for (int i = left; i <= right; i++) {
                    for (int j = top; j <= bottom; j++) {
                        neighbors += board[i][j];
                    }
                }

                neighbors -= board[x][y];

                count[x][y] = neighbors;
            }
        }
        return count;
    }

    /**
     * Create a string of the boards value, to use in a test.
     * @return a string with the boards value.
     */
    @Override
    public String toString() {
        String rekke = "";
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if(board[x][y] == 1) {
                    rekke += 1;
                } else {
                    rekke += 0;
                }
            }
        }
        return rekke;
    }

    /**
     * Will update the array board to the next generation dependent on the rules.
     * Switch the values from the next array to the board array.
     */
    public void nextGen() {
        int[][] next = new int[board.length][board[0].length];

        r.rules(countNeihbors(), board, next);
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                board[x][y] = next[x][y];
            }
        }
    }

    /**
     * Will set a cell to 1 if true and 0 if false.
     * @param x x position to cell.
     * @param y y position to cell.
     * @param b true or false.
     */
    public void setAlive(int x, int y, boolean b) {
        if(b) {
            board[x][y] = 1;
        } else {
            board[x][y] = 0;
        }
    }

    /**
     * The x and y position the user is pointing at will get alive.
     * @param x the x position of the array.
     * @param y the y position of the array.
     * @return the cell alive.
     */
    public boolean getAlive(int x, int y) {
        return board[x][y] == 1;
    }

    /**
     * Will return the height of the board.
     * @return height.
     */
    public int getH() {
        return h;
    }

    /**
     * Will return width of board.
     * @return width.
     */
    public int getW() {
        return w;
    }
}
