package com.pengge.musicplayer.ui.model;

import com.pengge.musicplayer.dataCenter.dataManage;

import java.util.HashMap;

/**
 * Created by pengge on 16/12/2.
 */

public class RandomListenModel {
    public RandomListenModel() {

    }
    public String getRandomType() {
        HashMap chartList = dataManage.getChartList();
        return dataManage.randomType();
    }
}
