package com.example.phone_notes.activity;

import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.phone_notes.R;
import com.example.phone_notes.adapter.NoteslistAdapter;
import com.example.phone_notes.base.BaseActivity;
import com.example.phone_notes.bean.notesItem;
import com.example.phone_notes.constant.myConstant;
import com.example.phone_notes.utils.TimeFormatUtil;
import com.example.phone_notes.utils.ToastUtils;

//主界面
public class MainActivity extends BaseActivity {
	private ImageView lock_unlock;// 加密按钮
	private EditText search;// 搜索框
	private boolean is_in_reme = false;// 判断是否开启了加密模式
	private boolean is_pass_setted = false;// 判断密码是否已设置
	private Editor editor;
	private boolean is_back_pressed = false;
	private ListView notes_list;// 笔记列表
	private ImageView notes_add;// 添加按钮
	private PopupWindow pop;// 悬浮窗控件
	private TextView add_type;// 添加分类
	private TextView add_notes;// 添加笔记
	private TextView retrieve;// 回收站
	private RelativeLayout data_null_notes;// 数据为空提示信息
	private ArrayList<notesItem> data;// 列表数据
	private NoteslistAdapter adapter;// 列表适配器
	private String CurrentType = myConstant.ParentTable;// 当前所在分类

	@Override
	protected void initView() {
		setContentView(R.layout.activity_main);
		lock_unlock = (ImageView) findViewById(R.id.lock_unlock);
		search = (EditText) findViewById(R.id.search);
		notes_list = (ListView) findViewById(R.id.notes_list);
		notes_add = (ImageView) findViewById(R.id.btn_add);
		data_null_notes = (RelativeLayout) findViewById(R.id.data_null_notes);
		initPopView();
	}

	@Override
	protected void initData() {
		// 加密处理
		initPass();
		// 初始化列表相关数据
		data = new ArrayList<notesItem>();
		adapter = new NoteslistAdapter(mContext, data, myNotesdatabase);
		// 设置适配器
		notes_list.setAdapter(adapter);
		// 读取数据库中笔记数据，并更新列表
		readDatabaseData();
	}

