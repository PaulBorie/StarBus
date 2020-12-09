package sample;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BusIconUpdater {


    private static String BASE_URL = "https://data.explore.star.fr/api/records/1.0/search/?dataset=tco-bus-circulation-passages-tr&q=&rows=0&sort=nomcourtligne&facet=nomcourtligne";
    private static String DOWNLOAD_URL = "https://data.explore.star.fr/explore/dataset/tco-bus-lignes-pictogrammes-dm/files/";
    private static String DOWNLOAD = "/download/";
    private static String RESOLUTION = "1:100";

    private static String httpRequest(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    private static List<String> getNomCourtLigne() throws IOException, ParseException {

        List<String> nomLigneList = new ArrayList<String>();
        String json = httpRequest(BASE_URL);
        JSONObject nomCourtLigne = (JSONObject) JSONValue.parseWithException(json);
        JSONArray facet_groups = (JSONArray) nomCourtLigne.get("facet_groups");
        JSONObject facets_object = (JSONObject) facet_groups.get(0);
        JSONArray facets = (JSONArray) facets_object.get("facets");
        for(Object o : facets){
            JSONObject facet = (JSONObject) o;
            String nomLigne = (String) facet.get("name");
            System.out.println(nomLigne);
            nomLigneList.add(nomLigne);
        }
        return nomLigneList;
    }

    private void getIcons(List<String> nomLigneList) throws IOException, ParseException {

        for(String s : nomLigneList){
            if(s.equals("API")){
                s = "Api'Bus";
            }
            String url = "https://data.explore.star.fr/api/records/1.0/search/?dataset=tco-bus-lignes-pictogrammes-dm&refine.resolution=" + RESOLUTION + "&refine.nomcourtligne=" + s;
            System.out.println(url);
            String json = httpRequest(url);
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(json);
            JSONArray records = (JSONArray) jsonObject.get("records");
            if(records.isEmpty()){
                System.out.println("mauvais nomCourtLigne : " + s);
            }else{
                for(Object o : records){
                    JSONObject record = (JSONObject) o;
                    JSONObject fields = (JSONObject) record.get("fields");
                    JSONObject image = (JSONObject) fields.get("image");
                    String id = (String) image.get("id");
                    String downloadLink = DOWNLOAD_URL + id + DOWNLOAD;
                    dowloadFile(downloadLink, "/ressources/busicones/" + s + ".png");
                }
            }
        }
    }

    private void dowloadFile(String url, String filename){

        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(new File("src/ressources/busicones"))) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateBusIcons() throws IOException, ParseException {
        getIcons(getNomCourtLigne());
    }

    private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }


}
