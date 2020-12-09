package sample;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BusStopController extends VBox {

    @FXML
    private Label busStopName;
    @FXML
    private ImageView numLine1;
    @FXML
    private ImageView numLine2;
    @FXML
    private ImageView numLine3;
    @FXML
    private Label dir1;
    @FXML
    private Label dir2;
    @FXML
    private Label dir3;
    @FXML
    private Label time1;
    @FXML
    private Label time2;
    @FXML
    private Label time3;
    @FXML
    private VBox vbox;
    @FXML
    private VBox rootVBox;




    private BusStop busStop;
    private String[] busSchedule;
    private ScheduledService<Void> busScheduleUpdater;
    private BottomBar bottomBar;
    private static final int REFRESH_RATE = 35; //in seconds
    private static final int FAIL_REFRESH_RATE = 3; //in seconds
    private String[] previousBusSchedule = null;

    public BusStopController(BusStop bs) {
        this.busStop = bs;


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ressources/fxml/busstop.fxml"));

        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.bottomBar = new BottomBar();
        this.rootVBox.getChildren().add(bottomBar);
        setBusStopName(bs.getName());
        setDir(bs.getDirection());
        setNumLine(bs.getIconUri());
        try {
            setColorToBottomBarAndTopBar();
        } catch (IOException e) {
            e.printStackTrace();
        }

        createBusScheduleUpdater(bs);
        busScheduleUpdater.start();

    }

    private void setColorToBottomBarAndTopBar() throws IOException {
            Color c = extractColorFromIcon( busStop.getIconUri());
            String hex = "#"+ Integer.toHexString(c.getRGB()).substring(2);
            bottomBar.setButtonColor(hex);
            vbox.setStyle("-fx-background-color: " + hex );
    }

    private Color extractColorFromIcon(String iconUri) throws IOException {
        Map<Color, Integer> countcolor = new HashMap<Color, Integer>();
        Color mostCommonColor;
        Image iconImage = new Image(iconUri);
        int width = (int) iconImage.getWidth();
        int height = (int) iconImage.getHeight();
        PixelReader pixelReader = iconImage.getPixelReader();


        for(int i=0; i<height; i++) {

            for(int j=0; j<width; j++) {
                javafx.scene.paint.Color fxColor = pixelReader.getColor(i,j);
                Color c = new Color((float) fxColor.getRed(), (float) fxColor.getGreen(), (float) fxColor.getBlue(), (float) fxColor.getOpacity());
                if(countcolor.containsKey(c)){
                    int nboccurence = countcolor.get(c) + 1;
                    countcolor.put(c, nboccurence);
                }else{
                    countcolor.put(c, 0);
                }
            }
        }
        Map.Entry<Color, Integer> maxEntry = null;

        for(Map.Entry<Color, Integer> entry : countcolor.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }
        mostCommonColor = maxEntry.getKey();
        return mostCommonColor;

    }

    private void setBusStopName(String busStopName) {
        this.busStopName.setText(busStopName);
    }

    private void setNumLine(String iconUri) {
        System.out.println(iconUri);
        try{
            Image iconImage = new Image(iconUri);
            this.numLine1.setImage(iconImage);
            this.numLine2.setImage(iconImage);
            this.numLine3.setImage(iconImage);
        }catch(Exception e){
            e.printStackTrace();
        }

    }



    private void setDir(String dir) {
        this.dir1.setText(dir);
        this.dir2.setText(dir);
        this.dir3.setText(dir);

    }


    private void setTime(Label label, String time){
            label.setText(time);
    }


    private void createBusScheduleUpdater(BusStop bs){

        busScheduleUpdater = new ScheduledService<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        busSchedule = StarApiRequestManager.getBusSchedule(bs);
                        return null;
                    }
                };
            }
        };
        busScheduleUpdater.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                StarApiRequestManager.printArray(busSchedule);
                resetGrid();

                if(!busServiceEnded(busSchedule)){
                    if(!apiServerFail(busSchedule)){

                        if(busSchedule[0] == null)
                            setVisibleLine(0, false);
                        else
                            setTime(time1, busSchedule[0]);
                        if(busSchedule[1] == null)
                            setVisibleLine(1, false);
                        else
                            setTime(time2, busSchedule[1]);
                        if(busSchedule[2] == null)
                            setVisibleLine(2, false);
                        else
                            setTime(time3, busSchedule[2]);
                    }

                }else{

                    setAllGridVisible(false);
                    numLine1.setVisible(true);
                    dir1.setVisible(true);
                    dir2.setVisible(true);
                    dir2.setText("SERVICE TERMINE");
                    dir2.setAlignment(Pos.CENTER);

                }
                previousBusSchedule = busSchedule;
            }
        });
        busScheduleUpdater.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                setAllGridVisible(false);
                numLine1.setVisible(true);
                dir1.setVisible(true);

                dir2.setVisible(true);
                dir2.setText("NO INTERNET");
                dir2.setAlignment(Pos.CENTER);


            }
        });
        busScheduleUpdater.setPeriod(Duration.seconds(REFRESH_RATE));
    }


    private void setVisibleLine(int numeroLine, boolean b){
        if(numeroLine == 0){
            numLine1.setVisible(b);
            dir1.setVisible(b);
            time1.setVisible(b);
        }
        if(numeroLine == 1){
            numLine2.setVisible(b);
            dir2.setVisible(b);
            time2.setVisible(b);
        }
        if(numeroLine == 2){
            numLine3.setVisible(b);
            dir3.setVisible(b);
            time3.setVisible(b);
        }
    }

    private boolean apiServerFail(String[] busSchedule){
        return busSchedule[0] == null && busSchedule[1] == null && busSchedule[2] == null;
    }

    private boolean busServiceEnded(String[] busSchedule){
      if (busSchedule == null) return true;
      if (previousBusSchedule != null && busScheduleEmpty(busSchedule) && busScheduleEmpty(previousBusSchedule)) return true;
      if (previousBusSchedule != null && busScheduleEmpty(busSchedule) && previousBusSchedule[0] != null && previousBusSchedule[1] == null && previousBusSchedule[2] == null) return true;
      return false;
    }

    private static boolean busScheduleEmpty(String[] busSchedule){
        return busSchedule[0] == null && busSchedule[1] == null && busSchedule[2] == null;
    }

    private void setAllGridVisible(boolean b){
        setVisibleLine(0,b);
        setVisibleLine(1,b);
        setVisibleLine(2,b);
    }

    private void resetGrid(){
        dir2.setAlignment(Pos.CENTER_LEFT);
        setAllGridVisible(true);
        setDir(busStop.getDirection());

    }

    public BusStop getBusStop() {
        return busStop;
    }

    public static void main (String[] args){
        System.out.println(busScheduleEmpty(null));;
    }


        public void dispose(){
        busScheduleUpdater.cancel();
    }

}


