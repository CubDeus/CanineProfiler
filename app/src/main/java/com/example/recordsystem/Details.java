package com.example.recordsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.recordsystem.databinding.ActivityDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class Details extends AppCompatActivity {
    ActivityDetailsBinding binding;
    String pva = "";
    String imageProfile = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            binding.pvaNum.setText(bundle.getString("PVA"));
            binding.detailspetName.setText(bundle.getString("Pet"));
            binding.detailsOwner.setText(bundle.getString("Owner"));
            binding.detailsBreed.setText(bundle.getString("Breed"));
            binding.detailsSex.setText(bundle.getString("Sex"));
            binding.detailsColor.setText(bundle.getString("Color"));
            binding.detailsContact.setText( bundle.getString("Contact"));
            binding.detailsDOB.setText(bundle.getString("Dob"));
            binding.detailsAddress.setText(bundle.getString("Address"));
            pva = bundle.getString("PVA");
            imageProfile  = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into((binding.petImage));
        }

        binding.deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Details.this);
            builder.setMessage("Are you sure you want to delete this record?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                deleteRecord();
                dialog.dismiss();
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        binding.updateNow.setOnClickListener(v -> {
            Intent intent = new Intent(Details.this, Update.class)
                    .putExtra("Pet", (binding.detailspetName.getText().toString()))
                    .putExtra("Owner", (binding.detailsOwner.getText().toString()))
                    .putExtra("Breed", (binding.detailsBreed.getText().toString()))
                    .putExtra("Sex", (binding.detailsSex.getText().toString()))
                    .putExtra("Color", (binding.detailsColor.getText().toString()))
                    .putExtra("Contact",(binding.detailsContact.getText().toString()))
                    .putExtra("Dob", (binding.detailsDOB.getText().toString()))
                    .putExtra("Address", (binding.detailsAddress.getText().toString()))
                    .putExtra("Image", imageProfile)
                    .putExtra("PVA", pva);
            startActivity(intent);
        });

        binding.transactBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Details.this, History.class)
                    .putExtra("PVA", pva);
            startActivity(intent);
        });

        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Details.this, Create.class)
                        .putExtra("Pet", (binding.detailspetName.getText().toString()))
                        .putExtra("PVA", pva);
                startActivity(intent);
            }
        });
    }

    private void deleteRecord() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Vet Records");
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReferenceFromUrl(imageProfile);
        storageReference.delete().addOnSuccessListener(unused ->
                reference.child(pva).removeValue()
                        .addOnSuccessListener(unused1 -> {
                            // Check if there are any remaining child nodes
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        // No remaining child nodes, delete the parent node as well
                                        Objects.requireNonNull(reference.getParent()).removeValue();
                                    }
                                    Toast.makeText(Details.this, "Successfully Deleted",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    showNotification();
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(Details.this, "Failed to delete: " + error.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).addOnFailureListener(e ->
                                Toast.makeText(Details.this, "Failed to delete: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show())).addOnFailureListener(e ->
                Toast.makeText(Details.this, "Failed to delete: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }
    @SuppressLint("MissingPermission")
    private void showNotification() {
        String CHANNEL_ID = "CHANNEL_ID";
        int notificationId = 1;
        // Define a notification channel
        {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Upload Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Details.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.care)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setContentTitle("Delete Success")
                .setContentText("Record Deleted")
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Details.this);
        notificationManager.notify(notificationId, builder.build());
    }

}