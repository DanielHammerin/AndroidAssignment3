package com.example.daniel.androidassignment3.Widget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.daniel.androidassignment3.R;

public class WidgetMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sendBroadcast(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER));
        //setContentView(R.layout.activity_widget_main);

        Intent intent = new Intent(this,WidgetGetter.class);
        intent.setAction(Constants.UPDATE_WIDGET);
        sendBroadcast(intent);

        Toast.makeText(WidgetMain.this, "Widget added!", Toast.LENGTH_LONG).show();

    }
}
