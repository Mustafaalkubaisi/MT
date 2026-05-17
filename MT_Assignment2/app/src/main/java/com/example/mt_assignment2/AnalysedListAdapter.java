package com.example.mt_assignment2;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnalysedListAdapter extends ArrayAdapter<AnalysedImage> {

    List<AnalysedImage> items = new ArrayList<>();

    public AnalysedListAdapter(@NonNull Context context, int resource,
                       @NonNull List<AnalysedImage> objects) {
        super(context, resource, objects);
        items = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_row, parent, false);
        }

        AnalysedImage item = items.get(position);

        ImageView icon = convertView.findViewById(R.id.imageViewItem);
        TextView textViewItem = convertView.findViewById(R.id.textViewItem);

        textViewItem.setText(item.getReader());

        try {
            Uri imageUri = loadImageFromGallery(item.getImageName());

            if (imageUri != null) {
                icon.setImageURI(imageUri);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.WHITE);
        } else {
            convertView.setBackgroundColor(Color.LTGRAY);
        }

        return convertView;
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

        Cursor cursor = getContext().getContentResolver().query(
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
