package com.example.phone_notes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.phone_notes.constant.myConstant;

/**
 * 
 * 
 * 笔记数据库帮助类
 * 
 * @author 孙文权
 * 
 */
public class notesDatabaseHelper extends SQLiteOpenHelper {
	private static notesDatabaseHelper helper;
	// 创建类似于文件管理器根目录的列表
	private String CreateRootTable = "create table " + myConstant.ParentTable
			+ "(" + "_id integer primary key autoincrement,"
			+ myConstant.NotesName + " text," + myConstant.NotesType
			+ " integer," + myConstant.NoteLists + " text,"
			+ myConstant.Notesmessage + " text," + myConstant.Images + " text,"
			+ myConstant.Parent + " text," + myConstant.NotesTime + " text"
			+ ")";
	// 创建所有具体笔记的列表
	private String CreateNotesOnlyTable = "create table "
			+ myConstant.NotesOnlyTable + "("
			+ "_id integer primary key autoincrement," + myConstant.NotesName
			+ " text," + myConstant.NotesType + " integer,"
			+ myConstant.NoteLists + " text," + myConstant.Notesmessage
			+ " text," + myConstant.Images + " text," + myConstant.Parent
			+ " text," + myConstant.NotesTime + " text" + ")";
	//创建存放回收站笔记的列表
	private String CreateRetrieveNotesTable = "create table "
			+ myConstant.RetrieveNotes + "("
			+ "_id integer primary key autoincrement," + myConstant.NotesName
			+ " text," + myConstant.NotesType + " integer,"
			+ myConstant.NoteLists + " text," + myConstant.Notesmessage
			+ " text," + myConstant.Images + " text," + myConstant.Parent
			+ " text," + myConstant.NotesTime + " text" + ")";
	// 私有化构造方法
	private notesDatabaseHelper(Context context) {
		super(context, "notes_db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建根列表
		db.execSQL(CreateRootTable);
		// 创建存储具体笔记的列表
		db.execSQL(CreateNotesOnlyTable);
		//创建存储回收站笔记的列表
		db.execSQL(CreateRetrieveNotesTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	// 外部获取帮助类对象的方法(单例模式)
	public static notesDatabaseHelper getInstance(Context mContext) {
		if (helper == null) {
			helper = new notesDatabaseHelper(mContext);
		}
		return helper;
	}
}
