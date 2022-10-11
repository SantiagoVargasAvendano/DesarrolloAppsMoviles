package com.example.reto9;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlFolder;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.location.GeoNamesPOIProvider;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView myOpenMapView;
    private IMapController myMapController;

    private Button mactualLocation;
    private Button mselectedLocation;
    private Slider mdistance;
    private Location lastKnowPosition = null;
    private boolean searchActual;
    private double latitude;
    private double longitude;
    private double distance = 1000;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
*/

        setContentView(R.layout.activity_main);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        myOpenMapView = findViewById(R.id.mapView);
        myOpenMapView.setBuiltInZoomControls(false);
        myMapController = myOpenMapView.getController();
        myMapController.setZoom(15);
        myOpenMapView.setMultiTouchControls(true);

        mactualLocation = (Button) findViewById(R.id.button1);
        mselectedLocation = (Button) findViewById(R.id.button2);
        mdistance = (Slider) (findViewById(R.id.slider));

        mactualLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchActual = true;
                GeoPoint actualPosition = new GeoPoint(lastKnowPosition.getLatitude(), lastKnowPosition.getLongitude());
                myMapController.animateTo(actualPosition);
                uploadMap();
                filter(distance);
            }
        });

        mselectedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchActual = false;
                GeoPoint point = (GeoPoint) myOpenMapView.getMapCenter();
                latitude = point.getLatitude();
                longitude = point.getLongitude();
                uploadMap();
                filter(distance);
            }
        });

        myOpenMapView.setOnTouchListener((v, event) -> {
            uploadMap();
            GeoPoint actualPosition = (GeoPoint) myOpenMapView.getMapCenter();
            Marker startMarker = new Marker(myOpenMapView);
            startMarker.setPosition(actualPosition);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            Drawable icon2 = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.icono));
            startMarker.setIcon(icon2);
            startMarker.setId("helper");
            myOpenMapView.getOverlays().add(startMarker);
            return false;
        });

        mdistance.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                distance = mdistance.getValue()*1000;
                filter(distance);
            }
        });

        searchActual = true;
        setActualLocation();
        uploadMap();
        filter(distance);
    }

    private void filter(double distance){
        clear(false);
        KmlDocument kmlDocument = new KmlDocument();
        kmlDocument.parseKMLStream(getResources().openRawResource(R.raw.sitiosbogota), null);

        int size = kmlDocument.mKmlRoot.mItems.size();
        int i = 0;
        while(i<size){
            GeoPoint point = ((KmlPlacemark)kmlDocument.mKmlRoot.mItems.get(i)).mGeometry.mCoordinates.get(0);
            GeoPoint actualPosition;
            if(searchActual){
                actualPosition = new GeoPoint(lastKnowPosition.getLatitude(), lastKnowPosition.getLongitude());
            }else{
                actualPosition = new GeoPoint(latitude, longitude);
            }
            double dist = point.distanceToAsDouble(actualPosition);
            if(dist>distance){
                kmlDocument.mKmlRoot.mItems.remove(i);
                size--;
            }else{
                i++;
            }
        }
        FolderOverlay kmlOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(myOpenMapView, null, null, kmlDocument);
        myOpenMapView.getOverlays().add(kmlOverlay);
        myOpenMapView.invalidate();

        /*KmlFolder placemark = (KmlFolder)kmlDocument.mKmlRoot.mItems.get(0);
        KmlPlacemark place = (KmlPlacemark)placemark.mItems.get(0);
        GeoPoint point = (GeoPoint)place.mGeometry.mCoordinates.get(0);

        KmlDocument folder = new KmlDocument();
        ((KmlFolder) kmlDocument.mKmlRoot.mItems.get(0)).mItems.size();*/
    }

    private void setActualLocation(){
        final MyLocationNewOverlay locationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), myOpenMapView);
        locationNewOverlay.enableMyLocation();
        locationNewOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                final GeoPoint myLocation = locationNewOverlay.getMyLocation();
                if (myLocation != null) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myMapController.animateTo(myLocation);
                        }
                    });
                };
            }
        });
    }

    private void uploadMap() {
        // Detectar cambios de ubicación mediante un listener (OSMUpdateLocation)
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        OSMUpdateLocation detectPosition = new OSMUpdateLocation(this);
        if (locationPermission()) {
            for (String provider : locationManager.getProviders(true)) {
                if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    lastKnowPosition = locationManager.getLastKnownLocation(provider);
                if (lastKnowPosition != null) {
                    updateActualPosition(lastKnowPosition);
                }
                locationManager.requestLocationUpdates(provider, 0, 0, detectPosition);
                break;
            }
        }
    }

    public void clear(boolean t){
        List<Overlay> list = myOpenMapView.getOverlays();
        for(Overlay ov : list){
            if(t && ov.getClass().equals(Marker.class)){
                list.remove(ov);
            }else if(!t && ov.getClass().equals(FolderOverlay.class)){
                list.remove(ov);
            }
        }
    }

    public void updateActualPosition(@NotNull Location location) {
        clear(true);
        GeoPoint actualPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
        Marker startMarker = new Marker(myOpenMapView);
        startMarker.setPosition(actualPosition);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Drawable icon = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.actual));
        startMarker.setIcon(icon);
        startMarker.setId("actual");
        startMarker.setTitle("Ubicación actual");
        myOpenMapView.getOverlays().add(startMarker);

        // Show search position
        Marker marker = new Marker(myOpenMapView);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Drawable icon2 = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.icono));
        marker.setIcon(icon2);
        marker.setTitle("Ubicación de busqueda");
        marker.setId("search");
        if(searchActual){
            marker.setPosition(actualPosition);
        }else{
            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
            marker.setPosition(geoPoint);
        }
        myOpenMapView.getOverlays().add(marker);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setClass(this, this.getClass());
            startActivity(intent);
            finish();
        }
    }

    public boolean locationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                return false;
            }
        } else {
            return true;
        }
    }
}