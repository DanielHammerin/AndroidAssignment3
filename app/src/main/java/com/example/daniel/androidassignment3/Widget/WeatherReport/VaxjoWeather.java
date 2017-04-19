package com.example.daniel.androidassignment3.Widget.WeatherReport;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.daniel.androidassignment3.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VaxjoWeather extends AppCompatActivity {
    public static String TAG = "dv606.weather";

    private InputStream input;
    private WeatherReport report = null;
    private List<WeatherForecast> forecastList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_vaxjo_weather);
        if (!hasInternet()) {
            Toast.makeText(VaxjoWeather.this, "This Application Requires Internet To Display Weather.", Toast.LENGTH_LONG).show();
        }
        else {
            try {
                URL url = new URL("http://www.yr.no/sted/Sverige/Kronoberg/V%E4xj%F6/forecast.xml");
                AsyncTask task = new WeatherRetriever().execute(url);
            }
            catch (IOException ioe ) {
                ioe.printStackTrace();
            }
        }

        listView = (ListView) findViewById(R.id.weatherList);
        //ListAdapter adapter = new CustomAdapter(this, R.layout.row, forecastList);
        //listView.setAdapter(adapter);

    }

    private boolean hasInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = cm.getActiveNetworkInfo();
        return activeInfo != null && activeInfo.isConnected();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.weather_menu, menu);
        return true;
    }

    private void printReportToLog() {
        if (this.report != null) {
            /*Print some meta data to the UI for the testing purposes*/
            //TextView placeholder = (TextView) findViewById(R.id.placeholder);
            //placeholder.append(" " + report.getLastUpdated());

        	/* Print location meta data */
            Log.i(TAG, report.toString());
        	/* Print forecasts */
            int count = 0;
            for (WeatherForecast forecast : report) {
                count++;
                Log.i(TAG, "Forecast #" + count);
                Log.i(TAG, forecast.toString());
                //System.out.println("Forecast "+count);
                //System.out.println( forecast.toString() );
                forecastList.add(forecast);
            }
            ListAdapter adapter = new CustomAdapter(this, R.layout.row, forecastList);
            listView.setAdapter(adapter);
        }
        else {
            Log.e(TAG, "Weather report has not been loaded.");
        }

    }

    private class WeatherRetriever extends AsyncTask<URL, Void, WeatherReport> {
        protected WeatherReport doInBackground(URL... urls) {
            try {
                return WeatherHandler.getWeatherReport(urls[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected void onProgressUpdate(Void... progress) {

        }

        protected void onPostExecute(WeatherReport result) {
            Toast.makeText(getApplicationContext(), "WeatherRetriever task finished", Toast.LENGTH_LONG).show();

            report = result;
            printReportToLog();
        }
    }
}
