package com.example.recordsystem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recordsystem.databinding.CardTransactionBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.viewHolder>{
    Context context;
    ArrayList<TransactModel> list;

    public HistoryAdapter(Context context, ArrayList<TransactModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_transaction, parent, false);
        return new HistoryAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.viewHolder holder, int position) {
        TransactModel Tm = list.get(position);

        if (Tm != null){

            Glide.with(context)
                    .load(list.get(position)
                            .getLabExamImage())
                    .into(holder.binding.historyImageProfile);

            holder.binding.historyPva.setText(Tm.getPva()); //
            holder.binding.historyPetName.setText(Tm.getPetName()); //
            holder.binding.historyTimeDate.setText(Tm.getCurrentDate()); //
            holder.binding.historyNote.setText(Tm.getClientNote()); //
            holder.binding.historyFinalDiagnos.setText(Tm.getFinalDiagnos()); //
            holder.binding.historyLabExam.setText(Tm.getLabExam()); //
            holder.binding.historyMedical.setText(Tm.getMedicalHistory());//
            holder.binding.historyNextVisit.setText(Tm.getNextVisit());//
            holder.binding.historyPatientNote.setText(Tm.getPatientNote());//
            holder.binding.historyPrescription.setText(Tm.getPrescription());//
            holder.binding.historyPrognosis.setText(Tm.getPrognosis());//
            holder.binding.historyRecommendation.setText(Tm.getRecommendation());
            holder.binding.historyTentative.setText(Tm.getTentativeDiagnos());
            holder.binding.historyTreatment.setText(Tm.getTreatment());

            holder.binding.transactCard.setOnClickListener(v ->
                    showHistory(Tm.getPva(),Tm.getPetName(),Tm.getCurrentDate(),
                    Tm.getLabExamImage(),Tm.getLabExam(),Tm.getClientNote(),
                    Tm.getFinalDiagnos(),Tm.getMedicalHistory(), Tm.getNextVisit(),
                    Tm.getPatientNote(),Tm.getPrescription(), Tm.getPrognosis(),
                    Tm.getRecommendation(), Tm.getTentativeDiagnos(), Tm.getTreatment(),
            Tm.getClinicExam(),Tm));

        }
        else {
            list.clear();
        }
    }

    @SuppressLint("SetTextI18n")
    private void showHistory(String pva, String petName,
                             String currentDate, String labExamImage,
                             String labExam, String clientNote,
                             String finalDiagnos, String medicalHistory,
                             String nextVisit, String patientNote,
                             String prescription, String prognosis,
                             String recommendation, String tentativeDiagnos,
                             String treatment, String clinicExam, TransactModel tm) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.history_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
        TextView Pva = dialog.findViewById(R.id.dialogPva);
        TextView Petname = dialog.findViewById(R.id.dialogPetName);
        TextView date = dialog.findViewById(R.id.dialogDate);
        TextView medical = dialog.findViewById(R.id.dialogMedicalHistory);
        TextView clinic = dialog.findViewById(R.id.dialogClinicExam);
        ImageView labImage = dialog.findViewById(R.id.dialogLabImage);
        TextView labexam = dialog.findViewById(R.id.dialogLabExam);
        TextView tenta = dialog.findViewById(R.id.dialogTentative);
        TextView finale = dialog.findViewById(R.id.dialogFinal);
        TextView progno = dialog.findViewById(R.id.dialogPrognosis);
        TextView treat = dialog.findViewById(R.id.dialogTreatment);
        TextView prescript = dialog.findViewById(R.id.dialogPrescription);
        TextView nextV = dialog.findViewById(R.id.dialogNextVisit);
        TextView clientN = dialog.findViewById(R.id.dialogClientNote);
        TextView patientN = dialog.findViewById(R.id.dialogPatientNote);
        TextView further = dialog.findViewById(R.id.dialogFurtherReco);

        ImageView delete = dialog.findViewById(R.id.deleteHistory);
        ImageView update = dialog.findViewById(R.id.updateHisto);
        ImageView downloadSS = dialog.findViewById(R.id.download);

        Pva.setText(pva);
        Petname.setText(petName);
        date.setText(currentDate);
        medical.setText(medicalHistory);
        clinic.setText(clinicExam);
        // Load the image using Glide
        Glide.with(context)
                .load(labExamImage)
                .into(labImage);
        labexam.setText(labExam);
        tenta.setText("Tentative: " + tentativeDiagnos);
        finale.setText("Final: " + finalDiagnos);
        progno.setText(prognosis);
        treat.setText(treatment);
        prescript.setText(prescription);
        nextV.setText("Next visit: " + nextVisit);
        clientN.setText("Client to note: " + clientNote);
        patientN.setText("Patient to note: " + patientNote);
        further.setText("Further recommendation: " + recommendation);

        downloadSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage();
            }

            private void downloadImage() {
                // Capture screenshot of the dialog layout
                Bitmap screenshot = captureView(dialog.getWindow().getDecorView());

                // Save the screenshot as an image file
                String filename = "transaction_screenshot_" + System.currentTimeMillis() + ".png";
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();

                    // Add the image to the gallery
                    addToGallery(file);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to save screenshot", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Dismiss the dialog
                dialog.dismiss();

                // Show a toast message indicating the screenshot is saved
                Toast.makeText(context, "Screenshot saved to gallery", Toast.LENGTH_SHORT).show();
            }

            private Bitmap captureView(View view) {
                Bitmap screenshot;
                // Check if the view is a ScrollView
                if (view instanceof ScrollView) {
                    ScrollView scrollView = (ScrollView) view;
                    // Capture only the content of the ScrollView
                    screenshot = Bitmap.createBitmap(scrollView.getChildAt(0).getWidth(),
                            scrollView.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(screenshot);
                    scrollView.getChildAt(0).draw(canvas);
                } else {
                    // Capture the entire view
                    screenshot = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(screenshot);
                    view.draw(canvas);
                }
                return screenshot;
            }

            private void addToGallery(File file) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Transaction_Screenshot");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Screenshot of transaction dialog");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());

                // Insert the image into the MediaStore
                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    Toast.makeText(context, "Screenshot saved to gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to save screenshot to gallery", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog(tm.getPva(),tm.getKey());
            }

            private void confirmationDialog(String pva, String key) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to delete this transaction?");
                builder.setPositiveButton("Yes", (dialog1, which) -> {
                    // Pass the key of the transaction to deleteTransactions method

                    deleteTransactions();
                });
                builder.setNegativeButton("No", (dialog12, which) -> {
                    // Dismiss the dialog
                    dialog12.dismiss();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            private void deleteTransactions() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Vet Transactions").child(pva);
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(labExamImage);
                storageReference.delete().addOnSuccessListener(unused ->
                        reference.removeValue().addOnSuccessListener(unused1 -> {
                            Objects.requireNonNull(reference.getParent()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        // No remaining child nodes, delete the parent node as well
                                        reference.getParent().removeValue();
                                    }
                                    Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                                    ((Activity) context).finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, "Failed to delete: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).addOnFailureListener(e ->
                                Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show())
                ).addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTransactions();
            }
            private void updateTransactions() {
                Intent intent = new Intent(context, History_Update.class)

                        .putExtra("NamePet",Petname.getText().toString())
                        .putExtra("MedicalHistory", medical.getText().toString())
                        .putExtra("ClinicExam", clinic.getText().toString())
                        .putExtra("ClientNote", clientN.getText().toString())
                        .putExtra("LabExam", labexam.getText().toString())
                        .putExtra("Tentative", tenta.getText().toString())
                        .putExtra("Final", finale.getText().toString())
                        .putExtra("Progno",progno.getText().toString())
                        .putExtra("Treatment", treat.getText().toString())
                        .putExtra("Prescription", prescript.getText().toString())
                        .putExtra("NextVisit", nextV.getText().toString())
                        .putExtra("PatientNote", patientN.getText().toString())
                        .putExtra("FurtherReco", further.getText().toString())
                        .putExtra("Image", labExamImage)
                        .putExtra("PVA", pva);

                        context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        CardTransactionBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardTransactionBinding.bind(itemView);

        }
    }
}
