package com.example.mt_assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activity6 extends AppCompatActivity {

    DatabaseReference dbref;

    ArrayList<AnalysedImage> items;
    AnalysedListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_6);

        ListView listView = findViewById(R.id.listView);
        Button buttonAdd = findViewById(R.id.buttonAdd);

        items = new ArrayList<>();

        adapter = new AnalysedListAdapter(
                this,
                R.layout.item_row,
                items
        );

        listView.setAdapter(adapter);

        dbref = FirebaseDatabase.getInstance()
                .getReference("analysed_items");

        loadData();

        buttonAdd.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Activity6.this,
                            MainActivity.class);

            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {

            AnalysedImage selectedItem =
                    items.get(position);

            Intent intent =
                    new Intent(Activity6.this,
                            Activity7.class);

            intent.putExtra("id",
                    selectedItem.getId());

            intent.putExtra("imageName",
                    selectedItem.getImageName());

            intent.putExtra("reader",
                    selectedItem.getReader());

            intent.putExtra("text",
                    selectedItem.getText());

            startActivity(intent);
        });
    }

    private void loadData() {

        dbref.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        items.clear();

                        for (DataSnapshot itemSnap :
                                snapshot.getChildren()) {

                            String imageName =
                                    itemSnap.child("filename")
                                            .getValue(String.class);

                            String reader =
                                    itemSnap.child("reader")
                                            .getValue(String.class);

                            String text =
                                    itemSnap.child("text")
                                            .getValue(String.class);

                            if (imageName != null &&
                                    reader != null &&
                                    text != null) {

                                AnalysedImage item =
                                        new AnalysedImage();

                                item.setId(itemSnap.getKey());
                                item.setImageName(imageName);
                                item.setReader(reader);
                                item.setText(text);

                                items.add(item);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                Activity6.this,
                                "Failed to load data",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}