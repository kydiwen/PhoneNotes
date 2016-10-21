package com.example.phone_notes.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.phone_notes.R;
import com.example.phone_notes.base.BaseActivity;
import com.example.phone_notes.constant.myConstant;
import com.example.phone_notes.utils.ToastUtils;

//登录界面
public class LoginActivity extends BaseActivity {
	private EditText input_password;// 密码框
	private Button ensure;// 确定按钮
	private String password;// 密码

	@Override
	protected void initView() {
		setContentView(R.layout.activity_login);
		input_password = (EditText) findViewById(R.id.input_password);
		ensure = (Button) findViewById(R.id.ensure);
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initListener() {
		// 确定按钮点击事件
		ensure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				password = mPreferences.getString(myConstant.PASS, "");
				if (TextUtils.isEmpty(input_password.getText().toString())) {
					ToastUtils.show(mContext, "请输入密码");
				} else if (input_password.getText().toString().equals(password)) {
					// 密码正确,进入主界面
					MainActivity.enterMain(mContext, MainActivity.class);
					finish();
				} else {
					ToastUtils.show(mContext, "密码错误，请重新输入");
					input_password.setText("");
				}
			}
		});
	}

	// 进入登录界面方法封装
	public static void enterLogin(Context con, Class<LoginActivity> class1) {
		Intent intent = new Intent(con, class1);
		con.startActivity(intent);
	}
}
