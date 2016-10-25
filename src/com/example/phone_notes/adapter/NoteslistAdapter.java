package com.example.phone_notes.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.phone_notes.R;
import com.example.phone_notes.bean.notesItem;

/**
 * 
 * 
 * 笔记列表适配器
 * 
 * @author孙文权
 * 
 */
public class NoteslistAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<notesItem> data;
	private SQLiteDatabase database;

	public NoteslistAdapter(Context mContext, ArrayList<notesItem> data,
			SQLiteDatabase database) {
		this.mContext = mContext;
		this.data = data;
		this.database = database;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext,
					R.layout.layout_noteslist_item, null);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.notesnum_summary = (TextView) convertView
					.findViewById(R.id.notesnum_summary);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 填充数据
		// 设置标题
		holder.title.setText(data.get(position).getNotesName());
		// 设置时间
		holder.time.setText(data.get(position).getNotesTime());
		// 设置分类中笔记数量或笔记概要
		if (data.get(position).getNotesType() == 0) {// 当前项为分类
			int i = 0;
			Cursor cursor = database.query(data.get(position).getNotesName(),
					null, null, null, null, null, null);
			// 获取查询到的数据数量
			i = cursor.getCount();
			// 显示当前分类下笔记数量
			holder.notesnum_summary.setText(i + "项");
		} else if (data.get(position).getNotesType() == 1) {// 当前项为笔记
			holder.notesnum_summary.setText(data.get(position).getMessage());
		}
		return convertView;
	}

	class ViewHolder {
		TextView title;
		TextView time;
		TextView notesnum_summary;
	}
}
