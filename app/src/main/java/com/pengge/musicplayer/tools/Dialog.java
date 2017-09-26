package com.pengge.musicplayer.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;

/**
 * Created by pengge on 16/9/12.
 */
public class Dialog {
    private static ProgressDialog dialog;
    private static  final long SET_TIME_OUT = 20000;
    public Dialog(Context context) {
        dialog = new ProgressDialog(context);
    }
    public void  show(CharSequence title, CharSequence message,boolean cancelable) {
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(cancelable);
        dialog.show();
    }
    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message) {
        return show(context, title, message, false);
    }

    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate,
                                      boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }
    private static void timerDelayRemoveDialog(long time, final ProgressDialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(d != null) {
                    d.dismiss();
                }
            }
        }, time);
    }
    public boolean isShowing() {
        return dialog.isShowing();
    }
    public void dismiss()
    {
        dialog.dismiss();
    }
    public void hide() {
        dialog.hide();
    }
}
