package fi.tuni.prog3.weatherapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WeatherApp extends Application {

    private ImageView weatherIcon = new ImageView();
    private Image favouriteIconImage = new Image("/icons/favourite.png");
    private Image favouritedIconImage = new Image("/icons/favourited.png");
    private ImageView favouriteIconView = new ImageView(favouriteIconImage);
    private Image precipIcon = new Image("/icons/drop.png");
    private Image windIcon = new Image("/icons/wind.png");
    private ImageView precipIconView;
    private ImageView windIconView;
    private ImageView windDirIcon = new ImageView();
    private Button favouriteButton = new Button("", favouriteIconView);
    private Button searchButton;
    private Button clearFavouritesButton;
    private Button forecastButton;
    private Button hourlyForecastButton;
    private TextField searchField;
    private Label descLabel;
    private Label locationLabel;
    private Label tempLabel;
    private Label unknownLabel;
    private Label precipLabel;
    private Label windspLabel;
    private ComboBox<String> favouritesComboBox;
    private String currentLocation;
    private static final String FILENAME = "./favouritesAndStatus.json";
    private FileManager fileManager;
    private FavouritesManager favouritesManager;
    private ProgramStatus programStatus;
    private HBox dailyForecastBox;
    private HBox hourlyForecastBox;
    private BorderPane root = new BorderPane();
    private static final String ICON_URL_PREFIX = "https://openweathermap.org/img/wn/";

    /**
     * Creates the graphical user interface for the application
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        root.setPadding(new Insets(10));

        createSearchComponents();
        createFavouriteComponents();
        favouritesManager = new FavouritesManager();
        programStatus = new ProgramStatus();
        fileManager = new FileManager(FILENAME, favouritesManager, programStatus);
        HBox searchBox = createSearchBox();
        VBox headerBox = createHeaderBox(searchBox);

        root.setTop(headerBox);
        root.setCenter(dailyForecastBox);
        root.setBottom(hourlyForecastBox);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("WeatherApp");
        primaryStage.show();
        searchField.requestFocus();

        fileManager.readFromFile(FILENAME);
        updateFavouritesComboBox();

        if (favouritesManager.getFavourites().isEmpty() && programStatus.getCurrentLocation().isEmpty()) {
            currentLocation = "Helsinki";
            searchWeather();
        } else {
            currentLocation = programStatus.getCurrentLocation();
            searchWeather();
        }
    }

    /**
     * Saves the favourites and the current location to a JSON file when the
     * program closes
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        try {
            fileManager.writeToFile(FILENAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Favourite locations: " + favouritesManager.getFavourites());
    }

    /**
     * Creates the GUI components responsible for searching the location
     */
    private void createSearchComponents() {

        searchField = new TextField();
        searchField.setPromptText("Enter location");
        searchField.getStyleClass().add("forecast-label");

        Image searchIconImage = new Image("/icons/search.png");
        ImageView searchIconView = new ImageView(searchIconImage);
        searchIconView.setFitWidth(24);
        searchIconView.setFitHeight(24);

        searchButton = new Button("", searchIconView);
        searchButton.setMaxWidth(Button.USE_PREF_SIZE);
        searchButton.getStyleClass().add("forecast-label");

        searchButton.setDisable(true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                searchButton.setDisable(newValue.trim().isEmpty());
            }
        });

        searchButton.setOnAction(e -> searchWeather());
        searchField.setOnAction(e -> searchButton.fire());
    }

    /**
     * Creates the GUI components responsible for managing favourite locations
     */
    private void createFavouriteComponents() {
        favouritesComboBox = new ComboBox<>();
        favouritesComboBox.setPromptText("Select favourite");
        favouritesComboBox.setVisibleRowCount(4);

        favouritesComboBox.setOnAction(e -> {
            String selectedLocation = favouritesComboBox.getValue();
            if (selectedLocation != null) {
                searchField.setText(selectedLocation);
                searchWeather();
            }
        });

        clearFavouritesButton = new Button("Clear favourites");

        clearFavouritesButton.setOnAction(e -> {
            favouritesManager.clearFavourites();
            updateFavouritesComboBox();
            clearFavouritesButton.setDisable(true);
            favouritesComboBox.setDisable(true);
        });

        favouriteButton.setDisable(true);
        favouriteIconView.setFitWidth(24);
        favouriteIconView.setFitHeight(24);

        favouriteButton.setStyle("-fx-font-size: 18");
        favouriteButton.setOnAction(e -> toggleFavourite());
    }

    /**
     * Updates the favourites in the combo box when they change
     */
    private void updateFavouritesComboBox() {
        List<String> favourites = favouritesManager.getFavourites();
        favouritesComboBox.setItems(FXCollections.observableArrayList(favourites));
        if (!favourites.isEmpty()) {
            clearFavouritesButton.setDisable(false);
            favouritesComboBox.setDisable(false);
        }
    }

    /**
     * Search box contains the topmost GUI element, containing the favourite
     * button and the search elements
     *
     * @return HBox the created search box
     */
    private HBox createSearchBox() {
        HBox searchBox = new HBox(5, favouriteButton, searchField, searchButton);
        searchBox.setPadding(new Insets(10));
        HBox.setHgrow(searchField, Priority.ALWAYS);
        return searchBox;
    }

    /**
     * Header box is the component which displays the current weather and
     * contains the search and favourite components at the top
     * @param searchBox the search box created in createSearchBox
     * @return VBox the created header box
     */
    private VBox createHeaderBox(HBox searchBox) {
        unknownLabel = new Label();
        unknownLabel.setPadding(new Insets(0, 0, 0, 10));
        HBox unknownLocBox = new HBox(favouritesComboBox, clearFavouritesButton, unknownLabel);
        unknownLocBox.setPadding(new Insets(5, 5, 5, 10));

        locationLabel = new Label();
        locationLabel.getStyleClass().add("location-label");
        descLabel = new Label();
        descLabel.getStyleClass().add("description-label");
        HBox locationBox = new HBox(locationLabel);
        locationBox.setAlignment(Pos.CENTER);
        HBox descriptionBox = new HBox(descLabel);
        descriptionBox.setAlignment(Pos.CENTER);
        
        HBox tempBox = createTemperatureBox();

        precipIconView = new ImageView();
        windIconView = new ImageView();
        windDirIcon = new ImageView();

        precipLabel = new Label();
        precipLabel.getStyleClass().add("detail-label");

        windspLabel = new Label();
        windspLabel.getStyleClass().add("detail-label");
        
        precipIconView.setFitWidth(40);
        precipIconView.setFitHeight(40);
        windIconView.setFitWidth(40);
        windIconView.setFitHeight(40);
        windDirIcon.setPreserveRatio(true);
        windDirIcon.setFitWidth(40);
        windDirIcon.setFitHeight(40);

        HBox detailBox = new HBox(10, precipIconView, precipLabel, windIconView, windspLabel, windDirIcon);
        detailBox.setAlignment(Pos.CENTER);

        VBox headerBox = new VBox(searchBox, unknownLocBox, locationBox, descriptionBox, tempBox, detailBox);
        headerBox.getStyleClass().add("vignette-box");
        headerBox.setPadding(new Insets(10));
        
        headerBox.setId("headerBox");

        return headerBox;
    }

    /**
     * Temperature box contains the current temperature at the given location
     * along with a weather icon
     *
     * @return HBox the created temperature box
     */
    private HBox createTemperatureBox() {
        tempLabel = new Label("");
        tempLabel.getStyleClass().add("temp-label");

        weatherIcon.setFitWidth(128);
        weatherIcon.setFitHeight(128);
        
        tempLabel.setId("tempLabel");
        weatherIcon.setId("iconShadow");

        HBox tempBox = new HBox(40, tempLabel, weatherIcon);
        tempBox.setPadding(new Insets(20));
        tempBox.setAlignment(Pos.CENTER);
        return tempBox;
    }

    /**
     * Used for creating the container for both the daily and hourly forecasts
     *
     * @return HBox the created forecast box
     */
    private HBox createForecastBox() {
        HBox forecastBox = new HBox(10);
        forecastBox.setPadding(new Insets(10, 0, 0, 0));
        forecastBox.setId("forecastBox");

        return forecastBox;
    }

    /**
     * Updates the hourly forecast contained in hourlyForecastBox
     *
     * @param hourlyData the hourly weather data to be displayed
     * @throws IOException
     */
    private void updateHourlyForecastBox(ArrayList<WeatherData> hourlyData) throws IOException {

        if (hourlyForecastBox == null) {
            hourlyForecastBox = createForecastBox();
            root.setBottom(hourlyForecastBox);
        }

        hourlyForecastBox.getChildren().clear();

        for (int i = 0; i < 12; i++) {
            WeatherData data = hourlyData.get(i);
            long roundedTemp = Math.round(data.getTemperature());
            LocalDateTime dateTime = LocalDateTime.parse(data.getHour(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String hour = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            Label hourLabel = new Label(hour);
            Label hourlyTempLabel = new Label(roundedTemp + "°C");
            long roundedWindSpeed = Math.round(data.getWindSpeed());
            Label windSpeedLabel = new Label(roundedWindSpeed + " m/s");
            Label precipLabel = new Label(data.getPrecipitation() + " mm");

            String iconPath = data.getIconPath();
            Image icon = downloadIcon(iconPath.substring(34));
            ImageView hourIconView = new ImageView(icon);

            String windIconPath = data.getWindDirectionPath(data.getWindDirection());
            Image windIcon = new Image(windIconPath);
            ImageView windIconView = new ImageView(windIcon);

            windIconView.setPreserveRatio(true);
            windIconView.setFitWidth(24);
            windIconView.setFitHeight(24);

            hourIconView.setFitWidth(64);
            hourIconView.setFitHeight(64);

            hourLabel.getStyleClass().add("forecast-label");
            hourlyTempLabel.getStyleClass().add("forecast-label");
            windSpeedLabel.getStyleClass().add("forecast-label");
            precipLabel.getStyleClass().add("forecast-label");

            VBox hourForecastBox = new VBox(0, hourLabel, hourIconView, hourlyTempLabel, windIconView, windSpeedLabel, precipLabel);
            hourForecastBox.getStyleClass().add("vignette-box");
            hourForecastBox.setMinWidth(50);
            hourForecastBox.setAlignment(Pos.CENTER);
            HBox.setHgrow(hourForecastBox, Priority.ALWAYS);

            hourlyForecastBox.getChildren().add(hourForecastBox);
        }
    }

    /**
     * Updates the daily forecast box container with new weather data
     *
     * @param dailyForecast the daily forecast data to be displayed
     * @throws IOException
     */
    private void updateForecastBox(ArrayList<WeatherData> dailyForecast) throws IOException {

        if (dailyForecastBox == null) {
            dailyForecastBox = createForecastBox();
            root.setCenter(dailyForecastBox);
        }

        dailyForecastBox.getChildren().clear();

        for (WeatherData data : dailyForecast) {
            String date = data.getDate();
            String[] dateParts = date.split(" ");
            String formattedDate = dateParts[0] + " " + dateParts[2] + " " + dateParts[1];
            Label dayLabel = new Label(formattedDate);
            long roundedMinTemp = Math.round(data.getMinTemp());
            long roundedMaxTemp = Math.round(data.getMaxTemp());
            Label minTempLabel = new Label(roundedMinTemp + "°C");
            Label maxTempLabel = new Label(roundedMaxTemp + "°C");

            String weatherIconPath = data.getIconPath();
            Image dailyWeatherIcon = downloadIcon(weatherIconPath.substring(34));
            ImageView dayIconView = new ImageView(dailyWeatherIcon);

            dayLabel.getStyleClass().add("forecast-label");
            minTempLabel.getStyleClass().add("forecast-label");
            maxTempLabel.getStyleClass().add("forecast-label");

            VBox dayForecastBox = new VBox(0, dayLabel, dayIconView, minTempLabel, maxTempLabel);
            dayForecastBox.getStyleClass().add("vignette-box");
            dayForecastBox.setMinWidth(50);
            dayForecastBox.setAlignment(Pos.CENTER);
            HBox.setHgrow(dayForecastBox, Priority.ALWAYS);

            dailyForecastBox.getChildren().add(dayForecastBox);
        }
    }

    /**
     * Handles fetching and loading of the weather icons. The icons are cached;
     * they are only downloaded when they don't exist locally. This will speed
     * up loading the forecasts.
     *
     * @param iconName the name of the icon to be fetched
     * @return Image the image fetched, or null if it can't be loaded
     */
    private Image downloadIcon(String iconName) {
        try {
            String localIconPath = "src/main/resources/cache/" + iconName;
            File localIconFile = new File(localIconPath);

            File parentDirectory = localIconFile.getParentFile();
            if (!parentDirectory.exists()) {
                Files.createDirectories(parentDirectory.toPath());
            }

            if (!localIconFile.exists()) {
                URL iconUrl = new URL(ICON_URL_PREFIX + iconName);
                try (InputStream in = iconUrl.openStream()) {
                    Files.copy(in, localIconFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    return null;
                }
            }

            Image iconImage = new Image(localIconFile.toURI().toString());
            return iconImage;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Searches the weather forecast for the given location
     */
    private void searchWeather() {
        String locationName;

        String lastLocation = programStatus.getCurrentLocation();
        if (lastLocation != null && !lastLocation.isEmpty()) {
            locationName = lastLocation;
        } else {
            locationName = "Helsinki";
        }

        String userInput = searchField.getText();
        if (!userInput.isEmpty()) {
            locationName = userInput;
        }

        DataFetcher dataFetcher = new DataFetcher();
        WeatherData weatherData = new WeatherData();

        boolean fetchSuccess = weatherData.fetchData(dataFetcher, locationName);

        if (!fetchSuccess) {
            unknownLabel.setText("Unknown location");
            unknownLabel.getStyleClass().add("unknown-label");
            searchField.setText(currentLocation);
            scheduleHideUnknownText();
            return;
        }

        programStatus.setCurrentLocation(locationName);

        displayWeather(weatherData);
        searchForecast();
        searchHourlyForecast();
    }

    /**
     * Displays the fetched weather data
     *
     * @param weatherData the weather data to be displayed
     */
    private void displayWeather(WeatherData weatherData) {
        System.out.println("Location: " + weatherData.getLocationName());
        System.out.println("Temperature: " + weatherData.getTemperature() + "°C");
        System.out.println("Description: " + weatherData.getDescription());
        System.out.println("Precipitation: " + weatherData.getPrecipitation());
        System.out.println("Wind Speed: " + weatherData.getWindSpeed());
        System.out.println("Icon Path: " + weatherData.getIconPath());
        System.out.println("Wind icon Path: " + weatherData.getWindIconPath());

        unknownLabel.setText("");

        currentLocation = weatherData.getLocationName();
        favouriteButton.setDisable(false);
        searchField.setText(currentLocation);
        long roundedTemp = Math.round(weatherData.getTemperature());
        String descr_lower = weatherData.getDescription();
        String description = descr_lower.substring(0, 1).toUpperCase() + descr_lower.substring(1);
        locationLabel.setText(currentLocation);
        descLabel.setText(description);
        tempLabel.setText(roundedTemp + "°C");
        weatherIcon.setImage(new Image(weatherData.getIconPath()));
        precipLabel.setText(weatherData.getPrecipitation() + " mm");
        precipIconView.setImage(precipIcon);
        long roundedWindSpeed = Math.round(weatherData.getWindSpeed());
        windspLabel.setText(roundedWindSpeed + " m/s");
        windIconView.setImage(windIcon);
        
        String windIconPath = weatherData.getWindDirectionPath(weatherData.getWindDirection());
        windDirIcon.setImage(new Image(windIconPath));

        updateTemperatureColor();
        updateFavouriteButtonIcon();
    }

    /**
     * Creates a timer for displaying the warning when the location was not
     * found
     */
    private void scheduleHideUnknownText() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> hideUnknownText());
                timer.cancel();
            }
        }, 2000);
    }

    /**
     * Hides the "Unknown location" text after delay
     */
    private void hideUnknownText() {
        unknownLabel.setText("");
    }

    /**
     * Matches the temperature colour to the current temperature. The
     * temperature is shown red if it's under 0 degrees, otherwise red.
     */
    private void updateTemperatureColor() {
        String temperatureText = tempLabel.getText();
        double temperature = Double.parseDouble(temperatureText.substring(0, temperatureText.length() - 2).trim());

        tempLabel.getStyleClass().removeAll("cold", "warm");
        
        if (temperature < 0) {
            tempLabel.getStyleClass().add("cold");
        } else {
            tempLabel.getStyleClass().add("warm");
        }

        tempLabel.setText((long) temperature + "°C");
    }

    /**
     * Toggles the favourite status of the location
     */
    private void toggleFavourite() {
        if (favouritesManager.addFavourite(currentLocation)) {
            favouriteIconView.setImage(favouritedIconImage);
        } else {
            favouritesManager.removeFavourite(currentLocation);
            favouriteIconView.setImage(favouriteIconImage);
            if (favouritesManager.getFavourites().isEmpty()) {
                favouritesComboBox.setDisable(true);
                clearFavouritesButton.setDisable(true);
            }
        }
        updateFavouritesComboBox();
        System.out.println("Favourite locations: " + favouritesManager.getFavourites());

        updateFavouriteButtonIcon();
    }

    /**
     * Updates the favourite button icon to match the favourite status of
     * the location
     */
    private void updateFavouriteButtonIcon() {
        String location = searchField.getText();
        boolean isFavourite = favouritesManager.isFavourite(location);

        if (isFavourite) {
            favouriteIconView.setImage(favouritedIconImage);
        } else {
            favouriteIconView.setImage(favouriteIconImage);
        }
    }

    /**
     * Searches for the daily forecast for the given location
     */
    private void searchForecast() {
        try {
            WeatherForecast weatherForecast = new WeatherForecast();
            weatherForecast.fetchDailyData(currentLocation);

            displayForecast(weatherForecast);

            updateForecastBox(weatherForecast.getDailyForecast());
        } catch (Exception e) {
        }
    }

    /**
     * Displays the daily forecast for the given location
     *
     * @param weatherForecast the weather data to be displayed
     */
    private void displayForecast(WeatherForecast weatherForecast) {
        ArrayList<WeatherData> dailyForecast = weatherForecast.getDailyForecast();
        for (WeatherData data : dailyForecast) {
            System.out.println("Location: " + data.getLocationName());
            System.out.println("Date: " + data.getDate());
            System.out.println("Min Temp: " + data.getMinTemp());
            System.out.println("Max Temp: " + data.getMaxTemp());
            System.out.println("Icon Path: " + data.getIconPath());
            System.out.println("-----------------------------------");
        }

    }

    /**
     * Searches for the hourly forecast for the given location
     */
    private void searchHourlyForecast() {
        try {
            HourlyForecast hourlyForecast = new HourlyForecast();
            hourlyForecast.fetchHourlyData(currentLocation);

            displayHourlyForecast(hourlyForecast);

            updateHourlyForecastBox(hourlyForecast.getHourlyForecast());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the hourly forecast for the given location
     *
     * @param hourlyForecast the weather data to be displayed
     */
    private void displayHourlyForecast(HourlyForecast hourlyForecast) {
        ArrayList<WeatherData> hourlyData = hourlyForecast.getHourlyForecast();
        for (WeatherData data : hourlyData) {
            System.out.println("Hour: " + data.getHour());
            System.out.println("Temperature: " + data.getTemperature() + "°C");
            System.out.println("Icon Path: " + data.getIconPath());
            System.out.println("Wind Speed: " + data.getWindSpeed());
            System.out.println("Precipitation: " + data.getPrecipitation());
            System.out.println("Wind direction: " + data.getWindDirection());
            System.out.println("-----------------------------------");
        }
    }

    /**
     * Starts the application
     *
     * @param args (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
