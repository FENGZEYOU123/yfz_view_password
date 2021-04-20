package com.yfz.password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ArrayList<View> mList=new ArrayList<>();
    private View mView1,mView2,mView3;
    private LayoutInflater layoutInflater;
    private PasswordView mPasswordView1;
    private TextView mTextView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialViewPager();
        initialOnListener();
    }
    private void initialViewPager(){
        mViewPager=findViewById(R.id.viewPager);
        layoutInflater=getLayoutInflater().from(this);
        mView1=layoutInflater.inflate(R.layout.style1,null);
        mView2=layoutInflater.inflate(R.layout.style2,null);
        mView3=layoutInflater.inflate(R.layout.style3,null);
        mList.add(mView1);
        mList.add(mView2);
        mList.add(mView3);
        mViewPager.setAdapter(new ViewPagerAdapter());
        mPasswordView1= mView1.findViewById(R.id.passwordView1);
        mTextView1=mView1.findViewById(R.id.textView1);
    }
    private void initialOnListener(){

        mPasswordView1.setOnResultListener(new PasswordView.OnResultListener() {
            @Override
            public void finish(String result) {
                mTextView1.setText(result);
            }

            @Override
            public void typing(String result) {
                mTextView1.setText(result);
            }
        });
    }


    class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mList.size();
        }
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mList.get(position));
            return mList.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }
    }
}