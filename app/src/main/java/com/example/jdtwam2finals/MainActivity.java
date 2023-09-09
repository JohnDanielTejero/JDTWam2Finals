package com.example.jdtwam2finals;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.jdtwam2finals.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

    }



}