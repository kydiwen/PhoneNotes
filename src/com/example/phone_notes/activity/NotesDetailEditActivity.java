package com.example.phone_notes.activity;

import com.example.phone_notes.R;
import com.example.phone_notes.base.BaseActivity;
import com.example.phone_notes.constant.myConstant;

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

	@Override
	protected void initView() {
		setContentView(R.layout.activity_notesdetailedit);
	}

	@Override
	protected void initData() {
		// 初始化操作类型
		initOperateType();
		// 初始化操作
		initOperate();
	}

	@Override
	protected void initListener() {

	}

	// 获取当前操作类型决定执行何种操作
	private void initOperateType() {
		CurrentOPerate = getIntent().getIntExtra(myConstant.NotesOperateType,
				-1);
	}

	// 初始化当前执行的操作
	private void initOperate() {
		switch (CurrentOPerate) {
		case myConstant.Notes_add:

			break;
		case myConstant.Notes_edit:
			
			break;
		default:
			break;
		}
	}
}
