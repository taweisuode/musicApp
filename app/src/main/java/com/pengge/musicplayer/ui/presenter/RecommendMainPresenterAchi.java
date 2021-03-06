package com.pengge.musicplayer.ui.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.pengge.musicplayer.R;
import com.pengge.musicplayer.tools.OKHttpManager;
import com.pengge.musicplayer.ui.view.RecommendMainListenView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import static com.pengge.musicplayer.tools.OKHttpManager.getUnsafeOkHttpClient;


public class RecommendMainPresenterAchi  implements RecommendMainPresenter{
    private RecommendMainListenView recommendMainListenView;
    public View view;
    private SafeHandler safeHandler;
    private static final int GET_IMAGE_LIST_SUCCESS = 200;
    private static final int GET_SONG_LIST_SUCCESS = 201;
    private static final int GET_IMAGE_LIST_ERROR = 500;
    private static final int GET_SONG_LIST_ERROR = 501;
    private static final int JSON_ERROR = 601;
    public RecommendMainPresenterAchi(RecommendMainListenView recommendMainListenView) {
        this.recommendMainListenView = recommendMainListenView;
        safeHandler = new SafeHandler(this);
    }

    @Override
    public String getParams() {
        return null;
    }
    public void setView(View view) {
        this.view = view;
    }
    private static ArrayList<Bitmap> initBitmap(String imageResult) {
        List<Bitmap> bitmapContainer = new ArrayList<>();
        try {
            JSONObject jsonImageResult = new JSONObject(imageResult);
            JSONArray imageArr = jsonImageResult.getJSONArray("data");
            if(imageArr.length() > 0) {
                for (int i = 0;i <imageArr.length();i++) {
                    JSONObject imageObject = imageArr.getJSONObject(i);
                    String imageUrl = imageObject.getString("img_url");
                    OkHttpClient client = getUnsafeOkHttpClient();

                    //获取请求对象
                    Request request = new Request.Builder().url(imageUrl).build();

                    //获取响应体

                    ResponseBody body = client.newCall(request).execute().body();

                    //获取流
                    InputStream in = body.byteStream();
                    //转化为bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    bitmapContainer.add(bitmap);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (ArrayList<Bitmap>) bitmapContainer;
    }
    @Override
    public void getImageFromApi() {
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                HashMap<String, String> params = new HashMap<>();
                String url =view.getContext().getResources().getString(R.string.recommend_image_api);
                OKHttpManager okHttpManager = OKHttpManager.getInstance();
                JSONObject result = okHttpManager.get(url);
                try {
                    JSONObject errorObject= result.getJSONObject("e");
                    Integer errorCode = errorObject.getInt("code");
                    if(errorCode != 0) {
                        msg.what = GET_IMAGE_LIST_ERROR;
                        safeHandler.sendMessage(msg);
                        return;
                    }
                    data.putString("imageResult",result.toString());
                    data.putParcelableArrayList("bitmapArr",initBitmap(result.toString()));
                    msg.setData(data);
                    msg.what = GET_IMAGE_LIST_SUCCESS;
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
    public void getSongListFromApi() {
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                HashMap<String, String> params = new HashMap<>();
                String url =view.getContext().getResources().getString(R.string.recommend_song_show);
                OKHttpManager okHttpManager = OKHttpManager.getInstance();
                JSONObject result = okHttpManager.get(url);
                try {
                    JSONObject errorObject= result.getJSONObject("e");
                    Integer errorCode = errorObject.getInt("code");
                    if(errorCode != 0) {
                        msg.what = GET_SONG_LIST_ERROR;
                        safeHandler.sendMessage(msg);
                        return;
                    }
                    data.putString("songResult",result.toString());
                    //data.putParcelableArrayList("bitmapArr",initBitmap(result.toString()));
                    msg.setData(data);
                    msg.what = GET_SONG_LIST_SUCCESS;
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
     *  弱引用 的Handler
     *  执行业务逻辑处理并分发到view执行界面渲染
     */
    private static class SafeHandler extends Handler {
        private WeakReference<RecommendMainPresenterAchi> recommendMainPresenterAchi;

        private SafeHandler(RecommendMainPresenterAchi achi) {
            recommendMainPresenterAchi = new WeakReference<>(achi);
        }
        @Override
        public void handleMessage(Message msg) {
            String desc;
            RecommendMainPresenterAchi achi = recommendMainPresenterAchi.get();
            if (achi != null) {
                Bundle data = msg.getData();
                switch (msg.what) {
                    case  GET_IMAGE_LIST_SUCCESS:
                        //解析数据
                        ArrayList<Bitmap> bitmapArr = data.getParcelableArrayList("bitmapArr");
                        achi.recommendMainListenView.parseImageMessage(bitmapArr);

                        break;
                    case  GET_SONG_LIST_SUCCESS:
                        //解析数据
                        achi.recommendMainListenView.parseSongMessage(data.getString("songResult"));

                        break;
                    case  GET_IMAGE_LIST_ERROR :
                        desc = data.getString("desc");
                        achi.recommendMainListenView.showToast(desc);
                        break;
                    case  GET_SONG_LIST_ERROR :
                        desc = data.getString("desc");
                        achi.recommendMainListenView.showToast(desc);
                        break;
                    case JSON_ERROR:
                        achi.recommendMainListenView.showToast(data.getString("JSON_ERROR"));
                        break;
                    default:
                        break;
                }
            }

        }
    }
}
