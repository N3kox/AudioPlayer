package com.example.audioplayer.mineFragment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.example.audioplayer.R;

import DB.DBHelper;

public class musicListDetail extends Activity {

    private int listId;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list_detail);
        Intent intent = getIntent();
        listId = intent.getIntExtra("id",0);
        listName = intent.getStringExtra("name");
        test();
    }

    public void test(){
        DBHelper dbHelper = new DBHelper(this,"mineMusic",null,1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(DBHelper.SELECT_FROM_DETAIL + " where listId = ?", new String[]{String.valueOf(listId)});
        if(cursor != null){
            Log.d("#data","length:"+cursor.getCount());
            while(cursor.moveToNext()){
                Log.d("#data","0:"+String.valueOf(cursor.getInt(0)));
                Log.d("#data","1:"+String.valueOf(cursor.getInt(1)));
                Log.d("#data","2:"+cursor.getString(2));
                Log.d("#data","3:"+cursor.getString(3));
                Log.d("#data","4:"+cursor.getString(4));
            }
        }
        dbHelper.close();
    }
}
