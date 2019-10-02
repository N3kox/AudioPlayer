package com.example.audioplayer.slideView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.audioplayer.R;
import DB.DBHelper;
import java.util.List;

public class MusicListAdapter extends BaseAdapter {
    private Context context;
    private List<String> id;
    private List<String> list;
    private DBHelper dbHelper;
    private LayoutInflater inflater;


    public MusicListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<String>id ,List<String> list,DBHelper dbHelper) {
        this.id = id;
        this.list = list;
        this.dbHelper = dbHelper;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        deleteView slideView = (deleteView) convertView;
        if (slideView == null) {
            View itemView = inflater.inflate(R.layout.music_list_adapter, null);

            slideView = new deleteView(context);
            slideView.setContentView(itemView);
            viewHolder = new ViewHolder(slideView);
            slideView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) slideView.getTag();
        }
        slideView.shrink();//设置删除按钮恢复原来的位置，即消失
        viewHolder.tv_message_item_content.setText(list.get(position));
        viewHolder.deleteHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("data","position:"+position);
                removeCurrentMusicList(position);
                id.remove(position);
                list.remove(position);

                notifyDataSetChanged();
            }
        });

        return slideView;
    }

    static class ViewHolder {
        public TextView tv_message_item_content;
        public TextView tv_message_item_time;
        public ViewGroup deleteHolder;
        ViewHolder(View view) {
            tv_message_item_content = (TextView) view.findViewById(R.id.tv_message_item_content);
            tv_message_item_time = (TextView) view.findViewById(R.id.item_top);
            deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
        }
    }

    public void removeCurrentMusicList(int position){
        //int deleteId = Integer.parseInt(id.get(position));
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(DBHelper.DELETE_FROM_MUSIC_DETAIL_ALL+id.get(position),null);
        cursor.getCount();
        cursor = db.rawQuery(DBHelper.DELETE_FROM_MUSIC_LIST+id.get(position),null);
        cursor.getCount();
    }
}
