package com.example.phone_notes.activity;

import java.util.ArrayList;

import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.phone_notes.R;
import com.example.phone_notes.adapter.RetrieveAdapter;
import com.example.phone_notes.base.BaseActivity;
import com.example.phone_notes.bean.retrieveItem;
import com.example.phone_notes.constant.myConstant;
import com.example.phone_notes.utils.ToastUtils;

/**
 * 
 * 
 * 回收站界面 读取回收站数据，点击还原按钮可以将数据保存到原来保存的表中，清空数据，可彻底删除数据
 * 
 * @author 孙文权
 * 
 */
public class RetrieveActivity extends BaseActivity {
	private ImageView back;// 顶部返回按钮
	private TextView clear;// 顶部清空按钮
	private ListView retrieve_list;// 回收站数据列表
	private ArrayList<retrieveItem> data = new ArrayList<retrieveItem>();// 回收站数据
	private RetrieveAdapter adapter;// 回收站列表适配器

	@Override
	protected void initView() {
		setContentView(R.layout.activity_retrieve);
		retrieve_list = (ListView) findViewById(R.id.retrieve_list);
		back = (ImageView) findViewById(R.id.back);
		clear = (TextView) findViewById(R.id.clear);
	}

	@Override
	protected void initData() {
		// 设置适配器
		adapter = new RetrieveAdapter(mContext, data, myNotesdatabase);
		retrieve_list.setAdapter(adapter);
		// 读取回收站数据
		readRetrieveData();
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
		// 顶部清空按钮点击事件
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		// 设置列表项单项长按点击事件
		retrieve_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				return true;
			}
		});
	}

	// 读取回收站数据
	private void readRetrieveData() {
		Cursor cursor = myNotesdatabase.query(myConstant.RetrieveNotes, null,
				null, null, null, null, null);
		if (cursor.getCount() == 0) {// 判断回收站是否为空
			ToastUtils.show(mContext, "回收站为空");
		} else {
			// 读取回收站数据
			cursor.move(-1);
			while (cursor.moveToNext()) {
				retrieveItem item = new retrieveItem();
				// 设置原保存分类
				item.setPreTable(cursor.getString(cursor
						.getColumnIndex(myConstant.NotesPreTable)));
				// 设置名称
				item.setNotesName(cursor.getString(cursor
						.getColumnIndex(myConstant.NotesName)));
				// 设置数据
				item.setMessage(cursor.getString(cursor
						.getColumnIndex(myConstant.Notesmessage)));
				// 设置数据类型
				item.setNotesType(cursor.getInt(cursor
						.getColumnIndex(myConstant.NotesType)));
				// 设置父分类
				item.setParentName(cursor.getString(cursor
						.getColumnIndex(myConstant.Parent)));
				// 设置数据时间
				item.setNotesTime(cursor.getString(cursor
						.getColumnIndex(myConstant.NotesTime)));
				// 设置图片
				item.setImages(readLabelsImages(cursor.getString(cursor
						.getColumnIndex(myConstant.Images))));
				// 设置标签
				item.setLabels(readLabelsImages(cursor.getString(cursor
						.getColumnIndex(myConstant.NotesLabel))));
				data.add(item);
				adapter.notifyDataSetChanged();
			}
		}
	}

	// 读取标签和图片数据
	private ArrayList<String> readLabelsImages(String data) {
		ArrayList<String> labels = new ArrayList<String>();
		// 首先判断数据是否为空值
		if (!TextUtils.isEmpty(data)) {
			String[] l = data.split("\\|");
			for (String la : l) {
				labels.add(la);
			}
		}
		return labels;
	}
}
