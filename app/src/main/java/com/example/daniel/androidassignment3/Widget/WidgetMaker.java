package com.example.daniel.androidassignment3.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.example.daniel.androidassignment3.R;
import com.example.daniel.androidassignment3.Widget.WeatherReport.CustomAdapter;
import com.example.daniel.androidassignment3.Widget.WeatherReport.VaxjoWeather;
import com.example.daniel.androidassignment3.Widget.WeatherReport.WeatherForecast;
import com.example.daniel.androidassignment3.Widget.WeatherReport.WeatherHandler;
import com.example.daniel.androidassignment3.Widget.WeatherReport.WeatherReport;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Daniel on 2017-01-15.
 */

public class WidgetMaker implements RemoteViewsService.RemoteViewsFactory{
    private ArrayList<WeatherForecast> list;
    private int appWidgetId;
    private Context context;
    private URL url;
    private boolean hasInternet;

    public WidgetMaker(Context context, Intent intent) {
        super();
        list = new ArrayList<>();
        this.context = context;
        appWidgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
        String city = intent.getStringExtra("city");
        hasInternet = intent.getBooleanExtra("internet", true);
        if (city != null) {
            try {
                url = new URL(getUrlByCity(city));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getUrlByCity(String city) {
        if (city != null) {
            switch (city) {
                case "Växjö":
                    return Constants.URL_VAXJO_WEATHER;
                case "Malmö":
                    return Constants.URL_MALMO_WEATHER;
                case "Stockholm":
                    return Constants.URL_STHLM_WEATHER;
                case "Gävle":
                    return Constants.URL_GÄVLE_WEATHER;
                default:
                    return Constants.URL_VAXJO_WEATHER;
            }
        }
        return Constants.URL_VAXJO_WEATHER;
    }

    private void populateList() {
        if (hasInternet) {
            if (url != null) {
                AsyncTask task = new WeatherRetriever().execute(url);
            } else {
                System.out.println("null url..");
                try {
                    AsyncTask task = new WeatherRetriever().execute(new URL(Constants.URL_VAXJO_WEATHER));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(context, "No internet connection available..", Toast.LENGTH_SHORT).show();
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
            list.addAll(result.getForecasts());
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_listview);
        }

    }

    @Override
    public void onCreate() {
        populateList();
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
        list.clear();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_row);
        WeatherForecast forecast = list.get(position);

        remoteViews.setTextViewText(R.id.widget_listitem_time, forecast.getStartHHMM() + " - " + forecast.getEndHHMM());
        remoteViews.setImageViewResource(R.id.widget_listitem_icon, CustomAdapter.getIcon(forecast.getWeatherCode()));
        remoteViews.setTextViewText(R.id.widget_listitem_rain, "Rain: " + forecast.getRain() + "mm/h");
        remoteViews.setTextViewText(R.id.widget_listitem_temperature, "Temp: " + forecast.getTemperature() + "\u00B0");

        return remoteViews;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
