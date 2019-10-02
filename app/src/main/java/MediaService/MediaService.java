package MediaService;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.audioplayer.playerActivity.PlayerActivity;

import java.util.Timer;
import java.util.TimerTask;

import Bean.song;
import Config.GlobalData;

public class MediaService extends Service {
    public MediaPlayer mediaPlayer;
    public int songId = -1;
    public int seekLength;
    public int duration = 0;
    private Timer timer;
    private TimerTask task;
    private song loadedSong;

    public static final int PLAY_LOOP = 1;
    public static final int PLAY_RANDOM = 2;
    public static final int PLAY_LISTPLAY = 3;



    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        loadedSong = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private class MyBinder extends Binder implements exposedServices{

        @Override
        public boolean playFromStart(int id, String mediaPath) {
            try{
                mediaPlayer.reset();
                mediaPlayer.setDataSource(mediaPath);
                mediaPlayer.prepare();
                duration = mediaPlayer.getDuration();
                mediaPlayer.setLooping(true);
                updateSeekBar();
                songId = id;
                System.out.println("##currentSongId:"+songId);
                mediaPlayer.start();
                return true;

            }catch (Exception e){
                e.printStackTrace();
            }

            return false;
        }

        @Override
        public boolean resume() {
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(seekLength);
                System.out.println("##currentSongId:"+songId);
                updateSeekBar();
                mediaPlayer.start();
                return true;
            }
            return false;
        }

        @Override
        public boolean pause() {
            if(mediaPlayer.isPlaying()){
                seekLength = mediaPlayer.getCurrentPosition();
                System.out.println("##currentSongId:"+songId);
                stopUpdateSeekBar();
                mediaPlayer.pause();
                return true;
            }
            return false;
        }

        @Override
        public boolean stop() {
            mediaPlayer.reset();
            return true;
        }

        @Override
        public boolean prev() {
            return false;
        }

        @Override
        public boolean next() {
            return false;
        }

        @Override
        public boolean isPlaying() {
            if(mediaPlayer.isPlaying())
                return true;
            return false;
        }

        @Override
        public int getSeekLength() {
            return mediaPlayer.getCurrentPosition();
        }

        @Override
        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        @Override
        public void updateSeekBar() {
            //保持音乐播放器seekbar刷新服务仅存在一个
            if(timer != null && task != null)
                return;
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = Message.obtain();
                    message.what = GlobalData.MSG_CURRENT_POSITION;
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    Bundle bundle = new Bundle();
                    bundle.putInt("currentPosition",currentPosition);
                    Log.d("#MEDIA","currentPosition:"+currentPosition);
                    message.setData(bundle);
                    PlayerActivity.handler.sendMessage(message);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            timer.cancel();
                            task.cancel();
                        }
                    });
                }
            };
            timer.schedule(task,500,500);
        }

        @Override
        public void seekToPosition(int currentPosition) {
            mediaPlayer.seekTo(currentPosition);
            seekLength = currentPosition;
        }

        @Override
        public void stopUpdateSeekBar() {
            if(timer != null)
                timer.cancel();

            if(task != null)
                task.cancel();

            timer = null;
            task = null;
        }

        @Override
        public int getSongId() {
            return songId;
        }

        @Override
        public void setSong(song song) {
            loadedSong = song;
        }

        @Override
        public song getSong() {
            return loadedSong;
        }
    }
}
