package com.example.audioplayer.mineFragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.audioplayer.MainActivity;
import com.example.audioplayer.R;
import com.example.audioplayer.slideView.MusicListAdapter;
import com.example.audioplayer.slideView.MusicListView;

import java.util.ArrayList;

import DB.DBHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment {

    DBHelper dbHelper;
    Fragment that = this;
    Button newMusicList;
    private MusicListView musicListView;
    private MusicListAdapter musicListAdapter;
    private ArrayList<String> listId;
    private ArrayList<String> listName;

    public MineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        initView(view);
        return view;
    }

    public void initView(View view){
        newMusicList = view.findViewById(R.id.mine_new_music_list);
        musicListView = view.findViewById(R.id.mine_music_list);
        newMusicList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(getContext());
                et.setSingleLine(true);
                et.setTextAlignment(EditText.TEXT_ALIGNMENT_TEXT_END);
                new AlertDialog.Builder(getActivity()).setTitle("请输入新歌单名称")
                        .setIcon(R.drawable.blackhole)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(et.getText().toString()!= null)
                                    insertIntoMusicList(et.getText().toString());
                                else
                                    Toast.makeText(getContext(),"输入为空",Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消",null).show();
            }
        });
        initListView();
    }

    public void insertIntoMusicList(String name){
        dbHelper = new DBHelper(getActivity(),"mineMusic",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("insert into musicList (listName) values ('"+name+"')",null);
        c.getCount();
        db.close();
        initListView();
    }

    public void initListView(){

        refreshData();
        musicListAdapter = new MusicListAdapter(getActivity());
        musicListAdapter.setData(listId,listName,new DBHelper(getActivity(),"mineMusic",null,1));
        musicListView.setAdapter(musicListAdapter);
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),"position:"+position,Toast.LENGTH_SHORT).show();
                Log.d("data","id:"+listId.get(position));
                Log.d("data","name:"+listName.get(position));

                Intent intent = new Intent(getContext(),musicListDetail.class);
                intent.putExtra("id",Integer.parseInt(listId.get(position)));
                intent.putExtra("name",listName.get(position));
                startActivity(intent);
            }
        });

    }

    public void refreshData(){
        dbHelper = new DBHelper(getActivity(),"mineMusic",null,1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(DBHelper.SELECT_FROM_LIST,null);

        listId = new ArrayList<>();
        listName = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                listId.add(String.valueOf(cursor.getInt(0)));
                listName.add(cursor.getString(1));
                Log.d("data","a:"+String.valueOf(cursor.getInt(0)));
                Log.d("data","b:"+cursor.getString(1));
            }
        }
        db.close();
    }

}
