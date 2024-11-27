package com.example.theychat;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ImageDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_detail);

        String imagePath = getIntent().getStringExtra("imagePath");
        ImageView iv_photo = findViewById(R.id.iv_photo);
        iv_photo.setImageURI(Uri.parse(imagePath));
        iv_photo.setOnClickListener(v -> finish());
    }
}
