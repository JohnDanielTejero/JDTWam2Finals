package com.example.jdtwam2finals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.jdtwam2finals.databinding.ActivityAuthBinding;
import com.example.jdtwam2finals.fragments.DashboardFragment;
import com.example.jdtwam2finals.fragments.ExpenseFragment;
import com.example.jdtwam2finals.fragments.IncomeFragment;
import com.example.jdtwam2finals.fragments.TransactionFragment;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding b;
    private int selectedFragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         b = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (savedInstanceState != null) {
            selectedFragmentId = savedInstanceState.getInt("selectedFragmentId");
            b.bottomNav.setSelectedItemId(selectedFragmentId);
        }

        initializeFragment(selectedFragmentId);

        b.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.dashboard_option){
                replaceFragment(new DashboardFragment());
            } else if (item.getItemId() == R.id.income_option) {
                replaceFragment(new IncomeFragment());
            } else if (item.getItemId() == R.id.expenses_option) {
                replaceFragment(new ExpenseFragment());
            } else if (item.getItemId() == R.id.transaction_option) {
                replaceFragment(new TransactionFragment());
            } else {
                return false;
            }
            return true;
        });
    }

    private void initializeFragment(int fragmentId) {
        Fragment fragment = null;
        if (fragmentId == R.id.dashboard_option) {
            fragment = new DashboardFragment();
        } else if (fragmentId == R.id.income_option) {
            fragment = new IncomeFragment();
        } else if (fragmentId == R.id.expenses_option) {
            fragment = new ExpenseFragment();
        } else if (fragmentId == R.id.transaction_option) {
            fragment = new TransactionFragment();
        }

        if (fragment != null) {
            replaceFragment(fragment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedFragmentId", selectedFragmentId);
    }
    private void replaceFragment (Fragment f){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(b.fragmentContainerView.getId(), f);
        ft.commit();
    }

}