package com.pengge.musicplayer.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pengge.musicplayer.R;
import com.pengge.musicplayer.diyView.GridItem;
import com.pengge.musicplayer.diyView.MainGridViewAdapter;
import com.pengge.musicplayer.tools.ImageLoaderManager;
import com.pengge.musicplayer.tools.ShowLog;
import com.pengge.musicplayer.ui.presenter.RecommendMainPresenterAchi;
import com.pengge.musicplayer.ui.view.RecommendMainListenView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendMainFragment extends Fragment implements RecommendMainListenView{
    private View view;
    private RelativeLayout imageGroup;
    private ViewPager viewPager;
    private ImageView dot_1,dot_2,dot_3,dot_4,dot_5,dot_6;
    private GridView gridView;
    private ImageLoaderManager imageLoaderManager = null;

    public RecommendMainPresenterAchi recommendMainPresenterAchi;

    private List<ImageView> imageViewContainer = null;
    public ArrayList<GridItem> songList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.recommend_fragment_main, container, false);

        initView();
        bindPresenterAchi();


        return view;
    }
    //初始化页面
    private void initView() {
        imageGroup = (RelativeLayout)view.findViewById(R.id.imageGroup);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        dot_1 = (ImageView) view.findViewById(R.id.dot_1);
        dot_2 = (ImageView) view.findViewById(R.id.dot_2);
        dot_3 = (ImageView) view.findViewById(R.id.dot_3);
        dot_4 = (ImageView) view.findViewById(R.id.dot_4);
        dot_5 = (ImageView) view.findViewById(R.id.dot_5);
        dot_6 = (ImageView) view.findViewById(R.id.dot_6);
        dot_1.setAlpha((float) 1.0);

        gridView = (GridView)view.findViewById(R.id.grid_view);
    }
    //绑定presenterAchi
    public void bindPresenterAchi() {
        recommendMainPresenterAchi = new RecommendMainPresenterAchi(this);
        recommendMainPresenterAchi.setView(view);
        recommendMainPresenterAchi.getImageFromApi();
        recommendMainPresenterAchi.getSongListFromApi();
    }
    //根据api返回的图片地址进行初始化页面
    private void renderView() {
        viewPager.setAdapter(new BannerAdapter());
        viewPager.addOnPageChangeListener(new BannerPageChangeListener());
        viewPager.setCurrentItem(0);
    }
    /**
     * ViewPager的适配器
     */
    private class BannerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViewContainer.get(position % imageViewContainer.size()));

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = imageViewContainer.get(position % imageViewContainer.size());

            container.addView(view);
            return view;
        }
        //欢迎界面则设为展示的页面总数,如果为轮播 则设置一个大数
        @Override
        public int getCount() {
            //页面最大值  (轮播时采用一个大数来近似于无限循环)
            return Integer.MAX_VALUE;
            ///return imageViewContainer.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    /**
     * Banner的Page切换监听器
     */
    private class BannerPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // Nothing to do
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // Nothing to do
        }

        @Override
        public void onPageSelected(int position) {
            int newPosition = position%6;
            Log.d("tag",String.valueOf(position));
            dot_1.setAlpha((float) 0.2);
            dot_2.setAlpha((float) 0.2);
            dot_3.setAlpha((float) 0.2);
            dot_4.setAlpha((float) 0.2);
            dot_5.setAlpha((float) 0.2);
            dot_6.setAlpha((float) 0.2);
            ImageView nowImageView = (ImageView) imageGroup.getChildAt(newPosition);
            nowImageView.setAlpha((float) 1.0);
        }
    }

    @Override
    public void parseImageMessage(ArrayList<Bitmap> bitmapArr) {
        imageViewContainer = new ArrayList<>();
        if(bitmapArr.size() > 0) {
            for (int i = 0;i <bitmapArr.size();i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setImageBitmap(bitmapArr.get(i));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageViewContainer.add(imageView);
            }
        }
        renderView();
    }

    @Override
    public void parseSongMessage(String songResult) {
        songList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(songResult);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0 ;i < 6 ; i++) {
                JSONObject listNode = (JSONObject) jsonArray.get(i);
                GridItem gridItem = new GridItem();
                gridItem.setThumb(listNode.getString("thumb"));
                gridItem.setTitle(listNode.getString("title"));
                gridItem.setPlaylistId(listNode.getString("playlist_id"));
                gridItem.setListenCount(listNode.getString("listen_count"));
                songList.add(gridItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ShowLog.e(songList.toString());
        MainGridViewAdapter mainGridViewAdapter = new MainGridViewAdapter(getContext(),R.layout.recommend_gridview_template,songList);
        gridView.setAdapter(mainGridViewAdapter);
    }

    @Override
    public void showToast(String desc) {

    }
}
