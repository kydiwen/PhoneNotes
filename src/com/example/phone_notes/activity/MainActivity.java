package com.example.phone_notes.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.phone_notes.R;
import com.example.phone_notes.base.BaseActivity;
import com.example.phone_notes.constant.myConstant;
import com.example.phone_notes.utils.ToastUtils;

//主界面
public class MainActivity extends BaseActivity {
	private ImageView lock_unlock;// 加密按钮
	private EditText search;// 搜索框
	private ImageView btn_add;// 添加按钮
	private boolean is_in_reme = false;// 判断是否开启了加密模式
	private boolean is_pass_setted = false;// 判断密码是否已设置
	private Editor editor;
	private boolean is_back_pressed = false;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_main);
		lock_unlock = (ImageView) findViewById(R.id.lock_unlock);
		search = (EditText) findViewById(R.id.search);
		btn_add = (ImageView) findViewById(R.id.btn_add);
	}

	@Override
	protected void initData() {
		// 加密处理
		initPass();
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
	}
}
