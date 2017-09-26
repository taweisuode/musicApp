package com.pengge.musicplayer.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;

import com.pengge.musicplayer.R;
import com.pengge.musicplayer.diyView.MyScrollView;
import com.pengge.musicplayer.tools.ImageLoaderManager;
import com.pengge.musicplayer.tools.ShowLog;
import com.pengge.musicplayer.ui.presenter.RandomListenPresenterAchi;
import com.pengge.musicplayer.ui.view.RandomListenView;

public class RandomListenViewActivity extends AppCompatActivity implements RandomListenView {
    private  LayoutInflater inflater;
    private LinearLayout serviceList;
    private ImageLoaderManager imageLoaderManager = null;
    private MyScrollView scrollView;
    private RandomListenPresenterAchi randomListenViewActivity;

    private HashMap freshMap;
    private HashMap loadMap;
    private int inflateSize;
    private int lastSize;
    private static final int FRESH_FLAG = 1;
    private static final int LOAD_FLAG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_listen_view);
        serviceList = (LinearLayout)findViewById(R.id.service_list);
        scrollView = (MyScrollView)findViewById(R.id.scroll_view);
        init();
    }
    /**
     *  初始化内容
     *  1.完成与presenter的绑定
     *  2.指挥调用presenter 中的方法
     *  3.设置下拉刷新监听回调
     */
    private void  init() {
        freshMap = new HashMap();
        freshMap.put("flag",FRESH_FLAG);
        freshMap.put("size","10");
        freshMap.put("offset","0");

        loadMap = new HashMap();
        loadMap.put("size","10");
        loadMap.put("flag",LOAD_FLAG);

        randomListenViewActivity = new RandomListenPresenterAchi(this);
        randomListenViewActivity.setScrollView(scrollView);
        final String params = randomListenViewActivity.getParams();

        randomListenViewActivity.getChartListFromUrl(params,freshMap);

        scrollView.setOnRefreshListener(new MyScrollView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                String refreshParams = randomListenViewActivity.getParams();
                randomListenViewActivity.getChartListFromUrl(refreshParams,freshMap);
            }
            @Override
            public void onReload() {
                String reLoadParams = randomListenViewActivity.getParams();
                loadMap.put("offset","0");
                randomListenViewActivity.getChartListFromUrl(reLoadParams,loadMap);
            }
        },1);
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
    /**
     *  返回上个activity
     */
    public void skipBackView(View view) {
        finish();
    }


    /**
     *  执行数据解析
     */
    @Override
    public void parseMessage(String songResult, int flag) {
        inflater = LayoutInflater.from(this);
        Matcher matcher;
        try {
            JSONObject songResultObject = new JSONObject(songResult);
            JSONArray songListArray = songResultObject.getJSONArray("song_list");
            inflateSize = songListArray.length();
            //这块的逻辑是:如果为刷新,则上一次刷新数为这次的inflateSize
            if(flag == FRESH_FLAG) {
                lastSize = inflateSize;
            }else if(flag == LOAD_FLAG) {
                //这块记录上一次的jsonarray的长度
                lastSize = lastSize + inflateSize;
            }
            for (int i = 0;i < inflateSize;i++) {
                final JSONObject jsonObject = songListArray.getJSONObject(i);

                inflater.inflate(R.layout.list_view_template1,serviceList,true);
                LinearLayout childLayout = (LinearLayout)serviceList.getChildAt(i);

                childLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String finalJsonString = jsonObject.toString();
                        ShowLog.e("finalJsonObject="+finalJsonString);
                        Intent intent = new Intent(RandomListenViewActivity.this,PlayActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("songObject",finalJsonString);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                //更新歌曲标题
                String songTitle = jsonObject.getString("title");
                TextView song_title = (TextView)childLayout.findViewById(R.id.song_title);
                song_title.setText(songTitle);

                //更新歌手以及album
                String author = jsonObject.getString("author");
                String album_title = jsonObject.getString("album_title");
                TextView singerName = (TextView)childLayout.findViewById(R.id.singer_name);
                singerName.setText(author+" - "+album_title);

                //更新图片
                ImageView singerImage = (ImageView)childLayout.findViewById(R.id.singer_image);
                String imgUrl = jsonObject.getString("pic_small");
                matcher = Patterns.WEB_URL.matcher(imgUrl);
                boolean hasImg  = matcher.find();
                if (hasImg) {
                    getImageLoaderManager().getImageLoader().displayImage(imgUrl, singerImage, getImageLoaderManager().getOptions());
                } else {
                    singerImage.setVisibility(View.GONE);
                }
            }

            //删除多余的结点
            int maxSize = 0;
            if(flag == (FRESH_FLAG)) {
                maxSize = inflateSize;
            }else if(flag == (LOAD_FLAG)) {
                maxSize = lastSize;
            }
            //2个参数 removeViews(index,count)一个为索引,一个为数量
            serviceList.removeViews(maxSize,serviceList.getChildCount()-maxSize);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *  显示toast
     */
    @Override
    public void showToast(String desc) {
        Toast.makeText(RandomListenViewActivity.this,desc,Toast.LENGTH_SHORT).show();
    }

}
