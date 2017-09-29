package com.pengge.musicplayer.ui.presenter;

/**
 * Created by pengge on 16/12/1.
 */

public interface PlayPresenter {
    void getSongInfoFromApi(String playListId);
    void initPlay(String songDownLoadUrl);

    void parseLrc(String lrcLink);
}
