package com.example.recordsystem;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recordsystem.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
FirebaseStorage storage;
FirebaseDatabase database;
DatabaseReference reference;
ValueEventListener eventListener;
StorageReference storageReference;

ArrayList<Records> list;
ClientAdapter adapter;

String currentPhotoPath, name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Connection from firebase database
       database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference("Vet Records");

        list = new ArrayList<>(); //nag call ka
        GridLayoutManager lm = new GridLayoutManager (MainActivity.this, 1); //
        binding.recMain.setLayoutManager(lm);
        adapter = new ClientAdapter(this, list);
        binding.recMain.setAdapter(adapter);

        Dialog loadingDialog = createLoadingDialog();
        loadingDialog.show();

        eventListener = reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Records dataClass = itemSnapshot.getValue(Records.class);
                    assert dataClass != null;
                    list.add(dataClass);
                    loadingDialog.dismiss(); // Dismiss loading dialog when data is loaded
                }
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss(); // Dismiss loading dialog when data is loaded
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss(); // Dismiss loading dialog when data is loaded
            }
        });

        binding.searchPet.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }

    private void searchList(String newText) {
        ArrayList<Records> searchList = new ArrayList<>();
        for (Records dataClass : list) {
            if (dataClass.getPva().toLowerCase().contains(newText.toLowerCase())
                    || dataClass.getPetName().toLowerCase().contains(newText.toLowerCase())) {
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }
    private Dialog createLoadingDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.progress);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
}