package com.example.phone_notes.constant;

/**
 * 
 * 
 * 保存常用得常量
 * 
 * @author孙文权
 * 
 */
public interface myConstant {
	// 保存共享参数名称常量
	public static final String OKQ_PRE = "okq_preference";
	// 保存是否是第一次进入应用的常量名
	public static final String ISFIRST_IN = "first_in";
	// 保存是否加密的常量名
	public static final String IS_REME_PASS = "is_reme_pass";
	// 保存密码是否已设置常量
	public static final String IS_PASS_SETED = "is_pass_seted";
	// 保存密码常量
	public static final String PASS = "pass";
	// 笔记数据库根表名称
	public static final String ParentTable = "parentTable";
	// 笔记名称 列名称
	public static final String NotesName = "netesname";
	// 保存标示是否是笔记的参数 列名称
	public static final String NotesType = "notestype";
	// 保存当前分类下笔记集合 列名称
	public static final String NoteLists = "noteslist";
	// 保存笔记文本信息 列名称
	public static final String Notesmessage = "notesmessage";
	// 保存笔记图片信息 列名称
	public static final String Images = "images";
	// 保存笔记父名称 列名称
	public static final String Parent = "parent";
	// 保存全部存储具体笔记类的表 名称
	public static final String NotesOnlyTable = "notesonlytable";
	// 保存笔记时间的列名称
	public static final String NotesTime = "notestime";
	// 保存回收站列表名称
	public static final String RetrieveNotes = "retrievenotes";
	// 保存对笔记的操作类型：添加笔记或者编辑笔记
	public static final String NotesOperateType = "operate_type";
	// 保存添加笔记标识
	public static final int Notes_add = 0;
	// 保存编辑笔记标识
	public static final int Notes_edit = 1;
	// 下面两个为添加笔记两种不同的请求码
	// 保存添加笔记请求码
	// 添加笔记后进入新类型页面
	public static final int RequestCode_addnotesEnterType = 2;
	// 添加笔记到当前页面
	public static final int RequestCode_addnotes = 3;
	// 保存笔记标签列名称
	public static final String NotesLabel = "noteslabel";
	// 下面两个标示添加笔记时的两种不同情况
	// 在当前类型添加笔记
	public static final int AddNotesInCurrentType = 4;
	// 点击数据为空的列表项，添加笔记后进入该分类
	public static final int AddNotesAndEnterType = 5;
	// 下面两个标示笔记查看编辑页面执行何种操作
	public static final String NotesDetailEditActivity_operatetype = "NotesDetailEditActivity_operatetype";
	// 添加笔记操作
	public static final int NotesDetailEditActivity_ADD = 6;
	// 编辑笔记操作
	public static final int notesDetailEditActivity_EDIT = 7;
	// 传递到笔记添加页面的表名
	public static final String notesDetailEditActivity_CurrentType = "currenttype";
	// 添加笔记后返回数据
	public static final String NotesReturned = "notesreturned";
	// 以下是对图片的操作
	public static final int Take_Photo = 8;
	public static final int Choose_Photo = 9;
	public static final int Crop_Photo = 10;

}
