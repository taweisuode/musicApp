package com.pengge.musicplayer.tools;

import com.pengge.musicplayer.BuildConfig;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;


/**
 * Created by pengge on 16/9/14.
 */
public class ShowLog {
    static String className,methodName,lineNumber;
    private static String showTag(StackTraceElement[] stackTrace) {

        //跟踪类名方法名行数
        className = stackTrace[1].getFileName();
        methodName = stackTrace[1].getMethodName();
        lineNumber = String.valueOf(stackTrace[1].getLineNumber());

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("(className:").append(className).append(", methodName:").append(methodName);
        stringBuffer.append(", lineIn:").append(lineNumber).append(")");
        return stringBuffer.toString();
    }
    public static void d(String... args) {

        if(!BuildConfig.DEBUG) {
            return;
        }
        switch (args.length) {
            case 1:
                Log.d(showTag(new Throwable().getStackTrace()), String.valueOf(args[0]));
                break;
            case 2:
                Log.d(args[0],args[1]);
                break;
            default:
                Log.e("tag","Max num of method params only limit in 2!");
                break;
        }

    }
    public static void v(String... args) {
        if(!BuildConfig.DEBUG) {
            return;
        }
        switch (args.length) {
            case 1:
                Log.v(showTag(new Throwable().getStackTrace()), String.valueOf(args[0]));
                break;
            case 2:
                Log.v(args[0],args[1]);
                break;
            default:
                Log.e("tag","Max num of method params only limit in 2!");
                break;
        }
    }
    public static void e(String... args) {
        if(!BuildConfig.DEBUG) {
            return;
        }
        switch (args.length) {
            case 1:
                Log.e(showTag(new Throwable().getStackTrace()), String.valueOf(args[0]));
                break;
            case 2:
                Log.e(args[0],args[1]);
                break;
            default:
                Log.e("tag","Max num of method params only limit in 2!");
                break;
        }
    }


}
