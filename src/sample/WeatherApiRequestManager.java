package sample;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeatherApiRequestManager {

    private static String url = "https://api.openweathermap.org/data/2.5/forecast?q=Rennes,fr&lang=fr&units=metric&appid=d4aac52703e53f10f4e9cd7d6d54875b";
    final static private String BASE_URL= "https://api.openweathermap.org/data/2.5/forecast?";
    final static  private String API_KEY= "d4aac52703e53f10f4e9cd7d6d54875b";
    final static private String LANG = "fr";
    //units parameter to get the results in Celcius unit"
    final static private String UNITS = "metric";
    final static private int NB_MINI_WEATHER = 8;




    private static String generateUrl(String city){
        String url = BASE_URL + "q=" + city + ",fr" + "&lang=" + LANG + "&units=" + UNITS + "&appid=" + API_KEY;
        System.out.println(url);
        return url;
    }

    private static String httpRequest(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    private static List<Weather> listWeatherFromJson(String jsonData) throws ParseException {

        List<Weather> res = new ArrayList<>();

        JSONObject weatherInfos = (JSONObject) JSONValue.parseWithException(jsonData);
        JSONArray list = (JSONArray) weatherInfos.get("list");
        JSONObject city = (JSONObject) weatherInfos.get("city");
        String cityname = (String) city.get("name");

        List<Object> sublist = list.subList(0, NB_MINI_WEATHER);
        for (Object o : sublist){
            if(o instanceof  JSONObject){
                JSONObject jo = (JSONObject) o;
                Weather w = jsonToWeather(jo, cityname);
                res.add(w);

            }
        }
        return res;
    }

    private static Weather jsonToWeather(JSONObject json, String cityname){

        String date = "";
        String description = "";
        double wind;
        long humidity;
        String iconUri = "";
        double temperature;
        double ressentie;

        JSONObject main = (JSONObject) json.get("main");
        JSONArray weather = (JSONArray) json.get("weather");
        JSONObject windinfo = (JSONObject) json.get("wind");

        date = (String) json.get("dt_txt");
        temperature = convertToDouble(main.get("temp"));
        humidity = (long) main.get("humidity");
        ressentie = convertToDouble(main.get("feels_like"));




        JSONObject weatherInfo = (JSONObject) weather.get(0);
        description = (String) weatherInfo.get("description");
        iconUri = (String) weatherInfo.get("icon");

        wind = convertToDouble(windinfo.get("speed"));

        Weather res = new Weather(cityname, date, description, iconUri, temperature, wind, humidity, ressentie);
        System.out.println(res);
        return res;
    }


    public static List<Weather> getWeatherInfos(String city) throws ParseException, IOException {
        String url = generateUrl(city);
        String jsonData = httpRequest(url);
        List<Weather> listWeather = listWeatherFromJson(jsonData);
        return listWeather;
    }

    private static double convertToDouble(Object o){
       if(o instanceof Double){
           return (double) o;
       }
       if(o instanceof Long){
           Long l = (long) o;
           return l.doubleValue();
       }
       return 0.0;
    }


    public static void main(String[] args){
        try {

            List<Weather> listWeather = WeatherApiRequestManager.getWeatherInfos("Rennes");

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
