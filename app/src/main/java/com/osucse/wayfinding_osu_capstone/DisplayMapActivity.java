package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;






import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import com.osucse.wayfinding_api.*;
//import com.osucse.utilities.Coordinate;


public class DisplayMapActivity extends FragmentActivity {

    private static final String URL = "http://54.200.238.22:9000/";

//    private static final LatLng DEST = new LatLng(39.9986444, -083.0150867);
//    private static final LatLng NEXT = new LatLng(39.9985652, -083.0151295);
//    private static final LatLng NEXT1 = new LatLng(39.9984717, -083.0151624);
//    private static final LatLng NEXT2 = new LatLng(39.9983703, -083.0151790);
//    private static final LatLng NEXT3 = new LatLng(39.9983301, -083.0151664);


    /*****
     * TO BE CHOPPED
     */
    private static String startLocation = "";
    private static String endLocation = "";






    private GoogleMap ourMap;
    private














    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);
        Intent intent = getIntent();
        startLocation = intent.getStringExtra(SelectDestinationLocation.SOURCE_LOCATION);
        endLocation = intent.getStringExtra(SelectDestinationLocation.DESTINATION_LOCATION);

        //TextView textView = new TextView(this);
        TextView startLocationDisplay = (TextView) findViewById(R.id.start_location_display);
        startLocationDisplay.setTextSize(20);
        startLocationDisplay.setText("This is all me -- starting id: " + startLocation + " ending id:" + endLocation);
        //setContentView(textView);
        new HttpRequestTask().execute();



        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        // Sets up a GoogleMap and calls onMapReady()
        map.getMapAsync(this);


//        setUpMapIfNeeded();
    }

//    private void setUpMapIfNeeded() {
//        // check if we have got the googleMap already
//        if (ourMap == null) {
//            ourMap = ((SupportMapFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.map)).getMap();
//            if (ourMap != null) {
//                ourMap.setMyLocationEnabled(true);
//            }
//        }
//    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Route> {
        @Override
        protected Route doInBackground(Void... params) {
            try {

                final String url = URL + "generateRoute?from=" + startLocation + "&to=" + endLocation;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Route collection = restTemplate.getForObject(url, Route.class);
                return collection;
            } catch (Exception e) {
                Log.e("Route", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Route collection) {
            final List<Node> routePoints = collection.getRoute();
            plotRoute(routePoints);
        }

    }


    /**
     * Plots the route on the map
     * @param route
     */
    private void plotRoute(List<Node> route) {
        int pathSize = route.size();
        int current =0;
        LatLng start = null;
        LatLng end = null;
        for (int i =1; i < pathSize; i++){
            if (start == null) {
                start = new LatLng(route.get(current).getCoordinate().getLatitude(), route.get(current).getCoordinate().getLongitude());
            }else{
                start = end;
            }
            current++;
            end = new LatLng(route.get(current).getCoordinate().getLatitude(), route.get(current).getCoordinate().getLongitude());
            googleMap
                    .addPolyline((new PolylineOptions())
                            .add(start, end).width(5).color(Color.BLUE)
                            .geodesic(true));
            // move camera to zoom on map
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start,
                    17));
        }
    }
}