	@Override
	protected void initListener() {
		// 开启和关闭加密按钮
		lock_unlock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 首先判断是否处于加密模式
				if (is_in_reme) {// 加密模式已开启,点击按钮关闭
					// 点击按钮关闭加密
					AlertDialog.Builder builder = new Builder(mContext);
					builder.setTitle("提示");
					builder.setMessage("开启加密更安全，确定要关闭吗？");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// 修改是否处于加密模式标示值
									editor = mPreferences.edit();
									editor.putBoolean(myConstant.IS_REME_PASS,
											false);
									editor.commit();
									is_in_reme = false;
									arg0.dismiss();
									lock_unlock
											.setImageResource(R.drawable.unlock);
								}
							});
					builder.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();
								}
							});
					builder.create().show();
				} else {// 未开启加密模式，点击按钮开启
					// 首先判断是否已设置密码，未设置密码是需要首先设置密码
					is_pass_setted = mPreferences.getBoolean(
							myConstant.IS_PASS_SETED, false);
					if (is_pass_setted) {// 密码已设置
						// 点击直接开启加密，修改标示加密模式的参数值
						editor = mPreferences.edit();
						editor.putBoolean(myConstant.IS_REME_PASS, true);
						editor.commit();
						is_in_reme = true;
						// 修改解锁图标
						lock_unlock.setImageResource(R.drawable.lock);
						ToastUtils.show(mContext, "加密已开启");
					} else {// 密码未设置
						// 首先设置密码，设置后开启加密
						setAndopenPass();
					}
				}
			}
		});
		// 添加按钮点击事件
		notes_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 点击按钮提示添加分类或者添加笔记
				if (pop.isShowing()) {
					pop.dismiss();
					pop.update();
				} else {
					showPopUp(notes_add);
				}
			}
		});
		// 添加分类按钮点击事件
		add_type.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击添加分类按钮弹出对话框，用户输入分类名称，操作数据库，在当前分类下添加分类
				AlertDialog.Builder builder = new Builder(mContext);
				View view = View.inflate(mContext, R.layout.dialog_addtype,
						null);
				final EditText input_typename = (EditText) view
						.findViewById(R.id.input_typename);
				Button ensure = (Button) view.findViewById(R.id.ensure);
				Button cancel = (Button) view.findViewById(R.id.cancel);
				builder.setTitle("添加分类");
				builder.setView(view);
				final Dialog dialog = builder.create();
				dialog.show();
				// 确定按钮点击事件
				ensure.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 执行添加分类操作，首先判断所处分类，然后设置新建分类父类为所处分类
						handleAddType(input_typename.getText().toString(),
								dialog);
					}
				});
				// 取消按钮点击事件
				cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 隐藏对话框
						dialog.dismiss();
					}
				});
			}
		});
		// 添加笔记点击事件
		add_notes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击添加笔记:此操作进入添加与编辑界面，传递操作类型值，在下一页面做出区分
				Intent intent = new Intent(mContext,
						NotesDetailEditActivity.class);
				intent.putExtra(myConstant.NotesOperateType,
						myConstant.Notes_add);
				startActivityForResult(intent, myConstant.RequestCode_addnotes);
			}
		});
		// 回收站按钮点击事件
		retrieve.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击进入回收站界面
				Intent intent = new Intent(mContext, RetrieveActivity.class);
				startActivity(intent);
			}
		});
	}

	// 初始化popwindow界面组件
	private void initPopView() {
		View view = View.inflate(mContext, R.layout.main_add_pop, null);
		// 测量popwindow
		view.measure(0, 0);
		add_type = (TextView) view.findViewById(R.id.add_type);
		add_notes = (TextView) view.findViewById(R.id.add_notes);
		retrieve = (TextView) view.findViewById(R.id.retrieve);
		pop = new PopupWindow(view, view.getMeasuredWidth(),
				view.getMeasuredHeight(), true);
		pop.setFocusable(true);
		pop.setTouchable(true);
		pop.setBackgroundDrawable(new BitmapDrawable());
	}

	// 设置密码，并开启加密
	private void setAndopenPass() {
		AlertDialog.Builder builder = new Builder(mContext);
		View view = View.inflate(mContext, R.layout.dialog_pass_setting, null);
		builder.setView(view);
		final EditText input_pass = (EditText) view
				.findViewById(R.id.input_pass);
		final EditText input_pass_again = (EditText) view
				.findViewById(R.id.input_pass_again);
		Button ensure = (Button) view.findViewById(R.id.ensure);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		final Dialog dialog = builder.create();
		// 确定按钮点击事件
		ensure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 判断用户输入是否合法
				if (!TextUtils.isEmpty(input_pass.getText().toString())
						&& !TextUtils.isEmpty(input_pass_again.getText()
								.toString())
						&& input_pass.getText().toString()
								.equals(input_pass_again.getText().toString())) {
					editor = mPreferences.edit();
					// 设置密码状态为已设置
					editor.putBoolean(myConstant.IS_PASS_SETED, true);
					// 设置开启加密状态
					editor.putBoolean(myConstant.IS_REME_PASS, true);
					// 保存密码
					editor.putString(myConstant.PASS, input_pass.getText()
							.toString());
					editor.commit();
					dialog.dismiss();
					lock_unlock.setImageResource(R.drawable.lock);
					// 提示密码已开启加密
					ToastUtils.show(mContext, "加密已开启");
					is_in_reme = true;
				} else {
					ToastUtils.show(mContext, "请确保两次输入一致");
				}
			}
		});
		// 取消按钮点击事件
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 隐藏对话框
				dialog.dismiss();
			}
		});
		// 显示对话框
		dialog.show();
	}

	// 进入主界面方法封装
	public static void enterMain(Context con, Class<MainActivity> class1) {
		Intent intent = new Intent(con, class1);
		con.startActivity(intent);
	}

	// 加密方法封装
	private void initPass() {
		// 获取是否开启了加密模式
		is_in_reme = mPreferences.getBoolean(myConstant.IS_REME_PASS, false);
		if (is_in_reme) {// 加密模式已开启
			lock_unlock.setImageResource(R.drawable.lock);
		} else {// 未加密情况
			lock_unlock.setImageResource(R.drawable.unlock);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// if (!is_back_pressed) {
		// ToastUtils.show(mContext, "再按一次退出");
		// is_back_pressed = true;
		// Timer timer = new Timer();
		// timer.schedule(new TimerTask() {
		// @Override
		// public void run() {
		// is_back_pressed = false;
		// }
		// }, 2000);
		// } else {
		// finish();
		// }
	}

	// 设置popwindow显示在组件上方
	private void showPopUp(View v) {
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		pop.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]
				- pop.getHeight());
	}

	// 处理添加分类操作方法封装
	private void handleAddType(String type, Dialog dialog) {
		// 添加分类操作，操作数据库，新建分类表，刷新当前列表
		// 首先用户输入是否为空
		if (TextUtils.isEmpty(type)) {
			ToastUtils.show(mContext, "不可为空，请重新输入");
		} else {
			String CreateTable = "create table " + type + "("
					+ "_id integer primary key autoincrement,"
					+ myConstant.NotesName + " text," + myConstant.NotesType
					+ " integer," + myConstant.NoteLists + " text,"
					+ myConstant.Notesmessage + " text," + myConstant.Images
					+ " text," + myConstant.NotesLabel + " text,"
					+ myConstant.Parent + " text," + myConstant.NotesTime
					+ " text" + ")";
			// 新建分类表
			myNotesdatabase.execSQL(CreateTable);
			// 为当前列表添加一列数据
			ContentValues values = new ContentValues();
			// 设置当前项为分类项
			values.put(myConstant.NotesType, 0);
			values.put(myConstant.NotesName, type);
			values.put(myConstant.Parent, CurrentType);
			values.put(myConstant.NotesTime, TimeFormatUtil.format(new Date()));
			myNotesdatabase.insert(CurrentType, null, values);
			// 更新列表
			notesItem item = new notesItem();
			item.setNotesType(0);
			item.setNotesName(type);
			item.setParentName(CurrentType);
			item.setNotesTime(TimeFormatUtil.format(new Date()));
			data.add(item);
			adapter.notifyDataSetChanged();
			// 隐藏对话框
			dialog.dismiss();
			data_null_notes.setVisibility(View.INVISIBLE);
		}
	}

	// 读取根列表数据库数据，并刷新列表
	private void readDatabaseData() {
		// 进行数据库操作，读取分类数据，更新列表
		Cursor cursor = myNotesdatabase.query(myConstant.ParentTable, null,
				null, null, null, null, null);
		cursor.move(-1);
		while (cursor.moveToNext()) {
			// 数据不为空，显示隐藏提示信息
			data_null_notes.setVisibility(View.INVISIBLE);
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
			ArrayList<String> labels = readLabels(cursor.getString(cursor
					.getColumnIndex(myConstant.NotesLabel)));
			item.setLabels(labels);
			// 读取图片
			ArrayList<String> images = readImages(cursor.getString(cursor
					.getColumnIndex(myConstant.Images)));
			item.setImages(images);
			// 添加数据到数据，更新列表
			data.add(item);
			adapter.notifyDataSetChanged();
		}
	}

	// 读取标签数据
	private ArrayList<String> readLabels(String data) {
		ArrayList<String> labels = new ArrayList<String>();
		String[] l = data.split("\\|");
		for (String la : l) {
			labels.add(la);
		}
		return labels;
	}

	// 读取图片数据
	private ArrayList<String> readImages(String data) {
		ArrayList<String> images = new ArrayList<String>();
		String[] ima = data.split("\\|");
		for (String i : ima) {
			images.add(i);
		}
		return images;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		// 添加笔记后更新当前分类列表
		case myConstant.RequestCode_addnotes:

			break;

		default:
			break;
		}
	}
}
