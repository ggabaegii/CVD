package com.example.cvd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
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

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "opencv";

    static File currentPhotoFile;
    static Uri currentPhotoUri;
    static String currentPhotoPath;
    static String currentPhotoFileName;




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

    //메뉴에서 카메라 버튼 선택 시 수행.
    private void camera() throws IOException {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity((getPackageManager())) != null) {

            File imageFile = createImageFile();
            if (imageFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                activityResultPicture.launch(cameraIntent);
            }
        }
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



    //이미지파일 생성
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File imagePath = getExternalFilesDir("images");

        File newFile = File.createTempFile(imageFileName, ".jpg", imagePath);

        currentPhotoFile = newFile;
        currentPhotoFileName = newFile.getName();
        currentPhotoPath = newFile.getAbsolutePath();

        try {
            currentPhotoUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    newFile);
        } catch (Exception ex) {
            Log.d("FileProvider", ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }

        return newFile;
    }


    //갤러리에 이미지 파일 생성
    private Uri galleryAddPic(Uri srcImageFileUri ,String srcImageFileName) {
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, srcImageFileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/MyImages"); // 두개의 경로[DCIM/ , Pictures/]만 가능함 , 생략시 Pictures/ 에 생성됨
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 1); //다른앱이 파일에 접근하지 못하도록 함(Android 10 이상)
        Uri newImageFileUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        try {
            AssetFileDescriptor afdInput = contentResolver.openAssetFileDescriptor(srcImageFileUri, "r");
            AssetFileDescriptor afdOutput = contentResolver.openAssetFileDescriptor(newImageFileUri, "w");
            FileInputStream fis = afdInput.createInputStream();
            FileOutputStream fos = afdOutput.createOutputStream();

            byte[] readByteBuf = new byte[1024];
            while(true){
                int readLen = fis.read(readByteBuf);
                if (readLen <= 0) {
                    break;
                }
                fos.write(readByteBuf,0,readLen);
            }

            fos.flush();
            fos.close();
            afdOutput.close();

            fis.close();
            afdInput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        contentValues.clear();
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0); //다른앱이 파일에 접근할수 있도록 함
        contentResolver.update(newImageFileUri, contentValues, null, null);
        return newImageFileUri;
    }





    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.menu.menu);

        setContentView(R.layout.activity_main);
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
