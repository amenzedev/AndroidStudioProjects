package com.example.ps_classifier;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ps_classifier.ml.Model;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button randomize;
    private TextView detection_result;
    private ImageView imageView;
    private Bitmap[] bitmaps = new Bitmap[2];
    String images[]={"console.jpg","controller.jpg"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        randomize = (Button) findViewById(R.id.button);
        detection_result= (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);

        //randomize.setText("hello");

        readImages();

        imageView.setImageBitmap(bitmaps[0]);

        setButtonClickListener();


    }

    // read images from the asset folder

    void readImages()
    {
        for(int i=0;i<bitmaps.length;i++)
        {
            InputStream bit = null;
            try {
                bit = getAssets().open(images[i]);
                bitmaps[i]= BitmapFactory.decodeStream(bit);
            }catch(IOException e){
                e.printStackTrace();
            }
            finally{
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
        }
    }


    void classifyImage(Bitmap bitmap)
    {
        try {
            Model model = Model.newInstance(this);
            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(bitmap);
            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();
            System.out.println(probability);
            //choose the maximum value from the list
            double max =0;
            int index = 0;
            for (int i = 0; i < probability.size() ; i++)
            {
                double score = probability.get(i).getScore();
                if(score > max)
                {
                    max = score;
                    index = i;
                }
            }
            //chose the highest score
            System.out.println(probability.get(index).getLabel());
            detection_result.setText(probability.get(index).getLabel());
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    void setButtonClickListener()
    {
        randomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int random_image=(int)  (Math.random()*2);
                System.out.println("*************\n"+random_image+"\n**************");
                imageView.setImageBitmap(bitmaps[random_image]);
                classifyImage(bitmaps[random_image]);

            }
        });
    }





}