package com.example.phone_notes.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
	private String ParentTable = "";// 父列表

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
						if (TextUtils.isEmpty(input_typename.getText()
								.toString())) {
							ToastUtils.show(mContext, "输入不能为空");
						} else {
							// 执行添加分类操作，首先判断所处分类，然后设置新建分类父类为所处分类
							// 此处不改变父列表的值，只允许在页面跳转过程中改变
							handleAddType(input_typename.getText().toString(),
									dialog, ParentTable);
							// 更新列表
							notesItem item = new notesItem();
							item.setNotesType(0);
							item.setNotesName(input_typename.getText()
									.toString());
							item.setParentName(ParentTable);
							item.setNotesTime(TimeFormatUtil.format(new Date()));
							data.add(item);
							adapter.notifyDataSetChanged();
						}
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
				// 点击后隐藏popwindow
				pop.dismiss();
			}
		});
		// 添加笔记点击事件
		add_notes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击添加笔记:此操作进入添加与编辑界面，传递操作类型值，在下一页面做出区分
				handleAddNotes(myConstant.AddNotesInCurrentType, ParentTable,
						null, CurrentType);
				data_null_notes.setVisibility(View.INVISIBLE);
				// 点击后隐藏popwindow
				pop.dismiss();
			}
		});
		// 回收站按钮点击事件
		retrieve.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击进入回收站界面
				Intent intent = new Intent(mContext, RetrieveActivity.class);
				startActivityForResult(intent,
						myConstant.RequestCode_openretrieve);
				// 点击后隐藏popwindow
				pop.dismiss();
			}
		});
		// 为笔记列表单项点击事件
		notes_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 首先判断当前项是分类还是笔记
				switch (data.get(position).getNotesType()) {
				case 0:// 当前是分类
						// 首先判断当前分类是否为空，若为空，则弹出对话框，提示新建分类或新建笔记
					handle_listitemclick_type(position);
					break;
				case 1:// 当前是笔记
					handle_listitemclick_note(position);
					break;
				default:
					break;
				}
			}
		});
		// 设置笔记列表的长按点击事件
		notes_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// 长按列表项他能出对话框，提示用户删除笔记或分类，可选择放入回收站或彻底删除，放入回收站只需要将数据从原来表中删除并保存到回收站中，彻底删除需要进行递归调用，彻底删除数据
				AlertDialog.Builder builder = new Builder(mContext);
				builder.setTitle("删除操作");
				// 彻底删除操作
				builder.setPositiveButton("彻底删除",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 处理直接删除操作，无需存入回收站，直接执行sqlite delete操作
								handleDeleteTabledirectly(position);
								dialog.dismiss();
							}
						});
				// 放入回收站操作
				builder.setNegativeButton("放入回收站",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 处理放入回收站操作，读取当前数据，保存到回收站，并添加currenttype值到pretable属性，用于撤销删除的操作
								handleDeleteTableIntoRetrieve(position);
								dialog.dismiss();
							}
						});
				builder.create().show();
				// 返回true拦截用户操作，避免触发点击事件
				return true;
			}
		});
		// 设置搜索框点击事件
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击进入搜索页面
				Intent intent = new Intent(mContext, SearchActivity.class);
				startActivity(intent);
			}
		});
	}

	// 处理删除数据到回收站的操作
	private void handleDeleteTableIntoRetrieve(int position) {
		// 首先保存数据到回收站
		notesItem item = data.get(position);
		ContentValues values = new ContentValues();

		// 保存原来报所在的分类
		values.put(myConstant.NotesPreTable, CurrentType);
		// 保存数据类型
		values.put(myConstant.NotesType, item.getNotesType());
		// 保存数据名称
		values.put(myConstant.NotesName, item.getNotesName());
		// 保存数据时间
		values.put(myConstant.NotesTime, item.getNotesTime());
		// 保存数据父表
		values.put(myConstant.Parent, item.getParentName());
		if (item.getNotesType() == 1) {// 当前是笔记
			// 保存笔记内容
			values.put(myConstant.Notesmessage, item.getMessage());
			// 保存图片资源
			values.put(myConstant.Images, splicecharacter(item.getImages()));
			// 保存标签
			values.put(myConstant.NotesLabel, splicecharacter(item.getLabels()));
		}
		// 保存到回收站
		myNotesdatabase.insert(myConstant.RetrieveNotes, null, values);
		// 从当前表删除数据，并更新列表
		myNotesdatabase.delete(CurrentType, myConstant.NotesName + "=?",
				new String[] { item.getNotesName() });
		data.remove(item);
		adapter.notifyDataSetChanged();
		if (data.size() == 0) {
			data_null_notes.setVisibility(View.VISIBLE);
		}
	}

	// 处理直接删除数据操作
	private void handleDeleteTabledirectly(int position) {
		// 获取当前数据对象
		notesItem item = data.get(position);
		deleteData(item, CurrentType);
		data.remove(item);
		adapter.notifyDataSetChanged();
		if (data.size() == 0) {
			data_null_notes.setVisibility(View.VISIBLE);
		}
	}

	// 递归删除数据
	private void deleteData(notesItem item, String table_belongto) {
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
				notesItem item2 = new notesItem();
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
				ArrayList<String> labels = readLabels(cursor.getString(cursor
						.getColumnIndex(myConstant.NotesLabel)));
				item2.setLabels(labels);
				// 读取图片
				ArrayList<String> images = readImages(cursor.getString(cursor
						.getColumnIndex(myConstant.Images)));
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

	// 处理笔记列表点击事件---点击笔记列表项
	private void handle_listitemclick_note(int position) {
		// 进入笔记详情页面
		Intent intent = new Intent(mContext, NotesDetailActivity.class);
		intent.putExtra(myConstant.NotesToShowIm_NotesDetailActivity,
				data.get(position));
		startActivity(intent);
	}

	// 处理笔记列表点击事件---点击分类列表项
	private void handle_listitemclick_type(final int position) {
		Cursor cursor = myNotesdatabase.query(
				data.get(position).getNotesName(), null, null, null, null,
				null, null);
		if (cursor.getCount() == 0) {// 当前分类无数据，点击提示添加分类或笔记
			AlertDialog.Builder builder = new Builder(mContext);
			builder.setTitle("选择操作");
			// 设置添加分类按钮点击事件
			builder.setPositiveButton("添加分类",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new Builder(mContext);
							View view = View.inflate(mContext,
									R.layout.dialog_addtype, null);
							final EditText input_typename = (EditText) view
									.findViewById(R.id.input_typename);
							Button ensure = (Button) view
									.findViewById(R.id.ensure);
							Button cancel = (Button) view
									.findViewById(R.id.cancel);
							builder.setTitle("添加分类");
							builder.setView(view);
							final Dialog dialog1 = builder.create();
							dialog1.show();
							// 确定按钮点击事件
							ensure.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									ParentTable = CurrentType;
									CurrentType = data.get(position)
											.getNotesName();
									handleAddType(input_typename.getText()
											.toString(), dialog1, ParentTable);
									// 添加成功后刷新当前列表，更新当前列表下数据
									adapter.notifyDataSetChanged();
									// 隐藏对话框
									dialog1.dismiss();
									dialog.dismiss();
									// 刷新数据后进入新建分类列表
									data.clear();
									// 重新读取数据库，获取更新后笔记列表
									readDatabaseData();
									// 隐藏对话框
									dialog1.dismiss();
								}
							});
							// 取消按钮点击事件
							cancel.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									dialog1.dismiss();
									dialog.dismiss();
								}
							});
						}

					});
			// 设置添加笔记按钮点击事件
			builder.setNegativeButton("添加笔记",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							ParentTable = CurrentType;
							CurrentType = data.get(position).getNotesName();
							handleAddNotes(myConstant.AddNotesAndEnterType,
									ParentTable, (Dialog) dialog, CurrentType);
						}
					});
			builder.create().show();
		} else {// 当前分类不为空，点击刷新当前分类列表，并更新CurrentType用于在监听返回键时返回上一级列表
			ParentTable = CurrentType;
			CurrentType = data.get(position).getNotesName();
			data.clear();
			// 重新读取当数据库，刷新笔记列表
			readDatabaseData();
		}

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
		if (CurrentType.equals(myConstant.ParentTable)) {
			if (!is_back_pressed) {
				ToastUtils.show(mContext, "再按一次退出");
				is_back_pressed = true;
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						is_back_pressed = false;
					}
				}, 2000);
			} else {
				finish();
			}
		} else {
			CurrentType = data.get(0).getParentName();
			data.clear();
			readDatabaseData();
		}
	}

	// 设置popwindow显示在组件上方
	private void showPopUp(View v) {
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		pop.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]
				- pop.getHeight());
	}

	// 处理添加分类操作方法封装
	private void handleAddType(String type, Dialog dialog, String parent) {
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
			values.put(myConstant.Parent, parent);
			values.put(myConstant.NotesTime, TimeFormatUtil.format(new Date()));
			myNotesdatabase.insert(CurrentType, null, values);
			// 隐藏对话框
			dialog.dismiss();
			data_null_notes.setVisibility(View.INVISIBLE);
		}
	}

	// 处理添加笔记操作
	private void handleAddNotes(int addType, String parent, Dialog dialog,
			String currenttype) {
		/**
		 * 
		 * 
		 * 添加笔记操作氛分为两种情况 1，点击主页面添加按钮：在当前类型下添加笔记：进入笔记编辑页面，添加后返回笔记bean值，然后刷新当前列表
		 * 2，点击笔记分类单项：当此分类为空时，点击进入笔记添加页面，添加后返回笔记bean值，然后进入此分类页面
		 * 
		 * 
		 */
		switch (addType) {
		case myConstant.AddNotesInCurrentType:
			// 在当前分类下添加笔记，即点击右下方按钮进行相关操作
			// 传递parent属性到笔记添加页面
			Intent intent = new Intent(mContext, NotesDetailEditActivity.class);
			intent.putExtra(myConstant.Parent, parent);
			intent.putExtra(myConstant.NotesDetailEditActivity_operatetype,
					myConstant.NotesDetailEditActivity_ADD);
			intent.putExtra(myConstant.notesDetailEditActivity_CurrentType,
					currenttype);
			startActivityForResult(intent, myConstant.RequestCode_addnotes);
			break;
		case myConstant.AddNotesAndEnterType:
			// 点击数据为空的列表项，选择添加笔记按钮，添加笔记后进入该分类
			// 点击笔记列表，当为分类并且所点击分类数据为空时，点击添加笔记，进入编辑页面，然后刷新列表
			// 传递parent属性到笔记添加页面
			Intent intent2 = new Intent(mContext, NotesDetailEditActivity.class);
			intent2.putExtra(myConstant.Parent, parent);
			intent2.putExtra(myConstant.NotesDetailEditActivity_operatetype,
					myConstant.NotesDetailEditActivity_ADD);
			intent2.putExtra(myConstant.notesDetailEditActivity_CurrentType,
					currenttype);
			startActivityForResult(intent2,
					myConstant.RequestCode_addnotesEnterType);
			// 隐藏对话框
			dialog.dismiss();
			break;
		default:
			break;
		}

	}

	// 读取根列表数据库数据，并刷新列表
	private void readDatabaseData() {
		// 进行数据库操作，读取分类数据，更新列表
		Cursor cursor = myNotesdatabase.query(CurrentType, null, null, null,
				null, null, null);
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
		// 首先判断数据是否为空值
		if (!TextUtils.isEmpty(data)) {
			String[] l = data.split("\\|");
			for (String la : l) {
				labels.add(la);
			}
		}
		return labels;
	}

	// 读取图片数据
	private ArrayList<String> readImages(String data) {
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		// 添加笔记后更新当前分类列表
		case myConstant.RequestCode_addnotes:
			if (resultCode == RESULT_OK) {
				notesItem item = (notesItem) intent
						.getSerializableExtra(myConstant.NotesReturned);
				data.add(item);
				adapter.notifyDataSetChanged();
			}
			break;
		// 添加分类后进入下一级页面
		case myConstant.RequestCode_addnotesEnterType:
			if (resultCode == RESULT_OK) {
				notesItem item2 = (notesItem) intent
						.getSerializableExtra(myConstant.NotesReturned);
				data.clear();
				readDatabaseData();
			}
			break;
		// 从回收站返回重新加载列表
		case myConstant.RequestCode_openretrieve:
			data.clear();
			readDatabaseData();
			break;
		default:
			break;
		}
	}
}
