package com.example.phone_notes.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
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
				clearRetrieveData();
			}
		});
		// 设置列表项单项长按点击事件
		retrieve_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// 弹出对话框提示用户还原数据
				AlertDialog.Builder builder = new Builder(mContext);
				builder.setTitle("选择操作");
				// 取消按钮
				builder.setPositiveButton("彻底删除",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// 从回收站删除该项
								deleteData(data.get(position),
										myConstant.RetrieveNotes);
								// 刷新列表
								data.remove(data.get(position));
								adapter.notifyDataSetChanged();
							}
						});
				// 还原按钮
				builder.setNegativeButton("还原",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								retrieveItem item = data.get(position);
								// 恢复数据
								restoreData(item);
								dialog.dismiss();
							}
						});
				// 显示对话框
				builder.create().show();
				return true;
			}
		});
	}

	// 清空回收站数据封装
	private void clearRetrieveData() {
		// 遍历所有数据，依次删除
		for (int i = 0; i < data.size(); i++) {
			deleteData(data.get(i), myConstant.RetrieveNotes);
		}
		data.clear();
		adapter.notifyDataSetChanged();
		ToastUtils.show(mContext, "清除成功");
	}

	// 递归删除数据
	private void deleteData(retrieveItem item, String table_belongto) {
		if (item.getNotesType() == 1) {// 需要删除项为笔记，直接从当前表删除
			myNotesdatabase.delete(table_belongto, myConstant.NotesName + "=?",
					new String[] { item.getNotesName() });
		} else if (myNotesdatabase.query(item.getNotesName(), null, null, null,// 当前是分类
				null, null, null).getCount() == 0) {// 当前分类中无数据，直接删除
			String deleteTable = "drop  table " + item.getNotesName();
			// 删除表
			myNotesdatabase.execSQL(deleteTable);
			// 从当前表删除数据
			myNotesdatabase.delete(table_belongto, myConstant.NotesName + "=?",
					new String[] { item.getNotesName() });
		} else {// 当前分类存在数据，递归调用，依次删除
			// 获取当前分类中所有数据
			Cursor cursor = myNotesdatabase.query(item.getNotesName(), null,
					null, null, null, null, null);
			cursor.move(-1);
			while (cursor.moveToNext()) {
				retrieveItem item2 = new retrieveItem();
				// 读取父列表
				item2.setParentName(cursor.getString(cursor
						.getColumnIndex(myConstant.Parent)));
				// 读取分类名称
				item2.setNotesName(cursor.getString(cursor
						.getColumnIndex(myConstant.NotesName)));
				// 读取笔记信息，当前为空值
				item2.setMessage(cursor.getString(cursor
						.getColumnIndex(myConstant.Notesmessage)));
				// 读取数据类型数据
				item2.setNotesType(cursor.getInt(cursor
						.getColumnIndex(myConstant.NotesType)));
				// 读取创建时间
				item2.setNotesTime(cursor.getString(cursor
						.getColumnIndex(myConstant.NotesTime)));
				// 读取标签
				ArrayList<String> labels = readLabelsImages(cursor
						.getString(cursor.getColumnIndex(myConstant.NotesLabel)));
				item2.setLabels(labels);
				// 读取图片
				ArrayList<String> images = readLabelsImages(cursor
						.getString(cursor.getColumnIndex(myConstant.Images)));
				item2.setImages(images);
				deleteData(item2, item.getNotesName());
			}
			// 最后删除分类
			String deleteTable = "drop  table " + item.getNotesName();
			// 删除表
			myNotesdatabase.execSQL(deleteTable);
			// 从当前表删除数据
			myNotesdatabase.delete(table_belongto, myConstant.NotesName + "=?",
					new String[] { item.getNotesName() });
		}
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

	// 恢复数据到原分类
	private void restoreData(retrieveItem item) {
		try {
			ContentValues values = new ContentValues();
			// 保存名称
			values.put(myConstant.NotesName, item.getNotesName());
			// 保存数据类型
			values.put(myConstant.NotesType, item.getNotesType());
			// 保存时间
			values.put(myConstant.NotesTime, item.getNotesTime());
			// 保存父列表
			values.put(myConstant.Parent, item.getParentName());
			// 保存笔记内容
			values.put(myConstant.Notesmessage, item.getMessage());
			// 保存图片
			values.put(myConstant.Images, splicecharacter(item.getImages()));
			// 保存标签
			values.put(myConstant.NotesLabel, splicecharacter(item.getLabels()));
			// 插入原列表
			myNotesdatabase.insert(item.getPreTable(), null, values);
			// 从回收站删除该项
			myNotesdatabase.delete(myConstant.RetrieveNotes,
					myConstant.NotesName + "=?",
					new String[] { item.getNotesName() });
			// 刷新列表
			data.remove(item);
			adapter.notifyDataSetChanged();
		} catch (Exception e) {
			// 当父列表被删除时会出现异常，此种情况直接删除该项
			ToastUtils.show(mContext, "还原失败");
			// 从回收站删除该项
			myNotesdatabase.delete(myConstant.RetrieveNotes,
					myConstant.NotesName + "=?",
					new String[] { item.getNotesName() });
			// 刷新列表
			data.remove(item);
			adapter.notifyDataSetChanged();

		}
	}

	// 拼接标签或图片字符串
	private String splicecharacter(ArrayList<String> data) {
		StringBuilder builder = new StringBuilder();
		if (data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				if (i != data.size() - 1) {
					builder.append(data.get(i) + "|");
				} else {
					builder.append(data.get(i));
				}
			}
		}
		return builder.toString();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 设置返回结果
		setResult(RESULT_OK);
	}
}
