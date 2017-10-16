package com.pengge.musicplayer.musicModel;

import com.pengge.musicplayer.tools.ShowLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    public class LrcObject {
        private String title;
        private String author;
        private String album;
        private ArrayList<HashMap<String,String>> lrcNode = new ArrayList<>();
        public String getTitle() {
            return title;
        }
        public String getAuthor() {
            return author;
        }
        public String getAlbum() {
            return album;
        }
    }
    public ParseLrc(String lrcUrl) {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(lrcUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            ShowLog.e(result);
            parseSong(result);
            this.lrc = result;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void  parseSong(String lrc) {
        LrcObject lrcObject = new LrcObject();
        String[] eachNode = lrc.split("\n");
        for (int i = 0;i<eachNode.length;i++) {
            String[] titleArr = eachNode[i].split("[ti:]");
            if(titleArr.length > 0) {
                lrcObject.title = titleArr[1];
            }
            ShowLog.e(lrcObject.getTitle());
        }
    }
}
