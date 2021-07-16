package com.yfz.password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class PasswordMainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ArrayList<View> mList=new ArrayList<>();
    private View mView1,mView2,mView3,mView4,mView5,mView6;
    private LayoutInflater layoutInflater;
    private PasswordView mPasswordView1,mPasswordView2,mPasswordView3,mPasswordView4,mPasswordView5,mPasswordView6;
    private TextView mTextView1,mTextView2,mTextView3,mTextView4,mTextView5,mTextView6;
    private Button mButtonView3,mButtonView4,mButtonView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialViewPager();
        initialOnListenerStyle1();
        initialOnListenerStyle2();
        initialOnListenerStyle3();
        initialOnListenerStyle4();
        initialOnListenerStyle5();
        initialOnListenerStyle6();

    }
    private void initialViewPager(){
        mViewPager=findViewById(R.id.viewPager);
        layoutInflater=getLayoutInflater().from(this);
        mView1=layoutInflater.inflate(R.layout.style1,null);
        mView2=layoutInflater.inflate(R.layout.style2,null);
        mView3=layoutInflater.inflate(R.layout.style3,null);
        mView4=layoutInflater.inflate(R.layout.style4,null);
        mView5=layoutInflater.inflate(R.layout.style5,null);
        mView6=layoutInflater.inflate(R.layout.style6,null);
        mList.add(mView1);
        mList.add(mView2);
        mList.add(mView3);
        mList.add(mView4);
        mList.add(mView5);
        mList.add(mView6);

        mViewPager.setAdapter(new ViewPagerAdapter());
        mViewPager.setOffscreenPageLimit(mList.size()-1);

        mPasswordView1= mView1.findViewById(R.id.passwordView1);
        mTextView1=mView1.findViewById(R.id.textView1);

        mPasswordView2= mView2.findViewById(R.id.passwordView2);
        mTextView2=mView2.findViewById(R.id.textView2);

        mPasswordView3= mView3.findViewById(R.id.passwordView3);
        mTextView3=mView3.findViewById(R.id.textView3);
        mButtonView3=mView3.findViewById(R.id.buttonView3);

        mPasswordView4= mView4.findViewById(R.id.passwordView4);
        mTextView4=mView4.findViewById(R.id.textView4);
        mButtonView4=mView4.findViewById(R.id.buttonView4);

        mPasswordView5= mView5.findViewById(R.id.passwordView5);
        mTextView5=mView5.findViewById(R.id.textView5);
        mButtonView5=mView5.findViewById(R.id.buttonView5);

        mPasswordView6= mView6.findViewById(R.id.passwordView6);
        mTextView6=mView6.findViewById(R.id.textView6);

    }
    private void initialOnListenerStyle1(){

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
    private void initialOnListenerStyle2(){

        mPasswordView2.setOnResultListener(new PasswordView.OnResultListener() {
            @Override
            public void finish(String result) {
                mTextView2.setText(result);
            }

            @Override
            public void typing(String result) {
                mTextView2.setText(result);
            }
        });
    }
    private void initialOnListenerStyle3(){

        mPasswordView3.setOnResultListener(new PasswordView.OnResultListener() {
            @Override
            public void finish(String result) {
                mTextView3.setText(result);
                mButtonView3.setVisibility(View.VISIBLE);
            }

            @Override
            public void typing(String result) {
                mTextView3.setText(result);
            }
        });
        mButtonView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordView3.setUnLock();

            }
        });
    }
    private void initialOnListenerStyle4(){

        mPasswordView4.setOnResultListener(new PasswordView.OnResultListener() {
            @Override
            public void finish(String result) {
                mTextView4.setText(result);
                mButtonView4.setVisibility(View.VISIBLE);
            }

            @Override
            public void typing(String result) {
                mTextView4.setText(result);
            }
        });
        mButtonView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordView4.setUnLock();
            }
        });
    }
    private void initialOnListenerStyle5(){

        mPasswordView5.setOnResultListener(new PasswordView.OnResultListener() {
            @Override
            public void finish(String result) {
                mTextView5.setText(result);
                mButtonView5.setVisibility(View.VISIBLE);
            }

            @Override
            public void typing(String result) {
                mTextView5.setText(result);
            }
        });
        mButtonView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordView5.setUnLock();
            }
        });
    }
    private void initialOnListenerStyle6(){

        mPasswordView6.setOnResultListener(new PasswordView.OnResultListener() {
            @Override
            public void finish(String result) {
                mTextView6.setText(result);
            }

            @Override
            public void typing(String result) {
                mTextView6.setText(result);
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