package com.example.cvd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cvd.databinding.ActivityMainBinding;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;





public class MainActivity extends AppCompatActivity {

    private static final String TAG = "opencv";
    private Mat matInput;
    private Mat matResult;
    Button bt_gallery,bt_camera;
    ImageView selectedImage;
    ImageView imageView;
    Bitmap bitmap;
    Uri uri;
    int SELECT_CODE = 100, CAMERA_CODE=101;

    private ActivityMainBinding binding;



    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /* 카메라, 갤러리 연결 코드 추가*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int itemId = item.getItemId();
        if (itemId == R.id.btn_camera) {
            camera();
            return true;
        } else if (itemId == R.id.btn_gallery) {
            gallery();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK /*&& result.getData() != null*/) {

                        Intent intent = result.getData();
                        Uri uri = intent.getData();

                        Log.i("checking", String.valueOf(uri));




                        //edit 화면으로 넘어가는 코드
                        Intent editIntent = new Intent(getApplicationContext(), edit.class);

                        assert uri != null;
                        editIntent.putExtra("imageUri", uri.toString()); //imageUri.toString
                        startActivity(editIntent);



                    }
                }
            });

    ActivityResultLauncher<Intent> activityResultPicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    //결과 OK , 데이터 null 아니면
                    if( result.getResultCode() == RESULT_OK && result.getData() != null){

                        Bundle extras = result.getData().getExtras();

                        bitmap = (Bitmap) extras.get("data");

                        //imageView.setImageBitmap(bitmap);


                        Intent editIntent = new Intent(getApplicationContext(), edit.class);

                        assert uri != null;
                        editIntent.putExtra("imageUri", uri.toString()); //imageUri.toString
                        startActivity(editIntent);
                    }
                }
            });

    public static byte[] imageViewToByte(ImageView image){

        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return  byteArray;
    }







    private void gallery() {
        Intent galleryintent = new Intent(Intent.ACTION_PICK);
        galleryintent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        galleryLauncher.launch(galleryintent);
    }

    private void camera() {
        bt_camera = findViewById(R.id.btn_camera);
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bt_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        activityResultPicture.launch(intent);
                    }

                });
        }
    });
    }






    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.menu.menu);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(OpenCVLoader.initDebug()) Log.d("LOADED","success");
        else Log.d("LOADED", "err");

        getPermission();
        imageView = findViewById(R.id.imageView);

        // Example of a call to a native method
        //TextView tv = binding.sampleText;
        //tv.setText(stringFromJNI());
    }



    @SuppressLint("NewApi")
    void getPermission(){
        /*int CameraPermission = checkSelfPermission(android.Manifest.permission.CAMERA);
        int ReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int WritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (CameraPermission == PackageManager.PERMISSION_DENIED || ReadPermission == PackageManager.PERMISSION_DENIED) {
            if (WritePermission == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            }
            return;
        } */

        if(checkSelfPermission(android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if(requestCode == 1000) {
            boolean check_result = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result == true) {

            }

            else {
                finish();;
            }
        }
        */

        /*if(requestCode==102&&grantResults.length>0){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                getPermission();
            }
        }

        if(requestCode==103&&grantResults.length>0){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                getPermission();
            }
        }

        if(requestCode==104&&grantResults.length>0){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                getPermission();
            }
        }

         */
    }

    //public native String stringFromJNI();


}
