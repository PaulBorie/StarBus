package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.ParseException;

public class MiniWeatherController extends VBox {

    @FXML
    private Label date;
    @FXML
    private Label temperature;
    @FXML
    private ImageView icon;

    private Weather weather;

    private StackPane weatherSwitcher;

    public MiniWeatherController(Weather w, StackPane sp){
        this.setWeather(w);
        this.setWeatherSwitcher(sp);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ressources/fxml/miniWeather.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        try {
            this.setDate(w.getDateShortFormated());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.setTemperature(w.getTemperature());
        this.setIcon(w.getIconUri());
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                weatherToTop();
            }
        });
    }

    public void weatherToTop(){
        for(Node n : weatherSwitcher.getChildren()){
            if(n instanceof WeatherController){
                WeatherController wc = (WeatherController) n;
                if(wc.getWeather() == weather){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            n.toFront();
                            WeatherScene.clearSelectedMiniWeather();
                            setStyle("-fx-border-color: black;" + "-fx-border-radius: 5;" + "-fx-border-width: 3.2;" + "-fx-border-style: solid inside;");
                        }
                    });
                }
            }
        }
    }

    public Weather getWeather() {
        return weather;
    }

    public StackPane getWeatherSwitcher() {
        return weatherSwitcher;
    }

    public void setWeatherSwitcher(StackPane sp){
        this.weatherSwitcher = sp;
    }
    public void setWeather(Weather w){
        this.weather = w;
    }
    public void setDate(String date) {
        this.date.setText(date);
    }
    public void setTemperature(String temperature) {
        this.temperature.setText(temperature);
    }

    public void setIcon(String iconUri){
        Image iconImage = new Image(iconUri);
        icon.setImage(iconImage);
    }
}

