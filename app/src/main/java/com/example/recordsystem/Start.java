package com.example.recordsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.recordsystem.databinding.ActivityStartBinding;

public class Start extends AppCompatActivity {
ActivityStartBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        binding.contBtn.setOnClickListener(v -> {
            // Makadto ni sa MainActivity
            Intent i = new Intent(Start.this, MainActivity.class);
            startActivity(i);
        });
        binding.newBtn.setOnClickListener(v -> {

            Intent intent = new Intent(Start.this, Input.class);   // Makadto ni sa Input activity
            startActivity(intent); // amo ni trigger makadto sa isa ka activity / Input
            // additional nga comment
        });
    }
}