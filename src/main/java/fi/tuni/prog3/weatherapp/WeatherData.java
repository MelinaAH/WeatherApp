package fi.tuni.prog3.weatherapp;

/**
 * A class for representing one set of weather data. Includes information about
 * weather conditions for a specific location. Contains methods to access and
 * manipulate weather data. The data can be retrieved from an external source
 * using a DataFetcher object.
 *
 * @author Jasper, Melina
 */
public class WeatherData {

    private String locationName;
    private double latitude;
    private double longitude;
    private double temperature;
    private String description;
    private double precipitation;
    private double windSpeed;
    private double windDirection;
    private Integer weatherId;
    private String iconPath = "";
    private String windIconPath = "";
    private String date;
    private double minTemp;
    private double maxTemp;
    private String hour;

    private static ProgramStatus programStatus;

    /**
     * Default constructor setting initial values for weather data. The default
     * location is set to "Helsinki" and other data to default numeric values.
     */
    public WeatherData() {
        this.locationName = "Helsinki";
        this.latitude = 0;
        this.longitude = 0;
        this.temperature = 0.0;
        this.description = "";
        this.precipitation = 0;
        this.windSpeed = 0;
        this.windDirection = 0;
        this.weatherId = 0;
        this.iconPath = "";
        this.windIconPath = "";
    }

    /**
     * Constructor with parameters to set weather data.
     *
     * @param locName Location name
     * @param lat Latitude
     * @param lon Longitude
     * @param temp Temperature
     * @param desc Description of weather conditions
     * @param precip Precipitation
     * @param windSp Wind speed
     * @param windDeg Wind direction
     * @param id Weather ID
     * @param iconPath Path to the weather icon
     * @param wIconPath Path to the wind icon
     */
    public WeatherData(String locName, double lat, double lon, double temp,
            String desc, double precip, double windSp, double windDeg,
            Integer id, String iconPath, String wIconPath) {

        this.locationName = locName;
        this.latitude = lat;
        this.longitude = lon;
        this.temperature = temp;
        this.description = desc;
        this.precipitation = precip;
        this.windSpeed = windSp;
        this.windDirection = windDeg;
        this.weatherId = id;
        this.iconPath = iconPath;
        this.windIconPath = wIconPath;
    }

    public static void setProgramStatus(ProgramStatus status) {
        programStatus = status;
    }

    public static String getCurrentLocation() {
        return programStatus != null ? programStatus.getCurrentLocation() : "";
    }

    public String getLocationName() {
        return locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public Integer getWeatherId() {
        return weatherId;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getWindIconPath() {
        return windIconPath;
    }

    public String getDate() {
        return date;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public String getHour() {
        return hour;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setWindIconPath(String windIconPath) {
        this.windIconPath = windIconPath;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    /**
     * Fetches weather data for the specified location name using the provided
     * DataFetcher.
     *
     * @param dataFetcher The DataFetcher object used to retrieve weather
     * information
     * @param locationName The name of the location for which weather data is
     * fetched
     * @return true, if fetching data was successful, otherwise false
     */
    public boolean fetchData(DataFetcher dataFetcher, String locationName) {

        String coord = dataFetcher.lookUpLocation(locationName);
        if (coord != null) {
            String[] coordArray = coord.split(",");
            double lat = Double.parseDouble(coordArray[0]);
            double lon = Double.parseDouble(coordArray[1]);
            this.locationName = coordArray[2];

            String weatherInfo = dataFetcher.getCurrentWeather(lat, lon);
            if (weatherInfo != null) {
                String[] weatherData = weatherInfo.split(",");
                double temp = Double.parseDouble(weatherData[0]);
                String desc = weatherData[1];
                double precip = Double.parseDouble(weatherData[2]);
                double windSp = Double.parseDouble(weatherData[3]);
                double windDeg = Double.parseDouble(weatherData[4]);
                int Id = Integer.parseInt(weatherData[5]);
                String path = weatherData[6];

                this.latitude = lat;
                this.longitude = lon;
                this.temperature = temp;
                this.description = desc;
                this.precipitation = precip;
                this.windSpeed = windSp;
                this.windDirection = windDeg;
                this.weatherId = Id;

                if (null != weatherId) {
                    switch (weatherId) {
                        case 801:
                            this.iconPath = "/icons/cloudy.png";
                            break;
                        case 802:
                            this.iconPath = "/icons/cloud.png";
                            break;
                        case 803:
                            this.iconPath = "/icons/clouds.png";
                            break;
                        case 804:
                            this.iconPath = "/icons/darkClouds.png";
                            break;
                        case 800:
                            this.iconPath = "/icons/sun.png";
                            break;
                        case 600:
                            this.iconPath = "/icons/snowflake.png";
                            break;
                        case 601:
                            this.iconPath = "/icons/snow.png";
                            break;
                        case 602:
                            this.iconPath = "/icons/heavySnow.png";
                            break;
                        case 615:
                            this.iconPath = "/icons/rainAndSnow.png";
                            break;
                        case 616:
                            this.iconPath = "/icons/rainAndSnow.png";
                            break;
                        case 502:
                            this.iconPath = "/icons/heavyRain.png";
                            break;
                        case 503:
                            this.iconPath = "/icons/heavyRain.png";
                            break;
                        case 521:
                            this.iconPath = "/icons/showerRain.png";
                            break;
                        default:
                            this.iconPath = path;
                            break;
                    }
                }

                if (windDirection != 0) {
                    this.windIconPath = getWindDirectionPath(windDirection);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Retrieves the file path for the wind direction icon based on the provided
     * wind direction angle.
     *
     * @param direction The wind direction angle in degrees
     * @return The file path for the corresponding wind direction icon
     */
    public String getWindDirectionPath(Double direction) {
        if (direction >= 337.5 || direction < 22.5) {
            return "/icons/north.png";
        } else if (direction >= 22.5 && direction < 67.5) {
            return "/icons/northeast.png";
        } else if (direction >= 67.5 && direction < 112.5) {
            return "/icons/east.png";
        } else if (direction >= 112.5 && direction < 157.5) {
            return "/icons/southeast.png";
        } else if (direction >= 157.5 && direction < 202.5) {
            return "/icons/south.png";
        } else if (direction >= 202.5 && direction < 247.5) {
            return "/icons/southwest.png";
        } else if (direction >= 247.5 && direction < 292.5) {
            return "/icons/west.png";
        } else if (direction == 0) {
            return "/icons/dot.png";
        } else {
            return "/icons/northwest.png";
        }
    }
}
