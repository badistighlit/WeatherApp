package com.example.weatherproject.Model;

import androidx.annotation.NonNull;

public enum Condition {
    SUNNY, CLOUDY, RAINY, SNOWY;

    @NonNull
    public String toString(){
        switch (this){
            case RAINY:
                return "It's raining";
            case SNOWY:
                return "It's snowing";
            case SUNNY:
                return "Sun is shining";
            case CLOUDY:
                return "Cloudy sky";
        }
        return "";
    }
}

