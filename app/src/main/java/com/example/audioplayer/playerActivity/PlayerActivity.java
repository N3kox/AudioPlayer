package com.example.audioplayer.playerActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.audioplayer.R;

import java.util.ArrayList;

import Bean.song;
import Config.GlobalData;

import DB.DBHelper;
import MediaService.MediaService;
import MediaService.exposedServices;
import Shapes.Disk;
import Shapes.Needle;

public class PlayerActivity extends Activity implements View.OnClickListener {


    private exposedServices iService;
    final private myConnection conn = new myConnection();

    private ImageButton player_start_pause;
    private boolean isPlaying = false;
    View myView;
    float downX,screenWidth;
    private song songInfo;
    private String filePath;
    public static int duration;
    private boolean navigatedFromSearch = false;
    TextView tvSongName,tvAlbumName,tvArtist;
    private static SeekBar seekBar;
    private static TextView tvCurrentPosition,tvDuration;
    private Needle needle;
    private Disk disk;
    private ImageButton listAdd,listDrop;
    private ArrayList<String> playListId,playListName;

    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //测试用
                case GlobalData.MSG_SONG_DURATION:{
                    duration = (int)msg.obj;
                    tvCurrentPosition.setText("0:0");
                    tvDuration.setText((duration/60000) +":"+(duration/1000 - duration/60000*60));
                    seekBar.setMax(duration);
                    break;
                }
                //获取当前位置
                case GlobalData.MSG_CURRENT_POSITION:{
                    Bundle bundle = msg.getData();
                    int currentPosition = bundle.getInt("currentPosition");
                    tvCurrentPosition.setText((currentPosition/60000) + ":" + (currentPosition/1000 - currentPosition/60000*60));
                    seekBar.setProgress(currentPosition);
                    break;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initView();
        navigatedFromSearch = true;
        Intent from = getIntent();
        switch (from.getStringExtra("action")){
            case "newSong":{
                filePath = from.getStringExtra("filePath");
                songInfo = (song)from.getSerializableExtra("detail");

                tvCurrentPosition.setText("0:0");
                tvDuration.setText((songInfo.getDuration()/60000) +":"+(songInfo.getDuration()/1000 - songInfo.getDuration()/60000*60));
                seekBar.setProgress(0);
                seekBar.setMax(songInfo.getDuration());
                String allNames = "";
                for(int i=0;i<songInfo.getArtists().size()-1;i++){
                    allNames += songInfo.getArtists().get(i).getName() + " / ";
                }
                allNames += songInfo.getArtists().get(songInfo.getArtists().size()-1).getName();

                tvSongName.setText(songInfo.getName());
                tvAlbumName.setText(songInfo.getAlbumName().length() > 12 ? songInfo.getAlbumName().substring(0,12) + "..." : songInfo.getAlbumName());
                tvArtist.setText(allNames.length() > 12 ? allNames.substring(0,12)+"..." : allNames);
                break;
            }
            case "showAncientMusic":{
                navigatedFromSearch = false;
                if(!from.getBooleanExtra("playingStatus",true)){
                    Log.d("#PLAYINGSTATUS","not playing");
                    player_start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
                }else{
                    Log.d("#PLAYINGSTATUS","playing");
                    player_start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
                }
                if(from.getBooleanExtra("loaded",false)){
                    songInfo = (song)from.getSerializableExtra("songInfo");
                    String allNames = "";
                    for(int i=0;i<songInfo.getArtists().size()-1;i++){
                        allNames += songInfo.getArtists().get(i).getName() + " / ";
                    }
                    allNames += songInfo.getArtists().get(songInfo.getArtists().size()-1).getName();
                    tvSongName.setText(songInfo.getName());
                    tvAlbumName.setText(songInfo.getAlbumName().length() > 12 ? songInfo.getAlbumName().substring(0,12) + "..." : songInfo.getAlbumName());
                    tvArtist.setText(allNames.length() > 12 ? allNames.substring(0,12)+"..." : allNames);
                    tvDuration.setText((songInfo.getDuration()/60000) +":"+(songInfo.getDuration()/1000 - songInfo.getDuration()/60000*60));
                    seekBar.setMax(songInfo.getDuration());

                }else{
                    Toast.makeText(this,"尚未选择歌曲",Toast.LENGTH_SHORT).show();
                    player_start_pause.setClickable(false);
                    return;
                }

                break;
            }
        }
        bindService(new Intent(this, MediaService.class),conn,BIND_AUTO_CREATE);

    }


