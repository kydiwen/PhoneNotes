package com.example.phone_notes.activity;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.phone_notes.R;
import com.example.phone_notes.base.BaseActivity;
import com.example.phone_notes.bean.notesItem;
import com.example.phone_notes.constant.myConstant;
import com.example.phone_notes.utils.TimeFormatUtil;
import com.example.phone_notes.utils.ToastUtils;

/**
 * 
 * 
 * 
 * 笔记编辑和查看页面
 * 
 * @author孙文权
 * 
 */
public class NotesDetailEditActivity extends BaseActivity {
	private int CurrentOPerate;// 当前操作类型：添加或编辑，添加成功返回添加成功信息更新列表，产生编辑操作，返回编辑信息，提示修改成功
	private Intent intent;
	private ImageView save;// 顶部保存按钮
	private String parentTable;// 新添加笔记父列表
	private EditText title;// 笔记标题
	private ImageView btn_addtag;// 添加标签按钮
	private LinearLayout tag_container;// 标签容器
	private EditText note_message;// 笔记内容
	private LinearLayout images_container;// 图片容器
	private ImageView btn_addimgs;// 添加图片按钮
	private ArrayList<String> tags = new ArrayList<String>();// 标签内容
	private ArrayList<String> images = new ArrayList<String>();// 图片内容
	private String currentType;// 当前所处分类

	@Override
	protected void initView() {
		setContentView(R.layout.activity_notesdetailedit);
		save = (ImageView) findViewById(R.id.save);
		title = (EditText) findViewById(R.id.title);
		btn_addtag = (ImageView) findViewById(R.id.btn_addtag);
		tag_container = (LinearLayout) findViewById(R.id.tag_container);
		note_message = (EditText) findViewById(R.id.note_message);
		images_container = (LinearLayout) findViewById(R.id.images_container);
		btn_addimgs = (ImageView) findViewById(R.id.btn_addimgs);
	}

	@Override
	protected void initData() {
		intent = getIntent();
		initOperate();
	}

	@Override
	protected void initListener() {
		// 顶部保存按钮点击事件
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				notesItem item = new notesItem();
				// 设置父表
				item.setParentName(parentTable);
				// 设置标题
				item.setNotesName(title.getText().toString());
				// 设置标签
				item.setLabels(tags);
				// 设置笔记内容
				item.setMessage(note_message.getText().toString());
				// 设置图片
				item.setImages(images);
				// 设置时间
				item.setNotesTime(TimeFormatUtil.format(new Date()));
				// 设置此项为笔记
				item.setNotesType(1);
				// 保存到数据库
				saveDataToDatabase(item);
			}
		});
	}

	// 保存数据到数据库
	private void saveDataToDatabase(notesItem item) {
		ContentValues values = new ContentValues();
		values.put(myConstant.NotesName, item.getNotesName());
		values.put(myConstant.NotesType, item.getNotesType());
		values.put(myConstant.Notesmessage, item.getMessage());
		values.put(myConstant.NotesLabel, splicecharacter(item.getLabels()));
		values.put(myConstant.Images, splicecharacter(item.getImages()));
		values.put(myConstant.Parent, parentTable);
		values.put(myConstant.NotesTime, item.getNotesTime());
		// 插入数据
		myNotesdatabase.insert(currentType, null, values);
		// 设置需要返回的数据，关闭当前页面
		Intent intent = new Intent();
		intent.putExtra(myConstant.NotesReturned, item);
		setResult(RESULT_OK, intent);
		finish();
	}

	// 拼接标签或图片字符串
	private String splicecharacter(ArrayList<String> data) {
		StringBuilder builder = new StringBuilder();
		if (data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				builder.append(data.get(i) + "|");
			}
		}
		return builder.toString();
	}

	// 初始化操作，封装笔记的添加和编辑操作
	private void initOperate() {
		switch (intent.getIntExtra(
				myConstant.NotesDetailEditActivity_operatetype, -1)) {
		// 笔记添加操作
		case myConstant.NotesDetailEditActivity_ADD:
			handleNotesAdd();
			break;
		// 笔记编辑操作
		case myConstant.notesDetailEditActivity_EDIT:
			handleNotesEdit();
			break;
		default:
			break;
		}
	}

	// 笔记添加操作
	private void handleNotesAdd() {
		parentTable = intent.getStringExtra(myConstant.Parent);
		currentType = intent
				.getStringExtra(myConstant.notesDetailEditActivity_CurrentType);
		ToastUtils.show(mContext, currentType);
	}

	// 笔记编辑操作
	private void handleNotesEdit() {
		ToastUtils.show(mContext, "编辑笔记");
	}
}
