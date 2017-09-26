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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;

import static com.pengge.musicplayer.dataCenter.dataManage.millersToSecond;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private LayoutInflater inflater;
    private FrameLayout bottomBarContainer;
    private ImageLoaderManager imageLoaderManager = null;
    private MediaPlayer mp;
    private ParseLrc parseLrc;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private SeekBar seekBar;
    private TextView beginTime;
    private TextView endTime;
    private ImageView playUp;
    private ImageView playDown;
    private ImageView playPause;
    private  ImageView songBackground;
    private Animation operatingAnim;

    private boolean isPlaying = true;

    private final int GET_MUSIC_RESOURCE_SUCCESS = 200;
    private final int GET_MUSIC_RESOURCE_ERROR = 400;
    private final int JSON_ERROR =500;

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
        parseMusic(bundle.getString("song_id"));

    }
    private void initView() {
        inflater = LayoutInflater.from(this);
        FrameLayout bottomBarContainer = (FrameLayout) findViewById(R.id.play_bottom_bar_container);
        inflater.inflate(R.layout.play_bottom_bar,bottomBarContainer,true);
        seekBar =  (SeekBar)findViewById(R.id.seek_bar);
        beginTime = (TextView) findViewById(R.id.begin_time);
        beginTime.setText("00:00");
        endTime = (TextView) findViewById(R.id.end_time);
        endTime.setText("00:00");
        songBackground = (ImageView) findViewById(R.id.song_background);

        playUp = (ImageView) findViewById(R.id.play_up);
        playDown = (ImageView)findViewById(R.id.play_down);
        playPause = (ImageView) findViewById(R.id.play);
        playUp.setOnClickListener(this);
        playDown.setOnClickListener(this);
        playPause.setOnClickListener(this);



    }
    private void  parseMusic(final String songIds) {
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    String downloadUrl = getResources().getString(R.string.download_url);
                    HashMap<String, String> params = new HashMap<>();
                    //offset 表示:最后一条数据在已加载数据中的位置数
                    params.put("songIds", songIds);
                    params.put("rate", "320");
                    OKHttpManager okHttpManager = OKHttpManager.getInstance();
                    JSONObject result = okHttpManager.get(downloadUrl, params);
                    ShowLog.e(result.toString());
                    JSONObject jsonObject1 = result.getJSONObject("data");
                    JSONArray jsonArray = jsonObject1.getJSONArray("songList");
                    int arrayListLength = jsonArray.length();
                    if(arrayListLength == 0) {
                        msg.what = GET_MUSIC_RESOURCE_ERROR;
                        handler.sendMessage(msg);
                        return;
                    }
                    data.putString("songResult",jsonObject1.toString());
                    msg.setData(data);
                    msg.what = GET_MUSIC_RESOURCE_SUCCESS;
                    handler.sendMessage(msg);

                } catch (JSONException e) {
                    data.putString("JSON_ERROR",e.getMessage());
                    msg.setData(data);
                    msg.what = JSON_ERROR;
                    handler.sendMessage(msg);
                }
            }
        }.start();

    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (msg.what) {
                case  GET_MUSIC_RESOURCE_SUCCESS:
                    //解析数据
                    parseMessage(data.getString("songResult"));

                    break;
                case  GET_MUSIC_RESOURCE_ERROR :
                    String desc = data.getString("desc");
                    Toast.makeText(PlayActivity.this,desc,Toast.LENGTH_SHORT).show();
                    break;
                case JSON_ERROR:
                    Toast.makeText(PlayActivity.this,data.getString("JSON_ERROR"),Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    private void parseMessage(String songResult) {
        try {
            JSONObject jsonObject = new JSONObject(songResult);
            String songXcode = jsonObject.getString("xcode");
            JSONArray jsonArray = jsonObject.getJSONArray("songList");
            JSONObject songObject = jsonArray.getJSONObject(0);
            String lrcLink = songObject.getString("lrcLink");
            String songDownLoadUrl = exchange(songObject.getString("songLink"),songXcode);

            decorateView(songObject);
            parseLrc = new ParseLrc(lrcLink);
            if(mp == null) {
                mp=new MediaPlayer();
                mp.setDataSource(songDownLoadUrl);
                mp.prepareAsync();


                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mp) {
                        //计算时间并添加seekbar
                        int mpTime = mp.getDuration();
                        seekBar.setMax(mpTime);
                        endTime.setText(millersToSecond(mpTime));
                        seekBar.setEnabled(true);
                        if(isPlaying) {
                            //----------定时器记录播放进度---------//
                            mTimer = new Timer();
                            mTimerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            seekBar.setProgress(mp.getCurrentPosition());
                                            beginTime.setText(millersToSecond(mp.getCurrentPosition()));
                                        }
                                    });
                                }
                            };
                            mTimer.schedule(mTimerTask, 0, 10);
                            seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());

                            mp.setLooping(true);
                            mp.start();
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            } else {
                songBackground.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_up:
                break;
            case R.id.play:
                if(isPlaying == true) {
                    playPause.setImageResource(R.drawable.play);
                    isPlaying = false;
                    mp.pause();
                    songBackground.clearAnimation();
                }else {
                    playPause.setImageResource(R.drawable.pause);
                    isPlaying = true;
                    mp.start();
                    songBackground.startAnimation(operatingAnim);
                }
                break;
            case R.id.play_down:
                break;
        }
    }

    private class OnSeekBarChangeListenerImp implements
            SeekBar.OnSeekBarChangeListener {

        // 触发操作，拖动
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        // 表示进度条刚开始拖动，开始拖动时候触发的操作
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        // 停止拖动时候
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }
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
        if(mp != null){
            mp.stop();
            mp = null;
        }
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
