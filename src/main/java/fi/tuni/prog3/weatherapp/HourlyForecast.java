package fi.tuni.prog3.weatherapp;

import java.util.ArrayList;

/**
 * HourlyForecast class handles fetching and storing hourly weather forecast
 * data. It communicates with DataFetcher to obtain weather information and
 * processes it to create WeatherData objects for each hourly forecast.
 *
 * This class maintains an ArrayList of WeatherData objects representing the
 * hourly forecast for a specific location.
 *
 * @author Melina
 */
public class HourlyForecast {

    private ArrayList<WeatherData> hourlyForecast;
    private String locationName;
    private DataFetcher dataFetcher;

    /**
     * Constructor for HourlyForecast class. Initializes empty variables.
     */
    public HourlyForecast() {
        this.hourlyForecast = new ArrayList<>();
        this.locationName = null;
        this.dataFetcher = new DataFetcher();
    }

    /**
     * Retrieves the list of hourly forecasts.
     *
     * @return ArrayList containing WeatherData objects representing hourly
     * forecasts.
     */
    public ArrayList<WeatherData> getHourlyForecast() {
        return hourlyForecast;
    }

    /**
     * Fetches hourly weather forecast data based on the provided location name.
     *
     * @param locationName The name of the location to fetch hourly forecast
     * data for.
     * @return boolean value of whether the fetching of hourly data was 
     * successful or not.
     */
    public boolean fetchHourlyData(String locationName) {
        this.locationName = locationName;
        this.hourlyForecast.clear();

        String coord = dataFetcher.lookUpLocation(locationName);
        if (coord != null) {
            String[] coordArray = coord.split(",");
            double lat = Double.parseDouble(coordArray[0]);
            double lon = Double.parseDouble(coordArray[1]);

            String forecastInfo = dataFetcher.getHourlyForecast(lat, lon);
            if (forecastInfo != null) {
                String[] hourlyForecasts = forecastInfo.split("\n");
                for (String hf : hourlyForecasts) {
                    String[] forecastDetails = hf.split(",");

                    if (forecastDetails.length >= 5) {
                        String hour = forecastDetails[0].substring(6).trim();
                        String iconURL = forecastDetails[1].substring(10).trim();
                        String temp = forecastDetails[2].substring(6).trim();
                        String windSp = forecastDetails[3].substring(12).trim();
                        String windDeg = forecastDetails[4].substring(10).trim();

                        //System.out.println("HourlyForecast iconURL: " + iconURL);
                        //System.out.println("HourlyForecast temp: " + temp);
                        //System.out.println("HourlyForecast windDeg: " + windDeg);

                        WeatherData weatherData = new WeatherData();
                        weatherData.setHour(hour);
                        weatherData.setIconPath(iconURL);
                        weatherData.setTemperature(Double.parseDouble(temp));
                        weatherData.setWindSpeed(Double.parseDouble(windSp));
                        weatherData.setWindDirection(Double.parseDouble(windDeg));

                        String precip = "0.0";

                        if (forecastDetails.length >= 5) {
                            precip = forecastDetails[5].substring(14).trim();
                        }

                        try {
                            weatherData.setPrecipitation(Double.parseDouble(precip));
                        } catch (NumberFormatException e) {
                            weatherData.setPrecipitation(0.0);
                        }

                        hourlyForecast.add(weatherData);
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
