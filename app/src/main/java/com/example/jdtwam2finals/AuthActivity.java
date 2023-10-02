package com.example.jdtwam2finals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.dao.UserTable;
import com.example.jdtwam2finals.databinding.ActivityAuthBinding;
import com.example.jdtwam2finals.databinding.SettingsDialogBinding;
import com.example.jdtwam2finals.dto.User;
import com.example.jdtwam2finals.fragments.DashboardFragment;
import com.example.jdtwam2finals.fragments.ExpenseFragment;
import com.example.jdtwam2finals.fragments.IncomeFragment;
import com.example.jdtwam2finals.fragments.TransactionFragment;
import com.example.jdtwam2finals.utils.QueryBuilder;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding b;
    private int selectedFragmentId;
    private SharedPreferences sp;
    private DbCon dbCon;
    private SettingsDialogBinding dialogBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         b = ActivityAuthBinding.inflate(getLayoutInflater());
         dialogBinding = SettingsDialogBinding.inflate(getLayoutInflater());
         dbCon = DbCon.getInstance(this);
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
        sp = getSharedPreferences("login", MODE_PRIVATE);

        b.settingsButton.setOnClickListener(v -> {
            showDialog();
        });
    }

    /**
     * Custom implementation for initializing fragments
     *
     * @param fragmentId - id of fragment
     */
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

    /**
     * Custom implementation for replacing fragments
     *
     * @param f - Fragment
     */
    private void replaceFragment (Fragment f){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(b.fragmentContainerView.getId(), f);
        ft.commit();
    }

    /**
     * Custom implementation for showing dialog
     *
     */
    private void showDialog() {
        if (dialogBinding.getRoot().getParent() != null) {
            ((ViewGroup) dialogBinding.getRoot().getParent()).removeView(dialogBinding.getRoot());
        }

        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogBinding.getRoot());

        int userId = sp.getInt("user", -1);
        String username = null;
        QueryBuilder<User> userQueryBuilder = new UserTable(dbCon.getReadableDatabase());
        Cursor cursor = userQueryBuilder
                .find()
                .one(userId)
                .exec();
        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            username = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_USERNAME));
            cursor.close();
            dialogBinding.dialogUsername.setText("Currently logged in as: " + username);
            dialogBinding.dialogLogoutButton.setOnClickListener(v -> {
                SharedPreferences.Editor Ed=sp.edit();
                Ed.remove("user").commit();
                dialog.dismiss();
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            });

            PackageInfo pInfo;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
            String version = pInfo.versionName;
            TextView versionNameTextView = dialogBinding.dialogVersionNumber;

            versionNameTextView.setText("Ver. " + version);

            dialogBinding.dismissDialog.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
    }
}