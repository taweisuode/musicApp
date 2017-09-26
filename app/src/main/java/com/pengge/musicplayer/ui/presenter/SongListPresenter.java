package com.pengge.musicplayer.ui.presenter;

/**
 * Created by pengge on 16/12/1.
 */

public interface SongListPresenter {
    String getParams();
    //void getImageFromApi();

    void getSongListFromApi(String playListId);
}
