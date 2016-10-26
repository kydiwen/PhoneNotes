package com.example.phone_notes.activity;

import java.io.File;
import java.io.FileNotFoundException;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
	private ImageView back;// 顶部返回按钮
	private TextView title;// 顶部笔记标题
	private LinearLayout note_container;// 笔记内容容器，用于动态添加图片
	private TextView time;// 笔记时间
	private TextView message;// 笔记内容
	private TextView tag;// 笔记标签

	@Override
	protected void initView() {
		setContentView(R.layout.activity_notesdetail);
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		note_container = (LinearLayout) findViewById(R.id.note_container);
		time = (TextView) findViewById(R.id.time);
		message = (TextView) findViewById(R.id.message);
		tag = (TextView) findViewById(R.id.tag);
	}

	@Override
	protected void initData() {
		notesItem item = (notesItem) getIntent().getSerializableExtra(
				myConstant.NotesToShowIm_NotesDetailActivity);
		initNotesData(item);
	}

	@Override
	protected void initListener() {
		// 顶部回退键点击事件
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	// 填充笔记数据
	private void initNotesData(notesItem item) {
		// 显示顶部标题
		title.setText(item.getNotesName());
		// 显示笔记时间
		time.setText(item.getNotesTime());
		// 显示笔记内容
		message.setText(item.getMessage());
		// 显示笔记标签
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < item.getLabels().size(); i++) {
			builder.append(item.getLabels().get(i) + "  ");
		}
		tag.setText(builder.toString());
		// 显示图片
		for (int i = 0; i < item.getImages().size(); i++) {
			ImageView imageView = (ImageView) View.inflate(mContext,
					R.layout.notesdetail_img, null);
			File file = new File(item.getImages().get(i));
			Uri imgUri = Uri.fromFile(file);
			try {
				imageView.setImageBitmap(BitmapFactory
						.decodeStream(getContentResolver().openInputStream(
								imgUri)));
				note_container.addView(imageView);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
