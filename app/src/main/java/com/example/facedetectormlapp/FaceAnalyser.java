package com.example.facedetectormlapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.CameraCharacteristics;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

public class FaceAnalyser implements ImageAnalysis.Analyzer {
    private Context context;
    private GraphicOverlay graphicOverlay;
    private float previewViewWidth;
    private float previewViewHeight;

    private int facing;


    /**
     * This parameters will handle preview box scaling
     */
    private float scaleX = 1;
    private float scaleY = 1;

    public FaceAnalyser(Context context, GraphicOverlay graphicOverlay, float previewViewWidth, float previewViewHeight, int facing)
                        {
        this.context = context;
        this.graphicOverlay = graphicOverlay;
        this.previewViewWidth = previewViewWidth;
        this.previewViewHeight = previewViewHeight;
        this.facing=facing;

    }


















    @Override
    public void analyze(@NonNull ImageProxy image) {

        @SuppressLint("UnsafeOptInUsageError") Image img=image.getImage();
        if (img!=null){
            // Update scale factors
            scaleX=previewViewWidth/((float) img.getHeight());
            scaleY=previewViewHeight/((float) img.getWidth()) ;



            InputImage inputImage=InputImage.fromMediaImage(img,image.getImageInfo().getRotationDegrees());

            // Process image searching for barcodes
            // High-accuracy landmark detection and face classification
            FaceDetectorOptions options =
                    new FaceDetectorOptions.Builder()
                            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                            .build();

            FaceDetector detector = FaceDetection.getClient(options);

            detector.process(inputImage)
                    .addOnSuccessListener(
                            new OnSuccessListener<List<Face>>() {
                                @Override
                                public void onSuccess(List<Face> faces) {


                                    // Task completed successfully
                                    if (faces.size() == 0) {
                                        showToast("No face found");
                                        graphicOverlay.clear();
                                        return;

                                    }
                                    graphicOverlay.clear();
                                    for (int i = 0; i < faces.size(); ++i) {
                                        Face face = faces.get(i);
                                        graphicOverlay.setScale(scaleX,scaleY);
                                        graphicOverlay.setCameraInfo(previewViewWidth,previewViewHeight, facing);
                                        FaceContourGraphic faceGraphic = new FaceContourGraphic(graphicOverlay);
                                        graphicOverlay.add(faceGraphic);
                                        faceGraphic.updateFace(face);
                                    }
                                    showToast("Face detected successfully");

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                private static final String TAG ="face" ;

                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    e.printStackTrace();
                                    Log.e(TAG, "onFailure: "+e.getMessage());
                                }
                            });


        }

        image.close();

    }


    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }











}


