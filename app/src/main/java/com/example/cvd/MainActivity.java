package com.example.cvd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cvd.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "opencv";

    static Uri currentPhotoUri;




    PhotoBookDB db;
    ArrayList<PhotoBook> photoList = new ArrayList<>();
    RecyclerView recyclerView;
    PhotoBookAdapter adapter;
    TextView noDataText;

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
            try {
                camera();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        } else if (itemId == R.id.btn_gallery) {
            gallery();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    //메뉴에서 갤러리 버튼 선택 시 수행.
    private void gallery() {
        Intent galleryintent = new Intent(Intent.ACTION_PICK);
        galleryintent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activityResultPicture.launch(galleryintent);
    }
    /*
    //메뉴에서 카메라 버튼 선택 시 수행.
    private void camera() throws IOException {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity((getPackageManager())) != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                activityResultCamera.launch(cameraIntent);
            }
        }
    */

    private void camera() throws IOException {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                activityResultCamera.launch(cameraIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("images");
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoUri = FileProvider.getUriForFile(this,
                "com.example.cvd.fileprovider",
                image);
        return image;
    }


    ActivityResultLauncher<Intent> activityResultPicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK /*&& result.getData() != null*/) {

                        Intent intent = result.getData();
                        Uri uri = intent.getData();

                        //로깅
                        Log.i("checking", String.valueOf(uri));


                        //edit 화면으로 넘어가는 코드
                        Intent editIntent = new Intent(getApplicationContext(), edit.class);
                        assert uri != null;
                        editIntent.putExtra("imageUri", uri.toString()); //imageUri.toString
                        startActivity(editIntent);
                    }
                }
            });


    ActivityResultLauncher<Intent> activityResultCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK /*&& result.getData() != null*/) {

                    /*    Intent intent = result.getData();
                        Uri uri = intent.getData();

                        //로깅
                        Log.i("checking", String.valueOf(uri));


                        //add 화면으로 넘어가는 코드
                        Intent addIntent = new Intent(getApplicationContext(), CameraFile.class);
                        assert uri != null;
                        addIntent.putExtra("imageUri", uri.toString()); //imageUri.toString
                        startActivity(addIntent);

                     */
                        Log.i("checking", String.valueOf(currentPhotoUri));
                        Intent addIntent = new Intent(getApplicationContext(), edit.class);
                        addIntent.putExtra("imageUri", currentPhotoUri.toString());
                        startActivity(addIntent);
                    }
                }
            });


    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.menu.menu);
        setContentView(R.layout.activity_main);

        // 권한 확인 및 요청
        getPermission();

        //데이터 유무 텍스트
        noDataText = findViewById(R.id.noData_text);
        //리스트 보여줄 화면
        recyclerView = findViewById(R.id.recyclerView);
        //어뎁터
        adapter = new PhotoBookAdapter(MainActivity.this);
        //어뎁터 등록
        recyclerView.setAdapter(adapter);
        //레이아웃 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        //DB 생성
        db = new PhotoBookDB(MainActivity.this);
        //데이터 가져오기
        storeDataInArrays();

    }

    /**
     * 데이터 가져오기
     */
    void storeDataInArrays(){

        Cursor cursor = db.readAllData();

        if(cursor.getCount() == 0){
            noDataText.setVisibility(noDataText.VISIBLE);
        }else{

            noDataText.setVisibility(noDataText.GONE);

            while (cursor.moveToNext()){

                PhotoBook photo = new PhotoBook(cursor.getString(0),
                        cursor.getBlob(1));

                //데이터 등록
                photoList.add(photo);
                adapter.addItem(photo);

                //적용
                adapter.notifyDataSetChanged();
            }
        }
    }



    @SuppressLint("NewApi")
    void getPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1000);
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
