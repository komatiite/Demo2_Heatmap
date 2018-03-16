package com.example.kcroz.demo_2_heat_map;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // 2. Create a HeatmapTileProvider and TileOverlay
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    // 3. Create the colour gradient
    int[] colours = {
            Color.rgb(0, 148, 255), // blue
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 0, 0)    // red
    };

    // 4. Set the 'starting points' for each colour
    // from above as a percentage of total max intensity
    float[] startPoints = {
            0.25f, .5f, 1f
    };

    // 5. Create gradient object
    Gradient gradient = new Gradient(colours, startPoints);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.8794123, -97.253401), 13.0f));

        // 6. Set up the heat map
        addHeatMap();
    }

    private void addHeatMap() {
        List<LatLng> list;

        try {
            // 7. Read in JSON data
            list = readItems(R.raw.jogging_data);

            // 8. Customize heatmap by daisy-chaining the builder
            // radius: value between 10 and 50 (default 20)
            // opacity: value between 0 and 1 (default 0.7)
            // gradient: requires Gradient object (see above) (default green-yellow-red)
            mProvider = new HeatmapTileProvider.Builder().data(list)
                                                         .radius(10)
                                                         .opacity(0.85)
                                                         .gradient(gradient)
                                                         .build();

            // 9. Add the heatmap overlay to Google Maps
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
        catch (JSONException e) {
            Toast.makeText(this, "Problem reading jogging data", Toast.LENGTH_LONG).show();
        }
    }


    // Read in the JSON file data
    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("long");
            list.add(new LatLng(lat, lng));
        }
        return list;
    }

}
