package com.pengge.musicplayer.musicModel;

import com.pengge.musicplayer.tools.ShowLog;

/**
 * Created by pengge on 16/11/15.
 */

public class ParseLrc {
    private String lrc;

    public ParseLrc(String lrc) {
        this.lrc = lrc;
        ShowLog.e(lrc);
    }
}
