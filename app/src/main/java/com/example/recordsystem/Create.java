package com.example.recordsystem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.recordsystem.databinding.ActivityCreateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Create extends AppCompatActivity {
ActivityCreateBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    String pva, petName, medicalHistory, clinicExam,
            labExam,labExamImage, tentativeDiagnos,
            finalDiagnos, prognosis, treatment,
            prescription,nextVisit,clientNote,
            patientNote,recommendation, currentDate;
    Uri contentUri;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        PushDownAnim.setPushDownAnimTo(binding.createLabExImage)
                .setScale(PushDownAnim.MODE_SCALE, 0.95f);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.prognosis_options));
        binding.createPrognosis.setAdapter(adapter);
        startTimestamp();

        ActivityResultLauncher<Intent> activityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        contentUri = data.getData();
                        binding.createLabExImage.setImageURI(contentUri);
                    } else {
                        Toast.makeText(Create.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            binding.createPetName.setText(bundle.getString("Pet"));
            binding.createPva.setText(bundle.getString("PVA"));
            pva = bundle.getString("PVA");
        }

        binding.createPrognosis.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPrognosis = (String) parent.getItemAtPosition(position);
            Toast.makeText(Create.this, "Selected: " + selectedPrognosis, Toast.LENGTH_SHORT).show();
            prognosis = selectedPrognosis; // Store the selected prognosis
        });

        binding.createLabExImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
            private void selectImage() {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResult.launch(photoPicker);

            }
        });


        binding.createTransactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTransaction();
            }
        });

    }

    private void uploadTransaction() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("TransactionImages")
                .child(Objects.requireNonNull(contentUri.getLastPathSegment()));
        Dialog loadingDialog = createLoadingDialog();
        loadingDialog.show();

        storageReference.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                labExamImage = urlImage.toString();
                uploadDataTransaction();
                loadingDialog.show();
                showNotification();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.show();;
            }
        });
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Create.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.care)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setContentTitle("Upload Successful")
                .setContentText("Transaction added")
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Create.this);
        notificationManager.notify(notificationId, builder.build());

        new Handler().postDelayed(() -> {
            // Cancel the notification
            notificationManager.cancel(notificationId);
        }, 3000); // 3000 milliseconds = 3 seconds
    }

    private void uploadDataTransaction() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Vet Transactions");

        pva = binding.createPva.getText().toString();
        petName = binding.createPetName.getText().toString();
        medicalHistory = binding.createMHCR.getText().toString();
        clinicExam = binding.createCE.getText().toString();
        labExam = binding.createLabExTest.getText().toString();
        tentativeDiagnos = binding.createTentative.getText().toString();
        finalDiagnos = binding.createFinal.getText().toString();
        prognosis = binding.createPrognosis.getText().toString();
        treatment = binding.createTreatment.getText().toString();
        prescription = binding.createPrescription.getText().toString();
        currentDate = binding.createTimeDate.getText().toString();
        nextVisit = binding.createNextVisit.getText().toString();
        clientNote = binding.createClientnote.getText().toString();
        patientNote = binding.createPatientNote.getText().toString();
        recommendation = binding.createRecommendation.getText().toString();

        TransactModel model = new TransactModel(pva, petName, medicalHistory, clinicExam, labExam,labExamImage, tentativeDiagnos, finalDiagnos, prognosis, treatment, prescription,nextVisit,clientNote, patientNote,recommendation, currentDate);

      /*  model.setPva(pva);
        model.setPetName(petName);
        model.setMedicalHistory(medicalHistory);
        model.setClinicExam(clinicExam);
        model.setLabExam(labExam);
        model.setTentativeDiagnos(tentativeDiagnos);
        model.setFinalDiagnos(finalDiagnos);
        model.setPrognosis(prognosis);
        model.setTreatment(treatment);
        model.setPrescription(prescription);
        model.setCurrentDate(currentDate);
        model.setNextVisit(nextVisit);
        model.setClientNote(clientNote);
        model.setPatientNote(patientNote);
        model.setRecommendation(recommendation);
        model.setLabExamImage(labExamImage); */

        DatabaseReference pvaRef = reference.child(pva);
        String transactionId = pvaRef.push().getKey();

        pvaRef.child(Objects.requireNonNull(transactionId)).setValue(model)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Create.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Create.this, MainActivity.class);
                        showProgress();
                        startActivity(intent);
                    }
                }).addOnFailureListener(e -> Toast.makeText(Create.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private Dialog createLoadingDialog() {
        Dialog dialog = new Dialog(Create.this);
        dialog.setContentView(R.layout.progress);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    private void showProgress() {
        Dialog loadingDialog = createLoadingDialog();
        loadingDialog.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop updating timestamp when activity is destroyed
        stopTimestamp();
    }

    private void stopTimestamp() {
        handler.removeCallbacks(updateTimestampRunnable);
    }
    private void startTimestamp() {
        // Schedule the update task every second
        handler.post(updateTimestampRunnable);
    }

    // Runnable to update the timestamp
    private final Runnable updateTimestampRunnable = new Runnable() {
        @Override
        public void run() {
            // Update the timestamp
            updateTimestamp();
            // Schedule the next update after 1 second
            handler.postDelayed(this, 1000);
        }
    };

    private void updateTimestamp() {
        // Get current time and date
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE MM/dd/yy HH:mm", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        // Update TextView with current time and date
        binding.createTimeDate.setText(currentDateAndTime);
    }
}