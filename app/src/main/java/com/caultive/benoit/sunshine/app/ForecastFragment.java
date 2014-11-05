package com.caultive.benoit.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by benoit on 2014-10-27.
 */
public  class ForecastFragment extends Fragment {
    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }



    @Override
    public void onStart(){
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent weatherIntent = new Intent(getActivity(),DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT,listView.getItemAtPosition(i).toString());

                startActivity(weatherIntent);
            }
        });
        // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
        // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.


        return rootView;
    }

    private void updateWeather(){

        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute();
    }

    public class FetchWeatherTask extends AsyncTask<Void,Void,String[]>{

        private final String LOG_TAG =FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Void... voids){

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr ;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String ID_PARAM = "q";
                final String MODE_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String COUNT_PARAM = "ctn";

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

                String city = sharedPref.getString(getString(R.string.city_key),getString(R.string.city_default_value));
                String unit = sharedPref.getString(getString(R.string.unit_key),getString(R.string.unit_default_value));

                Uri  finalUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(ID_PARAM,city)
                        .appendQueryParameter(MODE_PARAM,"json")
                        .appendQueryParameter(UNITS_PARAM,"metric")
                        .appendQueryParameter(COUNT_PARAM, "7")
                        .build();


                //Old Url Construction
                URL url = new URL(finalUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();



                try{return getWeatherDataFromJson(forecastJsonStr, 7);
                } catch(JSONException e){
                    Log.e(LOG_TAG,e.getMessage(),e);
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, e.toString());
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.

                return null;
            }finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            mForecastAdapter.clear();
            for(String s : strings)
            {
                mForecastAdapter.add(s);
            }

        }

    }
    /**
     * Converting unix timestamp to milliseconds and then converting to formatted date
     * */
    private String getReadableDateString(long time){
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date);

    }

    /**
     *
     * @param high temp for the day
     * @param low temp for the day
     * @return string containing both values formatted for print
     */
    private String formatingHighLows(double high, double low){


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unit = sharedPref.getString(getString(R.string.unit_key),getString(R.string.unit_default_value));
        if(unit=="imperial"){
            high = (high* 1.8) + 32;
            low = (low * 1.8) + 32;
        }
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);
        return roundedHigh + "/" + roundedLow;
    }

    /**
     * Get weather data as Json and format it as String Array
     * @param forecastJsonStr - contain unparsed data
     * @param numDays - number of days to extract
     * @return String[]
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException{


        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        String[] resultsStrs = new String[numDays];
        for(int i =0; i < weatherArray.length();i++){
            String day;
            String description;
            String highAndLow;

            //Get JSONObject for the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            //Start Converting timestamp to readable string
            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

            //Get description in the weather array
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            //Get the high/low temperature for the day
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);
            highAndLow = formatingHighLows(high,low);
            resultsStrs[i] = day + " - " + description + " - " + highAndLow;

        }

        return  resultsStrs;
    }






}