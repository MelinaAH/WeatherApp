package fi.tuni.prog3.weatherapp;

import java.util.ArrayList;

/**
 * A class for represeting forecasts of one location stored as an arraylist of
 * WeatherData objects. Communicates with DataFetcher to get forecast
 * information and processes it to create WeatherData objects for each daily
 * forecast.
 *
 * @author Jasper, Melina
 */
public class WeatherForecast {

    private ArrayList<WeatherData> dailyForecast;
    private String locationName;
    private DataFetcher dataFetcher;

    /**
     * Constructor Initializes empty variables
     */
    public WeatherForecast() {
        this.dailyForecast = new ArrayList<>();
        this.locationName = null;
        this.dataFetcher = new DataFetcher();
    }

    /**
     * Retrieves the list of daily forecasts.
     *
     * @return ArrayList containing WeatherData objects representing daily
     * forecasts
     */
    public ArrayList<WeatherData> getDailyForecast() {
        return dailyForecast;
    }

    /**
     * Fetches daily weather forecast data based on the provided location name.
     *
     * @param locationName The name of the location to fetch daily forecast data
     * @return boolean value of whether the fetching of daily data was 
     * successful or not.
     */
    public boolean fetchDailyData(String locationName) {
        this.locationName = locationName;
        this.dailyForecast.clear();

        String coord = dataFetcher.lookUpLocation(locationName);
        if (coord != null) {
            String[] coordArray = coord.split(",");
            double lat = Double.parseDouble(coordArray[0]);
            double lon = Double.parseDouble(coordArray[1]);
            String name = coordArray[2];

            String forecastInfo = dataFetcher.getForecast(lat, lon);
            if (forecastInfo != null) {
                String[] dailyForecasts = forecastInfo.split("\n");
                for (String df : dailyForecasts) {
                    String[] forecastDetails = df.split(",");
                    if (forecastDetails.length >= 4) {
                        String date = forecastDetails[0].substring(6).trim();
                        String iconURL = forecastDetails[1].split(": ")[1].trim();
                        String minTemp = forecastDetails[2].substring(10).trim();
                        String maxTemp = forecastDetails[3].substring(10).trim();
                        
                        System.out.println("WeatherForecast iconURL: " + iconURL);

                        WeatherData weatherData = new WeatherData(name, lat, lon,
                                0.0, "", 0.0, 0.0, 0.0, 0, "", "");
                        weatherData.setDate(date);
                        weatherData.setIconPath(iconURL);
                        weatherData.setMinTemp(Double.parseDouble(minTemp));
                        weatherData.setMaxTemp(Double.parseDouble(maxTemp));

                        dailyForecast.add(weatherData);
                    }

                }
            } 
        } else {
            return false;
        }
        return true;
    }
}
