package com.example.face_detector_android_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.face_detector_android_application.ml.Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;


import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button capture_image;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private boolean recognize_image;
    private int counter;
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        capture_image =(Button) findViewById(R.id.button);
        imageView=(ImageView) findViewById(R.id.imageView3);
        output =(TextView) findViewById(R.id.textView);
        recognize_image=true;
        counter = 0;
        InputStream bit=null;
        try {
            bit=getAssets().open("1.jpg");
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

        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

        capture_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view )
            {

                if(recognize_image)
                {
                    InputImage image = InputImage.fromBitmap(imageBitmap, 0);

                    detector.process(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<Face>>() {
                                        @Override
                                        public void onSuccess(List<Face> faces) {
                                            // Task completed successfully
                                            // ...
                                            for (Face face : faces) {
                                                Rect bounds = face.getBoundingBox();
                                                float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                                float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
                                                Bitmap resizedbitmap = Bitmap.createBitmap(imageBitmap,bounds.left,bounds.top,bounds.right-bounds.left,bounds.bottom-bounds.top);
                                                imageView.setImageBitmap(resizedbitmap);

                                                try {
                                                    Model model = Model.newInstance(view.getContext());

                                                    // Creates inputs for reference.
                                                    TensorImage image = TensorImage.fromBitmap(resizedbitmap);

                                                    // Runs model inference and gets result.
                                                    Model.Outputs outputs = model.process(image);
                                                    List<Category> probability = outputs.getProbabilityAsCategoryList();
                                                    double max = 0;
                                                    int index = 0;
                                                    for (int i = 0; i < probability.size(); i++) {
                                                        double score = probability.get(i).getScore();
                                                        if(score>max)
                                                        {
                                                            max = score;
                                                            index = i;
                                                        }

                                                    }
                                                    //index =(int) Integer.parseInt(probability.get(index).getLabel());
                                                    if(index==1)
                                                    output.setText("Vin Diesel");
                                                    else output.setText("Denzel");

                                                    // Releases model resources if no longer used.
                                                    model.close();
                                                } catch (IOException e) {
                                                    // TODO Handle the exception
                                                }
                                            }
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });
                    recognize_image=false;
                    capture_image.setText("Choose another image");

                }
                else{
                    counter++;
                    InputStream bit=null;
                    try {
                        String x ="";
                        if(counter%2==0)
                            x="1.jpg";
                        else x="2.jpg";

                        bit=getAssets().open(x);
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
                    capture_image.setText("Recognize face");
                    recognize_image=true;
                    output.setText("");


                }


            }

        });
    }
}