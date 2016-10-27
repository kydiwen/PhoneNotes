package com.example.phone_notes.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.phone_notes.R;
import com.example.phone_notes.adapter.NoteslistAdapter;
import com.example.phone_notes.base.BaseActivity;
import com.example.phone_notes.bean.notesItem;
import com.example.phone_notes.constant.myConstant;

/**
 * 
 * 
 * 搜索页面
 * 
 * @author孙文权
 * 
 */
public class SearchActivity extends BaseActivity {
	private ImageView back;// 顶部返回按钮
	private EditText user_input;// 搜索框
	private TextView cancel;// 搜索按钮
	private ListView noteslist;// 搜索结果列表
	private ArrayList<notesItem> data = new ArrayList<notesItem>();// 搜索结果数据
	private NoteslistAdapter adapter;// 搜索结果适配器
	private String data_userInput;// 用户输入数据

	@Override
	protected void initView() {
		setContentView(R.layout.activity_search);
		back = (ImageView) findViewById(R.id.back);
		user_input = (EditText) findViewById(R.id.user_input);
		cancel = (TextView) findViewById(R.id.cancel);
		noteslist = (ListView) findViewById(R.id.noteslist);
	}

	@Override
	protected void initData() {
		// 设置适配器
		adapter = new NoteslistAdapter(mContext, data, myNotesdatabase);
		noteslist.setAdapter(adapter);
	}

	@Override
	protected void initListener() {
		// 顶部返回按钮点击事件
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 搜索按钮点击事件
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 设置列表项点击事件
		noteslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 进入笔记详情页面
				Intent intent = new Intent(mContext, NotesDetailActivity.class);
				intent.putExtra(myConstant.NotesToShowIm_NotesDetailActivity,
						data.get(position));
				startActivity(intent);
			}
		});
		// 监听用户输入变化
		user_input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				data.clear();
				data_userInput = s.toString();
				searchData();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	// 执行搜索数据操作
	private void searchData() {
		readTable(myConstant.ParentTable);
	}

	private void readTable(String tableName) {
		Cursor cursor = myNotesdatabase.query(tableName, null, null, null,
				null, null, null);
		if (cursor.getCount() > 0) {
			cursor.move(-1);
			while (cursor.moveToNext()) {
				notesItem item = new notesItem();
				// 读取父列表
				item.setParentName(cursor.getString(cursor
						.getColumnIndex(myConstant.Parent)));
				// 读取分类名称
				item.setNotesName(cursor.getString(cursor
						.getColumnIndex(myConstant.NotesName)));
				// 读取笔记信息，当前为空值
				item.setMessage(cursor.getString(cursor
						.getColumnIndex(myConstant.Notesmessage)));
				// 读取数据类型数据
				item.setNotesType(cursor.getInt(cursor
						.getColumnIndex(myConstant.NotesType)));
				// 读取创建时间
				item.setNotesTime(cursor.getString(cursor
						.getColumnIndex(myConstant.NotesTime)));
				// 读取标签
				ArrayList<String> labels = readImagesLabels(cursor
						.getString(cursor.getColumnIndex(myConstant.NotesLabel)));
				item.setLabels(labels);
				// 读取图片
				ArrayList<String> images = readImagesLabels(cursor
						.getString(cursor.getColumnIndex(myConstant.Images)));
				item.setImages(images);
				if (item.getNotesType() == 0) {// 当前是分类
					if (item.getNotesName().equals(data_userInput)) {// 分类匹配
						// 读取此分类下的笔记和其字列表下的数据添加到搜索结果
						readAllNoteItem(item.getNotesName());
					} else {
						readTable(item.getNotesName());
					}
				} else {// 当前是笔记
					if (item.getNotesName().equals(data_userInput)
							|| cursor
									.getString(
											cursor.getColumnIndex(myConstant.NotesLabel))
									.equals(data_userInput)) {
						data.add(item);
						adapter.notifyDataSetChanged();
					}
				}
			}
		}
	}

	// 读取分类下所有的笔记单项
	private void readAllNoteItem(String tableName) {
		Cursor cursor = myNotesdatabase.query(tableName, null, null, null,
				null, null, null);
		if (cursor.getCount() > 0) {
			cursor.move(-1);
			while (cursor.moveToNext()) {
				notesItem item = new notesItem();
				// 读取父列表
				item.setParentName(cursor.getString(cursor
						.getColumnIndex(myConstant.Parent)));
				// 读取分类名称
				item.setNotesName(cursor.getString(cursor
						.getColumnIndex(myConstant.NotesName)));
				// 读取笔记信息，当前为空值
				item.setMessage(cursor.getString(cursor
						.getColumnIndex(myConstant.Notesmessage)));
				// 读取数据类型数据
				item.setNotesType(cursor.getInt(cursor
						.getColumnIndex(myConstant.NotesType)));
				// 读取创建时间
				item.setNotesTime(cursor.getString(cursor
						.getColumnIndex(myConstant.NotesTime)));
				// 读取标签
				ArrayList<String> labels = readImagesLabels(cursor
						.getString(cursor.getColumnIndex(myConstant.NotesLabel)));
				item.setLabels(labels);
				// 读取图片
				ArrayList<String> images = readImagesLabels(cursor
						.getString(cursor.getColumnIndex(myConstant.Images)));
				item.setImages(images);
				if (item.getNotesType() == 1) {
					data.add(item);
					adapter.notifyDataSetChanged();
				} else {
					readAllNoteItem(item.getNotesName());
				}
			}
		}
	}

	// 读取图片和标签数据
	private ArrayList<String> readImagesLabels(String data) {
		ArrayList<String> images = new ArrayList<String>();
		// 首先判断数据是否为空值
		if (!TextUtils.isEmpty(data)) {
			String[] ima = data.split("\\|");
			for (String i : ima) {
				images.add(i);
			}
		}
		return images;
	}
}
