package com.example.printeddigit_recognizer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.printeddigit_recognizer.ml.PrintedDigits;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button capture_image;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private TextView textView;
    private int solving_Flag;
    private int[][] sudoku=new int[9][9];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        capture_image =(Button) findViewById(R.id.button);
        imageView=(ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        solving_Flag =0;

        capture_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(solving_Flag == 0) {
                    //dispatchTakePictureIntent();     // takes the picture
                    InputStream bit=null;
                    try {
                        bit=getAssets().open("sudoku.jpeg");
                        Bitmap bitmap= BitmapFactory.decodeStream(bit);
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
                    capture_image.setText("Solve");  //change the button name to Solve
                    solving_Flag =1;                 // change the flag to solving mode
                }
                else
                {
                    //Capture the picture Inputs
                    //imageBitmap=Bitmap.createBitmap(imageBitmap, 0,0,imageBitmap.getWidth(), imageBitmap.getWidth());
                    //imageView.setImageBitmap(imageBitmap);
                    capture_picture_inputs(view);
                    print_sudoku();

                    /*
                    //solve sudoku
                    if(false)//no solution for the sudoku
                    {
                        textView.setText("No Solution!!!");
                    }
                    else //if solution is found
                    {
                        //textView.setText("");
                    }
                    solving_Flag = 0;

                    capture_image.setText("Capture Sudoku");*/


                }

            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }catch(ActivityNotFoundException e)
        {
            System.out.println("Camera not found!!!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            imageBitmap=Bitmap.createBitmap(imageBitmap, 0,0,imageBitmap.getWidth(), imageBitmap.getWidth());
            imageView.setImageBitmap(imageBitmap);
            //imageBitmap=Bitmap.createBitmap(imageBitmap, 0,0,imageBitmap.getWidth(), imageBitmap.getHeight()-200);

        }
    }

    private int image_analyzer(Context context, Bitmap bitmap_image) {
        double max = 0;
        int index = 0;
        try{
            PrintedDigits model = PrintedDigits.newInstance(context);

            //Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(bitmap_image);

            //Runs model inference and get result.
            PrintedDigits.Outputs  outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();
            System.out.println(Arrays.toString(probability.toArray()));

            for (int i = 0; i < probability.size(); i++) {
                double score = probability.get(i).getScore();
                if(score>max)
                {
                    max = score;
                    index = i;
                }

            }
            System.out.println(index+"   "+max );



            //Releases model resources if no longer used
            model.close();
            return (int) Integer.parseInt(probability.get(index).getLabel());


        }catch (IOException e)
        {
            //TODO handle the exception
            System.out.println("model didn't work!!!");

        }

        return index;


    }

    //capture_picture_inputs
    public void capture_picture_inputs(View view)
    {

        int delta_image_width=imageBitmap.getWidth()/9;
        int delta_image_height= imageBitmap.getHeight()/9;


        for (int i=0;i<6;i++)
        {
            for(int j=0;j<6;j++)
            {
                int start_x=delta_image_width*i;
                int start_y=delta_image_height*j;
                int end_x=delta_image_width*(i+1);
                int end_y=delta_image_height*(j+1);
                System.out.println("( "+start_x+ " , "+start_y+") up to ( "+end_x+" , "+end_y+" )" + "point at [ "+i+" , "+j+" ] dimensions of widht and height: "+imageBitmap.getWidth()+" by "+imageBitmap.getHeight());
                Bitmap resizedbitmap1=Bitmap.createBitmap(imageBitmap, start_x+1,start_y+1,delta_image_width-1, delta_image_width-1);//start_x,start_y
                imageView.setImageBitmap(resizedbitmap1);
                int result = image_analyzer(view.getContext(),resizedbitmap1);
                textView.setText("the found number is : "+result);
                sudoku[i][j]=result;

            }

        }


    }

    public void print_sudoku()
    {
        for (int i=0;i<9;i++)
        {
            for(int j=0;j<9;j++)
            {
                System.out.print(sudoku[i][j]+" ");
            }
            System.out.println();
        }
    }


}