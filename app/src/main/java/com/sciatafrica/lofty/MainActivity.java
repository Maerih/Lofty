package com.sciatafrica.lofty;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM = "com.sciatafrica.lofty.ITEM";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Request SMS permissions
        requestPermission();

        Button btn = (Button) findViewById(R.id.buttonSend);

        // Use existing button for both functions
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Test server connection first
                testServerConnection();

                // Then do the original activity navigation
                Intent i = new Intent(getApplicationContext(), DisplayListItems.class);
                EditText et = (EditText) findViewById(R.id.editTextListItem);
                TextView tv = (TextView) findViewById(R.id.textView);
                tv.setText(et.getText());
                String listItem = et.getText().toString();

                i.putExtra(EXTRA_ITEM, listItem);
                startActivity(i);
            }
        });

        // Auto-test server connection on app start
        testServerConnection();
    }

    private void testServerConnection() {
        String testMessage = "Test from MainActivity - " + System.currentTimeMillis();
        new TestNetTask().execute(testMessage);
    }

    private void requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)){
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 0);
            }
        }
    }

    // Test AsyncTask to verify server connection
    private class TestNetTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return httpPost(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "Server response: " + result);
            Toast.makeText(MainActivity.this, "Server: " + result, Toast.LENGTH_LONG).show();
        }
    }

    private String httpPost(String text) {
        String response = "";
        HttpURLConnection conn = null;

        try {
            // Use the correct IP for your setup:
            // For EMULATOR: "http://10.0.2.2:8000"
            // For PHYSICAL DEVICE: "http://YOUR_COMPUTER_IP:8000"
            String url = "http://10.0.2.2:8000"; // Change this to your computer's IP if using physical device
            URL urlObj = new URL(url);
            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);

            String paramsString = "smstext=" + text.replace(" ", "_");
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(paramsString);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "HTTP Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
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