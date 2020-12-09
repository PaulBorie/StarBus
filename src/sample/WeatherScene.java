package sample;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;

public class WeatherScene extends Parent {


    private static StackPane hourlyWeatherSwitcher;
    private static HBox nextHourWeatherList;
    private  static List<Weather> weatherList;
    private static ScheduledService schedule;
    private String city;
    private static AnchorPane noInternetConnection;
    private static final int REFRESH_RATE = 10; //in minutes
    private static final int FAIL_REFRESH_RATE = 5; //in seconds


    public WeatherScene(String city){
        this.city= city;

        VBox rootNode = new VBox();
        noInternetConnection = (AnchorPane) Main.loadFxml("/ressources/fxml/noInternetWeather.fxml");
        nextHourWeatherList = new HBox(2);
        nextHourWeatherList.setAlignment(Pos.TOP_CENTER);
        hourlyWeatherSwitcher = new StackPane();


        rootNode.setStyle("-fx-background-image: url(/ressources/images/bg.png)");

        rootNode.getChildren().add(hourlyWeatherSwitcher);
        rootNode.getChildren().add(nextHourWeatherList);

        this.getChildren().add(rootNode);

        /*
        this.setOnSwipeUp(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent swipeEvent) {
                Main.sceneTransitionTopSlide(Main.busScene);
            }
        });


        /*
        this.setOnSwipeLeft(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent swipeEvent) {
                int topNodeIndex = hourlyWeatherSwitcher.getChildren().size() - 1 ;
                int lastMiniWeatherIndex = nextHourWeatherList.getChildren().size() - 1;
                WeatherController topNode = (WeatherController) hourlyWeatherSwitcher.getChildren().get(topNodeIndex);
                for(int i = 0; i < nextHourWeatherList.getChildren().size(); i++){
                    MiniWeatherController mwc = (MiniWeatherController) nextHourWeatherList.getChildren().get(i);
                    if(mwc.getWeather().equals(topNode.getWeather())) {
                        if (i != lastMiniWeatherIndex) {
                            int nextMiniWeatherIndex = i + 1;
                            MiniWeatherController nextMiniWeather = (MiniWeatherController) nextHourWeatherList.getChildren().get(nextMiniWeatherIndex);
                            nextMiniWeather.weatherToTop();
                        }else{
                            MiniWeatherController nextMiniWeather = (MiniWeatherController)nextHourWeatherList.getChildren().get(0);
                            nextMiniWeather.weatherToTop();
                        }
                    }
                }
            }
        }); */

        selectFirstWeather();
        updateWeatherInfos(this.city);

    }

    public void updateWeatherInfos(String city){

        schedule = new ScheduledService<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        weatherList = WeatherApiRequestManager.getWeatherInfos(city);
                        return null;
                    }
                };
            }
        };

        schedule.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                hourlyWeatherSwitcher.getChildren().clear();
                nextHourWeatherList.getChildren().clear();
                for(Weather w : weatherList ){
                    hourlyWeatherSwitcher.getChildren().add(new WeatherController(w));
                    nextHourWeatherList.getChildren().add(new MiniWeatherController(w, hourlyWeatherSwitcher));
                }
            }
        });
        schedule.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                if(nextHourWeatherList.getChildren().isEmpty()){
                    if(hourlyWeatherSwitcher.getChildren().isEmpty())
                        hourlyWeatherSwitcher.getChildren().add(noInternetConnection);
                }else{
                    for(Node n : hourlyWeatherSwitcher.getChildren()){
                        if(n instanceof WeatherController){
                            WeatherController wc = (WeatherController) n;
                            ((WeatherController) n).noInternetAlertVisible(true);

                        }

                    }
                }
            }

        });
        schedule.setPeriod(Duration.minutes(REFRESH_RATE));
        schedule.start();
    }

    public static void clearSelectedMiniWeather(){
        for(Node n : nextHourWeatherList.getChildren()){
            if (n instanceof MiniWeatherController){
                MiniWeatherController miniWeather = (MiniWeatherController) n;
                n.setStyle(null);
            }
        }

    }


    public static void selectFirstWeather(){
        if(!nextHourWeatherList.getChildren().isEmpty()){
            MiniWeatherController miniWeather = (MiniWeatherController) nextHourWeatherList.getChildren().get(0);
            miniWeather.weatherToTop();
        }
    }

    public void setCity(String city) {
        this.city = city;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                schedule.cancel();
                updateWeatherInfos(city);
            }
        });
    }
}
