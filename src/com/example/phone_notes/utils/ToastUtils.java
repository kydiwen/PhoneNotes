package com.example.phone_notes.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtils {
	public static void show(Context mContext, String message) {
		Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
