package com.example.jdtwam2finals.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jdtwam2finals.MainActivity;
import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.utils.QueryBuilder;
import com.example.jdtwam2finals.dao.UserTable;
import com.example.jdtwam2finals.databinding.FragmentLoginBinding;
import com.example.jdtwam2finals.dto.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding b;
    private Context context;
    private DbCon dbCon;
    private MaterialButton submitButton;
    private TextInputEditText username, password;
    private SharedPreferences sp;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentLoginBinding.inflate(inflater, container, false);
        return (View) b.getRoot();
    }

    @SuppressLint("Range")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbCon = DbCon.getInstance(context);
        username = b.loginUsername;
        password = b.loginPassword;
        submitButton = b.submitButton;

        submitButton.setOnClickListener(v -> {
            boolean submittable = true;
            if (username.getText().toString().isEmpty()){
                username.setError("Username is required!");
                submittable = false;
            }
            if (password.getText().toString().isEmpty()){
                password.setError("Password is required!");
                submittable = false;
            }

            if (submittable) {
                User checkUser = new User();
                QueryBuilder<User> query = new UserTable(dbCon.getReadableDatabase());
                //QueryBuilder<User> query = userTable.database();
                Cursor cursor = query
                        .find()
                        .where("username", "=", username.getText().toString())
                        .limitBy(1)
                        .exec();
                if (cursor != null && cursor.getCount() > 0){
                    if (cursor.moveToFirst()){
                        checkUser
                                .setUserId((int) cursor
                                        .getLong(cursor.getColumnIndex(UserTable.COLUMN_USER_ID)));
                        checkUser
                                .setUsername(cursor
                                        .getString(cursor
                                                .getColumnIndex(UserTable.COLUMN_USERNAME)));
                        checkUser
                                .setPassword(cursor
                                        .getString(cursor
                                                .getColumnIndex(UserTable.COLUMN_PASSWORD)));
                    }
                    cursor.close();
                    dbCon.close();
                    Log.d("login", checkUser.getPassword());
                }else{
                    username.setError("User not found!");
                    return;
                }

                if (!password.getText().toString().equals(checkUser.getPassword())){
                    password.setError("Incorrect Password!");
                    return;
                }

                sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor Ed=sp.edit();
                Ed.putInt("user", checkUser.getUserId());
                Ed.commit();
                Intent i = new Intent(context, MainActivity.class);
                Log.d("login", "successfully logged user");
                startActivity(i);
            }
        });
    }
}
