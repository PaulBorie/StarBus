package sample;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Weather {

    private String city;
    private String date;
    private String description;
    private String wind;
    private String humidity;
    private String iconUri;
    private String temperature;
    private String ressentie;
    private final static String API_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final static String LONG_DATE_PATTERN = "EEEE d MMM HH:mm";
    private final static String SHORT_DATE_PATTERN= "EE\nHH:mm";



    public Weather(String city, String date, String description, String iconUri, double temperature, double wind, long humidity, double ressentie) {
        this.city = city;
        this.date = date;
        this.description = description;
        this.wind = Integer.toString((int) Math.round(toKmPerHour(wind))) + " km/h";
        this.humidity = Long.toString(Math.round(humidity)) + "%";
        this.temperature = Integer.toString((int) Math.round(temperature)) +"°C";
        this.iconUri = "/ressources/images/"+getImage(iconUri);
        this.ressentie = Integer.toString( (int) Math.round(ressentie)) +"°C";
    }

    private static double toKmPerHour(double meterPerSecond){
        double res = (meterPerSecond * 60 * 60) / 1000;
        return res;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "city='" + city + '\'' +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", wind='" + wind + '\'' +
                ", humidity='" + humidity + '\'' +
                ", iconUri='" + iconUri + '\'' +
                ", temperature='" + temperature + '\'' +
                ", ressentie='" + ressentie + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Weather)) {
            return false;
        }
        Weather w = (Weather) o;
        return city.equals(w.city) && date.equals(w.date) && description.equals(w.description) && wind.equals(w.wind) && humidity.equals(w.humidity) && temperature.equals(w.temperature)
                && iconUri.equals(w.iconUri) && ressentie.equals(w.ressentie);
    }

    public String getDateFormated() throws ParseException {
        SimpleDateFormat apiFormat = new SimpleDateFormat(API_DATE_PATTERN);
        SimpleDateFormat targetFormat = new SimpleDateFormat(LONG_DATE_PATTERN);
        Date parsedDate = apiFormat.parse(this.date);
        return targetFormat.format(parsedDate);

    }

    public static String getTodayDate(){
        Date todayDate = new Date();
        SimpleDateFormat targetFormat = new SimpleDateFormat(LONG_DATE_PATTERN);
        return targetFormat.format(todayDate);


    }

    public String getDateShortFormated() throws ParseException {
        SimpleDateFormat apiFormat = new SimpleDateFormat(API_DATE_PATTERN);
        SimpleDateFormat targetFormat = new SimpleDateFormat(SHORT_DATE_PATTERN);
        Date parsedDate = apiFormat.parse(this.date);
        String targetFormatDate = targetFormat.format(parsedDate);
        targetFormatDate = targetFormatDate.replace(".","");
        return targetFormatDate;
    }

    private static String getImage(String icon) {
        switch (icon){
            case "01d": case "01n":
                return "01d.png";
            case "02d": case "02n":
                return "02d.png";
            case "03d": case "03n": case "04d": case "04n":
                return "03.png";
            case "09d": case "09n":
                return "09.png";
            case "10d": case "10n":
                return "10d.png";
            case "11d": case "11n":
                return "11.png";
            case "13d": case "13n":
                return "13.png";
            case "50d": case "50":
                return "50.png";
            default: return "01d.png";
        }
    }

    public String getRessentie() { return ressentie; }

    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }

    public String getWind() {
        return wind;
    }

    public String getHumidity() {
        return humidity;
    }


    public String getIconUri() {
        return iconUri;
    }

    public String getTemperature() {
        return temperature;
    }
}
