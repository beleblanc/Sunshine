package com.caultive.benoit.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {
    private final static String LOG_CAT = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        Log.d(LOG_CAT,"onStart()");
        super.onStart();
        // The activity is about to become visible.
    }
    @Override
    protected void onResume() {
        Log.d(LOG_CAT,"onResume()");
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }
    @Override
    protected void onPause() {
        Log.d(LOG_CAT,"onPause()");
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        Log.d(LOG_CAT,"onStop()");
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        Log.d(LOG_CAT,"onDestroy()");
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity( new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }
        if(id== R.id.action_view_location)
        {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            String city = sharedPref.getString(getString(R.string.city_key),getString(R.string.city_default_value));

            showMap(city);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showMap(String city) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",city).build();

        intent.setData(uri);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }else{
            Log.d("MainActivity.showMap","Could not find intent resolver");
        }
    }

}

