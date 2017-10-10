package sample;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private double s = 6;
    private double time = 250000000.0;
    private double canvasW, canvasH;
    private double xPos;
    private double yPos;
    private double xMove = 0;
    private double yMove = 0;
    private int xKor;
    private int yKor;
    private Color c = Color.web("#6680e6");
    private Color colorG = Color.web("#e6e6e6");
    private boolean drawing = true;
    private boolean move;
    private GraphicsContext gc;

    ListBoard lb = new ListBoard();
    ReadFile read = new ReadFile();

    @FXML
    ColorPicker colorpicker;
    @FXML
    ColorPicker colorGrid;
    @FXML
    Slider size;
    @FXML
    Slider speed;
    @FXML
    private Canvas canvas;

    /**
     * Starts the canvas animation.
     */
    public void btnClick() {
        timer.start();
    }

    /**
     * Stops the canvas animation.
     */
    public void pause() {
        timer.stop();
    }

    /**
     * Clear the canvas.
     */
    public void clearCanvas() {
        gc.clearRect(0, 0, canvasW, canvasH);
    }

    /**
     * Will let the user draw on the canvas.
     */
    public void toDraw() {
        drawing = true;
        move = false;
    }

    /**
     * Will let the user erase from the canvas.
     */
    public void eraser() {
        drawing = false;
        move = false;
    }

    /**
     * Lets the user move the board around.
     */
    public void mover() {
        move = true;
    }

    /**
     * Sets the array board all to 0.
     */
    public void setZero() {
        clearCanvas();
        for (int x = 0; x < lb.getBoardList().size(); x++) {
            for (int y = 0; y < lb.getBoardList().get(x).size(); y++) {
                lb.setAlive(x,y, false);
            }
        }
        nett();
    }

    /**
     * Lets the user pick color on the cells.
     */
    public void pickCellColor() {
        c = colorpicker.getValue();
        draw();
    }

    /**
     * Lets the user pick color on the grid.
     */
    public void pickGridColor() {
        colorG = colorGrid.getValue();
        draw();
    }

    /**
     * Lets the user pick size to the cells.
     */
    public void pickSize() {
        int arrayLength = lb.getBoardList().get(0).size();
        double sv = size.getValue();
        s = (canvasW/arrayLength) + sv;
        if(s < 1.5) {
            s = 1.5;
        }
        clearCanvas();
        draw();
    }

    /**
     * Lets the user pick speed to the animation.
     */
    public void setSpeed() {
        time = (speed.getMax() -speed.getValue());
    }

    /**
     * Retrieves the x and y values from canvas click.
     * Decides which method to run, either draw/erase or moving the board.
     * @param e is the coordinates to where the user clicks on the canvas.
     */
    public void canvasEvent(MouseEvent e) {
        xKor = (int) e.getX();
        yKor = (int) e.getY();
        if(move) {
            drag();
        } else {
            pen();
        }
    }

    /**
     * Draws the array of the board.
     */
    public void draw() {
        nett();

        double amountOfCellsDisplayed = canvasW / s;
        double remain = lb.getBoardList().get(0).size() - amountOfCellsDisplayed;
        double startPos = remain / 2;

        gc.setFill(c);

        for (int x = 0; x < lb.getBoardList().size(); x++) {
            for (int y = 0; y < lb.getBoardList().get(x).size(); y++) {
                if(lb.getAlive(x,y)) {
                    xPos = (x - startPos) * s + yMove;
                    yPos = (y - startPos) * s + xMove;
                    gc.fillRect(yPos, xPos, s-1, s-1);
                }
            }
        }
    }

    /**
     * Draws the grid.
     */
    private void nett() {
        double amountOfCellsOnCanvas = canvasW / s;
        double rest = lb.getBoardList().get(0).size() - amountOfCellsOnCanvas;
        double startPos = rest / 2;

        gc.setFill(colorG);
        double width = lb.getBoardList().get(0).size() * s;
        double heigth = lb.getBoardList().size() * s;
        gc.fillRect(0, 0, width, heigth);
        gc.setFill(Color.WHITE);

        for (int x = 0; x < lb.getBoardList().size(); x++) {
            for (int y = 0; y < lb.getBoardList().get(x).size(); y++) {
                xPos = (x - startPos) * s + yMove;
                yPos = (y - startPos) * s + xMove;
                gc.fillRect(yPos, xPos, s-1, s-1);
            }
        }
    }


    /**
     * Lets the user draw on the canvas.
     * Will use the coordinates from MouseEvent to find out which cell in the array you are clicking on.
     */
    public void pen() {
        clearCanvas();
        draw();
        double xtab;
        double ytab;

        double amountOfCellsOnCanvas = canvasW / s;
        double rest = lb.getBoardList().get(0).size() - amountOfCellsOnCanvas;
        double startPos = rest / 2;

        for (int x = 0; x <lb.getBoardList().size(); x++) {
            for (int y = 0; y < lb.getBoardList().get(x).size(); y++) {
                ytab = (x - startPos) * s + yMove;
                xtab = (y - startPos) * s + xMove;
                if((ytab <= yKor && (ytab + s) >= yKor) && (xtab <= xKor && (xtab + s) >= xKor)){
                    lb.setAlive(x,y,drawing);
                }
            }
        }
    }

    /**
     * Let the user move the board.
     * It moves from the center of the board, is it not practical to use when zoomed in.
     * This method is mostly used to let the user see more of the board if the pattern is very big.
     */
    public void drag() {
        xMove = xKor - (lb.getBoardList().get(0).size()*s)/2;
        yMove = yKor - (lb.getBoardList().get(0).size()*s)/2;
        draw();
    }

    /**
     * Will start method to open file from disk.
     */
    public void fileDisk() {
        try {
            read = new ReadFile();

            read.openFile();
        } catch (FileNotFoundException e) {
            e.getMessage();
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        try {
            lb.setBoardList(read.pattern());
        } catch (PatternFormatException e) {
            e.alert(e.getMessage(), "The width and height for the pattern is to big, try another");
        }
        pickSize();
        clearCanvas();
        draw();
    }

    /**
     * Will start method to open URL.
     */
    public void fileUrl() {
        try {
            read.readGameBoardURL();
        } catch (IOException e) {
            System.out.println(e.getMessage() + " Error");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Warning, error in URL");
            alert.setContentText("The URL does not seem to exist, try again");

            alert.showAndWait();
        }
        try {
            lb.setBoardList(read.pattern());
        } catch (PatternFormatException e) {
            e.getMessage();
            e.alert(e.getMessage(), "The width and height for the pattern is to big, try another");
        }
        pickSize();
        clearCanvas();
        draw();
    }

    /**
     * The animation of the board.
     */
    private long tid = System.nanoTime();
    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if ((now - tid) > time) {
                clearCanvas();
                lb.nextNewGen();
                draw();

                tid = System.nanoTime();
            }
        }
    };

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        canvasH = canvas.getHeight();
        canvasW = canvas.getWidth();

        lb.setBoard();
        setZero();
    }
}

