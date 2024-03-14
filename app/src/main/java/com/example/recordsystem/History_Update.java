package com.example.recordsystem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.recordsystem.databinding.ActivityHistoryUpdateBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class History_Update extends AppCompatActivity {
ActivityHistoryUpdateBinding binding;

    FirebaseDatabase database;
    DatabaseReference reference;
    String pva, petName, medicalHistory, clinicExam, labExam,labExamImage, tentativeDiagnos, finalDiagnos, prognosis, treatment, prescription,nextVisit,clientNote, patientNote,recommendation, currentDate;
    String oldLabImage;
    Uri contentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        contentUri = data.getData();
                        binding.updateLabExImage.setImageURI(contentUri);
                    } else {
                        Toast.makeText(History_Update.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(History_Update.this).load(bundle.getString("Image")).into(binding.updateLabExImage);
            binding.upPetName.setText(bundle.getString("Pet"));
            binding.updateMHCR.setText(bundle.getString("MedicalHistory"));
            binding.updateCE.setText(bundle.getString("ClinicExam"));
            binding.upPva.setText(bundle.getString("PVA"));
            binding.updateTreatment.setText(bundle.getString("Treatment"));
            binding.updateClientnote.setText(bundle.getString("ClientNote"));
            binding.updateLabExTest.setText(bundle.getString("LabExam"));
            binding.updateTentative.setText(bundle.getString("Tentative"));
            binding.updateFinal.setText(bundle.getString("Final"));
            binding.updatePrognosis.setText(bundle.getString("Progno"));
            binding.updatePrescription.setText(bundle.getString("Prescription"));
            binding.updateNextVisit.setText(bundle.getString("NextVisit"));
            binding.updatePatientNote.setText(bundle.getString("PatientNote"));
            binding.updateRecommendation.setText(bundle.getString("FurtherReco"));
            pva = bundle.getString("PVA");
            oldLabImage = bundle.getString("Image");

        }

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Vet Transactions").child(pva);

        binding.updateLabExImage.setOnClickListener(v -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResult.launch(photoPicker);
            createLoadingDialog();
        });

        binding.updateTransactBtn.setOnClickListener(v ->
                updateData()
        );
    }

    private void updateData() {
        Dialog loadingDialog = createLoadingDialog();
        if (contentUri == null) {
            loadingDialog.dismiss();
            Toast.makeText(History_Update.this, "Please select an image", Toast.LENGTH_SHORT).show();
        } else {
            // New image selected, upload it
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("TransactionImages")
                    .child(Objects.requireNonNull(contentUri.getLastPathSegment()));

            storageReference.putFile(contentUri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnSuccessListener(uri -> {
                    labExamImage = uri.toString();
                    showNotification();
                    updateClient(); // Update client with new data
                    finish(); // Finish activity
                }).addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(History_Update.this, "Failed to update image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                loadingDialog.dismiss();
                Toast.makeText(History_Update.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(History_Update.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.care)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setContentTitle("Update Successful")
                .setContentText("Transaction Updated")
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(History_Update.this);
        notificationManager.notify(notificationId, builder.build());
        new Handler().postDelayed(() -> {
            // Cancel the notification
            notificationManager.cancel(notificationId);
        }, 3000); // 3000 milliseconds = 3 seconds
    }
    private Dialog createLoadingDialog() {
        Dialog dialog = new Dialog(History_Update.this);
        dialog.setContentView(R.layout.progress);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        return dialog;
    }
    private void updateClient() {
        Dialog loadingDialog = createLoadingDialog();

        medicalHistory = binding.updateMHCR.getText().toString().trim();
        clientNote = binding.updateClientnote.getText().toString().trim();
        clinicExam = binding.updateCE.getText().toString().trim();
        labExam = binding.updateLabExTest.getText().toString().trim();
        tentativeDiagnos = binding.updateTentative.getText().toString().trim();
        finalDiagnos = binding.updateFinal.getText().toString().trim();
        prognosis = binding.updatePrognosis.getText().toString().trim();
        prescription = binding.updatePrescription.getText().toString().trim();
        treatment = binding.updateTreatment.getText().toString().trim();
        nextVisit = binding.updateNextVisit.getText().toString().trim();
        patientNote = binding.updatePatientNote.getText().toString().trim();
        recommendation = binding.updateRecommendation.getText().toString().trim();


        TransactModel records = new TransactModel(pva, petName, medicalHistory, clinicExam, labExam,labExamImage, tentativeDiagnos, finalDiagnos, prognosis, treatment, prescription,nextVisit,clientNote, patientNote,recommendation, currentDate);

        FirebaseDatabase.getInstance().getReference("Vet Transactions").child(pva)
                .setValue(records).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        loadingDialog.show();
                        Toast.makeText(History_Update.this,"Successfully Updated", Toast.LENGTH_SHORT).show();
                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldLabImage);
                        reference.delete();
                        showNotification();
                        Intent intent = new Intent(History_Update.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(e -> Toast.makeText(History_Update.this, e.getMessage(),Toast.LENGTH_SHORT).show());
    }
    }