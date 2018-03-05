package info.apps.sub.takephoto;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Permissions;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
  private static final int REQUEST_TAKE_PHOTO = 1;

  Button btnTakePhoto;
  ImageView ivPreview;

  String mCurrentPhotoPath;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initInstances();
  }

  private void initInstances() {
    btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
    ivPreview = (ImageView) findViewById(R.id.ivPreview);

    btnTakePhoto.setOnClickListener(this);
  }

  /////////////////////
  // OnClickListener //
  /////////////////////

  @Override
  public void onClick(View view) {

    if (view == btnTakePhoto) {
      boolean result = Utility.checkPermission(MainActivity.this);
      if(result)
          try {
            dispatchTakePictureIntent();
          } catch (IOException e) {
            e.printStackTrace();
          }

    }
  }

  ////////////
  // Camera //
  ////////////
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
      // Show the thumbnail on ImageView
      Uri imageUri = Uri.parse(mCurrentPhotoPath);
      File file = new File(imageUri.getPath());
      try {
        InputStream ims = new FileInputStream(file);
        ivPreview.setImageBitmap(BitmapFactory.decodeStream(ims));
      } catch (FileNotFoundException e) {
        return;
      }

      // ScanFile so it will be appeared on Gallery
      MediaScannerConnection.scanFile(MainActivity.this,
          new String[]{imageUri.getPath()}, null,
          new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
            }
          });
    }
  }



  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DCIM), "Camera");
    File image = File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    );

    // Save a file: path for use with ACTION_VIEW intents
    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
    return image;
  }

  private void dispatchTakePictureIntent() throws IOException {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      // Create the File where the photo should go
      File photoFile = null;
      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        // Error occurred while creating the File
        return;
      }
      // Continue only if the File was successfully created
      if (photoFile != null) {
        Uri photoURI;
        if(Build.VERSION.SDK_INT >=24)
        {photoURI = FileProvider.getUriForFile(MainActivity.this,
            BuildConfig.APPLICATION_ID + ".provider",
            createImageFile());
        } else
          {
            photoURI = Uri.fromFile(createImageFile());
          }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
      }
    }
  }
}
