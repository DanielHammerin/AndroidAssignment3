package com.example.daniel.androidassignment3.CallHistory;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.test.espresso.core.deps.guava.base.Charsets;
import android.support.test.espresso.core.deps.guava.io.Files;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.daniel.androidassignment3.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CallHistoryActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    ArrayList<String> list = new ArrayList<>();
    ListView listView;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);


        file = this.getExternalFilesDir(null);
        listView = (ListView) findViewById(R.id.callHistoryList);
        adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openContextMenu(view);
            }
        });
        registerForContextMenu(listView);
        listView.setAdapter(adapter);
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int n = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        String numberFromList = list.get(n);
        int id = item.getItemId();

        if (id == 0) {
            callNumber(numberFromList);
            return true;
        } else if (id == 1) {
            dialNumber(numberFromList);
            return true;
        } else if (id == 2) {
            messageNumber(numberFromList);
            return true;
        } else {
            return false;
        }
    }

    private void callNumber(String num) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    private void dialNumber(String num) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
        startActivity(intent);
    }

    private void messageNumber(String num) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("sms:" + num));
        intent.putExtra("sms_body", num);
        intent.setType("text/plan");
        startActivity(Intent.createChooser(intent, "Send Message"));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, 0, "Call");
        menu.add(0, 1, 0, "Message");
        menu.add(0, 2, 0, "Dial");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.callhistory_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.updateCalls) {
            update();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void update() {
        File f = new File(file, "callHistory");
        String obj;
        if (f.isFile()) {
            try {
                obj = Files.toString(f, Charsets.UTF_8);
                list.clear();
                String[] objectString = obj.split(",");
                list.addAll(Arrays.asList(objectString));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, "List Updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }
}
