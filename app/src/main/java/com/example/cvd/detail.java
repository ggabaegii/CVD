package com.example.cvd;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class detail extends AppCompatActivity {
    ImageView imageView;
    Uri imageUri;
    EditText editTitle;
    Button changeBtn;
    String oldTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView = findViewById(R.id.detail_imageView);
        editTitle = findViewById(R.id.name_detail);
        changeBtn = findViewById(R.id.change_detail);

        getAndSetIntentData();

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = editTitle.getText().toString();
                PhotoBookDB db = new PhotoBookDB(detail.this);
                db.updateData(oldTitle, newTitle);
            }
        });
    }

    /**
     * 데이터 가져와서 화면에 보여주기
     */
    private void getAndSetIntentData() {
        if (getIntent().hasExtra("title") && getIntent().hasExtra("imageUri")) {
            // 데이터 가져오기
            oldTitle = getIntent().getStringExtra("title");
            String imageUriString = getIntent().getStringExtra("imageUri");
            imageUri = Uri.parse(imageUriString);

            // 데이터 넣기
            editTitle.setText(oldTitle);
            imageView.setImageURI(imageUri);
        }
    }
}