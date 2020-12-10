package sample;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.List;

public class BusScene extends Parent {

    private static StackPane busStopSwitcher;
    private static VBox noBusStopAlert;

    public BusScene(){

        busStopSwitcher = new StackPane();


        StackPane mainStackPain= new StackPane();
        noBusStopAlert = (VBox) Main.loadFxml("/ressources/fxml/nobustopalert.fxml");
        BottomBar customButtomBar = new BottomBar();
        customButtomBar.hideRemoveAndNextButton();
        noBusStopAlert.getChildren().add(customButtomBar);

        mainStackPain.getChildren().add(busStopSwitcher);
        mainStackPain.getChildren().add(noBusStopAlert);

        this.getChildren().add(mainStackPain);

        busStopSwitcher.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> change) {
                // Rappel : plusieurs modifications peuvent être agrégées dans un seul événement.
                while (change.next()) {
                    if(busStopSwitcher.getChildren().isEmpty()){
                        noBusStopAlert.toFront();
                    }else{
                        noBusStopAlert.toBack();
                    }

                }
            }
        });

        /*
        this.setOnSwipeDown(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent swipeEvent) {
                WeatherScene.selectFirstWeather();
                Main.sceneTransitionBottomSlide(Main.weatherScene);
            }
        });

        this.setOnSwipeRight(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent swipeEvent) {
                changeFrontBusStop();
            }
        });

        this.setOnSwipeLeft(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent swipeEvent) {
                changeFrontBusStop();
            }
        }); */
        loadBusStopFromConfigFile();
    }

    public static void addBusStop(BusStop bs){
        if(!busStopAlreadyDisplayed(bs)){
            BusStopController busStopController = new BusStopController(bs);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    busStopSwitcher.getChildren().add(busStopController);
                }
            });
        }else{
            System.out.println("busStop " + bs.toString() + " already displayed");
        }
    }


    public static void deleteTopNode(){
        ObservableList<Node> childs = busStopSwitcher.getChildren();
        if(!childs.isEmpty()){
            int topNodeIndex = childs.size()-1;
            ((BusStopController) childs.get(topNodeIndex)).dispose();
            childs.remove(topNodeIndex);
        }
    }

    private static boolean busStopAlreadyDisplayed(BusStop bs){
        boolean alreadyDisplayed = false;
        List<Node> busStopsDisplayed = busStopSwitcher.getChildren();
        for(Node n : busStopsDisplayed){
            BusStopController busStopController = (BusStopController) n;
            if(bs.equals(busStopController.getBusStop()))
                alreadyDisplayed  = true;
        }
        return alreadyDisplayed;
    }

    public static StackPane getBusStopSwitcher() {
        return busStopSwitcher;
    }

    public static void removeBusStop(BusStop bs){

        List<Node> busStopsDisplayed = busStopSwitcher.getChildren();
        for(Node n : busStopsDisplayed){
            BusStopController busStopController = (BusStopController) n;
            if (bs.equals(busStopController.getBusStop())){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        busStopSwitcher.getChildren().remove(n);
                        ((BusStopController) n).dispose();

                    }
                });
            }
        }
    }

    public static void changeFrontBusStop() {
        ObservableList<Node> childs = busStopSwitcher.getChildren();
        if (childs.size() > 1) {
            Node topNode = childs.get(childs.size()-1);
            topNode.toBack();
        }
    }



    private void loadBusStopFromConfigFile(){
        try {
            String path = "./config.txt";
            InputStream file = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                BusStop bs = new BusStop(line);
                busStopSwitcher.getChildren().add(new BusStopController(bs));
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void saveUserConfig(){

        try {

            String path = "./config.txt";
            FileWriter fw = new FileWriter(path,false);
            List<Node> busStopDisplayed = getBusStopSwitcher().getChildren();
            BufferedWriter bw = null;
            for(Node n : busStopDisplayed) {
                BusStopController busStopController = (BusStopController) n;
                String busStopToSave = busStopController.getBusStop().toString();
                bw = new BufferedWriter(fw);
                bw.write(busStopToSave);
                bw.newLine();
                bw.flush();
            }
            if (bw != null){
                bw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
