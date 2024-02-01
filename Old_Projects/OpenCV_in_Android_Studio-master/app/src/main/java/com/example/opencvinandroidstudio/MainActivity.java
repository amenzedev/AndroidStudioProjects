package com.example.opencvinandroidstudio;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(!Python.isStarted())
            Python.start(new AndroidPlatform(this));

        Python py = Python.getInstance();
        PyObject pyobj = py.getModule("script");
        TextView tv = (TextView)findViewById(R.id.textview);
        imageView=(ImageView) findViewById(R.id.imageView);

        //bitmap = BitmapFactory.decodeFile("../res/drawable/a.jpg");
        InputStream bit=null;

        try {
            bit=getAssets().open("a.jpg");
            Bitmap bitmap=BitmapFactory.decodeStream(bit);
            imageView.setImageBitmap(bitmap);
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

        //imageView.setImageBitmap(bitmap);

        PyObject obj = pyobj.callAttr("find_puzzle",bitmap);
        tv.setText(obj.toString());

    }
}