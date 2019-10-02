package WebServices;

import android.os.Message;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import Bean.*;
import Config.GlobalData;



public class OkHttpRequests {
    private String base = "http://bzpnb.xyz:3000";
    private DownloadFunction downloadFunction = new DownloadFunction();

    //获取response.body(String)
    private String getStringModule(String url){
        try{
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //获取response.body(byte[])
    private byte[] getBytesModule(String url){
        try{
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().bytes();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    //获取搜索歌曲列表
    public void fetchSearchList(final Handler handler,final String keyWord){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = base+"/search?keywords="+keyWord;
                String responseData = getStringModule(url);
                if(responseData!=null){
                    ArrayList<song> songs = JSONParser.parseSearchResult(responseData);
                    Message msg = new Message();
                    msg.what = GlobalData.MSG_FETCHSEARCHLIST;
                    msg.obj = songs;
                    handler.sendMessage(msg);
                }else{
                    Message msg = new Message();
                    msg.what = GlobalData.MSG_ERROR;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }


    //先检测歌曲是否可用
    public void requestAvailable(final Handler handler, final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = base + "/check/music?id="+id;
                String responseData = getStringModule(url);
                if(JSONParser.checkAvailable(responseData)){
                    //歌曲可用，获取歌曲至本地
                    fetchSoundTrack(handler,id);
                }else{
                    //歌曲不可用
                    Message msg = new Message();
                    msg.what = GlobalData.MSG_ERROR;
                    handler.sendMessage(msg);
                    return;
                }
            }
        }).start();
    }


    //获取获取音频音轨（url->MP3）
    public void fetchSoundTrack(final Handler handler, final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //检查本地文件
                Message msg = new Message();
                String filePath = GlobalData.dirPath + "/music_" + id + ".mp3";
                File file = new File(filePath);
                if(file.exists()){
                    msg.what = GlobalData.MSG_SONG_READY;
                    msg.obj = id;
                    handler.sendMessage(msg);
                    return;
                }
                //获取歌曲url
                String url = base + "/song/url?id=" + id;
                Log.d("#URL",url);
                String responseData = getStringModule(url);
                if(responseData == null){
                    msg.what = GlobalData.MSG_ERROR;
                    msg.obj = "获取音乐信息失败";
                    handler.sendMessage(msg);
                }else{
                    //从歌曲url下载歌曲
                    String musicUrl = JSONParser.parseMusicUrl(responseData);
                    if(musicUrl!=null){
                        byte[] bytes;
                        if((bytes= getBytesModule(musicUrl)) != null){
                            Log.d("###",file.getPath());
                            if(downloadFunction.buildMP3(bytes,filePath)){
                                msg.what = GlobalData.MSG_SONG_READY;
                                msg.obj = id;
                                handler.sendMessage(msg);
                                return;
                            }else{
                                msg.what = GlobalData.MSG_ERROR;
                                msg.obj = "mp3下载失败";
                                handler.sendMessage(msg);
                            }
                        }else{
                            msg.what = GlobalData.MSG_ERROR;
                            msg.obj = "url获取音乐失败";
                            handler.sendMessage(msg);
                        }
                    }else{
                        msg.what = GlobalData.MSG_ERROR;
                        msg.obj = "获取音乐地址失败";
                        handler.sendMessage(msg);
                    }
                }
            }
        }).start();
    }
}
