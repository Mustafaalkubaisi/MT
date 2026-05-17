package com.example.mt_assignment2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class Activity2 extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewOutput;
    private Uri imageFileUri;
    private String mode;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Declaring EditResults button. it starts as "gone" but will appear once photo uploaded.
        Button buttonEdit = findViewById(R.id.buttonEdit);

        buttonEdit.setOnClickListener(v -> {

            Intent intent = new Intent(Activity2.this, Activity5.class);

            intent.putExtra("MODE", mode);
            intent.putExtra("ml_result", textViewOutput.getText().toString());

            // If using camera bitmap
            if (imageFileUri != null) {
                intent.putExtra("image_uri", imageFileUri.toString());
            }

            startActivity(intent);
        });

        // UI
        imageView = findViewById(R.id.imageView);
        textViewOutput = findViewById(R.id.textViewOutput); // FIXED (was wrong id)

        // MODE from Activity 1
        mode = getIntent().getStringExtra("MODE");

        Button buttonOpenCamera = findViewById(R.id.buttonOpenCamera);
        Button buttonLoadImage = findViewById(R.id.buttonLoadImage);

        buttonOpenCamera.setOnClickListener(v -> openCamera());
        buttonLoadImage.setOnClickListener(v -> loadImage());

        // NEW CLEAN RESULT HANDLER
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        // =========================
                        // CAMERA RESULT (Bitmap)
                        // =========================
                        if (result.getData().getExtras() != null
                                && result.getData().getExtras().get("data") != null) {

                            Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");

                            //Displays image in imageview section.
                            imageView.setImageBitmap(bitmap);
                            // Change visibility of EditResults Button to visible
                            buttonEdit.setVisibility(View.VISIBLE);

                            InputImage image = InputImage.fromBitmap(bitmap, 0);

                            runMLKit(image);
                            return;
                        }

                        // =========================
                        // GALLERY RESULT (URI)
                        // =========================
                        Uri uri = result.getData().getData();

                        if (uri != null) {
                            imageFileUri = uri;

                            //Displays image in imageview section.
                            imageView.setImageURI(uri);

                            // Set EditResults button to visible after image uploaded:
                            buttonEdit.setVisibility(View.VISIBLE);

                            try {
                                InputImage image =
                                        InputImage.fromFilePath(getBaseContext(), imageFileUri);

                                runMLKit(image);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
    }

    // =========================
    // CAMERA
    // =========================
    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(intent);
    }

    // =========================
    // GALLERY
    // =========================
    public void loadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    // =========================
    // ROUTER (IMPORTANT FIX)
    // =========================
    private void runMLKit(InputImage image) {

        textViewOutput.setText("");

        if ("BARCODE".equals(mode)) {
            processBarcode(image);

        } else if ("IMAGE".equals(mode)) {
            processContent(image);

        } else if ("TEXT".equals(mode)) {
            processText(image);
        }
    }

    // ===================== ML KIT =====================

    public void processBarcode(InputImage image) {

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                        .build();

        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {

                    StringBuilder result = new StringBuilder("Detected barcode:\n");

                    for (Barcode barcode : barcodes) {
                        result.append(barcode.getRawValue()).append("\n");
                    }

                    textViewOutput.setText(result.toString());
                })
                .addOnFailureListener(e -> textViewOutput.setText("Failed"));
    }

    public void processContent(InputImage image) {

        ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                .process(image)
                .addOnSuccessListener(labels -> {

                    StringBuilder result = new StringBuilder("Detected image content:\n");

                    int i = 1;
                    for (ImageLabel label : labels) {
                        result.append(i++)
                                .append(". ")
                                .append(label.getText())
                                .append("\n");
                    }

                    textViewOutput.setText(result.toString());
                })
                .addOnFailureListener(e -> textViewOutput.setText("Failed"));
    }

    public void processText(InputImage image) {

        TextRecognizer recognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener((Text text) -> {
                    textViewOutput.setText("Extracted text:\n" + text.getText());
                })
                .addOnFailureListener(e -> textViewOutput.setText("Failed"));
    }
}