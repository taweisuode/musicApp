package com.pengge.musicplayer.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pengge.musicplayer.R;
import com.pengge.musicplayer.musicModel.ParseLrc;
import com.pengge.musicplayer.tools.Displaymetrics;
import com.pengge.musicplayer.tools.ImageLoaderManager;
import com.pengge.musicplayer.tools.OKHttpManager;
import com.pengge.musicplayer.tools.ShowLog;
import com.pengge.musicplayer.ui.presenter.PlayPresenterAchi;
import com.pengge.musicplayer.ui.view.PlayView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;

import static com.pengge.musicplayer.dataCenter.dataManage.millersToSecond;

public class PlayActivity extends AppCompatActivity implements PlayView {
    public PlayPresenterAchi playPresenterAchi;
    private LayoutInflater inflater;
    private ImageLoaderManager imageLoaderManager = null;
    private MediaPlayer mp;
    private  ImageView songBackground;
    private Animation operatingAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle == null) {
            return;
        }
        //初始化播放界面
        initView();

        //绑定present
        bindPresent(bundle.getString("song_id"));

    }
    private void initView() {
        inflater = LayoutInflater.from(this);
        FrameLayout bottomBarContainer = (FrameLayout) findViewById(R.id.play_bottom_bar_container);
        inflater.inflate(R.layout.play_bottom_bar,bottomBarContainer,true);
        songBackground = (ImageView) findViewById(R.id.song_background);

    }
    //绑定presenterAchi
    private void bindPresent(String songIds) {
        playPresenterAchi = new PlayPresenterAchi(this);
        playPresenterAchi.setActivity(this);
        playPresenterAchi.getSongInfoFromApi(songIds);
    }
    private void decorateView(JSONObject songObject) {
        try {
            Matcher matcher;
            String songName = songObject.getString("songName");
            String artistName = songObject.getString("artistName");

            //替换标题
            TextView playTitle = (TextView) findViewById(R.id.play_title);
            playTitle.setText(artistName+" -- "+songName);

            //加背景图
            final String songPicBig = songObject.getString("songPicRadio");
            matcher = Patterns.WEB_URL.matcher(songPicBig);
            boolean hasImg  = matcher.find();
            if (hasImg) {
                getImageLoaderManager().getImageLoader().displayImage(songPicBig, songBackground, getImageLoaderManager().getOptions());
                operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
                LinearInterpolator lin = new LinearInterpolator();
                operatingAnim.setInterpolator(lin);
                songBackground.startAnimation(operatingAnim);
                playPresenterAchi.setAnim(operatingAnim);
            } else {
                songBackground.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void parseSongInfoMessage(String songResult) {
        try {
            JSONObject jsonObject = new JSONObject(songResult);
            String songXcode = jsonObject.getString("xcode");
            JSONArray jsonArray = jsonObject.getJSONArray("songList");
            JSONObject songObject = jsonArray.getJSONObject(0);
            String lrcLink = songObject.getString("lrcLink");
            String songDownLoadUrl = exchange(songObject.getString("songLink"),songXcode);

            decorateView(songObject);

            //初始化播放
            playPresenterAchi.initPlay(songDownLoadUrl);
            //歌词播放
            playPresenterAchi.parseLrc(lrcLink);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showToast(String desc) {

    }

    //替换xcode
    private String exchange(String originUrl,String exchageUrl) {
        ShowLog.e("originUrl="+originUrl);
        String[] tempArray = originUrl.split("xcode=");
        String newUrl = originUrl.replace(tempArray[1],exchageUrl);
        ShowLog.e("newUrl="+newUrl);
        return newUrl;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        playPresenterAchi.stopActivity();
    }
    private ImageLoaderManager getImageLoaderManager() {
        if (imageLoaderManager != null) {
            return imageLoaderManager;
        }

        ImageLoaderManager manager = ImageLoaderManager.getInstance(getApplicationContext());
        manager.init();

        return manager;
    }

}
