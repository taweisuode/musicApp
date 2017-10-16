package com.pengge.musicplayer.diyView;

/**
 * Created by pengge on 16/11/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pengge.musicplayer.R;
import com.pengge.musicplayer.tools.ImageLoaderManager;
import com.pengge.musicplayer.ui.PlayActivity;
import com.pengge.musicplayer.ui.SongListActivity;

import java.util.ArrayList;
import java.util.regex.Matcher;


public class SongListViewAdapter extends ArrayAdapter<ListItem> {
    private final ArrayList<String> songIdArr;
    private Context context;
    private int resource;
    private ArrayList<ListItem> listData = new ArrayList<ListItem>();
    public SongListViewAdapter(Context context, int resource, ArrayList<ListItem> list) {
        super(context,resource,list);
        this.resource = resource;
        this.context = context;
        this.listData = list;
        ArrayList<String> songIdArr = new ArrayList<>();
        for (int i = 0;i < list.size();i++) {
            songIdArr.add(list.get(i).getSongId());
        }
        this.songIdArr = songIdArr;
    }
    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public ListItem getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        Matcher matcher;
        boolean  hasImg;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
            // 初始化组件
            viewHolder.songKey = (TextView) convertView.findViewById(R.id.song_key);
            viewHolder.title = (TextView) convertView.findViewById(R.id.song_title);
            viewHolder.songAuthor = (TextView) convertView.findViewById(R.id.song_author);
            viewHolder.convertView = convertView;
            //viewHolder.singerImage = (ImageView) convertView.findViewById(R.id.singer_image);
            // 给converHolder附加一个对象
            convertView.setTag(viewHolder);
        }else  {
            // 取得converHolder附加的对象
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ListItem item = listData.get(position);
        viewHolder.songKey.setText(String.valueOf(position+1));
        viewHolder.title.setText(item.getTitle());
        StringBuffer sb = new StringBuffer();
        sb.append(item.getAuthor()).append("-").append(item.getAlbumTitle());
        viewHolder.songAuthor.setText(sb.toString());

        viewHolder.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayActivity.class);
                intent.putExtra("song_id", item.getSongId());
                intent.putExtra("song_id_arr",songIdArr);
                context.startActivity(intent);
            }
        });


        return convertView;
    }
    class ViewHolder {
        public TextView songKey;
        public TextView title;
        public TextView songAuthor;
        public View convertView;
    }
}
