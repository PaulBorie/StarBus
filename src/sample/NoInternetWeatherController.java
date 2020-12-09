package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;


public class NoInternetWeatherController implements Initializable {

    @FXML
    private Button bus;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Main.sceneTransition(Main.busScene);
            }
        });

    }
}
