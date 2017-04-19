package com.example.daniel.androidassignment3.RoadMap;

import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.daniel.androidassignment3.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.commons.io.FileUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RoadMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private File file;
    Polyline currRoute;
    AsyncTask<String, Void, Void> asyncTask;
    private final ArrayList<Marker> routeMarkers = new ArrayList<>();
    private String vaxjoToStockholm = "http://cs.lnu.se/android/VaxjoToStockholm.kml";
    private String vaxjoToCopenhagen = "http://cs.lnu.se/android/VaxjoToCopenhagen.kml";
    private String vaxjoToOdessa = "http://cs.lnu.se/android/VaxjoToOdessa.kml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_map);
        asyncTask = new FileGetter();
        asyncTask.execute(vaxjoToCopenhagen, vaxjoToOdessa, vaxjoToStockholm);
        file = this.getExternalFilesDir(null);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void fileDownloader(String[] urls) throws IOException {
        for (String s : urls) {
            URL url = new URL(s);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();

            if (response == HttpsURLConnection.HTTP_OK) {
                String fileName = s.substring(s.lastIndexOf("/") + 1, s.length());
                FileUtils.copyURLToFile(url, new File(file, fileName));
            }
        }
    }

    private void showRoute(File file, String name) {
        ArrayList<String> coords = getCoordinates(file);
        ArrayList<LatLng> latitudeLongitude = coordConverter(coords.get(0));
        if (currRoute != null) {
            currRoute.remove();
        }
        if (!routeMarkers.isEmpty()) {
            for (Marker m : routeMarkers) {
                m.remove();
            }
        }
        currRoute = mMap.addPolyline(new PolylineOptions().addAll(latitudeLongitude));
        LatLng vaxjo = placeMarker(coords.get(1), "Växjö");
        LatLng destination = placeMarker(coords.get(2), name);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(vaxjo);
        builder.include(destination);
        for (LatLng ll : currRoute.getPoints()) {
            builder.include(ll);
        }
        LatLngBounds bounds = builder.build();
        //LatLngBounds cameraBounds = getCameraPosition(markerList);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 600, 600, 5);
        mMap.moveCamera(cu);
    }

    private LatLng placeMarker(String coords, String name) {
        String[] items = coords.split(",");
        LatLng coordinates = new LatLng(Double.valueOf(items[1]), Double.valueOf(items[0]));
        routeMarkers.add(mMap.addMarker(new MarkerOptions().title(name).position(coordinates)));
        return coordinates;
    }

    private ArrayList<LatLng> coordConverter(String coordinates) {
        ArrayList<LatLng> coords = new ArrayList<>();
        String[] items = coordinates.split("\\s+");
        for (String s : items) {
            String[] item = s.split(",");
            LatLng pos = new LatLng(Double.valueOf(item[1]), Double.valueOf(item[0]));
            coords.add(pos);
        }
        return coords;
    }

    private ArrayList<String> getCoordinates(File file) {
        ArrayList<String> list = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(file);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, null);

            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    if (parser.getName().equals("coordinates")) {
                        list.add(parser.nextText());
                    }
                }
                parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private class FileGetter extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                fileDownloader(params);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.road_map_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        File f;
        int id = item.getItemId();

        if (id == R.id.noRoute) {
            if (currRoute != null) {
                currRoute.remove();
            }
            if (!routeMarkers.isEmpty()) {
                for (Marker m : routeMarkers) {
                    m.remove();
                }
            }
            return true;
        }
        else if (id == R.id.copenhagenRoute) {
            f = new File(file, "VaxjoToCopenhagen.kml");
            showRoute(f, "Copenhagen");
            return true;
        }
        else if (id == R.id.odessaRoute) {
            f = new File(file, "VaxjoToOdessa.kml");
            showRoute(f, "Odessa");
            return true;
        }
        else if (id == R.id.stockholmRoute) {
            f = new File(file, "VaxjoToStockholm.kml");
            showRoute(f, "Stockholm");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
