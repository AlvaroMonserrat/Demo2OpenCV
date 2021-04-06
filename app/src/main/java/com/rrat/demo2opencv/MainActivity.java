package com.rrat.demo2opencv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    String currentPhotoPath;
    private ImageView imageView;
    private TextView heightText;
    private TextView widthText;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenCVLoader.initDebug();

        this.imageView = findViewById(R.id.image_cv);
        this.heightText = findViewById(R.id.text_height);
        this.widthText = findViewById(R.id.text_width);

        Button photoButton = findViewById(R.id.button);
        photoButton.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
            else
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                }catch (IOException exception){
                    Log.e("ErrorFile", "Ha ocurrido un error creando el archivo.");
                }

                if(photoFile != null){

                    Uri photoUri = FileProvider.getUriForFile(this, "com.rrat.fileprovider", photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                }

            }
        });
    }


    Bitmap makeGray(Bitmap bitmap ){

        // Create OpenCV mat object and copy content from bitmap
        Mat mat = new Mat();

        Utils.bitmapToMat(bitmap, mat);

        // Convert to grayscale
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

        // Make a mutable bitmap to copy grayscale image
        Bitmap grayBitmap = bitmap.copy(bitmap.getConfig(), true);
        Utils.matToBitmap(mat, grayBitmap);

        return grayBitmap;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            galleryAddPic();
           // Bitmap photo = (Bitmap) data.getExtras().get("data");

            //heightText.setText(Integer.toString(photo.getHeight()));
           // widthText.setText(Integer.toString(photo.getWidth()));

            //Bitmap result = makeGray(photo);

            // imageView.setImageBitmap(result);


        }
    }

    private File createImageFile() throws IOException{

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        //
        currentPhotoPath = image.getAbsolutePath();
        Log.i("MainActivity", "Path image: " + currentPhotoPath);
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);

        if(f.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            Log.i("MainActivity", "Width image: " + Integer.toString(bitmap.getWidth()));
            Log.i("MainActivity", "Width image: " + Integer.toString(bitmap.getHeight()));

            Bitmap result = makeGray(bitmap);

            imageView.setImageBitmap(result);

        }

        notifyMediaStoreScanner(f);
        //Uri contentUri = Uri.fromFile(f);
        //mediaScanIntent.setData(contentUri);
        //this.sendBroadcast(mediaScanIntent);
    }

    public final void notifyMediaStoreScanner(final File file) {
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            this.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}