package com.example.weatherproject.Model;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActualWeather implements Response.Listener {

    private Condition condition;

    private double actualTemperature;

    private double windSpeed;

    private int humidity;

    private double pressure;

    public ActualWeather() {
    }

    public double getActualTemperature() {
        return this.actualTemperature;
    }

    public double getWindSpeed() {
        return this.windSpeed;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public boolean isRainAlert() {
        return this.condition == Condition.RAINY;
    }

    public boolean isSnowAlert() {
        return this.condition == Condition.SNOWY;
    }

    public Condition getCondition() {
        return condition;
    }

    @Override
    public void onResponse(Object response) {
        try {
            JSONObject jsonResponse = new JSONObject((String)response);
            JSONArray jsonArray = jsonResponse.getJSONArray("weather");
            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
            JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");

            this.actualTemperature = jsonObjectMain.getDouble("temp") - 273.15;
            this.actualTemperature = Math.round(this.actualTemperature * 100)/100.0;

            this.pressure = jsonObjectMain.getInt("pressure");

            JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
            this.windSpeed = jsonObjectWind.getInt("speed");

            this.humidity = jsonObjectMain.getInt("humidity");

            String description = jsonObjectWeather.getString("description");

            if(description.trim().contains("clear") || description.trim().contains("sun")){
                this.condition = Condition.SUNNY;
            }
            if(description.trim().contains("clouds") || description.trim().contains("mist")){
                this.condition = Condition.CLOUDY;
            }
            if(description.trim().contains("rain")){
                this.condition = Condition.RAINY;
            }
            if(description.trim().contains("snow")){
                this.condition = Condition.SNOWY;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
