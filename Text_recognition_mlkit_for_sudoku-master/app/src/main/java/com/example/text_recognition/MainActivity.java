package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Button check;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        check = (Button) findViewById(R.id.button);



        InputStream bit = null;
        try{
            bit = getAssets().open("input.jpeg");
            imageBitmap = BitmapFactory.decodeStream(bit);
            imageView.setImageBitmap(imageBitmap);

        }catch(IOException e)
        {
            e.printStackTrace();
        }finally{
            if(bit!=null)
            {
                try{
                    bit.close();
                }catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                Task<Text> result =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text visionText) {
                                        // Task completed successfully
                                        // ...
                                        System.out.println("image processed successfully");
                                        for (Text.TextBlock block : visionText.getTextBlocks()) {
                                            Rect boundingBox = block.getBoundingBox();
                                            Point[] cornerPoints = block.getCornerPoints();
                                            String text = block.getText();

                                            for (Text.Line line: block.getLines()) {
                                                // ...
                                                String lineText = line.getText();
                                                Point[] lineCornerPoints = line.getCornerPoints();
                                                Rect lineFrame = line.getBoundingBox();
                                                for (Text.Element element: line.getElements()) {
                                                    // ...
                                                    String elementText = element.getText();
                                                    Point[] elementCornerPoints = element.getCornerPoints();
                                                    Rect elementFrame = element.getBoundingBox();
                                                    System.out.println(elementText);
                                                }
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
                                                System.out.println("Task failed!!!");
                                            }
                                        });


            }

        });
    }
}