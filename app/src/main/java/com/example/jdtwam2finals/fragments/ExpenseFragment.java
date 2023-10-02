package com.example.jdtwam2finals.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.jdtwam2finals.dao.DbCon;
import com.example.jdtwam2finals.dao.ExpenseTable;
import com.example.jdtwam2finals.utils.QueryBuilder;
import com.example.jdtwam2finals.dao.TransactionTable;
import com.example.jdtwam2finals.databinding.FragmentExpenseBinding;
import com.example.jdtwam2finals.dto.Expense;
import com.example.jdtwam2finals.dto.Transaction;
import com.example.jdtwam2finals.utils.MonthSetter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {

    private FragmentExpenseBinding b;
    private TextInputEditText amount, category, note;
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

    public ExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
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
        b = FragmentExpenseBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        dbCon = DbCon.getInstance(context);
        amount = b.expenseAmount;
        category = b.expenseCategory;
        note = b.expenseNote;
        submit = b.expenseSubmit;

        submit.setOnClickListener(v -> {

            boolean amountIsSubmittable = true;
            boolean noteIsSubmittable = true;
            boolean categoryIsSubmittable = true;
            String regex = "[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?";

            if (amount.getText().toString().isEmpty()){
                amount.setError("Field is required!");
                amountIsSubmittable = false;
            }

            if(note.getText().toString().isEmpty()){
                note.setError("Field is required!");
                noteIsSubmittable = false;
            }

            if (category.getText().toString().isEmpty()){
                category.setError("Field is required!");
                categoryIsSubmittable = false;
            }

            if (!amount.getText().toString().matches(regex)){
                amount.setError("Field should be in correct format.");
                amountIsSubmittable = false;
            }

            if (amountIsSubmittable && noteIsSubmittable && categoryIsSubmittable){

                QueryBuilder<Transaction> transactionBuilder = (QueryBuilder<Transaction>) TransactionTable.getAndSetInstance(new TransactionTable());
                transactionBuilder.database(dbCon.getWritableDatabase());
                int userId = sp.getInt("user", -1);
                long transactionId = transactionBuilder.insert(new Transaction("Expense", new Date(), MonthSetter.currentMonth(), userId));
                if (transactionId != -1){
                    Log.d("insertTransaction", "Transaction successfully inserted: " + transactionId);
                    QueryBuilder<Expense> expenseQueryBuilder = new ExpenseTable(dbCon.getWritableDatabase());

                    long expenseId = expenseQueryBuilder.insert(new Expense(
                            Double.parseDouble(amount.getText().toString()),
                            category.getText().toString(),
                            note.getText().toString(),
                            (int) transactionId
                    ));
                    if (expenseId != -1){
                        Log.d("insertTransaction", "Expense inserted: " + expenseId);
                        Toast.makeText(context, "Expense inserted", Toast.LENGTH_SHORT).show();
                        amount.setText("");
                        note.setText("");
                        category.setText("");
                    }else{
                        Log.d("insertTransaction",
                                "Expense did not insert, deleting transaction: " + transactionId
                        );
                        transactionBuilder.delete()
                                .one((int) transactionId)
                                .execDelete();
                    }
                }

            }else{
                Toast.makeText(context, "Field is required!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
