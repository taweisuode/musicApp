package com.pengge.musicplayer.ui.view;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by pengge on 16/12/1.
 */

public interface RecommendMainListenView {
    void parseImageMessage(ArrayList<Bitmap> bitmapArr);
    void parseSongMessage(String songResult);
    void showToast(String desc);
}
