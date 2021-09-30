package com.example.internshiptask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CameraActivity extends AppCompatActivity {
    FloatingActionButton cameraButton,saveButton;
    ImageView imageView;
    ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private static final int PERMISSION_CAMERA_CODE=121;
    private static final String[] appPermission =
            {  Manifest.permission.CAMERA};
    OutputStream outputStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initViews();
        intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()==RESULT_OK)
                        {
                            if(result.getData()!=null){
                                Bundle bundle=result.getData().getExtras();
                                Bitmap bitmap= (Bitmap) bundle.get("data");
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    }
                });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               if(intent.resolveActivity(getPackageManager()) != null) {
                   intentActivityResultLauncher.launch(intent);
               }else{
                   Toast.makeText(CameraActivity.this, "Intent is not work.", Toast.LENGTH_SHORT).show();
               }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImageInInternalStorage();
            }
        });
    }

    private void saveImageInInternalStorage()
    {
        BitmapDrawable bitmapDrawable=(BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap=bitmapDrawable.getBitmap();

        File filepath= Environment.getExternalStorageDirectory();
        File dir=new File(filepath.getAbsolutePath()+"/Internship/");
        dir.mkdir();
        File file=new File(dir,System.currentTimeMillis()+".jpg");
        try {
            outputStream=new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        Toast.makeText(CameraActivity.this, "Image saved in internal storage", Toast.LENGTH_SHORT).show();
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission(PERMISSION_CAMERA_CODE);
    }

    private void checkPermission(int permissionCameraCode)
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,appPermission ,permissionCameraCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CAMERA_CODE && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(CameraActivity.this, "Permission granted..", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(CameraActivity.this, "Please allow the permission of camera...", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        cameraButton=findViewById(R.id.clickButton);
        saveButton=findViewById(R.id.saveButton);
        imageView=findViewById(R.id.imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.undoImage){
            undoImage();
            return true;
        }else if(item.getItemId()==R.id.cropImage){
            cropImage();
            return true;
        }else if (item.getItemId()==R.id.rotateImage){
            rotateImage();
            return true;
        }
        return false;
    }

    private void undoImage() {

    }
    private void cropImage(){
    }
    private void rotateImage(){
        Matrix matrix = new Matrix();
        imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
        matrix.postRotate((int) 20, imageView.getDrawable().getBounds().width() >> 8, imageView.getDrawable().getBounds().height() >> 8);
        imageView.setImageMatrix(matrix);
    }
}