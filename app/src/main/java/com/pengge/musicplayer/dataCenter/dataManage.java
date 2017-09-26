package com.pengge.musicplayer.dataCenter;

import java.util.HashMap;

/**
 * Created by pengge on 16/11/10.
 */

public class dataManage {
    private static HashMap<String,String> chartList =  new HashMap();
    private static String[] randomTypeList;
    public static HashMap getChartList() {
        chartList.put("1","新歌榜");
        chartList.put("2","热歌榜");
        chartList.put("11","摇滚榜");
        chartList.put("12","爵士");
        chartList.put("16","流行");
        chartList.put("21","欧美金曲榜");
        chartList.put("22","经典老歌榜");
        chartList.put("23","情歌对唱榜");
        chartList.put("24","影视金曲榜");
        chartList.put("25","网络歌曲榜");
        dataManage.randomTypeList = new String[]{"1", "2", "11", "12", "16", "21", "22", "23", "24", "25"};
        return chartList;
    }
    public static String randomType() {
        final double d = Math.random();
        final int i = (int)(d*randomTypeList.length);
        return randomTypeList[i];
    }
    public static String millersToSecond(int millers) {
        String all_time ;
        millers = millers/1000;
        int all_m  = millers%60;
        int all_f  = millers/60;
        if (all_f<10){
            all_time= "0"+String.valueOf(all_f)+":";
        }else {
            all_time=String.valueOf(all_f)+":";
        }
        if(all_m<10){
            all_time +="0"+String.valueOf(all_m);
        }else {
            all_time += String.valueOf(all_m);
        }
        return all_time;
    }
}
