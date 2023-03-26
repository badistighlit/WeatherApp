package com.example.weatherproject.Model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Preference {
    @NonNull
    private Location location;
    private boolean maxTempBoo;
    private double maxTemperature;
    private boolean minTempBoo;
    private double minTemperature;
    private boolean windSpeedBoo;
    private double windSpeed;
    private boolean humidityBoo;
    private int humidity;
    private boolean pressureBoo;
    private double pressure;
    private boolean rainAlert;
    private boolean snowAlert;
    private ActualWeather aw;

    public Preference(String city){
        this.location = new City(city);
        this.maxTempBoo = false;
        this.minTempBoo = false;
        this.windSpeedBoo = false;
        this.humidityBoo = false;
        this.pressureBoo = false;
        this.rainAlert = false;
        this.snowAlert = false;
        this.aw = new ActualWeather();
    }

    public void setMaxTempBoo(boolean maxTempBoo) {
        this.maxTempBoo = maxTempBoo;
    }

    public void setMinTempBoo(boolean minTempBoo) {
        this.minTempBoo = minTempBoo;
    }

    public void setPressureBoo(boolean pressureBoo) {
        this.pressureBoo = pressureBoo;
    }

    public void setHumidityBoo(boolean humidityBoo) {
        this.humidityBoo = humidityBoo;
    }

    public void setWindSpeedBoo(boolean windSpeedBoo) {
        this.windSpeedBoo = windSpeedBoo;
    }

    public Location getLocation(){
        return this.location;
    }

    public double getMaxTemperature(){
        return this.maxTemperature;
    }

    public double getMinTemperature(){
        return this.minTemperature;
    }

    public void setMaxTemperature(double temp){
        this.maxTemperature = temp;
    }

    public void setMinTemperature(double temp){
        this.minTemperature = temp;
    }

    public void setLocation(@NonNull Location location) {
        this.location = location;
    }

    public void setWindSpeed(double windSpeed){
        this.windSpeed = windSpeed;
    }

    public void setPressure(double windSpeed){
        this.pressure = pressure;
    }

    public void setRainAlert(boolean tsunamiAlert){
        this.rainAlert = tsunamiAlert;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setSnowAlert(boolean snowAlert) {
        this.snowAlert = snowAlert;
    }

    @NonNull
    public String toString(){
        String res = this.location + "\n";

        if(aw.getCondition() != null){
            res += aw.getActualTemperature() + " °c\n";
            res += aw.getCondition() + "\n";
        }

        if(noAlertSet()){
            return res;
        }

        res += "Alert :\n";
        if(maxTempBoo){
            res += "Maximum temperature " + maxTemperature + "\n";
        }
        if(minTempBoo){
            res += "Minimum temperature " + minTemperature + "\n";
        }
        if(windSpeedBoo){
            res += "Wind " + windSpeed + "\n";
        }
        if(humidityBoo){
            res += "Humidity " + humidity + "\n";
        }
        if(pressureBoo){
            res += "Pressure " + pressure + "\n";
        }
        if(rainAlert){
            res += "Rain alert\n";
        }
        if(snowAlert){
            res += "Snow alert\n";
        }
        return res;
    }

    public String alert(Context context){
        String res = "";
        final String url = "https://api.openweathermap.org/data/2.5/weather";
        String tempUrl = "";
        String appid = "e842953bf61f54428e7e92ff06df2e9c"; //my personal id

        tempUrl = url + "?q=" + location + "&appid=" + appid;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, aw,new Response.ErrorListener(){

            /**
             * If there is an error, like a bad city name for example.
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("No such a city wow !");
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        if((this.maxTempBoo) && (aw.getActualTemperature() > maxTemperature)){
            res += "Heat waves : " + aw.getActualTemperature();
        }

        if ((this.minTempBoo) && (aw.getActualTemperature() < minTemperature)){
            res += "\nFreezing : " + aw.getActualTemperature();
        }

        if((this.windSpeedBoo) && (aw.getWindSpeed() > windSpeed)){
            res += "\nAlerting wind : " + aw.getWindSpeed();
        }

        if((this.humidityBoo) && (aw.getHumidity() > humidity)){
            res += "\nHigh humidity : " + aw.getHumidity();
        }

        if((this.pressureBoo) && (aw.getPressure() > pressure)){
            System.out.println("rl.get = " + aw.getPressure());
            System.out.println("pressure = " + pressure + "hPa");
            res += "\nHigh pressure : " + aw.getPressure() + "hPa";
        }

        if(this.rainAlert && aw.isRainAlert()){
            res += "Rain alert !";
        }
        if(this.snowAlert && aw.isSnowAlert()){
            res += "Snow alert !";
        }

        return res.trim();
    }

    public boolean existsSimilar(ArrayList<Preference> prefs){
        for (Preference p : prefs){
            if(this.location.toString().toLowerCase().equals(p.getLocation().toString().toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public boolean noAlertSet(){
        return !maxTempBoo && !minTempBoo && !humidityBoo && !pressureBoo && !windSpeedBoo && !snowAlert && !rainAlert;
    }
}
