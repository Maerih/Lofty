package com.sciatafrica.lofty;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

public class DeviceInfo {
    public static String getDeviceInfo(Context context) {
        StringBuilder info = new StringBuilder();
        info.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
        info.append("Android: ").append(Build.VERSION.RELEASE).append("\n");
        info.append("SDK: ").append(Build.VERSION.SDK_INT).append("\n");

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            info.append("Network: ").append(activeNetwork.getTypeName()).append("\n");
            info.append("Connected: ").append(activeNetwork.isConnected()).append("\n");
        }
        return info.toString();
    }
}