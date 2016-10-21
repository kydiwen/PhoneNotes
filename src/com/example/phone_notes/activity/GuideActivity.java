package com.example.phone_notes.activity;

import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.example.phone_notes.R;
import com.example.phone_notes.base.BaseActivity;
import com.example.phone_notes.constant.myConstant;

//引导页面
public class GuideActivity extends BaseActivity {

	private ImageView guide_logo;// 引导页面logo
	private boolean isfirst_in = true;// 标示是否第一次进入应用
	private boolean isreme_pass = false;// 标示是否选择加密

	@Override
	protected void initView() {
		setContentView(R.layout.activity_guide);
		guide_logo = (ImageView) findViewById(R.id.guide_logo);
	}

	@Override
	protected void initData() {
		// 初始化动画
		initAnimation();
	}

	@Override
	protected void initListener() {

	}

	// 设置动画操作
	private void initAnimation() {
		// 透明度动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		alphaAnimation.setFillAfter(true);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				// 显示引导页面logo
				guide_logo.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				/**
				 * 做出如下判断，根据不同结果进入不同页面 1.判断是否第一次进入应用：第一次进入直接进入主界面，否则判断是否选择了加密操作
				 * 2，判断是否选择了加密：若选择了加密操作，进入登录界面，否则直接进入主界面
				 * 
				 */
				isfirst_in = mPreferences.getBoolean(myConstant.ISFIRST_IN,
						true);
				if (!isfirst_in) {// 不是第一次进入应用
					// 获取标示是否加密的参数
					isreme_pass = mPreferences.getBoolean(
							myConstant.IS_REME_PASS, false);
					if (isreme_pass) {// 选择了加密操作
						LoginActivity.enterLogin(mContext, LoginActivity.class);
						finish();
					} else {// 未选择加密操作
						MainActivity.enterMain(mContext, MainActivity.class);
						finish();
					}
				} else {// 第一次进入应用，编辑标记是否第一次进入应用的值
					MainActivity.enterMain(mContext, MainActivity.class);
					finish();
					Editor editor = mPreferences.edit();
					editor.putBoolean(myConstant.ISFIRST_IN, false);
					editor.commit();
				}
			}
		});
		//开启动画
		guide_logo.setAnimation(alphaAnimation);
	}
}
