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
	//保存回收站列表名称
	public static final String RetrieveNotes="retrievenotes";
	//保存对笔记的操作类型：添加笔记或者编辑笔记
	public static final  String  NotesOperateType="operate_type";
	//保存添加笔记标识
	public static final int Notes_add=0;
	//保存编辑笔记标识
	public static final int Notes_edit=1;
}
