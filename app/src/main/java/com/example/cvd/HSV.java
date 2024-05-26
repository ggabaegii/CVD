package com.example.cvd;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HSV extends AppCompatActivity {
    ImageView imageView;
    Uri imageUri;
    EditText editTitle;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsv);

        imageView = findViewById(R.id.hsv_imageView);
        editTitle = findViewById(R.id.name_HSV);
        saveButton = findViewById(R.id.save_HSV);

        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            imageView.setImageURI(imageUri);
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                if (!title.isEmpty() && imageUri != null) {
                    PhotoBookDB db = new PhotoBookDB(HSV.this);
                    db.addPhoto(title, imageUri.toString());
                    finish(); // 저장 후 종료
                }
            }
        });
    }
}