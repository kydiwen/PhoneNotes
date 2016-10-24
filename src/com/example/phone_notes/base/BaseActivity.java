package com.example.phone_notes.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;

import com.example.phone_notes.R;
import com.example.phone_notes.constant.myConstant;
import com.example.phone_notes.db.notesDatabaseHelper;
import com.example.phone_notes.utils.color_same_to_app;

//Activity基类
public abstract class BaseActivity extends Activity {
	protected Context mContext;
	protected SharedPreferences mPreferences;
	protected SQLiteDatabase myNotesdatabase;

	/**
	 * 进行一些初始化操作 隐藏标题栏， 初始化全局context， 设置状态栏颜色与App保持一致 ，初始化全局preference参数，
	 * 初始化全局可用数据库帮助类
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 初始化操作
		mContext = this;
		// 设置状态栏颜色与App保持一致
		color_same_to_app.setTopColorSameToApp(this, R.color.main_color);
		mPreferences = getSharedPreferences(myConstant.OKQ_PRE,
				Context.MODE_PRIVATE);
		// 初始化数据库
		myNotesdatabase = notesDatabaseHelper.getInstance(mContext)
				.getWritableDatabase();
		initView();
		initData();
		initListener();
	}

	protected abstract void initView();

	protected abstract void initData();

	protected abstract void initListener();
}
