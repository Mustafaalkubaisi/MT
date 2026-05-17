package com.example.mt_assignment2;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

public class Activity7 extends AppCompatActivity {

    String id;
    String reader = "No reader available";
    String text = "No result available";
    String imageName;

    DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_7);

        TextView textViewReader =
                findViewById(R.id.textViewReader);

        TextView textViewResult =
                findViewById(R.id.textViewResult);

        ImageView imageViewResult =
                findViewById(R.id.imageViewResult);

        Button buttonEdit =
                findViewById(R.id.buttonEdit);

        Button buttonDelete =
                findViewById(R.id.buttonDelete);

        Button buttonCancel =
                findViewById(R.id.buttonCancel);

        dbref = FirebaseDatabase.getInstance()
                .getReference("analysed_items");

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            id = extras.getString("id");

            reader = extras.getString("reader");

            text = extras.getString("text");

            imageName = extras.getString("imageName");
        }

        textViewReader.setText(reader);

        textViewResult.setText(text);

        try {
            Uri imageUri = loadImageFromGallery(imageName);

            if (imageUri != null) {
                imageViewResult.setImageURI(imageUri);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        buttonEdit.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Activity7.this,
                            Activity5.class);

            intent.putExtra("id", id);

            intent.putExtra("reader", reader);

            intent.putExtra("text", text);

            intent.putExtra("imageName", imageName);

            startActivity(intent);
        });

        buttonDelete.setOnClickListener(v -> {

            dbref.child(id).removeValue();

            Intent intent =
                    new Intent(Activity7.this,
                            Activity6.class);

            startActivity(intent);

            finish();
        });

        buttonCancel.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Activity7.this,
                            Activity6.class);

            startActivity(intent);

            finish();
        });
    }


    private Uri loadImageFromGallery(String filename) {

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
        };

        String selection =
                MediaStore.Images.Media.DISPLAY_NAME + "=?";

        String[] selectionArgs = {filename};

        Cursor cursor = getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {

            int idColumn =
                    cursor.getColumnIndexOrThrow(
                            MediaStore.Images.Media._ID
                    );

            long imageId = cursor.getLong(idColumn);

            cursor.close();

            return ContentUris.withAppendedId(uri, imageId);
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }
}