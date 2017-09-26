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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pengge.musicplayer.R;
import com.pengge.musicplayer.tools.ImageLoaderManager;
import com.pengge.musicplayer.tools.ShowLog;
import com.pengge.musicplayer.ui.SongListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;


public class MainGridViewAdapter extends ArrayAdapter<GridItem> {
    private Context context;
    private int resource;
    private ImageLoaderManager imageLoaderManager = null;
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();
    public MainGridViewAdapter(Context context, int resource, ArrayList<GridItem> list) {
        super(context,resource,list);
        this.resource = resource;
        this.context = context;
        this.mGridData = list;
    }
    private ImageLoaderManager getImageLoaderManager() {
        if (imageLoaderManager != null) {
            return imageLoaderManager;
        }

        ImageLoaderManager manager = ImageLoaderManager.getInstance(this.context);
        manager.init();

        return manager;
    }
    @Override
    public int getCount() {
        return mGridData.size();
    }

    @Override
    public GridItem getItem(int position) {
        return mGridData.get(position);
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
            viewHolder.title = (TextView) convertView.findViewById(R.id.song_title);
            viewHolder.listenCount = (TextView) convertView.findViewById(R.id.listen_count);
            viewHolder.singerImage = (ImageView) convertView.findViewById(R.id.singer_image);
            // 给converHolder附加一个对象
            convertView.setTag(viewHolder);
        }else  {
            // 取得converHolder附加的对象
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final GridItem item = mGridData.get(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.listenCount.setText(item.getListenCount());

        //判断是否有图片
        matcher = Patterns.WEB_URL.matcher(item.getThumb());
        hasImg = matcher.find();
        if (hasImg) {
            viewHolder.singerImage.setVisibility(View.VISIBLE);
            getImageLoaderManager().getImageLoader().displayImage(item.getThumb(), viewHolder.singerImage, getImageLoaderManager().getOptions());
        } else {
            viewHolder.singerImage.setVisibility(View.GONE);
        }
        viewHolder.singerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SongListActivity.class);
                intent.putExtra("playlist_id", item.getPlaylistId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
    class ViewHolder {
        public TextView title;
        public ImageView singerImage;
        public TextView listenCount;
    }
}
