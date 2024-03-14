package com.example.recordsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import com.example.recordsystem.databinding.ActivityHistoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class History extends AppCompatActivity {
ActivityHistoryBinding binding;
ArrayList<TransactModel> list;
HistoryAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference reference;
    ValueEventListener eventListener;
    Dialog loadingDialog;
    String pva;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            pva = bundle.getString("PVA");
        }

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Vet Transactions").child(pva);

        list = new ArrayList<>();
        GridLayoutManager lm = new GridLayoutManager (History.this, 1);
        binding.recHistory.setLayoutManager(lm);
        adapter = new HistoryAdapter(this, list);
        binding.recHistory.setAdapter(adapter);

       loadingDialog = createLoadingDialog();
       loadingDialog.show();

        eventListener = reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
              //  DataSnapshot snap = snapshot.child(pva);
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    TransactModel dataClass = itemSnapshot.getValue(TransactModel.class);
                    assert dataClass != null;
                    list.add(dataClass);

                }
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss(); // Dismiss loading dialog when data is loaded
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss(); // Dismiss loading dialog when data is loaded
            }
        });
    }
    private Dialog createLoadingDialog() {
        Dialog dialog = new Dialog(History.this);
        dialog.setContentView(R.layout.progress);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
}