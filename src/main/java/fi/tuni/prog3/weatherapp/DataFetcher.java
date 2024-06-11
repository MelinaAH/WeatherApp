package fi.tuni.prog3.weatherapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DataFetcher class retrieves weather-related data from the OpenWeatherMap
 * API. It provides methods to fetch location coordinates, current weather data,
 * and forecasts. This class utilizes HTTP requests to communicate with the
 * OpenWeatherMap API and parse the JSON responses to obtain weather
 * information.
 *
 * This class implements the iAPI interface to define standard methods for data
 * retrieval.
 *
 * @author Melina
 */
public class DataFetcher implements iAPI {

    private final static String URL_LOC = "http://pro.openweathermap.org/geo/1.0/direct?q=";
    private final static String URL_COORD = "https://pro.openweathermap.org/data/2.5/weather?lat=";
    private final static String URL_ICON = "https://openweathermap.org/img/wn/";
    private final static String URL_DAILY_FORECAST = "https://pro.openweathermap.org/data/2.5/forecast/daily?lat=";
    private final static String URL_HOURLY_FORECAST = "https://pro.openweathermap.org/data/2.5/forecast/hourly?lat=";
    private final static String API_KEY = "4e6c29b273b04ea62bc7278f8ba72853";

    /**
     * Returns coordinates for a location.
     *
     * @param loc Name of the location for which coordinates should be fetched.
     * @return String - coordinates for a location
     * @throws MalformedURLException If there is a problem with the location URL
     * format.
     * @throws IOException If an I/O error occurs while fetching the location
     */
    @Override
    public String lookUpLocation(String loc) {
        try {
            URL url = new URL(URL_LOC + loc + "&limit=5&appid=" + API_KEY);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            Gson gson = new Gson();
            JsonArray array = gson.fromJson(response.toString(), JsonArray.class);

            if (array.size() > 0) {
                JsonObject location = array.get(0).getAsJsonObject();
                String name = location.get("name").getAsString();
                double lat = location.get("lat").getAsDouble();
                double lon = location.get("lon").getAsDouble();

                return lat + "," + lon + "," + name;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Returns the current weather for the given coordinates.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String - the current weather for the given coordinates
     * (temperature, description, precipitation, windSpeed, weatherId and
     * iconUrl)
     * @throws MalformedURLException If there is a problem with the coordinates
     * URL format.
     * @throws IOException If an I/O error occurs while fetching the current
     * weather data
     */
    @Override
    public String getCurrentWeather(double lat, double lon) {
        try {
            URL url = new URL(URL_COORD + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            Gson gson = new Gson();
            JsonObject jsonObj = gson.fromJson(response.toString(), JsonObject.class);

            JsonArray weatherArray = jsonObj.getAsJsonArray("weather");
            JsonObject weatherObj = weatherArray.get(0).getAsJsonObject();

            String desc = weatherObj.get("description").getAsString();
            String id = weatherObj.get("id").getAsString();
            String icon = weatherObj.get("icon").getAsString();
            String iconUrl = URL_ICON + icon + "@2x.png";
            
            System.out.println("DataFetcher current weather iconUrl: " + iconUrl);

            JsonObject main = jsonObj.getAsJsonObject("main");
            double temp = main.get("temp").getAsDouble();

            JsonObject rain = jsonObj.getAsJsonObject("rain");
            double precip = 0.0;
            if (rain != null && rain.has("1h")) {
                precip = rain.get("1h").getAsDouble();
            }

            JsonObject wind = jsonObj.getAsJsonObject("wind");
            double windSp = wind.get("speed").getAsDouble();
            double windDeg = wind.get("deg").getAsDouble();

            return temp + "," + desc + "," + precip + "," + windSp + "," + windDeg + "," + id + "," + iconUrl;

        } catch (MalformedURLException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Converts unix timestamps to dates and returns them as a String.
     *
     * @param timeStamp unix timestamp as long.
     * @return String form of a date.
     */
    public String unixToString(long timeStamp) {
        Date date = new Date(timeStamp*1000);
        String strDate = date.toString();
        return strDate;
    }

    /**
     * Returns 7 days daily forecast for the given coordinates.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String - the 16 days daily forecast for the given coordinates
     * (Date, IconPath, MinTemp and MaxTemp)
     * @throws MalformedURLException If there is a problem with the forecast URL
     * format.
     * @throws IOException If an I/O error occurs while fetching the forecast
     * data.
     */
    @Override
    public String getForecast(double lat, double lon) {
        try {
            URL forecastUrl = new URL(URL_DAILY_FORECAST + lat + "&lon=" + lon + "&cnt=7" + "&appid=" + API_KEY + "&units=metric");
            HttpURLConnection con = (HttpURLConnection) forecastUrl.openConnection();
            con.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            Gson gson = new Gson();
            JsonObject jsonObj = gson.fromJson(response.toString(), JsonObject.class);

            JsonArray forecastArray = jsonObj.getAsJsonArray("list");
            StringBuilder result = new StringBuilder();

            for (JsonElement elem : forecastArray) {
                JsonObject dayForecast = elem.getAsJsonObject();
                
                long timeStamp = dayForecast.get("dt").getAsLong();
                String date = unixToString(timeStamp);
                        
                String icon = dayForecast.getAsJsonArray("weather").get(0).getAsJsonObject().get("icon").getAsString();
                String iconUrl = URL_ICON + icon + "@2x.png";
                String minTemp = dayForecast.getAsJsonObject("temp").get("min").getAsString();
                String maxTemp = dayForecast.getAsJsonObject("temp").get("max").getAsString();

                System.out.println("DataFetcher getForecast iconUrl: " + iconUrl);
                
                String dayForecastData = "Date: " + date + ", IconPath: " + iconUrl + ", MinTemp: " + minTemp + ", MaxTemp: " + maxTemp + "\n";
                result.append(dayForecastData);
            }

            return result.toString();

        } catch (MalformedURLException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Returns hourly forecast for the given coordinates.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String - the hourly forecast for the given coordinates (hour,
     * iconPath, temp, windSpeed, precipitation)
     * @throws MalformedURLException If there is a problem with the hourly
     * forecast URL format.
     * @throws IOException If an I/O error occurs while fetching the hourly
     * forecast data.
     */
    public String getHourlyForecast(double lat, double lon) {
        try {
            URL forecastUrl = new URL(URL_HOURLY_FORECAST + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric");
            HttpURLConnection con = (HttpURLConnection) forecastUrl.openConnection();
            con.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            Gson gson = new Gson();
            JsonObject jsonObj = gson.fromJson(response.toString(), JsonObject.class);

            JsonArray forecastArray = jsonObj.getAsJsonArray("list");
            StringBuilder result = new StringBuilder();

            for (JsonElement elem : forecastArray) {
                JsonObject hourForecast = elem.getAsJsonObject();

                String hour = hourForecast.get("dt_txt").getAsString();
                String icon = hourForecast.getAsJsonArray("weather").get(0).getAsJsonObject().get("icon").getAsString();
                String iconUrl = URL_ICON + icon + "@2x.png";
                String temp = hourForecast.getAsJsonObject("main").get("temp").getAsString();
                String windSpeed = hourForecast.getAsJsonObject("wind").get("speed").getAsString();
                String windDeg = hourForecast.getAsJsonObject("wind").get("deg").getAsString();
                String precipitation = "0"; // Initial value if no rain data available
                if (hourForecast.has("rain") && hourForecast.getAsJsonObject("rain").has("1h")) {
                    precipitation = hourForecast.getAsJsonObject("rain").get("1h").getAsString();
                }
                
                //System.out.println("DataFetcher getHourlyForecast temp: " + temp);

                String hourForecastData = "Hour: " + hour + ", IconPath: " + iconUrl + ", Temp: " + temp + ", WindSpeed: " + windSpeed + ", WindDeg: " + windDeg + ", Precipitation: " + precipitation + "\n";
                result.append(hourForecastData);
            }

            return result.toString();

        } catch (MalformedURLException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
