package com.pengge.musicplayer.musicModel;

import com.pengge.musicplayer.tools.ShowLog;

import java.io.IOException;
import java.util.TreeMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pengge on 16/11/15.
 */

public class ParseLrc {
    private String lrc;
    private OkHttpClient client = null;

    public ParseLrc(String lrcUrl) {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(lrcUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            ShowLog.e(result);
            this.lrc = result;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
