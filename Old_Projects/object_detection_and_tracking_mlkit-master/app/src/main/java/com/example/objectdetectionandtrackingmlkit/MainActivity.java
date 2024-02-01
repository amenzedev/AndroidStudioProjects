package com.example.objectdetectionandtrackingmlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import com.google.mlkit.vision.objects.defaults.PredefinedCategory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Button capture_image;
    private ImageView imageView;
    private Bitmap imageBitmap;
    MyCanvas myCanvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capture_image =(Button) findViewById(R.id.button);
        imageView=(ImageView) findViewById(R.id.imageView);

        // Multiple object detection in static images

        InputStream bit=null;
        try {
            bit=getAssets().open("input.jpg");
            imageBitmap= BitmapFactory.decodeStream(bit);
            imageView.setImageBitmap(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bit!=null) {
                try {
                    bit.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ObjectDetectorOptions options =
                new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()  // Optional
                        .build();

        ObjectDetector objectDetector = ObjectDetection.getClient(options);

        capture_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view )
            {
                InputImage image = InputImage.fromBitmap(imageBitmap,0);
                objectDetector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<DetectedObject>>() {
                                    @Override
                                    public void onSuccess(List<DetectedObject> detectedObjects) {
                                        // Task completed successfully
                                        // ...

                                        System.out.println("*************\nSuccess\n****************************\n"+detectedObjects.size());

                                        //List<DetectedObject> results = new ArrayList<>();
                                        // [START read_results_default]
                                        // The list of detected objects contains one item if multiple
                                        // object detection wasn't enabled.
                                        for (DetectedObject detectedObject : detectedObjects) {
                                            //Rect boundingBox = detectedObject.getBoundingBox();
                                            //Integer trackingId = detectedObject.getTrackingId();
                                            String text = detectedObject.getLabels()+"";
                                            System.out.println(detectedObject.getLabels());
                                            Rect boundingBox=detectedObject.getBoundingBox();
                                            System.out.println(boundingBox.top);
                                            Bitmap resizedbitmap = Bitmap.createBitmap(imageBitmap,boundingBox.left,boundingBox.top,boundingBox.right-boundingBox.left,boundingBox.bottom-boundingBox.top);
                                            imageView.setImageBitmap(resizedbitmap);




                                        }
                                        // [END read_results_default]

                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...

                                        System.out.println("*************\nFailed\n****************************");
                                    }
                                });


            }

        });


    }
}