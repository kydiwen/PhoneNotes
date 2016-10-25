package com.example.phone_notes.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatUtil {
	public static String format(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
		return format.format(date).toString();
	}
}
