package com.pengge.musicplayer.ui.presenter;

import com.pengge.musicplayer.diyView.MyScrollView;

import java.util.HashMap;

/**
 * Created by pengge on 16/12/1.
 */

public interface RecommendMainPresenter {
    String getParams();
    void getImageFromApi();

    void getSongListFromApi();
    //void setScrollView(MyScrollView scrollView);
}
