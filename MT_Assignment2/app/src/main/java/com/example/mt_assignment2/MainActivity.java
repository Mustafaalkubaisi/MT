package com.example.mt_assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView barcodeImage = findViewById(R.id.BarcodeImage);
        ImageView contentImage = findViewById(R.id.ContentReaderImage);
        ImageView textImage = findViewById(R.id.TextReaderImage);

        // Barcode
        barcodeImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Activity2.class);
            intent.putExtra("MODE", "BARCODE");
            intent.putExtra("image_res", R.drawable.barcode);
            startActivity(intent);
        });

        // Image Labelling
        contentImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Activity2.class);
            intent.putExtra("MODE", "IMAGE");
            intent.putExtra("image_res", R.drawable.content);
            startActivity(intent);
        });

        // Text Recognition
        textImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Activity2.class);
            intent.putExtra("MODE", "TEXT");
            intent.putExtra("image_res", R.drawable.text);
            startActivity(intent);
        });

        Button buttonGoToList = findViewById(R.id.buttonListOfAnalysedImages);

        buttonGoToList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Activity6.class);
            startActivity(intent);
        });
    }
}