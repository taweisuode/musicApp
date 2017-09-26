package com.pengge.musicplayer.tools;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pengge.musicplayer.R;

/**
 * Created by pengge on 16/8/16.
 */
public class ImageLoaderManager {
    private Context context;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = null;
    private ImageLoaderConfiguration configuration = null;
    private static ImageLoaderManager instance = null;

    public synchronized static ImageLoaderManager getInstance(Context context) {
        if (instance == null) {
            instance = new ImageLoaderManager(context);
        }

        return instance;
    }

    private ImageLoaderManager(Context context) {
        this.context    = context;
    }

    public void init() {
        if (imageLoader != null) {
            return ;
        }

        //TODO 上线前需要把调试关掉
        configuration  = new ImageLoaderConfiguration.Builder(context)
                .writeDebugLogs()
                .build();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.placeholder)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(configuration);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public DisplayImageOptions getOptions() {
        return options;
    }
}
