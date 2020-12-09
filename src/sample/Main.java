package sample;



import javafx.application.Application;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends Application {

    public static BusScene busScene;
    public static WeatherScene weatherScene;
    private static StackPane stackPane;
    private static final int TRANSITION_DURATION = 200; //milliseconds
    private static final KeyCombination keyCombinationShiftC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
    private static AtomicBoolean running;
    private static Thread listenUserCommandsThread;


    public enum CommandType{
        ADD,
        REMOVE,
        GETBUSTOPS,
        GETIP,
        SETCITY
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        running = new AtomicBoolean(true);

        primaryStage.setTitle("Star Bus");
        primaryStage.setWidth(800);
        primaryStage.setHeight(480);

        stackPane = new StackPane();
        busScene = new BusScene();
        weatherScene = new WeatherScene("Rennes");

        stackPane.getChildren().add(busScene);

        primaryStage.setScene((new Scene((stackPane))));
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.show();

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent key) -> {

            if(key.getCode().equals(KeyCode.F)) {

                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent key) -> {

            if(key.getCode().equals(KeyCode.C) || key.getCode().equals((KeyCode.Q)) || keyCombinationShiftC.match(key)) {

                primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
                primaryStage.close();
            }
        });

        primaryStage.setOnCloseRequest(e ->  handleExit());

        listenUserCommandsThread = new Thread(){
            public void run(){
                userCommandListener();
            }
        };

        listenUserCommandsThread.start();

    }

    public static void sceneTransition(Node paneToAdd){
        stackPane.getChildren().add(paneToAdd);
        Node paneToRemove = stackPane.getChildren().get(0);
        stackPane.getChildren().remove(paneToRemove);




    }

    public static void sceneTransitionRightSlide(Node paneToAdd) {

        stackPane.getChildren().add(paneToAdd);
        Node paneToRemove = stackPane.getChildren().get(0);
        paneToAdd.translateXProperty().set(stackPane.getWidth());
        KeyValue keyValue = new KeyValue(paneToAdd.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(TRANSITION_DURATION), keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.setOnFinished(evt -> {
            stackPane.getChildren().remove(paneToRemove);
        });
        timeline.play();
    }

    public static void sceneTransitionBottomSlide(Node paneToAdd){
        stackPane.getChildren().add(paneToAdd);
        Node paneToRemove = stackPane.getChildren().get(0);

        paneToAdd.translateYProperty().set(stackPane.getHeight());
        KeyValue keyValue = new KeyValue(paneToAdd.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(TRANSITION_DURATION), keyValue);
        Timeline timeline =  new Timeline(keyFrame);
        timeline.setOnFinished(evt -> {
            stackPane.getChildren().remove(paneToRemove);
        });
        timeline.play();

    }

    public static void sceneTransitionTopSlide(Node paneToAdd){
        stackPane.getChildren().add(paneToAdd);
        Node paneToRemove = stackPane.getChildren().get(0);
        paneToAdd.translateYProperty().set(-1 * stackPane.getHeight());
        KeyValue keyValue = new KeyValue(paneToAdd.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(TRANSITION_DURATION), keyValue);
        Timeline timeline =  new Timeline(keyFrame);
        timeline.setOnFinished(evt -> {
            stackPane.getChildren().remove(paneToRemove);
        });
        timeline.play();

    }

    public static void sceneTransitionFadeOut(Node paneToAdd){
        stackPane.getChildren().add(paneToAdd);
        Node paneToRemove = stackPane.getChildren().get(0);
        FadeTransition fadeInTransition = new FadeTransition(Duration.millis(TRANSITION_DURATION));

        fadeInTransition.setOnFinished(evt -> {
            stackPane.getChildren().remove(paneToRemove);
        });
        fadeInTransition.setNode(paneToAdd);
        fadeInTransition.setFromValue(0);
        fadeInTransition.setToValue(1);
        fadeInTransition.play();

    }

    private static void killListenUserCommandThread(){
        running.set(false);
        listenUserCommandsThread.interrupt();
    }



    public static Node loadFxml(String fxml) {
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(Main.class.getResource(fxml));
            Node root = (Node) loader.load(Main.class.getResource(fxml).openStream());
            return root;
        } catch (IOException e) {
            throw new IllegalStateException("cannot load FXML screen", e);
        }
    }

    public void userCommandListener(){

        try{
            int listenPort = 9999;
            ServerSocket socketServeur = new ServerSocket(listenPort);
            while (running.get()) {

                try{
                    System.out.println("attends les clients");
                    Socket socketVersUnClient = socketServeur.accept();
                    System.out.println("Le client" + socketVersUnClient.getInetAddress() + " est connecté");
                    traiterSocketCliente(socketVersUnClient);

                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void traiterSocketCliente(Socket socketVersUnClient) throws IOException {

        PrintWriter out = new PrintWriter(socketVersUnClient.getOutputStream());;
        BufferedReader in = new BufferedReader(new InputStreamReader(socketVersUnClient.getInputStream()));
        String currentLine;
        while ((currentLine = in.readLine()) != null) {
            System.out.println(currentLine);
            parseCommand(currentLine, out);
        }
        System.out.println();
        socketVersUnClient.close();

    }

    private void parseCommand(String command, PrintWriter out){
        try{
            String[] splitCommand = command.split("/");
            CommandType commandType = CommandType.valueOf(splitCommand[0]);
            switch(commandType){
                case ADD :
                    BusStop bsToAdd = new BusStop(splitCommand[1], splitCommand[2], splitCommand[3]);
                    busScene.addBusStop(bsToAdd);
                    break;
                case REMOVE :
                    BusStop bsToRemove = new BusStop(splitCommand[1], splitCommand[2], splitCommand[3]);
                    busScene.removeBusStop(bsToRemove);
                    break;
                case GETIP:
                    sendIpAddress(out);
                    break;
                case GETBUSTOPS:
                    sendConfigFile(out);
                    break;
                case SETCITY:
                    String city = splitCommand[1];
                    weatherScene.setCity(city);
                default:
                    break;
            }
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            out.println("UNKNOWN COMMAND");
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void sendConfigFile(PrintWriter out) throws FileNotFoundException {
        busScene.saveUserConfig();
        String path = "./config.txt";
        InputStream file = new FileInputStream(path);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                out.println(line);
                out.flush();
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        }
                        else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        }
        catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

    private static void sendIpAddress(PrintWriter out){
        try{
            InetAddress privateIp = getLocalHostLANAddress();
            out.println(privateIp.getHostAddress().trim());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleExit(){
        busScene.saveUserConfig();
        Platform.exit();
        killListenUserCommandThread();
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
