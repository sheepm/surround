package com.example.surround;

import java.math.BigInteger;

import com.example.fragment.MapFragment;
import com.example.fragment.RoadFragment;
import com.example.fragment.ZhoubianFragment;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;

public class MainActivity extends Activity implements OnClickListener {

	private MapFragment mapFragment;
	private ZhoubianFragment zhoubianFragment;
	private RoadFragment roadFragment;
	private LinearLayout mMap;
	private LinearLayout mZhoubian;
	private LinearLayout mRoad;
	private Fragment mFragment;

	private TextView mTxtMap;
	private TextView mTxtZhoubian;
	private TextView mTxtSource;
	private ImageView mImgMap;
	private ImageView mImgZhoubian;
	private ImageView mImgSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		setDefaultFragment();
		setOnclickListener();
	}


	private void setOnclickListener() {
		mMap.setOnClickListener(this);
		mZhoubian.setOnClickListener(this);
		mRoad.setOnClickListener(this);
	}

	private void initView() {
		mapFragment = new MapFragment();
		zhoubianFragment = new ZhoubianFragment();
		roadFragment = new RoadFragment();
		mFragment = mapFragment;
		mMap = (LinearLayout) findViewById(R.id.linear_map);
		mZhoubian = (LinearLayout) findViewById(R.id.linear_zhoubian);
		mRoad = (LinearLayout) findViewById(R.id.linear_road);
		mTxtMap = (TextView) findViewById(R.id.map_txt);
		mTxtZhoubian = (TextView) findViewById(R.id.zhoubian_txt);
		mTxtSource = (TextView) findViewById(R.id.source_txt);
		mImgMap = (ImageView) findViewById(R.id.map_img);
		mImgZhoubian = (ImageView) findViewById(R.id.zhoubian_img);
		mImgSource = (ImageView) findViewById(R.id.source_img);
	}
	

	private void setDefaultFragment() {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.content, mFragment);
		transaction.commit();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
//		super.onSaveInstanceState(outState);
	}

	private void replaceFragment(Fragment fragment) {
		if (mFragment != fragment) {
			FragmentManager manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			if (!fragment.isAdded()) {
				transaction.hide(mFragment).add(R.id.content, fragment)
						.commit();
			} else {
				transaction.hide(mFragment).show(fragment).commit();
			}
			mFragment = fragment;
		}

	}

	private void resetColor() {
		mTxtMap.setTextColor(Color.BLACK);
		mTxtZhoubian.setTextColor(Color.BLACK);
		mTxtSource.setTextColor(Color.BLACK);
		mImgMap.setImageResource(R.drawable.map);
		mImgZhoubian.setImageResource(R.drawable.zhoubian);
		mImgSource.setImageResource(R.drawable.source);
		mMap.setBackgroundColor(Color.WHITE);
		mZhoubian.setBackgroundColor(Color.WHITE);
		mRoad.setBackgroundColor(Color.WHITE);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.linear_map:
			resetColor();
			mMap.setBackgroundColor(getResources().getColor(
					R.color.linear_press));
			mTxtMap.setTextColor(0xFF56ABE4);
			mImgMap.setImageResource(R.drawable.map_press);
			replaceFragment(mapFragment);
			break;

		case R.id.linear_zhoubian:
			resetColor();
			mZhoubian.setBackgroundColor(getResources().getColor(
					R.color.linear_press));
			mTxtZhoubian.setTextColor(0xFF56ABE4);
			mImgZhoubian.setImageResource(R.drawable.zhoubian_press);
			replaceFragment(zhoubianFragment);
			break;

		case R.id.linear_road:
			resetColor();
			mRoad.setBackgroundColor(getResources().getColor(
					R.color.linear_press));
			mTxtSource.setTextColor(0xFF56ABE4);
			mImgSource.setImageResource(R.drawable.source_press);
			replaceFragment(roadFragment);
			break;

		default:
			break;
		}
	}

}
