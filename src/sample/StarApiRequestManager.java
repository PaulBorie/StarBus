package sample;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class StarApiRequestManager {

    private final static String API_KEY = "c0a50b783dba345059c2181132ae15f41a914b124510a6b0cc2ec3ab";
    private final static String URL=  "https://data.explore.star.fr/api/records/1.0/search/?dataset=tco-bus-circulation-passages-tr&q=";
    private final static String SORT = "-depart";
    private final static String TIMEZONE = "Europe%2FParis";
    private final static int ROWS = 3;
    private final static String RESOLUTION = "1:100";

    private final static String URL_PICTO = "https://data.explore.star.fr/api/records/1.0/search/?dataset=tco-bus-lignes-pictogrammes-dm&refine.resolution=" + RESOLUTION + "&refine.nomcourtligne=";



    private static String refineFacet(String facet){
        return "&refine." + facet + "=";
    }

    private static String generatetUrl(String nomArret, String nomLigne, String destination ){

        String facetNomCourtligne = "nomcourtligne";
        String facetNomArret = "nomarret";
        String facetDestination ="destination";

        String url = URL + "&sort=" + SORT + "&rows=" + ROWS + refineFacet(facetNomCourtligne) +
                nomLigne +refineFacet(facetDestination) + destination + refineFacet(facetNomArret) + nomArret + "&timezone=" + TIMEZONE + "&apikey=" + API_KEY;

        System.out.println("url generated :" + url) ;
        return url;
    }

    public static String getPictoUri(BusStop bs) throws IOException {
        String url = URL_PICTO + bs.getNumLine();
        String json = httpRequest(url);
        JSONObject jsonObject = (JSONObject) JSONValue.parse(json);
        JSONArray records = (JSONArray) jsonObject.get("records");
        JSONObject firstrecord = (JSONObject) records.get(0);
        JSONObject fields = (JSONObject) firstrecord.get("fields");
        JSONObject image = (JSONObject) fields.get("image");
        String filename = (String) image.get("filename");
        return "/ressources/busicones/" + filename;
    }



    private static String httpRequest(String url) throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    public static String[] getBusSchedule(BusStop bs) throws IOException {
        System.out.println("GET BUS CHEDULE");
        String url = generatetUrl(bs.getName(), bs.getNumLine(), bs.getDirection());
        String busScheduleJson = httpRequest(url);
        return busScheduleFromJson(busScheduleJson);
    }

    private static String[] busScheduleFromJson(String jsonData)  {

        JSONObject busSchedule = (JSONObject) JSONValue.parse(jsonData);
        if(!busSchedule.containsKey("error")){
            String[] busScheduleArray = new String[3];
            JSONArray records = (JSONArray) busSchedule.get("records");
            int i = 0;
            for (Object record : records){
                JSONObject recordJSON = (JSONObject) record;
                JSONObject fields = (JSONObject) recordJSON.get("fields");
                String depart = (String) fields.get("depart");
                String[] departSplit = depart.split("\\+");
                busScheduleArray[i] = timeDifference(departSplit[0]);
                i++;
            }
            return busScheduleArray;
        }else{
            return null;
        }
    }

    public static void printArray(String[] array){
        if (array != null ){
            for(String s : array)
                System.out.println(s);
        }
    }



    private static String timeDifference(String start_date) {

        // SimpleDateFormat converts the string format to date object
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            // parse method is used to parse the text from a string to produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = new Date();

            // Calculate time difference in milliseconds
            long difference_In_Time = d1.getTime() - d2.getTime();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(difference_In_Time);
            if (minutes < 0)
                minutes = 0;
            return Long.toString(minutes);

        }catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args){
        BusStop bs = new BusStop("Dargent", "9", "Cleunay");

        try {
            String iconUri = StarApiRequestManager.getPictoUri(bs);
            System.out.println(iconUri);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}


