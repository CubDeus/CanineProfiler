package com.example.recordsystem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.recordsystem.databinding.ActivityUpdateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class Update extends AppCompatActivity {
ActivityUpdateBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    String pva, petName, dob, sex, breed, color, ownerName, contact,address, imageProfile;
    String oldImage;
    Uri contentUri;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        contentUri = data.getData();
                        binding.updateImage.setImageURI(contentUri);
                    } else {
                        Toast.makeText(Update.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(Update.this).load(bundle.getString("Image")).into(binding.updateImage);
            binding.updateName.setText(bundle.getString("Pet"));
            binding.updateDOB.setText(bundle.getString("Dob"));
            binding.updateId.setText(bundle.getString("PVA"));
            binding.updateOwnername.setText(bundle.getString("Owner"));
            binding.updateBreed.setText(bundle.getString("Breed"));
            binding.updateSex.setText(bundle.getString("Sex"));
            binding.updateColor.setText(bundle.getString("Color"));
            binding.updateContact.setText(bundle.getString("Contact"));
            binding.updateAddress.setText(bundle.getString("Address"));
            pva = bundle.getString("PVA");
            oldImage = bundle.getString("Image");
        }

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Vet Records").child(pva);

        binding.updateImage.setOnClickListener(v -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResult.launch(photoPicker);
            createLoadingDialog();
        });

        binding.updatePatient.setOnClickListener(v -> updateData());

    }
    private void updateData() {
        Dialog loadingDialog = createLoadingDialog();
        if (contentUri == null) {
            loadingDialog.dismiss();
            Toast.makeText(Update.this, "Please select an image", Toast.LENGTH_SHORT).show();
        } else {
            // New image selected, upload it
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("Pets")
                    .child(Objects.requireNonNull(contentUri.getLastPathSegment()));

            storageReference.putFile(contentUri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnSuccessListener(uri -> {
                    imageProfile = uri.toString();
                    showNotification();
                    updateClient(); // Update client with new data
                    finish(); // Finish activity
                }).addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(Update.this, "Failed to update image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                loadingDialog.dismiss();
                Toast.makeText(Update.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Update.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.care)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setContentTitle("Update Successful")
                .setContentText("Record Updated")
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Update.this);
        notificationManager.notify(notificationId, builder.build());
        new Handler().postDelayed(() -> {
            // Cancel the notification
            notificationManager.cancel(notificationId);
        }, 3000); // 3000 milliseconds = 3 seconds
    }
    private Dialog createLoadingDialog() {
        Dialog dialog = new Dialog(Update.this);
        dialog.setContentView(R.layout.progress);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        return dialog;
    }
    private void updateClient() {
        Dialog loadingDialog = createLoadingDialog();

        petName = binding.updateName.getText().toString().trim();
        dob = binding.updateDOB.getText().toString().trim();
        sex = binding.updateSex.getText().toString().trim();
        breed = binding.updateBreed.getText().toString().trim();
        color = binding.updateColor.getText().toString().trim();
        ownerName = binding.updateOwnername.getText().toString().trim();
        contact = binding.updateContact.getText().toString().trim();
        address = binding.updateAddress.getText().toString().trim();

        Records records = new Records(pva, petName, dob, sex, breed, color, ownerName, contact, address, imageProfile);

        FirebaseDatabase.getInstance().getReference("Vet Records").child(pva)
                .setValue(records).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        loadingDialog.show();
                        Toast.makeText(Update.this,"Successfully Updated", Toast.LENGTH_SHORT).show();
                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImage);
                        reference.delete();
                        showNotification();
                        Intent intent = new Intent(Update.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(e -> Toast.makeText(Update.this, e.getMessage(),Toast.LENGTH_SHORT).show());

    }


}