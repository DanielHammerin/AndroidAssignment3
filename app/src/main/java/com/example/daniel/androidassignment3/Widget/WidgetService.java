package com.example.daniel.androidassignment3.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Daniel on 2017-01-15.
 */

public class WidgetService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetMaker(this.getApplicationContext(), intent));
    }
}
