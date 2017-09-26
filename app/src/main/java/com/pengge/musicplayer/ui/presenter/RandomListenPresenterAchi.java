package com.pengge.musicplayer.ui.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.pengge.musicplayer.R;
import com.pengge.musicplayer.diyView.MyScrollView;
import com.pengge.musicplayer.tools.OKHttpManager;
import com.pengge.musicplayer.ui.model.RandomListenModel;
import com.pengge.musicplayer.ui.view.RandomListenView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by pengge on 16/12/1.
 */

public class RandomListenPresenterAchi implements RandomListenPresenter {
    private RandomListenView randomListenView;


    private HashMap chartList= null;
    private MyScrollView scrollView;
    private SafeHandler safeHandler;
    private RandomListenModel randomListenModel;

    private static final int GET_LIST_SUCCESS = 100;
    private static final int GET_LIST_ERROR = 99;
    private static final int JSON_ERROR = 1008;

    public RandomListenPresenterAchi(RandomListenView randomListenView) {
        this.randomListenView = randomListenView;
        initRandomListenModel();
        safeHandler = new SafeHandler(this);
    }
    /**
     *  绑定randomListenModel
     */
    private void initRandomListenModel() {
        randomListenModel = new RandomListenModel();
    }

    /**
     *  获取随机音乐接口参数(从model中获得)
     */
    @Override
    public String getParams() {
       return randomListenModel.getRandomType();
    }

    /**
     *  获取服务列表数据
     */
    @Override
    public void getChartListFromUrl(final String type,final HashMap map) {
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                HashMap<String, String> params = new HashMap<>();
                //offset 表示:最后一条数据在已加载数据中的位置数
                params.put("method","baidu.ting.billboard.billList");
                params.put("type", type);
                params.put("size", String.valueOf(map.get("size")));
                params.put("offset", String.valueOf(map.get("offset")));
                String url = scrollView.getContext().getResources().getString(R.string.baidu_music_api_root);
                OKHttpManager okHttpManager = OKHttpManager.getInstance();
                JSONObject result = okHttpManager.get(url,params);
                Log.e("result",result.toString());
                try {
                    JSONArray songListArray = result.getJSONArray("song_list");
                    if(songListArray.length() == 0) {
                        msg.what = GET_LIST_ERROR;
                        safeHandler.sendMessage(msg);
                        return;
                    }
                    data.putString("songResult",result.toString());
                    data.putInt("flag", (Integer) map.get("flag"));
                    msg.setData(data);
                    msg.what = GET_LIST_SUCCESS;
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
    /**
     *  获取scrollView
     */
    @Override
    public void setScrollView(MyScrollView scrollView) {
        this.scrollView = scrollView;
    }

    /**
     *  弱引用 的Handler
     *  执行业务逻辑处理并分发到view执行界面渲染
     */
    private static class SafeHandler extends Handler {
        private WeakReference<RandomListenPresenterAchi> randomListenPresenterAchi;

        private SafeHandler(RandomListenPresenterAchi achi) {
            randomListenPresenterAchi = new WeakReference<>(achi);
        }
        @Override
        public void handleMessage(Message msg) {
            RandomListenPresenterAchi achi = randomListenPresenterAchi.get();
            if (achi != null) {
                Bundle data = msg.getData();
                switch (msg.what) {
                    case  GET_LIST_SUCCESS:
                        //解析数据
                        achi.randomListenView.parseMessage(data.getString("songResult"),data.getInt("flag"));

                        break;
                    case  GET_LIST_ERROR :
                        String desc = data.getString("desc");
                        achi.randomListenView.showToast(desc);
                        break;
                    case JSON_ERROR:
                        achi.randomListenView.showToast(data.getString("JSON_ERROR"));
                        break;
                    default:
                        break;
                }
            }
            if(achi.scrollView != null) {
                achi.scrollView.finishRefreshing();
            }

        }
    }

}
