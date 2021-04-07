package com.rrat.demo2opencv.utils;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImagesOpenCVUtils {

    public static Bitmap makeGray(Bitmap bitmap ){

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

    public static Bitmap addBlur(Bitmap bitmap ){

        // Create OpenCV mat object and copy content from bitmap
        Mat mat = new Mat();

        Utils.bitmapToMat(bitmap, mat);

        // Convert to grayscale
        //Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

        Imgproc.blur(mat, mat, new Size(20, 20));

        // Make a mutable bitmap to copy blur image
        Bitmap blurBitmap = bitmap.copy(bitmap.getConfig(), true);
        Utils.matToBitmap(mat, blurBitmap);

        return blurBitmap;
    }

    public static Bitmap edgeDetectionCanny(Bitmap bitmap ){

        // Create OpenCV mat object and copy content from bitmap
        Mat mat = new Mat();

        Utils.bitmapToMat(bitmap, mat);

        // Convert to grayscale
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

        Imgproc.Canny(mat, mat, 100, 200);

        // Make a mutable bitmap to copy blur image
        Bitmap cannyBitmap = bitmap.copy(bitmap.getConfig(), true);
        Utils.matToBitmap(mat, cannyBitmap);

        return cannyBitmap;
    }

}

