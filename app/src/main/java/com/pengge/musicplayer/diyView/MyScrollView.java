package com.pengge.musicplayer.diyView;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pengge.musicplayer.R;

/**
 * Created by pengge on 16/10/21.
 */

public class MyScrollView extends ScrollView {
    private View parentView;
    private Context context;
    //手指按下时的屏幕纵坐标
    private float yDown;
    private PullToRefreshListener mlistener;
    private int mid = 1;
    //手指移动最大距离
    private int maxFingerMove;
    private int initMarginTop;
    private int initMarginBottom;
    private MarginLayoutParams scrollViewLayoutParams;
    int i = 0;
    int youment_count = 0;
    //拉力倍数
    private  final float PULL_TIMES = (float) 5;
    //下拉刷新一些控件
    private View freshHeader;
    private ProgressBar freshProgressBar;
    private ImageView freshArrowImage;
    private TextView freshDescription;
    private int freshHeight;
    private int currentFreshStatus = STATUS_REFRESH_FINISHED;
    //下拉状态
    public static final int STATUS_PULL_TO_REFRESH = 0;
    //释放立即刷新状态
    public static final int STATUS_RELEASE_TO_REFRESH = 1;
    //正在刷新状态
    public static final int STATUS_REFRESHING = 2;
    //刷新完成或未刷新状态
    public static final int STATUS_REFRESH_FINISHED = 3;

    //上拉加载的一些控件
    private View loadBottom;
    private ProgressBar loadProgressBar;
    private ImageView loadArrowImage;
    private TextView loadDescription;
    private int loadHeight;
    private int currentLoadStatus = STATUS_RELOAD_FINISHED;
    //上拉状态
    public static final int STATUS_PULL_TO_RELOAD = 4;
    //释放立即加载状态
    public static final int STATUS_RELEASE_TO_RELOAD = 5;
    //正在加载状态
    public static final int STATUS_RELOADING = 6;
    //加载完成或未加载状态
    public static final int STATUS_RELOAD_FINISHED = 7;
    private  boolean isTouchable  = true;
    public boolean isTouchable() {
        return isTouchable;
    }
    //增加是否可以被点击功能
    public void setTouchable(boolean isTouchable) {
        this.isTouchable = isTouchable;
    }

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /**
     * 进行一些关键性的初始化操作，这里可以加载其下拉上拉界面并根据touch事件做出改变
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        parentView = (View) this.getParent();

        //计算导航栏高度
        if(parentView.findViewById(R.id.tool_bar) != null) {
            View freshToolBar = parentView.findViewById(R.id.tool_bar);
            initMarginTop =freshToolBar.getHeight();
        }else {
            initMarginTop = 0;
        }

        LayoutInflater layoutInflater;
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        maxFingerMove = ViewConfiguration.get(context).getScaledTouchSlop();

        scrollViewLayoutParams = (MarginLayoutParams) getLayoutParams();
        /**
         * 这里只记录第一次的marginbottom值,因为这个onLayout是由父View触发当前View layout方法的时候的一个callback.
         * 当布局里面我任何一个View的frame改变了, 都会触发最上层的View的onLayout, 然后一级一级的向下传递.
         * 所以每次加载时bottomMargin 会不断增加,所以这块取第一次的bottomMargin值
         */
        if(i == 0) {
            initMarginBottom = scrollViewLayoutParams.bottomMargin;
            youment_count = 0;
        }
        i = i + 1;
        //加载下拉刷新界面
        LinearLayout pullFreshView = (LinearLayout) parentView.findViewById(R.id.add_fresh_view);
        freshHeader = layoutInflater.inflate(R.layout.pull_to_fresh, pullFreshView, true);
        freshHeight = freshHeader.getHeight();
        freshProgressBar = (ProgressBar) freshHeader.findViewById(R.id.fresh_progress_bar);
        freshArrowImage = (ImageView) freshHeader.findViewById(R.id.fresh_arrow);
        freshDescription = (TextView) freshHeader.findViewById(R.id.fresh_description);
        pullFreshView.setVisibility(VISIBLE);