    private void initView(){
        //滑屏功能坐标初始化
        myView = getWindow().getDecorView();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //screenWidth = displayMetrics.widthPixels;

        screenWidth = getScreenWidth();
        player_start_pause = findViewById(R.id.player_start_pause);
        player_start_pause.setOnClickListener(this);

        tvSongName = findViewById(R.id.player_music_detail_songName);
        tvArtist = findViewById(R.id.player_music_detail_artists);
        tvAlbumName = findViewById(R.id.player_music_detail_albumName);
        seekBar = findViewById(R.id.player_seekBar);
        tvCurrentPosition = findViewById(R.id.player_text_currentPosition);
        tvDuration = findViewById(R.id.player_text_duration);

        needle = findViewById(R.id.player_image_needle);
        disk = findViewById(R.id.player_image_blackhole);
        listAdd = findViewById(R.id.player_image_list_add);
        listAdd.setOnClickListener(this);

        RelativeLayout.LayoutParams sl = (RelativeLayout.LayoutParams)seekBar.getLayoutParams();
        sl.width = (int)(getScreenWidth()*0.7);
        seekBar.setLayoutParams(sl);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                iService.seekToPosition(seekBar.getProgress());
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.player_start_pause:{
                //歌曲装载入mediaPlayer

                if(iService.getSongId() != songInfo.getId()){
                    iService.playFromStart(songInfo.getId(), filePath);
                    iService.setSong(songInfo);
                    navigatedFromSearch = false;
                    player_start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
                }
                else{
                    if(iService.isPlaying()){
                        iService.pause();
                        player_start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
                    }else{
                        iService.resume();
                        player_start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
                    }
                }
                needle.play();
                disk.play();
                break;
            }
            case R.id.player_image_list_add:{
                if(songInfo != null){
                    refreshPlayList();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle("选择歌单")
                            .setSingleChoiceItems(playListName.toArray(new String[playListName.size()]), -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("data","which:"+which);
                                    dialog.dismiss();
                                    insertIntoMusicListDetail(which);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();
                }
                break;
            }
        }
    }

    //seekbar动态宽度
    public int getScreenWidth(){
        return getBaseContext().getApplicationContext().getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(iService != null){
            iService.stopUpdateSeekBar();
            unbindService(conn);
        }
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            downX = event.getX();
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            float xDistance = event.getX() - downX;
            if(xDistance > 0)
                myView.setX(event.getX());
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            float xDistance = event.getX() - downX;
            //返回上级与重置
            if(xDistance > screenWidth /2){
                finish();
            }else{
                myView.setX(0);
            }
        }
        return super.onTouchEvent(event);
    }

    public class myConnection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iService = (exposedServices) service;
            if(songInfo == null){
                iService.updateSeekBar();
                needle.play();
                disk.play();
            }
            else if(iService.getSongId() != songInfo.getId()){
                player_start_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
                tvCurrentPosition.setText("0:0");
                seekBar.setProgress(0);
            }else{
                iService.updateSeekBar();
                if(iService.isPlaying()){
                    needle.play();
                    disk.play();
                }

            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iService = null;
        }
    }

    private void refreshPlayList(){
        DBHelper dbHelper = new DBHelper(this,"mineMusic",null,1);
        SQLiteDatabase sqLiteDatabase= dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DBHelper.SELECT_FROM_LIST,null);
        if(cursor != null){
            playListId = new ArrayList<>();
            playListName = new ArrayList<>();
            while(cursor.moveToNext()){
                playListId.add(String.valueOf(cursor.getInt(0)));
                playListName.add(cursor.getString(1));
            }
        }
        dbHelper.close();
    }

    private void insertIntoMusicListDetail(int position){
        DBHelper dbHelper = new DBHelper(this,"mineMusic",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(checkValid(Integer.parseInt(playListId.get(position)),songInfo.id)){

            ContentValues cv = new ContentValues();
            cv.put("listId",Integer.parseInt(playListId.get(position)));
            cv.put("musicId",songInfo.getId());
            cv.put("musicName",songInfo.getName());
            cv.put("artistName",getArtistsName());
            cv.put("albumName",songInfo.getAlbumName());
            long stat = db.insert("musicDetail",null,cv);
            if(stat == -1)
                Toast.makeText(this,"sql错误",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this,"加入歌曲成功",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this,"此歌单已有此歌曲",Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkValid(int listId,int songId){
        DBHelper dbHelper = new DBHelper(this,"mineMusic",null,1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from musicDetail where listId = " + listId + " and musicId = "+songId,null);
        if(cursor.getCount() > 0)
            return false;
        return true;
    }

    private String getArtistsName(){
        String temp = "";
        for(int i = 0;i<songInfo.getArtists().size() - 1;i++){
            temp += songInfo.getArtists().get(i).getName() + "/";
        }
        return temp + songInfo.getArtists().get(songInfo.getArtists().size()-1).getName();
    }



}
