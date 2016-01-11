package com.luowei.gaodemappolylinedemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AMap.OnMapLoadedListener {

    private MapView mapView;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initMap();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    private void initMap() {
        aMap = mapView.getMap();
        aMap.setOnMapLoadedListener(this);
        aMap.getUiSettings().setZoomControlsEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapLoaded() {
        drawGradientRoute();
    }

    private List<LatLng> getData(){
        InputStream is = getResources().openRawResource(R.raw.locations);
        InputStreamReader isr = new InputStreamReader(is);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<LatLng>>(){}.getType();
        List<LatLng> oss = gson.fromJson(isr, listType);
        return oss;
    }

    /**
     * 渐变线条
     */
    private void drawGradientRoute() {


        List<LatLng> path = getData();
        LatLng[] latLngs =  path.toArray(new LatLng[path.size()]);

        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }
//        PolylineOptions polyline = new PolylineOptions().add(latLngs);
//        polyline.color(Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
//        polyline.color(getResources().getColor(R.color.primary));
//        polyline.width(4f);
//        aMap.addPolyline(polyline);


        ColorAnimate colorAnimate = new ColorAnimate(getResources().getColor(R.color.green), getResources().getColor(R.color.red));
        for (int i = 0; i < latLngs.length - 2; i++) {
            PolylineOptions polyline = new PolylineOptions();
            polyline.add(latLngs[i],latLngs[i + 1], latLngs[i + 2]);
//            for (int j = i; j < i + 2 && j < latLngs.length; j++) {
//                polyline.add(latLngs[j]);
//            }
//            polyline.color(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            polyline.color(colorAnimate.getColorCurrent( i*1f/(latLngs.length - 1)));

//            polyline.setCustomTexture()
//            polyline.width(4f);
            aMap.addPolyline(polyline);
        }


//        PolylineOptions polylineOptions;
//        polylineOptions = new PolylineOptions();
//        polylineOptions.add(latLngs);


        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_start);
        MarkerOptions startMarker = new MarkerOptions().position(latLngs[0]).icon(descriptor).anchor(.5f, .5f);
        int i = latLngs.length - 1;
        BitmapDescriptor endIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_end);
        MarkerOptions endMarker = new MarkerOptions().position(latLngs[(i < 0 ? 0 : i)]).icon(endIcon).anchor(.5f, .5f);
        aMap.addMarker(startMarker);
        aMap.addMarker(endMarker);

//        polyline = aMap.addPolyline(polylineOptions);
//        polyline.setColor(Color.RED);
//        polyline.setWidth(20);


        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 0);
        aMap.animateCamera(cameraUpdate);
    }

}
