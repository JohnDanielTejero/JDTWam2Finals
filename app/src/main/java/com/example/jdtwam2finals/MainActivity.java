package com.example.jdtwam2finals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.jdtwam2finals.databinding.ActivityMainBinding;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;
    private SharedPreferences sp;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Boolean isLogged;
        ExecutorService e = Executors.newCachedThreadPool();
        Future<Boolean> futureTask = e.submit(() -> {
            sp = getSharedPreferences("login", MODE_PRIVATE);
            if (sp.getInt("user", -1) != -1){
                return true;
            }
            return false;
        });

        try {
            isLogged = futureTask.get();
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        e.shutdown();

        if (isLogged) {
            Log.d("custom", "User is logged in");
            i = new Intent(this, AuthActivity.class);
            startActivity(i);
        }else {
            Log.d("custom", "User is not logged in");
            i = new Intent(this, UnauthActivity.class);
            startActivity(i);
        }

    }



}