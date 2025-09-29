package com.sciatafrica.lofty;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import androidx.core.content.ContextCompat;

public class LocationTracker {
    private Context context;

    public LocationTracker(Context context) {
        this.context = context;
    }

    public String getLastKnownLocation() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return "Location: Permission required";
        }

        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Try GPS first
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // If GPS not available, try network
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                return String.format("Location: %.6f,%.6f (Accuracy: %.0fm)", lat, lon, location.getAccuracy());
            } else {
                return "Location: No recent location available";
            }
        } catch (Exception e) {
            return "Location: Error - " + e.getMessage();
        }
    }
}