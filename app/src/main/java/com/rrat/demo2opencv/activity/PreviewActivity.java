package com.rrat.demo2opencv.activity;


import com.rrat.demo2opencv.R;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;

public class PreviewActivity extends CameraActivity {


    private static final String TAG = "PreviewActivity";
    private JavaCameraView mOpenCvCameraView;

    Mat mRgba;

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                mOpenCvCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mOpenCvCameraView = findViewById(R.id.preview_camera_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);



        mOpenCvCameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

                mRgba = inputFrame.rgba();
                Mat mRgbaT = mRgba.t();
                Core.flip(mRgba.t(), mRgbaT, 1);
                Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());
                return mRgbaT;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        }else{
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }
}