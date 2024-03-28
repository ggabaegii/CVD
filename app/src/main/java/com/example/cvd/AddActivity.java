package com.example.cvd;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AddActivity extends AppCompatActivity {

    Uri uri;

    ImageView imageView;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        imageView = findViewById(R.id.imageView);






        //저장버튼
        Button addBtn = findViewById(R.id.edit_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //입력값 변수에 담기
                String photo = addPhotoEdit.getText().toString();
                Uri imageUri =
                //DB 객체 생성
                PhotoBookDB addressDB = new PhotoBookDB(AddActivity.this);

                //DB에 저장하기
                uri.toString();
                addressDB.addPhotoNumber(photo, data);
            }
        });
    }

    //사진 가져오기
    ActivityResultLauncher<Intent> activityResultSelect = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if( result.getResultCode() == RESULT_OK && result.getData() != null){

                        try {
                            uri = result.getData().getData();

                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                            imageView.setImageURI(uri);

                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
}
