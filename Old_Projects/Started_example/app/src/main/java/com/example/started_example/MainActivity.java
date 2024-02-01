package com.example.started_example;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.started_example.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {


    private ImageView imageView;
    private Bitmap imageBitmap;
    private Button button;
    private TextView text;
    private ByteBuffer byteBuffer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button);
        text = (TextView) findViewById(R.id.textView);
        InputStream bit=null;
        try {
            bit=getAssets().open("face.jpg");
            imageBitmap= BitmapFactory.decodeStream(bit);
            int size = imageBitmap.getRowBytes() * imageBitmap.getHeight();
            System.out.println(size);
            byteBuffer = ByteBuffer.allocate(size);
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Model model = Model.newInstance(view.getContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 200, 200, 3}, DataType.FLOAT32);
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                    System.out.println(outputFeature0);

                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }


            }
        });

    }
}