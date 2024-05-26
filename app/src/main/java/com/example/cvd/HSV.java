package com.example.cvd;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class HSV extends AppCompatActivity {
    ImageView imageView;
    Uri imageUri;
    EditText editTitle;
    Button saveButton;

    private Mat imageMat;

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

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            imageMat = new Mat();
            Utils.bitmapToMat(bitmap, imageMat);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupHSVAdjustment();


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

    private void setupHSVAdjustment() {
        //HSV 값 조정 SeekBar
        SeekBar hueSeekBar = findViewById(R.id.seekBar5);
        SeekBar saturationSeekBar = findViewById(R.id.seekBar6);
        SeekBar valueSeekBar = findViewById(R.id.seekBar7);

        hueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                adjustHSV(progress, saturationSeekBar.getProgress(), valueSeekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        saturationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                adjustHSV(hueSeekBar.getProgress(), progress, valueSeekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        valueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                adjustHSV(hueSeekBar.getProgress(), saturationSeekBar.getProgress(), progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void adjustHSV(int hue, int saturation, int value) {
        Mat hsvMat = new Mat();
        Imgproc.cvtColor(imageMat, hsvMat, Imgproc.COLOR_BGR2HSV);
        Core.add(hsvMat, new Scalar(hue, saturation, value), hsvMat);
        Imgproc.cvtColor(hsvMat, imageMat, Imgproc.COLOR_HSV2BGR);
        Bitmap bitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imageMat, bitmap);
        imageView.setImageBitmap(bitmap);
    }

}