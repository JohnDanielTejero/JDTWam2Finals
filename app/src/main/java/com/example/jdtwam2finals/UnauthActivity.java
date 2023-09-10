package com.example.jdtwam2finals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.jdtwam2finals.databinding.ActivityUnauthBinding;
import com.example.jdtwam2finals.fragments.LoginFragment;
import com.example.jdtwam2finals.fragments.RegisterFragment;

public class UnauthActivity extends AppCompatActivity {

    private ActivityUnauthBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityUnauthBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        replaceFragment(new LoginFragment());

        b.selectForm.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.go_to_login){
                replaceFragment(new LoginFragment());
                return true;
            } else if (item.getItemId() == R.id.go_to_register) {
                replaceFragment(new RegisterFragment());
                return true;
            }
            return false;
        });


    }

    private void replaceFragment (Fragment f){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(b.formFragment.getId(), f);
        ft.commit();
    }
}