package info.apps.sub.takephoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;

public class SelectImageActivity extends AppCompatActivity implements ImageInputHelper.ImageActionListener {

  private ImageInputHelper imageInputHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_image);

    imageInputHelper = new ImageInputHelper(this);
    imageInputHelper.setImageActionListener(this);

    findViewById(R.id.btn_gal).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        imageInputHelper.selectImageFromGallery();
      }
    });

    findViewById(R.id.btn_cam).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        imageInputHelper.takePhotoWithCamera();
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    imageInputHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onImageSelectedFromGallery(Uri uri, File imageFile) {
    // cropping the selected image. crop intent will have aspect ratio 16/9 and result image
    // will have size 800x450
    imageInputHelper.requestCropImage(uri, 800, 450, 16, 9);
  }

  @Override
  public void onImageTakenFromCamera(Uri uri, File imageFile) {
    // cropping the taken photo. crop intent will have aspect ratio 16/9 and result image
    // will have size 800x450
    imageInputHelper.requestCropImage(uri, 800, 450, 16, 9);
  }

  @Override
  public void onImageCropped(Uri uri, File imageFile) {
    try {
      // getting bitmap from uri
      Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

      // showing bitmap in image view
      ((ImageView) findViewById(R.id.iv)).setImageBitmap(bitmap);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
