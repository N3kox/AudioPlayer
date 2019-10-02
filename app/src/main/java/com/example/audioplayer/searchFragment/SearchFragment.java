package com.example.audioplayer.searchFragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.audioplayer.MainActivity;
import com.example.audioplayer.R;
import com.example.audioplayer.playerActivity.PlayerActivity;

import java.util.ArrayList;

import WebServices.Distributer;
import Config.GlobalData;
import Bean.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    private SearchFragment that = this;
    private SearchView searchView;
    private ImageButton playerEntry;
    private ArrayList<song> songs;
    private ListView listView;
    private SearchListAdapter ada;
    private song typed;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                //获取关键词搜索结果
                case GlobalData.MSG_FETCHSEARCHLIST:{
                    songs = (ArrayList<song>)msg.obj;
                    ada = new SearchListAdapter(getContext(),songs,R.layout.activity_search_list_adapter);
                    listView.setAdapter(ada);
                    listView.setOnItemClickListener(that);
                    break;
                }
                case GlobalData.MSG_SONG_READY:{
                    String filePath = GlobalData.dirPath + "/music_" + msg.obj+".mp3";
                    Log.d("#MEDIA","media ready:"+filePath);
                    Intent intent = new Intent(getActivity(),PlayerActivity.class);
                    intent.putExtra("action","newSong");
                    intent.putExtra("filePath",filePath);
                    intent.putExtra("detail",typed);
                    startActivity(intent);

                    break;
                }
                case GlobalData.MSG_SONG_NOTAVAILABLE:{
                    Toast.makeText(getContext(),"歌曲被版权了哦",Toast.LENGTH_SHORT).show();
                    break;
                }
                case GlobalData.MSG_ERROR:{
                    Toast.makeText(getContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //主activity存储当前service内存储的song信息

        if(((MainActivity)getActivity()).iService.getSongId() == songs.get(position).getId()){
            playerEntry.performClick();
        }else{
            typed = songs.get(position);
            distributer.fetchMusic(songs.get(position).getId());
        }
    }


    private Distributer distributer = new Distributer(handler);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        searchView = view.findViewById(R.id.fragment_search_search);
        playerEntry = view.findViewById(R.id.fragment_search_player_entry);
        listView = view.findViewById(R.id.fragment_search_listview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("#FRAGMENT","submit:"+query);
                distributer.fetchSearchList(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        playerEntry.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fragment_search_player_entry:{
                //右上角点击进入播放器界面
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                intent.putExtra("action","showAncientMusic");
                //Log.d("#MEDIA","seek:"+((MainActivity)getActivity()).iService.getSeekLength());
                //Log.d("#MEDIA","duration:"+((MainActivity)getActivity()).iService.getDuration());
                if(((MainActivity)getActivity()).iService.isPlaying()){
                    intent.putExtra("playingStatus",true);
                }else{
                    intent.putExtra("playingStatus",false);
                }
                if(((MainActivity)getActivity()).iService.getSong() != null){
                    intent.putExtra("loaded",true);
                    intent.putExtra("songInfo",((MainActivity)getActivity()).iService.getSong());
                }else{
                    intent.putExtra("loaded",false);
                }


                startActivity(intent);
                break;
            }
        }
    }
}
