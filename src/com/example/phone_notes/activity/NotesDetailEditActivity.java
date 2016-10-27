package com.example.phone_notes.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
	private ArrayList<Uri> imgUris = new ArrayList<Uri>();
	private Uri imgUri;

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
				if (TextUtils.isEmpty(title.getText().toString())
						|| TextUtils.isEmpty(note_message.getText().toString())) {
					ToastUtils.show(mContext, "您的输入不完整，请确定后再试");
				} else if (tags.size() == 0 || images.size() == 0) {// 强制添加标签和图片
					ToastUtils.show(mContext, "请确保您已添加图片和标签");
				} else {

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
			}
		});
		// 添加标签按钮
		btn_addtag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(mContext);
				builder.setTitle("添加标签");
				View view = View
						.inflate(mContext, R.layout.dialog_addtag, null);
				final EditText input_tagname = (EditText) view
						.findViewById(R.id.input_tagname);
				Button ensure = (Button) view.findViewById(R.id.ensure);
				Button cancel = (Button) view.findViewById(R.id.cancel);
				builder.setView(view);
				final Dialog dialog = builder.create();
				dialog.show();
				// 确定按钮点击事件
				ensure.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 点击确定按钮添加标签到编辑界面
						final View view = View.inflate(mContext,
								R.layout.layout_tag, null);
						final TextView tag = (TextView) view
								.findViewById(R.id.tag);
						ImageView btn_close = (ImageView) view
								.findViewById(R.id.btn_close);
						// 添加标签
						tag.setText(input_tagname.getText().toString());
						tags.add(input_tagname.getText().toString());
						tag_container.addView(view);
						dialog.dismiss();
						// 点击关闭按钮
						btn_close.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								tags.remove(tag.getText().toString());
								tag_container.removeView(view);
							}
						});
					}
				});
				// 取消按钮点击事件
				cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		});
		// 添加图片按钮
		btn_addimgs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(mContext);
				builder.setTitle("添加图片");
				// 调用系统相机拍摄照片
				builder.setPositiveButton("拍摄照片",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String path = DateFormat.format(
										"yyyyMMddhhmmss", new Date())
										.toString();
								File file = new File(Environment
										.getExternalStorageDirectory(),
										"/okq/imgs/" + path + ".jpg");
								imgUri = Uri.fromFile(file);
								Intent intent = new Intent(
										"android.media.action.IMAGE_CAPTURE");
								intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
								startActivityForResult(intent,
										myConstant.Take_Photo);// 启动相机拍照
								dialog.dismiss();
							}
						});
				// 从相册选取图片
				builder.setNegativeButton("选取图片",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										"android.intent.action.GET_CONTENT");
								intent.setType("image/*");
								startActivityForResult(intent,
										myConstant.Choose_Photo);// 从相册选取图片
								dialog.dismiss();
							}
						});
				builder.create().show();
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
				if (i != data.size() - 1) {
					builder.append(data.get(i) + "|");
				} else {
					builder.append(data.get(i));
				}
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
	}

	// 笔记编辑操作
	private void handleNotesEdit() {
		ToastUtils.show(mContext, "编辑笔记");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case myConstant.Choose_Photo:
			// 选取图片
			Uri uri_choose;
			uri_choose = data.getData();// 获取选取的图片路径
			String path = DateFormat.format("yyyyMMddhhmmss", new Date())
					.toString();
			File file = new File(Environment.getExternalStorageDirectory(),
					"/okq/imgs/" + path + ".jpg");
			imgUri = Uri.fromFile(file);
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri_choose, "image/*");// 第一个参数表示要处理图片的路径
			intent.putExtra("scale", true);// 允许缩放
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);// 输出路径
			startActivityForResult(intent, myConstant.Crop_Photo);
			break;
		case myConstant.Take_Photo:
			// 拍摄照片
			Intent intent2 = new Intent("com.android.camera.action.CROP");
			intent2.setDataAndType(imgUri, "image/*");
			intent2.putExtra("scale", true);// 允许缩放
			intent2.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);// 输出路径
			startActivityForResult(intent2, myConstant.Crop_Photo);
			break;
		case myConstant.Crop_Photo:
			// 裁剪照片
			// 添加图片
			final View view = View.inflate(mContext, R.layout.img_add, null);
			ImageView img_add = (ImageView) view.findViewById(R.id.img_add);
			ImageView img_del = (ImageView) view.findViewById(R.id.img_del);
			try {
				img_add.setImageBitmap(BitmapFactory
						.decodeStream(getContentResolver().openInputStream(
								imgUri)));
				images_container.addView(view);
				// 添加到需要保存的图片集合
				images.add(imgUri.getPath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			// 删除按钮点击事件
			img_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					images_container.removeView(view);
					images.remove(imgUri.getPath());
				}
			});
			break;
		default:
			break;
		}
	}
}
