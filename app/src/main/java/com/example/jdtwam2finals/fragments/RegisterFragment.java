package com.example.jdtwam2finals.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jdtwam2finals.R;
import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.dao.QueryBuilder;
import com.example.jdtwam2finals.dao.UserTable;
import com.example.jdtwam2finals.databinding.FragmentRegisterBinding;
import com.example.jdtwam2finals.dto.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding b;
    private DbCon dbCon;
    private Context context;
    private TextInputEditText username, password;
    private MaterialButton submit;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        b = FragmentRegisterBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbCon = DbCon.getInstance(context);
        submit = b.submitButton;
        username = b.registerUsername;
        password = b.registerPassword;

        submit.setOnClickListener(v -> {
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

                User createUser = new User(username.getText().toString(), password.getText().toString());
                QueryBuilder<User> query = new UserTable(dbCon.getReadableDatabase());
                Cursor cursor = query
                        .find()
                        .where("username","=", username.getText().toString())
                        .exec();

                if (cursor != null && cursor.getCount() > 0){
                    username.setError("Username is duplicated!");
                    cursor.close();
                }else{
                    query.database(dbCon.getWritableDatabase());
                    long id = query.insert(createUser);
                    if (id != -1){
                        Log.d("register_user", "User successfully inserted");
                        LoginFragment loginFragment = new LoginFragment();
                        FragmentManager fm = requireActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        ((BottomNavigationView) requireActivity()
                                .findViewById(R.id.select_form))
                                .setSelectedItemId(R.id.go_to_login);
                        transaction.replace(R.id.form_fragment, loginFragment);
                        transaction.addToBackStack(null); // Add the transaction to the back stack
                        transaction.commit();
                    }
                }
            }else {
                Toast.makeText(requireActivity(),"Please fill in the fields.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}