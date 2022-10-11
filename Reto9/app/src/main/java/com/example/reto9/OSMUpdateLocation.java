package com.example.reto9;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by equipo on 07/08/2017.
 */

public class OSMUpdateLocation implements LocationListener {
    private MainActivity actividad;

    public OSMUpdateLocation(MainActivity actividad) {
        this.actividad = actividad;
    }

    @Override
    public void onLocationChanged(Location location) {
        actividad.updateActualPosition(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
