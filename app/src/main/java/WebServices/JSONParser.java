package WebServices;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import Bean.*;

public class JSONParser {

    public static ArrayList<song> parseSearchResult(String responseData){
        ArrayList<song> songs = new ArrayList<>();
        try{

            JSONArray songsArray = new JSONObject(responseData).getJSONObject("result").getJSONArray("songs");
            for(int i = 0;i<songsArray.length();i++){
                JSONObject songInfo = songsArray.getJSONObject(i);
                JSONArray artistList = songInfo.getJSONArray("artists");
                song s = new song();
                ArrayList<artist> artists = new ArrayList<>();
                s.setId(songInfo.getInt("id"));
                s.setName(songInfo.getString("name"));
                s.setAlbumName(songInfo.getJSONObject("album").getString("name"));
                s.setDuration(songInfo.getInt("duration"));
                for(int j=0;j<artistList.length();j++){
                    artist a = new artist();
                    a.setId(artistList.getJSONObject(j).getInt("id"));
                    a.setName(artistList.getJSONObject(j).getString("name"));
                    artists.add(a);
                }
                s.setArtists(artists);
                songs.add(s);
            }

            return songs;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkAvailable(String responseData){
        try{
            JSONObject info = new JSONObject(responseData);
            if(info.getBoolean("success") == true)
                return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static String parseMusicUrl(String responseData){
        try{
            JSONObject info = new JSONObject(responseData).getJSONArray("data").getJSONObject(0);
            return info.getString("url");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
