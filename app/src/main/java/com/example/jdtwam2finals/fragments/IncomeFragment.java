package com.example.jdtwam2finals.fragments;

import android.content.Context;
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
import android.widget.Button;
import android.widget.Toast;

import com.example.jdtwam2finals.R;
import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.dao.IncomeTable;
import com.example.jdtwam2finals.dao.QueryBuilder;
import com.example.jdtwam2finals.dao.TransactionTable;
import com.example.jdtwam2finals.databinding.FragmentIncomeBinding;
import com.example.jdtwam2finals.dto.Income;
import com.example.jdtwam2finals.dto.Transaction;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment {

    private FragmentIncomeBinding b;
    private TextInputEditText amount, note;
    private Button submit;
    private Context context;
    private DbCon dbCon;
    private SharedPreferences sp;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IncomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IncomeFragment newInstance(String param1, String param2) {
        IncomeFragment fragment = new IncomeFragment();
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
        b = FragmentIncomeBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        dbCon = DbCon.getInstance(context);
        submit = b.incomeSubmit;
        amount = b.incomeAmount;
        note = b.incomeNote;

        submit.setOnClickListener(v -> {

            boolean amountIsSubmittable = true;
            boolean noteIsSubmittable = true;

            if (amount.getText().toString().isEmpty()){
                amount.setError("Field is required!");
                amountIsSubmittable = false;
            }

            if(note.getText().toString().isEmpty()){
                note.setError("Field is required!");
                noteIsSubmittable = false;
            }

            if (amountIsSubmittable && noteIsSubmittable){

                QueryBuilder<Transaction> transactionBuilder = new TransactionTable(dbCon.getWritableDatabase());
                int userId = sp.getInt("user", -1);
                long transactionId = transactionBuilder.insert(new Transaction("Income", new Date(), (int) userId));
                if (transactionId != -1){
                    Log.d("insertTransaction", "Transaction successfully inserted: " + transactionId);
                    QueryBuilder<Income> incomeQueryBuilder = new IncomeTable(dbCon.getWritableDatabase());

                    long incomeId = incomeQueryBuilder.insert(new Income(
                            Double.parseDouble(amount.getText().toString()),
                            note.getText().toString(),
                            (int) transactionId
                            ));
                    if (incomeId != -1){
                        Log.d("insertTransaction", "Income inserted: " + incomeId);
                        Toast.makeText(context, "Income inserted", Toast.LENGTH_SHORT).show();
                        amount.setText("");
                        note.setText("");
                    }else{
                        Log.d("insertTransaction", "Income did not insert, deleting transaction: " + transactionId);
                    }
                }

            }else{
                Toast.makeText(context, "Field is required!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
