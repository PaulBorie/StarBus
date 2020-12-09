package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherController extends Pane {




    @FXML
    private Label city;
    @FXML
    private Label temperature;
    @FXML
    private Label description;
    @FXML
    private Label humidity;
    @FXML
    private Label wind;
    @FXML
    private Label ressentie;
    @FXML
    private ImageView icon;
    @FXML
    private Label date;
    @FXML
    private Button bus;
    @FXML
    private Label nointernet;

    private Weather weather;
    private ScheduledService<Void> clockUpdater;
    private String todayDate;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Weather.LONG_DATE_PATTERN);


    public WeatherController(Weather w) {

        this.setWeather(w);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ressources/fxml/weather2.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.setCity(w.getCity());
        this.setDate(Weather.getTodayDate());
        this.setDescription(w.getDescription());
        this.setHumidity(w.getHumidity());
        this.setWind(w.getWind());
        this.setTemperature(w.getTemperature());
        this.setRessentie(w.getRessentie());
        this.setIcon(w.getIconUri());
        noInternetAlertVisible(false);


        bus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Main.sceneTransition(Main.busScene);
            }
        });

        initClock();
        clockUpdater.start();

    }

    private void initClock(){

        clockUpdater = new ScheduledService<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Date date = new Date();
                        todayDate = simpleDateFormat.format(date);
                        return null;
                    }
                };
            }
        };
        clockUpdater.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                setDate(todayDate);
            }
        });
        clockUpdater.setPeriod(Duration.seconds(30));
    }

    public void noInternetAlertVisible(boolean b){
        nointernet.setVisible(b);
    }


    private static String capitalizeFirstLetter(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public Weather getWeather() { return weather; }

    public void setRessentie(String ressentie){

        this.ressentie.setText(ressentie);
    }

    public void setCity(String city) {
        this.city.setText(city);
    }

    public void setTemperature(String temperature) {
        this.temperature.setText(temperature);
    }

    public void setDescription(String description) {
        this.description.setText(capitalizeFirstLetter(description));
    }

    public void setHumidity(String humidity) {
        this.humidity.setText(humidity);
    }

    public void setWind(String wind) {
        this.wind.setText(wind);
    }

    public void setDate(String date) {
        this.date.setText(capitalizeFirstLetter(date));
    }

    public void setIcon(String iconUri){
        try{
            Image iconImage = new Image(iconUri);
            icon.setImage(iconImage);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
