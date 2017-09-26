package com.pengge.musicplayer.ui.presenter;

import com.pengge.musicplayer.diyView.MyScrollView;
import java.util.HashMap;

/**
 * Created by pengge on 16/12/1.
 */

public interface RandomListenPresenter {
    String getParams();
    void getChartListFromUrl(final String type, final HashMap map);
    void setScrollView(MyScrollView scrollView);
}
