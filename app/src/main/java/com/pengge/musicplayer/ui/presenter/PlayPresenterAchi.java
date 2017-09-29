package com.pengge.musicplayer.ui.presenter;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pengge.musicplayer.R;
import com.pengge.musicplayer.musicModel.ParseLrc;
import com.pengge.musicplayer.tools.OKHttpManager;
import com.pengge.musicplayer.ui.PlayActivity;
import com.pengge.musicplayer.ui.view.PlayView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


import static com.pengge.musicplayer.dataCenter.dataManage.millersToSecond;


public class PlayPresenterAchi implements PlayPresenter,View.OnClickListener{
    private PlayView playView;
    private PlayActivity playActivity;
    public View view;
    private SafeHandler safeHandler;
    private MediaPlayer mp;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private SeekBar seekBar;
    private TextView beginTime;
    private TextView endTime;
    private ImageView playUp;
    private ImageView playDown;
    private ImageView playPause;
    private ImageView songBackground;
    private Animation operatingAnim;
    private boolean isPlaying = true;

    private static final int GET_SONG_INFO_SUCCESS = 201;
    private static final int GET_SONG_INFO_ERROR = 501;
    private static final int JSON_ERROR = 601;
    public PlayPresenterAchi(PlayView playView) {
        this.playView = playView;
        safeHandler = new SafeHandler(this);
    }

    public void setActivity(PlayActivity playActivity) {
        this.playActivity = playActivity;
        this.view = playActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        seekBar =  (SeekBar) view.findViewById(R.id.seek_bar);
        beginTime = (TextView) view.findViewById(R.id.begin_time);
        beginTime.setText("00:00");
        endTime = (TextView) view.findViewById(R.id.end_time);
        endTime.setText("00:00");
        songBackground = (ImageView) view.findViewById(R.id.song_background);
        playUp = (ImageView) view.findViewById(R.id.play_up);
        playDown = (ImageView) view.findViewById(R.id.play_down);
        playPause = (ImageView) view.findViewById(R.id.play);
        playUp.setOnClickListener(this);
        playDown.setOnClickListener(this);
        playPause.setOnClickListener(this);
    }
    @Override
    public void getSongInfoFromApi(final String songIds) {
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                String downloadUrl = view.getContext().getString(R.string.download_url);
                HashMap<String, String> params = new HashMap<>();
                //offset 表示:最后一条数据在已加载数据中的位置数
                params.put("songIds", songIds);
                params.put("rate", "320");
                OKHttpManager okHttpManager = OKHttpManager.getInstance();
                JSONObject result = okHttpManager.get(downloadUrl, params);
                try {
                    JSONObject jsonObject1 = result.getJSONObject("data");
                    JSONArray jsonArray = jsonObject1.getJSONArray("songList");
                    int arrayListLength = jsonArray.length();
                    if(arrayListLength == 0) {
                        msg.what = GET_SONG_INFO_ERROR;
                        safeHandler.sendMessage(msg);
                        return;
                    }
                    data.putString("songResult",jsonObject1.toString());
                    msg.setData(data);
                    msg.what = GET_SONG_INFO_SUCCESS;
                    safeHandler.sendMessage(msg);
                } catch (JSONException e1) {
                    data.putString("JSON_ERROR",e1.getMessage());
                    msg.setData(data);
                    msg.what = JSON_ERROR;
                    safeHandler.sendMessage(msg);
                }
            }
        }.start();
    }
    @Override
    public void initPlay(String songDownLoadUrl) {
        if(mp == null) {
            mp = new MediaPlayer();

            try {
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
                                    playActivity.runOnUiThread(new Runnable() {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAnim(Animation operatingAnim) {
        this.operatingAnim = operatingAnim;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_up:
                break;
            case R.id.play:
                if(isPlaying) {
                    Drawable playDrawable = view.getResources().getDrawable(R.drawable.play);
                    playPause.setImageDrawable(playDrawable);
                    isPlaying = false;
                    mp.pause();
                    songBackground.clearAnimation();
                }else {
                    Drawable pauseDrawable = view.getResources().getDrawable(R.drawable.pause);
                    playPause.setImageDrawable(pauseDrawable);
                    isPlaying = true;
                    mp.start();
                    songBackground.startAnimation(operatingAnim);
                }
                break;
            case R.id.play_down:
                break;
        }
    }
    @Override
    public void parseLrc(String lrcLink) {
        ParseLrc parseLrc = new ParseLrc(lrcLink);
    }
    public void stopActivity() {
        if(mp != null){
            mp.stop();
            mp = null;
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
    /**
     *  弱引用 的Handler
     *  执行业务逻辑处理并分发到view执行界面渲染
     */
    private static class SafeHandler extends Handler {
        private WeakReference<PlayPresenterAchi> playPresenterAchi;

        private SafeHandler(PlayPresenterAchi achi) {
            playPresenterAchi = new WeakReference<>(achi);
        }
        @Override
        public void handleMessage(Message msg) {
            String desc;
            PlayPresenterAchi achi = playPresenterAchi.get();
            if (achi != null) {
                Bundle data = msg.getData();
                switch (msg.what) {
                    case  GET_SONG_INFO_SUCCESS:
                        //解析数据
                        achi.playView.parseSongInfoMessage(data.getString("songResult"));

                        break;
                    case  GET_SONG_INFO_ERROR :
                        desc = data.getString("desc");
                        achi.playView.showToast(desc);
                        break;
                    case JSON_ERROR:
                        achi.playView.showToast(data.getString("JSON_ERROR"));
                        break;
                    default:
                        break;
                }
            }

        }
    }
}
