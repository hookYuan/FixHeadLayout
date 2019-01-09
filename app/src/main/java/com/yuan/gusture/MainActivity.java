package com.yuan.gusture;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.yuan.gusture.gesture.FixHeadLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuanye
 * @date 2018-12-21 9:31:04
 */
public class MainActivity extends AppCompatActivity {


    private FixHeadLayout fixHeadLayout;

    private ViewPager viewPager;

    private TabLayout tabLayout;

    private TextView headLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fixHeadLayout = findViewById(R.id.fixHeadLayout);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        headLayout = findViewById(R.id.head_layout);
//        headLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getBaseContext(), "onClick", Toast.LENGTH_SHORT).show();
//            }
//        });

        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }


    public class MyAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(new ViewPagerFragment());
            fragments.add(new ViewPagerFragment());
            fragments.add(new ViewPagerFragment());
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page" + (position + 1);
        }
    }
}
