package com.example.recordsystem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.recordsystem.databinding.ActivityInputBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Input extends AppCompatActivity {
ActivityInputBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    String pva, petName, dob, sex, breed, color, ownerName, contact, imageProfile, address;
    Uri contentUri;
    Handler handler = new Handler();
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PushDownAnim.setPushDownAnimTo(binding.camera)
                .setScale(PushDownAnim.MODE_SCALE, 0.95f);

        startTimestamp();

        ActivityResultLauncher <Intent> activityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        contentUri = data.getData();
                        binding.camera.setImageURI(contentUri);
                    } else {
                        Toast.makeText(Input.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Fetch the next available PVA number
        fetchNextPvaNumber();
        binding.camera.setOnClickListener(new View.OnClickListener() {
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
        binding.uploadPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

    }



    private Dialog createLoadingDialog() {
        Dialog dialog = new Dialog(Input.this);
        dialog.setContentView(R.layout.progress);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    private void fetchNextPvaNumber() {
        DatabaseReference recordsRef = FirebaseDatabase.getInstance().getReference("Vet Records");
        recordsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long maxPva = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Records records = dataSnapshot.getValue(Records.class);
                        if (records != null && !TextUtils.isEmpty(records.getPva())) {
                            long pva = Long.parseLong(records.getPva());
                            maxPva = Math.max(maxPva, pva);
                        }
                    }
                    // Set the next available PVA number
                    binding.transactionId.setText(String.valueOf(maxPva + 1));
                } else {
                    // No existing records, start with 1
                    binding.transactionId.setText("1");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Input.this, "Failed to retrieve PVA number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Pets")
                .child(Objects.requireNonNull(contentUri.getLastPathSegment()));

        Dialog loadingDialog = createLoadingDialog();
     loadingDialog.show();


        storageReference.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                 imageProfile = urlImage.toString();
                upload();
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Input.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.care)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setContentTitle("Upload Successful!")
                .setContentText("New client added!")
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Input.this);
        notificationManager.notify(notificationId, builder.build());

        new Handler().postDelayed(() -> {
            // Cancel the notification
            notificationManager.cancel(notificationId);
        }, 3000); // 3000 milliseconds = 3 seconds

    }
    private void upload() {


        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Vet Records");

        pva = binding.transactionId.getText().toString();
        petName = binding.newName.getText().toString();
        dob = binding.newDOB.getText().toString();
        sex = binding.newSex.getText().toString();
        breed = binding.newBreed.getText().toString();
        color = binding.newColor.getText().toString();
        ownerName = binding.newOwnername.getText().toString();
        contact = binding.newContact.getText().toString();
        address = binding.newAddress.getText().toString();

        if (TextUtils.isEmpty(pva)) {
            binding.transactionId.setError("Enter PVA #");
        } else if (TextUtils.isEmpty(petName)) {
            binding.newName.setError("Enter pets name");
        } else if (TextUtils.isEmpty(dob)) {
            binding.newDOB.setError("Enter dob");
        }else if (TextUtils.isEmpty(sex)) {
           binding.newSex.setError("Enter pets sex");
        } else if (TextUtils.isEmpty(breed)) {
            binding.newBreed.setError("Enter breed");
        }else if (TextUtils.isEmpty(color)) {
            binding.newColor.setError("Enter color");
        }else if (TextUtils.isEmpty(ownerName)) {
            binding.newOwnername.setError("Enter Owner Name");
        }else if (TextUtils.isEmpty(contact)) {
            binding.newContact.setError("Enter contact");
        }else if (TextUtils.isEmpty(contact)) {
            binding.newContact.setError("Enter contact");
        }else if (TextUtils.isEmpty(address)) {
            binding.newAddress.setError("Enter address");
        }else  {
            showProgress();
        }

        Records records = new Records(pva, petName, dob, sex, breed, color, ownerName, contact,address, imageProfile);

        FirebaseDatabase.getInstance().getReference("Vet Records").child(pva)
                .setValue(records).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(Input.this,"Saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Input.this, MainActivity.class);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Input.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

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
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMMM dd yyyy HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        // Update TextView with current time and date
        binding.timeDate.setText(currentDateAndTime);
    }
}