package com.example.jdtwam2finals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jdtwam2finals.*;
import com.example.jdtwam2finals.ViewHolders.TransactionViewHolder;
import com.example.jdtwam2finals.databinding.TransactionViewHolderBinding;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {

    private Context context;
    private TransactionViewHolderBinding tViewBinding;
    public TransactionAdapter(){}

    public TransactionAdapter(Context context){
        this.context = context;
    }
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        tViewBinding = TransactionViewHolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TransactionViewHolder(tViewBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
