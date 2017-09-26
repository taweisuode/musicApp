package com.pengge.musicplayer.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pengge.musicplayer.R;
import com.pengge.musicplayer.diyView.ListItem;
import com.pengge.musicplayer.diyView.SongListViewAdapter;
import com.pengge.musicplayer.tools.ImageLoaderManager;
import com.pengge.musicplayer.tools.ShowLog;
import com.pengge.musicplayer.ui.presenter.SongListPresenterAchi;
import com.pengge.musicplayer.ui.view.SongListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class SongListActivity extends AppCompatActivity implements SongListView{
    private ImageView songListImage;
    private TextView songTitle;
    private ListView songListView;
    private RelativeLayout songListInfo;
    public SongListPresenterAchi songListPresenterAchi;
    private  String playListId;
    private ImageLoaderManager imageLoaderManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        getMessage();

        bindPresent();

        //底部导航
        initRadioButton();

    }
    private void getMessage() {
        Intent intent = getIntent();
        playListId = intent.getStringExtra("playlist_id");
        //Toast.makeText(this,playListId,Toast.LENGTH_SHORT).show();
        songListImage = (ImageView) findViewById(R.id.song_list_image);
        songTitle     = (TextView) findViewById(R.id.song_title);
        songListView   = (ListView) findViewById(R.id.song_list_listview);
        songListInfo = (RelativeLayout) findViewById(R.id.song_list_info);

    }
    //绑定presenterAchi
    private void bindPresent() {
        songListPresenterAchi = new SongListPresenterAchi(this);
        songListPresenterAchi.setView(getWindow().getDecorView().findViewById(android.R.id.content));
        songListPresenterAchi.getSongListFromApi(playListId);
    }
    /**
     *  图片加载
     */
    private ImageLoaderManager getImageLoaderManager() {
        if (imageLoaderManager != null) {
            return imageLoaderManager;
        }

        ImageLoaderManager manager = ImageLoaderManager.getInstance(getApplicationContext());
        manager.init();

        return manager;
    }
    @Override
    public void parseSongListMessage(String songResult) {
        ShowLog.e(songResult);
        Matcher matcher;
        try {
            JSONObject result = new JSONObject(songResult);
            JSONObject jsonData = result.getJSONObject("data");

            //更新图片
            String imgUrl = jsonData.getString("thumb");
            matcher = Patterns.WEB_URL.matcher(imgUrl);
            boolean hasImg  = matcher.find();
            if (hasImg) {
                getImageLoaderManager().getImageLoader().displayImage(imgUrl, songListImage, getImageLoaderManager().getOptions());
            } else {
                songListImage.setVisibility(View.GONE);
            }
            songTitle.setText(jsonData.getString("title"));

            //背景图片模糊化
            byte[] bitmapArray = Base64.decode(String.valueOf(jsonData.get("blur_background")),Base64.CRLF);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            ShowLog.e(bitmap.toString());
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            songListInfo.setBackground(drawable);
            JSONArray jsonArray = jsonData.getJSONArray("list");
            ArrayList<ListItem> dataList = new ArrayList<>();
            if(jsonArray.length() > 0) {
                for(int i = 0 ; i < jsonArray.length();i++) {
                    ListItem listItem = new ListItem();
                   JSONObject jsonNode = (JSONObject) jsonArray.get(i);
                    listItem.setTitle(jsonNode.getString("title"));
                    listItem.setSongId(jsonNode.getString("song_id"));
                    listItem.setAuthor(jsonNode.getString("author"));
                    listItem.setAlbumId(jsonNode.getString("album_id"));
                    listItem.setAlbumTitle(jsonNode.getString("album_title"));
                    listItem.setAllArtistId(jsonNode.getString("all_artist_id"));
                    dataList.add(listItem);
                }
            }
            ShowLog.e(String.valueOf(dataList));
            drawListView(dataList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showToast(String desc) {

    }
    private void drawListView(ArrayList<ListItem> jsonArray) {
        SongListViewAdapter adapter = new SongListViewAdapter(this,R.layout.songlist_listview_template,jsonArray);
        songListView.setAdapter(adapter);
    }
    private void initRadioButton() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout container = (FrameLayout) findViewById(R.id.fragment_container);
        inflater.inflate(R.layout.fragment_bottom_bar, container, true);
    }
    public void skipBackView(View view) {
        finish();
    }
}
