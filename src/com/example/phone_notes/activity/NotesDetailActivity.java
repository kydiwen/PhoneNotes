package com.example.phone_notes.activity;

import android.util.Log;

import com.example.phone_notes.R;
import com.example.phone_notes.base.BaseActivity;
import com.example.phone_notes.bean.notesItem;
import com.example.phone_notes.constant.myConstant;

/**
 * 
 * 笔记详情页面
 * 
 * @author 孙文权
 * 
 */
public class NotesDetailActivity extends BaseActivity {

	@Override
	protected void initView() {
		setContentView(R.layout.activity_notesdetail);
	}

	@Override
	protected void initData() {
		notesItem item = (notesItem) getIntent().getSerializableExtra(
				myConstant.NotesToShowIm_NotesDetailActivity);
		Log.d("kydiwen", item.getNotesName());
		Log.d("kydiwen", item.getMessage());
	}

	@Override
	protected void initListener() {

	}

}
