package com.example.facedetectormlapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.facedetectormlapp.databinding.ActivityFaceDetectionBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FaceDetectionActivity extends AppCompatActivity {

    private ExecutorService cameraExecutor;
    private BoxView boxView;
    private ActivityFaceDetectionBinding binding;
    private GraphicOverlay graphicOverlay;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFaceDetectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cameraExecutor= Executors.newSingleThreadExecutor();
        boxView=new BoxView(this);
        graphicOverlay=new GraphicOverlay(this);

        addContentView(graphicOverlay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        checkCameraPermission();




    }

    /**
     * This function is responsible to request the required CAMERA permission
     */
    private void checkCameraPermission() {
        try {
            String[] requiredPermissions = new String[]{Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, requiredPermissions, 0);
        } catch (IllegalArgumentException e) {
            checkIfCameraPermissionIsGranted();
        }



    }




    /**
     * This function is executed once the user has granted or denied the missing permission
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkIfCameraPermissionIsGranted();

    }






    /**
     * This function will check if the CAMERA permission has been granted.
     * If so, it will call the function responsible to initialize the camera preview.
     * Otherwise, it will raise an alert.
     */

    private void checkIfCameraPermissionIsGranted() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted: start the preview
            startCamera();
        } else {
            // Permission denied
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Permission required")
                    .setMessage("This application needs to access the camera to process barcodes")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            checkCameraPermission();

                        }
                    })
                    .setCancelable(false)
                    .show()  ;







        }
    }

    /**
     * This function is responsible for the setup of the camera preview and the image analyzer.
     */

    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {

                try {
                    ProcessCameraProvider cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                    // Preview
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                    ImageAnalysis imageAnalyzer= new ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();
                    imageAnalyzer.setAnalyzer(cameraExecutor,
                            new FaceAnalyser(
                                    FaceDetectionActivity.this,
                                    graphicOverlay,
                                    (float) binding.previewView.getWidth(),
                                    (float) binding.previewView.getHeight()

                            ));


                    // Select back camera as a default
                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll();

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                            FaceDetectionActivity.this, cameraSelector, preview, imageAnalyzer
                    );




                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        },ContextCompat.getMainExecutor(this));



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }









}