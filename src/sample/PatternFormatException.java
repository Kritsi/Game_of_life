package sample;

import javafx.scene.control.Alert;

/**
 * Created by kristine B. Skjellestad on 23.03.2017.
 */
public class PatternFormatException extends Exception {

    PatternFormatException(String msg){
        super(msg);
    }

    /**
     * Will show an alert box with input message for the error.
     * @param heading what type of error.
     * @param description the error message that will be shown.
     */
    public void alert(String heading, String description) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText(heading);
        alert.setContentText(description);

        alert.showAndWait();
    }
}
