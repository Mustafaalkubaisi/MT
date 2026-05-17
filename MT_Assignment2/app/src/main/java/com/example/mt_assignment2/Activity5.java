package com.example.mt_assignment2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

public class Activity5 extends AppCompatActivity {
    String id;
    String type;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_5);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI elements
        ImageView imageView = findViewById(R.id.imageView2);
        EditText resultText = findViewById(R.id.MLKitResults);
        EditText title = findViewById(R.id.textReaderTitle);
        Button buttonSave = findViewById(R.id.buttonSave);

        // Firebase reference
        DatabaseReference dbref =
                FirebaseDatabase.getInstance().getReference("analysed_items");

        // Receive data from Activity2. Seeing which Picture was selected and displaying that on this Activity
        Intent intent = getIntent();

        type = intent.getStringExtra("MODE");
        result = intent.getStringExtra("ml_result");
        id = intent.getStringExtra("id");

// NEW: URI support (THIS is the real fix)
        String imageUriString = intent.getStringExtra("image_uri");

        byte[] imageBytes = intent.getByteArrayExtra("image_bytes");
        int imageRes = intent.getIntExtra("image_res", -1);

// 1. PRIORITY: gallery / camera via URI (MOST IMPORTANT)
        if (imageUriString != null) {
            Uri uri = Uri.parse(imageUriString);
            imageView.setImageURI(uri);
        }

// 2. camera fallback (old bitmap method)
        else if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
        }

// 3. last fallback: MainActivity static icons
        else if (imageRes != -1) {
            imageView.setImageResource(imageRes);
        }

        // Display ML result
        if (result != null) {
            resultText.setText(result);
        }

        // Display title
        if (type != null) {
            title.setText(type.toUpperCase() + " Reader");
        }

        // Save button → Firebase
        buttonSave.setOnClickListener(v -> {

            String key = dbref.push().getKey();
            if (key == null) return;

            String filename = null;

            if (imageUriString != null) {
                filename = saveImageToGallery(Uri.parse(imageUriString));
            }

            if (filename == null) {
                Toast.makeText(Activity5.this,
                        "No image selected",
                        Toast.LENGTH_SHORT).show();
                return;
            }


            String reader = title.getText().toString();
            String text = resultText.getText().toString();



            dbref.child(key).child("filename").setValue(filename);
            dbref.child(key).child("reader").setValue(reader);
            dbref.child(key).child("text").setValue(text);

            Intent goToActivity6 =
                    new Intent(Activity5.this, Activity6.class);

            startActivity(goToActivity6);
            finish();
        });
    }




    private String saveImageToGallery(Uri imageUri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(imageUri)
            );

            String filename = "MT_Assignment_" + System.currentTimeMillis() + ".png";

            android.content.ContentValues values =
                    new android.content.ContentValues();

            values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH,
                    android.os.Environment.DIRECTORY_PICTURES);

            Uri savedImageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
            );

            java.io.OutputStream outputStream =
                    getContentResolver().openOutputStream(savedImageUri);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.flush();
            outputStream.close();

            return filename;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}