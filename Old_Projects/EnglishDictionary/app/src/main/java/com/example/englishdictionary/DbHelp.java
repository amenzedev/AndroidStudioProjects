package com.example.englishdictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DbHelp extends SQLiteOpenHelper {

    String dbName;
    Context context;
    String dbPath;

    String tableName = "entries";
    String EngCol = "word";

    public DbHelp(Context mcontext, String name, int version)
    {
        super(mcontext,name,null,version);
        this.context = mcontext;
        this.dbName = name;
        this.dbPath = "/data/data/com.example.englishdictionary/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void CheckDb(){
        SQLiteDatabase checkDb = null;
        try{
            String filepath = dbPath + dbName;
            checkDb =SQLiteDatabase.openDatabase(filepath, null,0);

        }catch (Exception e){}

        if(checkDb != null)
        {
            Log.d("checkDb", "Database already exists");
            checkDb.close();
        }
        else {
            CopyDatabase();

        }

    }

    public void CopyDatabase(){

        this.getReadableDatabase();

        try{
            InputStream is = context.getAssets().open(dbName);
            OutputStream os = new FileOutputStream(dbPath + dbName);
            byte[] buffer = new byte[1024];
            int len;
            while((len = is.read(buffer))>0)
            {
                os.write(buffer,0,len);
            }
            os.flush();
            is.close();
            os.close();

        }catch (Exception e){e.printStackTrace();}
        Log.d("output",context.getFilesDir().getAbsolutePath());
        Log.d("CopyDb", "Database Copied");

    }

    public void OpenDatabase(){
        String filePath =  dbPath + dbName;
        SQLiteDatabase.openDatabase(filePath,null,0);
    }

    public ArrayList<String> getEngWord(String query){
        ArrayList<String> engList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        //Cursor cursor = sqLiteDatabase.query(tableName,new String[]{EngCol},EngCol,new String[]{EngCol +" LIKE "+query+"%"},null,null,null);

        Cursor cursor = sqLiteDatabase.query(
                tableName,
                new String[]{EngCol},
                    EngCol + " LIKE ?",
                new String[]{query + "%"},
                    null,null,EngCol
        );

        int index = cursor.getColumnIndex(EngCol);
        while(cursor.moveToNext())
        {
            engList.add(cursor.getString(index));
        }
        sqLiteDatabase.close();
        cursor.close();
        return engList;
    }

    public String GetAns(String word)
    {
        SQLiteDatabase sqLiteDatabase= this.getReadableDatabase();
        String ans = "";
        word = word.replace("'","''");
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM "+tableName + " WHERE word = '"+word+"'",null);

        int i = 1;
        while(cursor.moveToNext())
        {
            int index = cursor.getColumnIndex("definition");
            ans += i+++ ". "+cursor.getString(index).replace("\n"," ")+"\n\n";
        }
        //Log.d("ans",word);
        return ans;
    }
}
