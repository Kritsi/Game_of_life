package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by kristine B. Skjellestad on 25.03.2017.
 * This class has two nextGen, one using threads and the other not.
 * The nextGen that is using threads is faster over time.
 * I have made a test in the Test class, which tests 1000 generations, both using the same pattern.
 * The original nextGen use around 25000 ms, the new one with threads use around 20000 ms.
 */
public class ListBoard extends AbstractBoard {
    private Rules r = new Rules();
    private int sizeH = 100;
    private int sizeW = 100;
    private List<List<Integer>> boardList = new ArrayList<List<Integer>>();

    /**
     * Set the height of the array.
     * @param sizeH height of the array.
     */
    public void setSizeH(int sizeH) {
        this.sizeH = sizeH;
    }

    /**
     * Set the width of the array.
     * @param sizeW width of the array.
     */
    public void setSizeW(int sizeW) {
        this.sizeW = sizeW;
    }

    /**
     * Get the height of the array.
     * @return height of the array.
     */
    public int getSizeH() {
        return sizeH;
    }

    /**
     * Get the width of the array.
     * @return width of the array.
     */
    public int getSizeW() {
        return sizeW;
    }

    /**
     * Sets the start board.
     */
    public void setBoard() {
        for(int i = 0; i < sizeH; i++)  {
            boardList.add(new ArrayList<Integer>());
        }

        for (int x = 0; x < boardList.size(); x++) {
            for (int y = 0; y < sizeW; y++) {
                boardList.get(x).add(y, 0);
            }
        }
    }

    /**
     * Set the board.
     * @param boardList replace existing board with the input board.
     */
    public void setBoardList(List<List<Integer>> boardList) {
        this.boardList = boardList;
    }

    /**
     * Counts neighbours for each cell 3x3.
     * @return an array with each cells numbers of neighbours.
     */
    public List<List<Integer>> countNeighbours() {
        List<List<Integer>> count = new ArrayList<List<Integer>>(boardList.size());

        int h = boardList.size();
        int w = boardList.get(0).size();

        for(int i = 0; i < h; i++)  {
            count.add(new ArrayList<Integer>(w));
        }

        for (int x = 0; x < boardList.size(); x++) {
            for (int y = 0; y < boardList.get(x).size(); y++) {
                int neighbors = 0;

                //Defines the area of cells to be counted.
                int top = y-1;
                int bottom = y+1;
                int right = x+1;
                int left = x-1;

                //Prevents array outofbounds.
                if(top < 0) top = 0;
                if(bottom > boardList.get(x).size()-1) bottom = boardList.get(x).size()-1;
                if(right > boardList.size()-1) right = boardList.size()-1;
                if(left < 0) left = 0;

                //Checking all cells 3x3
                for (int i = left; i <= right; i++) {
                    for (int j = top; j <= bottom; j++) {
                        neighbors += boardList.get(i).get(j);
                    }
                }

                neighbors -= boardList.get(x).get(y);

                count.get(x).add(y, neighbors);
            }
        }
        return count;
    }

    /**
     * Counts neighbours for each cell 3x3 with threads.
     * @param count a empty array that will be filled with each cells neighbours.
     * @param nbr a number to calculate the area for the thread to work.
     */
    public void cn(List<List<Integer>> count, int nbr) {
        int length = boardList.get(0).size() / 4;
        int start = length*nbr;
        int end = (length*nbr)+length-1;

        for (int x = 0; x < boardList.size(); x++) {
            for (int y = start; y <= end; y++) {
                int neighbors = 0;

                int top = y-1;
                int bottom = y+1;
                int right = x+1;
                int left = x-1;

                if(top < 0) top = 0;
                if(bottom > boardList.get(x).size()-1) bottom = boardList.get(x).size()-1;
                if(right > boardList.size()-1) right = boardList.size()-1;
                if(left < 0) left = 0;

                for (int i = left; i <= right; i++) {
                    for (int j = top; j <= bottom; j++) {
                        neighbors += boardList.get(i).get(j);
                    }
                }

                neighbors -= boardList.get(x).get(y);

                count.get(x).set(y, neighbors);
            }
        }
    }

    /**
     * Create four threads to use to count the neighbours.
     * @return a array with values of each cells neighbours.
     */
    public List<List<Integer>> countNewNeighbours() {
        List<List<Integer>> count = new ArrayList<List<Integer>>(boardList.size());

        int h = boardList.size();
        int w = boardList.get(0).size();

        for(int i = 0; i < h; i++)  {
            count.add(new ArrayList<Integer>(w));
        }

        for (int x = 0; x < boardList.size(); x++) {
            for (int y = 0; y < boardList.get(x).size(); y++) {
                count.get(x).add(y, 0);
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.submit(() -> {
            cn(count, 0);
        });
        executor.submit(() -> {
            cn(count, 1);
        });
        executor.submit(() -> {
            cn(count, 2);
        });
        executor.submit(() -> {
            cn(count, 3);
        });

        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
        }

        return count;
    }

    /**
     * Will update board to the next generation dependent on the rules.
     * Switch the values from the next array to the board array.
     */
    public void nextGen() {
        List<List<Integer>> next = new ArrayList<List<Integer>>(boardList.size());
        int h = boardList.size();
        int w = boardList.get(0).size();

        for(int i = 0; i < h; i++)  {
            next.add(new ArrayList<Integer>(w));
        }

        r.rulesList(countNeighbours(), boardList, next);
        for (int x = 0; x < boardList.size(); x++) {
            for (int y = 0; y < boardList.get(x).size(); y++) {
                boardList.get(x).set(y, next.get(x).get(y));
            }
        }
    }

    /**
     * A new version of the nextGen method using threads.
     * Will update board to the next generation dependent on the rules.
     * Switch the values from the next array to the board array.
     */
    public void nextNewGen() {
        List<List<Integer>> next = new ArrayList<List<Integer>>(boardList.size());
        int h = boardList.size();
        int w = boardList.get(0).size();

        for(int i = 0; i < h; i++)  {
            next.add(new ArrayList<Integer>(w));
        }

        r.rulesList(countNewNeighbours(), boardList, next);
        for (int x = 0; x < boardList.size(); x++) {
            for (int y = 0; y < boardList.get(x).size(); y++) {
                boardList.get(x).set(y, next.get(x).get(y));
            }
        }
    }

    /**
     * Create a string of the array board values, to use in a test.
     * @return a string with the boards value.
     */
    @Override
    public String toString() {
        String rekke = "";
        for (int x = 0; x < boardList.size(); x++) {
            for (int y = 0; y < boardList.get(x).size(); y++) {
                if(boardList.get(x).get(y)== 1) {
                    rekke += 1;
                } else {
                    rekke += 0;
                }
            }
        }
        return rekke;
    }

    /**
     * Will set a cell to 1 if true and 0 if false.
     * @param x x position to cell.
     * @param y y position to cell.
     * @param b true or false.
     */
    public void setAlive(int x, int y, boolean b) {
        if(b) {
            boardList.get(x).set(y, 1);
        } else {
            boardList.get(x).set(y, 0);
        }
    }

    /**
     * The x and y position the user is pointing at will get alive.
     * @param x the x position of the array.
     * @param y the y position of the array.
     * @return the cell alive.
     */
    public boolean getAlive(int x, int y) {
        return boardList.get(x).get(y) == 1;
    }

    /**
     * Get the ListArray of the board.
     * @return a ListArray.
     */
    public List<List<Integer>> getBoardList() {
        return boardList;
    }
}
