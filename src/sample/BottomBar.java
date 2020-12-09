package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class BottomBar extends GridPane {


    @FXML
    private Button next;

    @FXML
    private Button removebs;

    @FXML
    private Button weather;



    public BottomBar(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ressources/fxml/bottombar.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        weather.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                WeatherScene.selectFirstWeather();
                Main.sceneTransition(Main.weatherScene);
            }
        });

        removebs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                BusScene.deleteTopNode();
            }
        });

        next.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                BusScene.changeFrontBusStop();
            }
        });


    }

    public void setButtonColor(String colorHex){
        String roundButton = "-fx-background-radius: 1em";
        removebs.setStyle("-fx-background-color: " + colorHex + ";"+ roundButton + ";" );
        next.setStyle("-fx-background-color: " + colorHex + ";"+ roundButton + ";");
        weather.setStyle("-fx-background-color: " + colorHex + ";"+ roundButton + ";");

    }

    public void hideRemoveAndNextButton() {
        removebs.setVisible(false);
        next.setVisible(false);
    }
}
