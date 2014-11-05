package com.caultive.benoit.sunshine.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by benoit on 2014-10-29.
 */

public class WeatherDataParser {

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {


        JSONObject json_object = new JSONObject(weatherJsonStr);
        if(json_object.has("list"))
        {
            JSONArray day_list = json_object.getJSONArray("list");
            if(dayIndex <= day_list.length())
            {
                JSONObject queried_day = new JSONObject(day_list.get(dayIndex).toString());
                return queried_day.getJSONObject("temp").getDouble("max");
            }
        }

        return -1;
    }

}