        //加载上拉加载界面
        LinearLayout pullLoadingView = (LinearLayout) parentView.findViewById(R.id.add_load_view);
        loadBottom = layoutInflater.inflate(R.layout.pull_to_load,pullLoadingView,true);
        loadHeight = loadBottom.getHeight();
        loadProgressBar = (ProgressBar) loadBottom.findViewById(R.id.load_progress_bar);
        loadArrowImage = (ImageView) loadBottom.findViewById(R.id.load_arrow);
        loadDescription = (TextView) loadBottom.findViewById(R.id.load_description);
        pullLoadingView.setVisibility(VISIBLE);

    }
    /**
     *   这里需要重写onInterceptTouchEvent的目的是:1.考虑到touch这块事件传递,如果想要获取onTouchEvent的ACTION_DOWN的值
     *   就必须将onInterceptTouchEvent的返回值设为true,但是这样的话,scrollview中的控件就无法监听到onclick事件;2.如果去除
     *   重写onInterceptTouchEvent的方法,则获取不到onTouchEvent的ACTION_DOWN的值,则下拉刷新就显得非常不正常;3.所以我在
     *   onInterceptTouchEvent的ACTION_DOWN中获取的y轴的值,然后传给touchEvent做判断。则解决了这2个冲突问题,4.scrollview
     *   在不饱满的情况下不会触发onTouchEvent,所以在ontouchEvent写的逻辑同时在onInterceptTouchEvent中显示
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(!isTouchable){
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                yDown = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveAction(event);
                break;
            case MotionEvent.ACTION_UP:
            default:
                upAction(event);
                break;
        }
        afterAction(event);
        return super.onInterceptTouchEvent(event);
    }

    //是否需要刷新
    private boolean canPullFresh(MotionEvent event) {
        LinearLayout serviceList = (LinearLayout) this.findViewById(R.id.service_list);
        View serviceChildView = serviceList.getChildAt(0);
        Rect rect = new Rect();
        Point globalOffset = new Point();
        if(serviceChildView == null) {
            serviceList.getGlobalVisibleRect(rect,globalOffset);
        }else {
            serviceChildView.getGlobalVisibleRect(rect,globalOffset);
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            return globalOffset.y >= rect.top;
        }
        return true;
    }
    //是否需要加载
    private boolean canPullLoad(MotionEvent event) {
        LinearLayout serviceList = (LinearLayout) this.findViewById(R.id.service_list);
        Rect rect = new Rect();
        Point globalOffset = new Point();
        if(serviceList.getChildCount() == 0) {
            serviceList.getGlobalVisibleRect(rect,globalOffset);
        }else {
            //获取列表最后一个元素
            int index = serviceList.getChildCount() -1 ;
            View serviceChildView = serviceList.getChildAt(index);
            serviceChildView.getGlobalVisibleRect(rect,globalOffset);
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            return globalOffset.y == rect.top;
        }
        return true;
    }
    //改变刷新时的文字
    private void changeFreshView() {

        freshProgressBar.setVisibility(GONE);
        freshArrowImage.setVisibility(VISIBLE);
        //更改文字
        if(currentFreshStatus == STATUS_PULL_TO_REFRESH) {
            freshDescription.setText("下拉可以刷新");
        }else if(currentFreshStatus == STATUS_RELEASE_TO_REFRESH) {
            freshDescription.setText("释放刷新");
        }

    }
    //改变加载时的文字
    private void changeLoadView() {

        loadProgressBar.setVisibility(GONE);
        loadArrowImage.setVisibility(VISIBLE);
        //更改文字
        if(currentLoadStatus == STATUS_PULL_TO_RELOAD) {
            loadDescription.setText("上拉继续加载");
        }else if(currentLoadStatus == STATUS_RELEASE_TO_RELOAD) {
            loadDescription.setText("释放加载数据");
        }

    }
    //正在刷新动作
    private void doFreshView() {
        currentFreshStatus = STATUS_REFRESHING;
        //加载圈圈
        freshArrowImage.setVisibility(GONE);
        freshProgressBar.setVisibility(VISIBLE);
        freshDescription.setText("正在加载...");

    }
    //正在加载动作
    private void doLoadView() {
        currentLoadStatus = STATUS_RELOADING;
        //加载圈圈
        loadArrowImage.setVisibility(GONE);
        loadProgressBar.setVisibility(VISIBLE);
        loadDescription.setText("正在加载...");
    }
    //结束刷新界面
    private void finishFreshView() {
        currentFreshStatus = STATUS_REFRESH_FINISHED;
        if(freshProgressBar != null && freshArrowImage != null) {
            freshProgressBar.setVisibility(GONE);
            freshArrowImage.setVisibility(GONE);
        }
        if(scrollViewLayoutParams != null) {
            scrollViewLayoutParams.topMargin = initMarginTop;
            this.setLayoutParams(scrollViewLayoutParams);
        }
    }
    //结束加载界面
    private void finishloadView() {
        currentLoadStatus = STATUS_RELOAD_FINISHED;
        if(loadProgressBar != null && loadArrowImage != null) {
            loadProgressBar.setVisibility(GONE);
            loadArrowImage.setVisibility(GONE);
        }
        if(scrollViewLayoutParams != null) {
            scrollViewLayoutParams.bottomMargin = initMarginBottom;
            this.setLayoutParams(scrollViewLayoutParams);
        }
    }
    /**
     * 正在刷新的任务，doInBackground 回去回调注册该监听器的控件的onRefresh方法
     */
    class RefreshingTask extends AsyncTask<Void, Integer, Void> {
        //设置刷新的视图
        @Override
        protected void onPreExecute() {
            doFreshView();
        }
        @Override
        protected Void doInBackground(Void... params) {
            if(mlistener != null) {
                mlistener.onRefresh();
            }
            return null;
        }
    }
    /**
     * 正在加载的任务，doInBackground 回去回调注册该监听器的控件的onReload方法
     */
    class ReloadingTask extends AsyncTask<Void, Integer, Void> {
        //设置加载的视图
        @Override
        protected void onPreExecute() {
            doLoadView();
        }
        @Override
        protected Void doInBackground(Void... params) {
            if(mlistener != null) {
                mlistener.onReload();
            }
            return null;
        }
    }
    //TODO 只有在onTouchEvent  才能监听到ACTION_DOWN 事件,在onTouch 方法中监听不到(待查明问题原因)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //当onInterceptTouchEvent为true时,才会传递到本view的onTouchEvent事件,并且为false时,才说明不可点击
        if(!isTouchable){
            return false;
        }
        super.onTouchEvent(event);
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                moveAction(event);
                break;
            case MotionEvent.ACTION_UP:
            default:
               upAction(event);
                break;
        }
        afterAction(event);
        return true;
    }
    private boolean moveAction(MotionEvent event) {
        float yMove = event.getRawY();
        //说明是下拉
        if(yMove > yDown) {
            if(!canPullFresh(event)) {
                return false;
            }
            int distance = (int) ((yMove - yDown));
            if (distance < maxFingerMove) {
                return false;
            }
            if (currentFreshStatus != STATUS_REFRESHING) {

                if (scrollViewLayoutParams.topMargin - initMarginTop > freshHeight - 40) {
                    currentFreshStatus = STATUS_RELEASE_TO_REFRESH;
                } else {
                    currentFreshStatus = STATUS_PULL_TO_REFRESH;
                }
                scrollViewLayoutParams.topMargin = (int) (initMarginTop + (distance/PULL_TIMES));
                this.setLayoutParams(scrollViewLayoutParams);
            }
        }else {
            if(!canPullLoad(event)) {
                return false;
            }
            int distance = (int) ((yDown - yMove));
            if (distance < maxFingerMove) {
                return false;
            }
            if (currentLoadStatus != STATUS_RELOADING) {
                if (scrollViewLayoutParams.bottomMargin - initMarginBottom > loadHeight - 40) {
                    currentLoadStatus = STATUS_RELEASE_TO_RELOAD;
                } else {
                    currentLoadStatus = STATUS_PULL_TO_RELOAD;
                }
                scrollViewLayoutParams.bottomMargin = (int) (initMarginBottom + (distance/PULL_TIMES));
                this.setLayoutParams(scrollViewLayoutParams);
            }
        }
        return true;
    }
    private boolean upAction(MotionEvent event) {
        if (currentFreshStatus == STATUS_RELEASE_TO_REFRESH) {
            // 松手时如果是释放立即刷新状态，就去调用正在刷新的任务
            new RefreshingTask().execute();
        } else if (currentFreshStatus == STATUS_PULL_TO_REFRESH) {
            // 松手时如果是下拉状态，就去调用隐藏下拉头的任务
            finishFreshView();
        }
        if(currentLoadStatus == STATUS_RELEASE_TO_RELOAD) {
            new ReloadingTask().execute();
        }else  if(currentLoadStatus == STATUS_PULL_TO_RELOAD) {
            finishloadView();
        }
        return true;
    }
    private boolean afterAction(MotionEvent event) {
        // 时刻记得更新下拉头中的信息
        if (currentFreshStatus == STATUS_PULL_TO_REFRESH || currentFreshStatus == STATUS_RELEASE_TO_REFRESH) {
            changeFreshView();
        }
        if(currentLoadStatus == STATUS_PULL_TO_RELOAD || currentLoadStatus == STATUS_RELEASE_TO_RELOAD) {
            changeLoadView();
        }
        this.setPressed(false);
        this.setFocusable(false);
        this.setFocusableInTouchMode(false);
        // 当前正处于下拉或释放状态，要让ListView失去焦点
        return true;
    }
    /**
     * 给该控件注册一个监听器。
     *
     * @param listener
     *            监听器的实现。
     * @param id
     *            为了防止不同界面的下拉刷新在上次更新时间上互相有冲突， 请不同界面在注册下拉刷新监听器时一定要传入不同的id。
     */
    public void setOnRefreshListener(PullToRefreshListener listener, int id) {
        mlistener = listener;
        mid = id;
    }
    /**
     * 当所有的刷新以及加载逻辑完成后,需要回调给这个scrollview
     */
    public void finishRefreshing() {
        finishFreshView();
        finishloadView();
    }

    /**
     * 下拉刷新以及上拉加载的监听器
     *
     */
    public interface PullToRefreshListener {

        /**
         * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑
         */
        void onRefresh();
        /**
         * 加载时会去回调此方法，在方法内编写具体的刷新逻辑
         */
        void onReload();

    }
}