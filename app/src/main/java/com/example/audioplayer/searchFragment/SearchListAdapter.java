package com.example.audioplayer.searchFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.audioplayer.R;
import Bean.*;

import java.util.ArrayList;

public class SearchListAdapter extends BaseAdapter {

    private Context myContext;
    private ArrayList<song> songs;
    private int layoutId;
    private ViewHolder viewHolder = null;

    public class ViewHolder {
        TextView txtName, txtArtistsName;
    }

    public SearchListAdapter(Context myContext,ArrayList<song> songs,int layoutId){
        this.myContext = myContext;
        this.songs = songs;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(myContext).inflate(layoutId,null);
            viewHolder.txtName = convertView.findViewById(R.id.adapter_music_name);
            viewHolder.txtArtistsName = convertView.findViewById(R.id.adapter_artists_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.txtName.setText(songs.get(position).getName());
        ArrayList<artist> artists = songs.get(position).getArtists();
        String nameList = "";
        for(int i=0;i<artists.size()-1;i++){
            nameList += artists.get(i).name + "/";
        }
        nameList += artists.get(artists.size()-1).name;
        viewHolder.txtArtistsName.setText(nameList);

        return convertView;
    }
}
