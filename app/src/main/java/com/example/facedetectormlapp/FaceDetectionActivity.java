package com.example.facedetectormlapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Bundle;
import android.view.View;
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

    private ActivityFaceDetectionBinding binding;
    private GraphicOverlay graphicOverlay;
    private CameraSelector cameraSelector;
    private int facing;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFaceDetectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cameraExecutor= Executors.newSingleThreadExecutor();

        graphicOverlay=new GraphicOverlay(this);



        binding.buttonFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphicOverlay.clear();





                binding.buttonFront.setEnabled(false);
                binding.buttonBack.setEnabled(true);
                startCameraFront();
            }
        });


        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphicOverlay.clear();



                binding.buttonBack.setEnabled(false);
                binding.buttonFront.setEnabled(true);
                startCameraBack();
            }
        });







        // Select back camera as a default
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        // initiialize facing
        facing= CameraCharacteristics.LENS_FACING_BACK;
        binding.buttonBack.setEnabled(false);




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
            startCameraBack();
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
     * This function is responsible for the setup of the back camera preview and the image analyzer.
     */

    private void startCameraBack() {

        cameraSelector=CameraSelector.DEFAULT_BACK_CAMERA;
        facing=CameraCharacteristics.LENS_FACING_BACK;










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
                                    (float) binding.previewView.getHeight(),
                                    facing

                            ));


                    // Select back camera as a default
//                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

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






    /**
     * This function is responsible for the setup of the back camera preview and the image analyzer.
     */

    private void startCameraFront() {

    cameraSelector=CameraSelector.DEFAULT_FRONT_CAMERA;
    facing=CameraCharacteristics.LENS_FACING_FRONT;







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
                                    (float) binding.previewView.getHeight(),
                                    facing

                            ));


                    // Select back camera as a default
//                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

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