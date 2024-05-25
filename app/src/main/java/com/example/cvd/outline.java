package com.example.cvd;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class outline extends AppCompatActivity {
    ImageView imageView;
    EditText editTitle;
    Button saveButton;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outline);

        imageView = findViewById(R.id.outline_imageView);
        editTitle = findViewById(R.id.name_outline);
        saveButton = findViewById(R.id.save_outline);

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
                    PhotoBookDB db = new PhotoBookDB(outline.this);
                    db.addPhoto(title, imageUri.toString());
                    finish(); // 저장 후 종료
                }
            }
        });
    }
}
