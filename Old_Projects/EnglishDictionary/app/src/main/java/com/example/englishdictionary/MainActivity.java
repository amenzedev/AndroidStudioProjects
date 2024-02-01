package com.example.englishdictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    AutoCompleteTextView autoCompleteTextView;
    TextView wordtxt, anstxt;

    DbHelp dbHelp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoCompleteTextView = findViewById(R.id.autotxt);
        wordtxt = findViewById(R.id.txtword);
        anstxt = findViewById(R.id.txtans);

        dbHelp = new DbHelp(this, "Dictionary.db",1);
        try{
            dbHelp.CheckDb();
            dbHelp.OpenDatabase();
        }catch (Exception e){}


        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length()==1){
                    ArrayList<String> newList = dbHelp.getEngWord(s.toString());
                    autoCompleteTextView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,newList));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String word = (String) adapterView.getItemAtPosition(position);
                getAns(word);
            }
        });
    }

    public void getAns(String word){
        String ans = dbHelp.GetAns(word);
        wordtxt.setText(word);
        anstxt.setText(ans);

    }


}