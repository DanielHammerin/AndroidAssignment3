package com.example.daniel.androidassignment3.Widget;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Daniel on 2017-01-15.
 */

public class WidgetGetter extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context, WidgetUpdater.class);
        intent.setAction(Constants.UPDATE_WIDGET);
        intent.putExtra(appWidgetManager.EXTRA_APPWIDGET_IDS,appWidgetIds);
        context.startService(intent);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
