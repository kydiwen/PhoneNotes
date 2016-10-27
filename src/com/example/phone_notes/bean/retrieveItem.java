package com.example.phone_notes.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * 
 * 
 * 回收站笔记bean类，类似于文件管理器中bean类设计，每一个bean类可以包含一个bean集合，以此类推
 * 标示是分类还是具体笔记的int(0和1)值决定message和images的值是否设置 与笔记bean类类似，新增原保存分类属性
 * 
 * @author 孙文权
 * 
 */
public class retrieveItem implements Serializable {
	private String notesName;// 分类名称或笔记标题
	private int notesType;// 标示是分类还是具体的笔记，只有是具体的笔记时，message和images才会存在
	private ArrayList<notesItem> listNotes;// 分类下笔记或分类集合
	private String message;// 笔记内容
	private ArrayList<String> images;// 笔记中的图片资源
	private String parentName;// 所在分类名称
	private ArrayList<String> labels;// 存放标签信息
	private String notesTime;// 笔记的时间，分类不设置此属性
	private String preTable;// 原保存分类

	public String getPreTable() {
		return preTable;
	}

	public void setPreTable(String preTable) {
		this.preTable = preTable;
	}

	public String getNotesTime() {
		return notesTime;
	}

	public void setNotesTime(String notesTime) {
		this.notesTime = notesTime;
	}

	public ArrayList<String> getLabels() {
		return labels;
	}

	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getNotesName() {
		return notesName;
	}

	public void setNotesName(String notesName) {
		this.notesName = notesName;
	}

	public int getNotesType() {
		return notesType;
	}

	public void setNotesType(int NotesType) {
		this.notesType = NotesType;
	}

	public ArrayList<notesItem> getListNotes() {
		return listNotes;
	}

	public void setListNotes(ArrayList<notesItem> listNotes) {
		this.listNotes = listNotes;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<String> getImages() {
		return images;
	}

	public void setImages(ArrayList<String> images) {
		this.images = images;
	}

	// 获取所在分类名称
	public String getParent() {
		return parentName;
	}
}
