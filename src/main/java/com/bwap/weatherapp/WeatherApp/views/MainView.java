package com.bwap.weatherapp.WeatherApp.views;

import com.bwap.weatherapp.WeatherApp.controller.WeatherService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;

@SpringUI(path = "")
public class MainView extends UI {

    @Autowired
    private WeatherService weatherService;

    private VerticalLayout vLayout;
    private NativeSelect<String> unitSelect;
    private TextField cityField;
    private Button searchButton;
    private HorizontalLayout dashboard;
    private Label cityName ;
    private Label currentTemp;
    private HorizontalLayout dashboarddesc;
    private Label weatherDescription;
    private Label maxWeather;
    private Label minWeather;
    private Label humidity;
    private Label pressure;
    private Label windSpeed;
    private Label feelsLike;
    private Image iconImg;



    @Override
    protected void init(VaadinRequest vaadinRequest) {

        mainLayout();
        setHeader();
        setLogo();
        setForm();
        dashboardTitle();
        dashboardDescription();

        searchButton.addClickListener(clickEvent -> {
            if(!cityField.getValue().equals("")){
                try{
                    updateUI();
                }catch(JSONException e){
                    e.getMessage();
                }
            }else{
                Notification.show("Please Enter the City Name");
            }
        });

    }

    private void mainLayout(){
        iconImg = new Image();
        vLayout = new VerticalLayout();
        vLayout.setWidth("100%");
        vLayout.setSpacing(true);
        vLayout.setMargin(true);
        vLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        setContent(vLayout);

    }

    private void setHeader(){

        HorizontalLayout header = new HorizontalLayout();

        header.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        Label title = new Label("Weather App");

        header.addComponent(title);

        vLayout.addComponent(header);

    }

    private void setLogo(){
        HorizontalLayout logo = new HorizontalLayout();

        logo.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Image image = new Image(null, new ClassResource("/logo.png"));

        logo.setWidth("200px");
        logo.setHeight("200px");

        logo.addComponent(image);

        vLayout.addComponent(logo);

    }

    private void setForm(){
        HorizontalLayout form = new HorizontalLayout();

        form.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        form.setStyleName(String.valueOf(true));
        form.setSpacing(true);
        form.setMargin(true);

        //Selection Component
        unitSelect = new NativeSelect<>();
        ArrayList<String>  items = new ArrayList<String>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(1));

        form.addComponent(unitSelect);

        //Adding Text Field
        cityField = new TextField();
        cityField.setWidth("80%");
        form.addComponent(cityField);


        //Adding Button
        searchButton = new Button();
        searchButton.setIcon(VaadinIcons.SEARCH);
        form.addComponent(searchButton);

        vLayout.addComponent(form);
    }

    private void dashboardTitle(){

         dashboard = new HorizontalLayout();
         dashboard.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

         //City Name
        cityName = new Label("Currently in Patna");
        cityName.addStyleName(ValoTheme.LABEL_H2);
        cityName.addStyleName(ValoTheme.LABEL_LIGHT);

        //Current Temperature
        currentTemp = new Label("14 C");
        currentTemp.setStyleName(ValoTheme.LABEL_BOLD);
         currentTemp.setStyleName(ValoTheme.LABEL_H1);

         dashboard.addComponents(cityName, iconImg, currentTemp);
    //    vLayout.addComponent(dashboard);

    }

    private void dashboardDescription(){
        dashboarddesc = new HorizontalLayout();

        dashboarddesc.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //Description Layout
        VerticalLayout descriptionLayout = new VerticalLayout();
        descriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //weather description
        weatherDescription = new Label("Description: Clear Sky");
        weatherDescription.setStyleName(ValoTheme.LABEL_SUCCESS);

        descriptionLayout.addComponent(weatherDescription);

        //Min Weather
        minWeather = new Label("Min Temp: 9 C");
        descriptionLayout.addComponent(minWeather);

        maxWeather = new Label("Max Temp: 20 C");
        descriptionLayout.addComponent(maxWeather);

        VerticalLayout pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        pressure = new Label("Pressure: 231 pa");
        pressureLayout.addComponent(pressure);

        humidity = new Label("Himidity: 23");
        pressureLayout.addComponent(humidity);

        windSpeed = new Label("Wind Speed : 23");
        pressureLayout.addComponent(windSpeed);

        feelsLike = new Label();
        pressureLayout.addComponent(feelsLike);

        dashboarddesc.addComponents(descriptionLayout, pressureLayout);

     //   vLayout.addComponent(dashboarddesc);



    }

    private void updateUI(){
        String city = cityField.getValue();
        String defaultUnit;
        weatherService.setCityName(city);

        if(unitSelect.getValue().equals("F")){
            weatherService.setUnit("imperials");
            unitSelect.setValue("F");
            defaultUnit = "\u00b0" + "F";
        }else{
            weatherService.setUnit("metric");
            unitSelect.setValue("C");
            defaultUnit = "\u00b0" + "C";
        }

        cityName.setValue("Currently in  " + city);
        JSONObject mainObject = weatherService.returnMain();

        int temp = mainObject.getInt("temp");
        currentTemp.setValue(temp + defaultUnit);

        //getting Icon from API
        String iconCode = null;
         String weatherDescriptionNew = null;

        JSONArray jsonArray = weatherService.returnWeatherArray();

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject weatherObject = jsonArray.getJSONObject(i);
            iconCode = weatherObject.getString("icon");
            weatherDescriptionNew = weatherObject.getString("description");
        }

        iconImg.setSource(new ExternalResource("http://openweathermap.org/img/wn" +iconCode + "@2x.png"));

        weatherDescription.setValue("Description: "+ weatherDescriptionNew);
            minWeather.setValue("Min Temp: " + weatherService.returnMain().getInt("temp_min") + unitSelect.getValue());
            maxWeather.setValue("Max Temp: " + weatherService.returnMain().getInt("temp_max") + unitSelect.getValue());
            pressure.setValue("Pressure: " + weatherService.returnMain().getInt("pressure"));
            humidity.setValue("Humidity: " + weatherService.returnMain().getInt("humidity"));
            windSpeed.setValue("Wind Speed: "+ weatherService.returnWind().getInt("speed"));
            feelsLike.setValue("Feels Like: " + weatherService.returnMain().getDouble("feels_like"));


        vLayout.addComponents(dashboard, dashboarddesc);

//        vLayout.addComponent(dashboarddesc);


    }

}
