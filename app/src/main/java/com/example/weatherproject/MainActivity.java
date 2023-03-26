package com.example.weatherproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherproject.Model.City;
import com.example.weatherproject.Model.Preference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Preference newPref;

    private final Handler mainHandler = new Handler();

    private NotificationManagerCompat notificationManager;

    private int numberOfNotifications = 0;

    private volatile boolean stopThread = false;

    private Context context;

    private final boolean lock = true;

    private EditText cityName;
    private EditText maxTemperature;
    private EditText minTemperature;
    private EditText windSpeed;
    private EditText humidity;
    private EditText pressure;
    private TextView cityRequiredLabel;
    private CheckBox rainAlertCheckBox;
    private CheckBox snowAlertCheckBox;
    private ListView listView;
    private LinearLayout homeView;
    private Button addButton;

    private ArrayList<Preference> prefs;

    private boolean delete;

    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new ArrayList<>();
        setContentView(R.layout.home_view);
        context = getApplicationContext();
        notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
    }

    public void nextHomeViewOnClick(View view){
        System.out.println("next function");
        loadMainView();
        startThread();
    }

    public void addMainOnClick(View view){
        System.out.println("add main activity");
        setContentView(R.layout.add_preference_view);
        maxTemperature = findViewById(R.id.maxTemperature);
        minTemperature = findViewById(R.id.minTemperature);
        windSpeed = findViewById(R.id.windSpeed);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        rainAlertCheckBox = findViewById(R.id.rainAlertCheckBox);
        snowAlertCheckBox = findViewById(R.id.snowAlertCheckBox);
        context = getApplicationContext();
        notificationManager = NotificationManagerCompat.from(this);
    }

    public void exitOnClick(View view){
        System.out.println("Exit");
        loadMainView();
    }

    public void deleteOnClick(View view){
        homeView = findViewById(R.id.homeView);
        addButton = findViewById(R.id.addButton);
        delete = !delete;
        addButton.setEnabled(!addButton.isEnabled());
        if(delete){
           homeView.setBackgroundColor(Color.RED);
        }else{
            homeView.setBackgroundColor(Color.WHITE);
        }
    }

    public void addNewPreferenceOnClick(View view) {
        System.out.println("add new pref activity");
        context = getApplicationContext();
        cityName = findViewById(R.id.cityName);
        cityRequiredLabel = findViewById(R.id.cityRequiredLabel);

        String city = cityName.getText().toString().trim();
        if(city.isEmpty()){
            cityName.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
            cityRequiredLabel.setVisibility(View.VISIBLE);
            return;
        }
        System.out.println("city = " + city);

        newPref = new Preference(city);

        String maxText = maxTemperature.getText().toString().trim();
        String minText = minTemperature.getText().toString().trim();
        String windSpeedText = windSpeed.getText().toString().trim();
        String humidityText = humidity.getText().toString().trim();
        String pressureText = pressure.getText().toString().trim();

        if(maxText.isEmpty()){
            System.out.println("not added max temp");
        }else{
            double max = Double.valueOf(maxText);
            System.out.println("max = " + max);
            newPref.setMaxTempBoo(true);
            newPref.setMaxTemperature(max);
        }

        if(minText.isEmpty()){
            System.out.println("not added min temp");
        }else{
            double min = Double.valueOf(minText);
            System.out.println("min = " + min);
            newPref.setMinTempBoo(true);
            newPref.setMinTemperature(min);
        }

        if(windSpeedText.isEmpty()){
            System.out.println("not added wind speed");
        }else{
            double wind = Double.valueOf(windSpeedText);
            System.out.println("wind speed = " + wind);
            newPref.setWindSpeedBoo(true);
            newPref.setWindSpeed(wind);
        }

        if(humidityText.isEmpty()){
            System.out.println("not added humidity");
        }else{
            int hum = Integer.valueOf(humidityText);
            System.out.println("humidity = " + hum);
            newPref.setHumidityBoo(true);
            newPref.setHumidity(hum);
        }

        if(pressureText.isEmpty()){
            System.out.println("not added pressure");
        }else{
            double pressureLevel = Double.valueOf(pressureText);
            System.out.println("pressure level = " + pressureLevel);
            newPref.setPressureBoo(true);
            newPref.setPressure(pressureLevel);
        }

        newPref.setRainAlert(rainAlertCheckBox.isChecked());
        newPref.setSnowAlert(snowAlertCheckBox.isChecked());

        getWeatherDetails(city);

        if(newPref == null){
            return;
        }

        // update data a first time
        newPref.alert(context);

        if(newPref.existsSimilar(prefs)){
            System.out.println("can't do it, already exists");
            return;
        }

        prefs.add(newPref);

        loadMainView();
    }

    public void loadMainView(){
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        delete = false;
        ArrayAdapter<Preference> itemsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1 , prefs);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if(delete) {
                prefs.remove(position);
                itemsAdapter.notifyDataSetChanged();
            }
        });
        itemsAdapter.notifyDataSetChanged();
    }

    public void startThread() {
        new Thread(() -> {
            while (lock) {
                if (stopThread) return;
                checkAndAlert();
                try {
                    Thread.sleep(1000*20); // 20 secs
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendNotification(@NonNull String title,@NonNull String alertText){
        Notification notification = new NotificationCompat.Builder(this, "channel")
                .setSmallIcon(R.drawable.orage)
                .setContentTitle(title.toUpperCase())
                .setContentText(alertText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        numberOfNotifications ++;
        notificationManager.notify(numberOfNotifications, notification);
    }

    public void createNotificationChannel(){
        NotificationChannel channel1 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel1 = new NotificationChannel("channel",
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            System.out.println("notifications chanel creation");

        }

    }

    private void checkAndAlert() {
        if (prefs.isEmpty()) return;
        for (Preference p : prefs) {
            System.out.println("prefs : " + prefs);
            String alertText = p.alert(context);
            if(!alertText.equals("")){
                // notify
                sendNotification(p.getLocation().toString(),alertText);
                System.out.println("checked : " + p.getLocation().toString() + " " + alertText);
            }
        }
    }

    public void getWeatherDetails(@NonNull String city){
        String tempUrl = "";
        String appId = "e842953bf61f54428e7e92ff06df2e9c"; //my personal id
        if(!city.equals("")){
            tempUrl = url + "?q=" + city + "," + city + "&appid=" + appId;
        }else{
            tempUrl = url + "?q=" + city + "&appid=" + appId;
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, response -> {
            String output = "";
            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                String description = jsonObjectWeather.getString("description");
                JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                double temp = jsonObjectMain.getDouble("temp") - 273.15;
                double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                float pressure = jsonObjectMain.getInt("pressure");
                int humidity = jsonObjectMain.getInt("humidity");
                JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                String wind = jsonObjectWind.getString("speed");
                JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                String clouds = jsonObjectClouds.getString("all");
                JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                String countryName = jsonObjectSys.getString("country");
                String cityName = jsonResponse.getString("name");
                newPref.setLocation(new City((cityName)));
                output += "Current weather of " + cityName + " (" + countryName + ")"
                        + "\n Temp: " + df.format(temp) + " °C"
                        + "\n Feels Like: " + df.format(feelsLike) + " °C"
                        + "\n Humidity: " + humidity + "%"
                        + "\n Description: " + description
                        + "\n Wind Speed: " + wind + "m/s (meters per second)"
                        + "\n Cloudiness: " + clouds + "%"
                        + "\n Pressure: " + pressure + " hPa";
                System.out.println(output);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new Response.ErrorListener(){

            /**
             * If there is an error, like a bad city name for example.
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("No such a city !");
                newPref = null;
                cityRequiredLabel.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}