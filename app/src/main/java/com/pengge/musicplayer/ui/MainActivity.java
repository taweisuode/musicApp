package com.pengge.musicplayer.ui;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pengge.musicplayer.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    RecommendMainFragment recommendMainFragment;
    RanklistMainFragment ranklistMainFragment;
    FragmentManager fragmentManager = null;
    TextView show_recommend,show_ranklist;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        fragmentManager = getSupportFragmentManager();
        if(recommendMainFragment == null) {
            recommendMainFragment = new RecommendMainFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container,recommendMainFragment).commit();
        }
        show_recommend = (TextView) findViewById(R.id.show_recommend);
        show_ranklist = (TextView) findViewById(R.id.show_ranklist);
        show_recommend.setOnClickListener(this);
        show_ranklist.setOnClickListener(this);

        search = (EditText) findViewById(R.id.search);
        //底部导航
       initRadioButton();
    }

    private void initRadioButton() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout container = (FrameLayout) findViewById(R.id.fragment_container);
        inflater.inflate(R.layout.fragment_bottom_bar, container, true);
    }
    private void hideFragments(FragmentTransaction transaction) {
/*        if (mainFragment != null) {
            transaction.hide(mainFragment);
        }*/
        if (recommendMainFragment != null) {
            transaction.hide(recommendMainFragment);
        }

        if (ranklistMainFragment != null) {
            transaction.hide(ranklistMainFragment);
        }
    }
    //监听菜单栏
    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (v.getId()) {
            case R.id.show_recommend:
                show_ranklist.setBackground(null);
                show_recommend.setBackground(getResources().getDrawable(R.drawable.main_recommend_change));
                if(recommendMainFragment == null) {
                    recommendMainFragment = new RecommendMainFragment();
                    transaction.add(R.id.fragment_container, recommendMainFragment);
                }else {
                    transaction.show(recommendMainFragment);
                }
                transaction.commit();
                break;
            case R.id.show_ranklist:
                show_recommend.setBackground(null);
                show_ranklist.setBackground(getResources().getDrawable(R.drawable.main_recommend_change));
                if(ranklistMainFragment == null) {
                    ranklistMainFragment = new RanklistMainFragment();
                    transaction.add(R.id.fragment_container, ranklistMainFragment);
                }else {
                    transaction.show(ranklistMainFragment);
                }
                transaction.commit();
                break;
            default:
        }
    }
}
