package com.danc.firebaseapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if(isGranted){
                    initFCMToken();
                }else {
                    Toast.makeText(this, "권한 허용해주세요", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();
    }

    private void getPermission(){
        //TODO permission
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }


    private void initFCMToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d("FCM", token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();

                        // Send fcm token to server

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendTokenToServer(token);
                            }
                        }).start();
                    }
                });
    }

    private void sendTokenToServer(String token){
        HttpURLConnection conn =  null;

        try {
            URL url = new URL("http://172.30.1.78:8080/fcm/api/token");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json; charset=utf-8");
            conn.setRequestProperty("Accept", "application/json");

            conn.connect();

            OutputStream os = conn.getOutputStream();
            byte[] data = token.getBytes(StandardCharsets.UTF_8);
            os.write(data, 0, data.length);
            os.flush();
            os.close();

            if(conn.getResponseCode() == 200){
                Log.e("token to server", "token sent to server!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}