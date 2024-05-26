package com.example.cvd;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.IOException;

public class auto extends AppCompatActivity {
    ImageView imageView;
    Uri imageUri;
    EditText editTitle;
    Button saveButton;
    Button autoCorrectButton;

    private Mat imageMat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);

        imageView = findViewById(R.id.auto_imageView);
        editTitle = findViewById(R.id.name_auto);
        saveButton = findViewById(R.id.save_auto);

        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            imageView.setImageURI(imageUri);
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            imageMat = new Mat();
            Utils.bitmapToMat(bitmap, imageMat);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupAutoCorrection();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                if (!title.isEmpty() && imageUri != null) {
                    PhotoBookDB db = new PhotoBookDB(auto.this);
                    db.addPhoto(title, imageUri.toString());
                    finish(); // 저장 후 종료
                }
            }
        });
    }

    @SuppressLint("WrongViewCast")
    private void setupAutoCorrection() {

        autoCorrectButton = findViewById(R.id.change_auto);
        autoCorrectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCorrectImage();
            }
        });
    }

    private void autoCorrectImage() {
        Mat correctedMat = imageMat.clone();
        // 빨간색과 초록색을 감지하여 더 명확하게 보정하는 코드 작성
        // 예시: 간단한 필터 적용 (실제 구현 시 더 복잡한 로직 필요)
        Core.inRange(correctedMat, new Scalar(35, 100, 100), new Scalar(85, 255, 255), correctedMat); // 초록색 범위 감지
        Core.inRange(correctedMat, new Scalar(0, 100, 100), new Scalar(10, 255, 255), correctedMat);  // 빨간색 범위 감지

        Bitmap bitmap = Bitmap.createBitmap(correctedMat.cols(), correctedMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(correctedMat, bitmap);
        imageView.setImageBitmap(bitmap);
    }

    /*
    private void setupAutoCorrection1() {
        Button autoCorrectButton = findViewById(R.id.btn_auto);
        autoCorrectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCorrectImage1();
            }
        });
    }

    private void autoCorrectImage1() {
        Mat correctedMat = imageMat.clone();
        // 빨간색과 초록색을 노란색과 파란색으로 변경하는 코드 작성
        // 예시: 간단한 필터 적용 (실제 구현 시 더 복잡한 로직 필요)
        Core.inRange(correctedMat, new Scalar(35, 100, 100), new Scalar(85, 255, 255), correctedMat); // 초록색 범위 감지
        Core.inRange(correctedMat, new Scalar(0, 100, 100), new Scalar(10, 255, 255), correctedMat);  // 빨간색 범위 감지

        // 노란색과 파란색으로 변경 로직 추가
        // 예시: 픽셀 단위로 변경 (실제 구현 시 더 복잡한 로직 필요)
        for (int i = 0; i < correctedMat.rows(); i++) {
            for (int j = 0; j < correctedMat.cols(); j++) {
                double[] pixel = correctedMat.get(i, j);
                if (pixel[0] == 35) {
                    correctedMat.put(i, j, new double[]{255, 255, 0}); // 노란색
                } else if (pixel[0] == 0) {
                    correctedMat.put(i, j, new double[]{0, 0, 255}); // 파란색
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(correctedMat.cols(), correctedMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(correctedMat, bitmap);
        imageView.setImageBitmap(bitmap);
    }

     */
}