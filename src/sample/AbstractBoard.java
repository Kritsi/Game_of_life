package sample;

/**
 * Created by kristine B. Skjellestad on 05.04.2017.
 */
public abstract class AbstractBoard {
    /**
     * Will update board to the next generation dependent on the rules.
     */
    public abstract void nextGen();

    /**
     * Will set a cell to 1 if true and 0 if false.
     * @param x x position to cell.
     * @param y y position to cell.
     * @param b true or false.
     */
    public abstract void setAlive(int x, int y, boolean b);

    /**
     * The x and y position the user is pointing at will get alive.
     * @param x the x position of the array.
     * @param y the y position of the array.
     * @return the cell alive.
     */
    public abstract boolean getAlive(int x, int y);
}
