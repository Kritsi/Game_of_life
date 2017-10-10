package sample;

import java.util.List;

/**
 * Created by kristine B. Skjellestad on 22.02.2017.
 */
public class Rules {
    private int born = 3;
    private int overPopulation = 4;
    private int underPopulation = 1;

    /**
     * Decides which cells will live and die with specific rules.
     * @param count takes in array with number of neighbors per cell.
     * @param board takes in the array board of how the cells was placed.
     * @param next will put in new values dependent on the rules.
     */
    public void rules(int[][] count, int[][] board, int[][] next) {
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if (board[x][y] == 0) {
                    if (count[x][y] == born) {
                        next[x][y] = 1;
                    } else {
                        next[x][y] = 0;
                    }
                } else {
                    if (count[x][y] > underPopulation && count[x][y] < overPopulation) {
                        next[x][y] = 1;
                    } else {
                        next[x][y] = 0;
                    }
                }
            }
        }
    }

    /**
     * Decides which cells will live and die with specific rules.
     * @param count takes in array with number of neighbors per cell.
     * @param board takes in the array board of how the cells was placed.
     * @param next will put in new values dependent on the rules.
     */
    public void rulesList(List<List<Integer>> count, List<List<Integer>> board, List<List<Integer>> next) {
        for (int x = 0; x < board.size(); x++) {
            for (int y = 0; y < board.get(x).size(); y++) {
                if (board.get(x).get(y) == 0) {
                    if (count.get(x).get(y) == born) {
                        next.get(x).add(y, 1);
                    } else {
                        next.get(x).add(y, 0);
                    }
                } else {
                    if (count.get(x).get(y) > underPopulation && count.get(x).get(y) < overPopulation) {
                        next.get(x).add(y, 1);
                    } else {
                        next.get(x).add(y, 0);
                    }
                }
            }
        }
    }
}
