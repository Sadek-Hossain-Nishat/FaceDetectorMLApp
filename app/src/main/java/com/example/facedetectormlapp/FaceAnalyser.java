package com.example.facedetectormlapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
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


    /**
     * This parameters will handle preview box scaling
     */
    private float scaleX = 1;
    private float scaleY = 1;

    public FaceAnalyser(Context context, GraphicOverlay graphicOverlay, float previewViewWidth, float previewViewHeight)
                        {
        this.context = context;
        this.graphicOverlay = graphicOverlay;
        this.previewViewWidth = previewViewWidth;
        this.previewViewHeight = previewViewHeight;

    }


    private float translateX(float x){
        return  x * scaleX;
    }


    private float translateY(float y){
        return  y * scaleY;
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
                                        FaceContourGraphic faceGraphic = new FaceContourGraphic(graphicOverlay);
                                        graphicOverlay.add(faceGraphic);
                                        faceGraphic.updateFace(face);
                                    }
                                    showToast("Face detected successfully");

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });


        }

        image.close();

    }


    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }











}


/***

 if (!faces.isEmpty()) {
 for (Face face : faces) {
 // Handle received faces...
 Toast.makeText(
 context,
 "Value: " + face.getTrackingId(),
 Toast.LENGTH_LONG
 )
 .show();
 // Update bounding rect
 boxView.setRect(adjustBoundingRect(Objects.requireNonNull(face.getBoundingBox())));
 }
 } else {
 // Remove bounding rect
 boxView.setRect(new RectF());
 }




 ***/