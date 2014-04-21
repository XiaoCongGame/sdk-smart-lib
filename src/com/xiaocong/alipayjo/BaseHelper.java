/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.xiaocong.alipayjo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

import com.xiaocong.activity.ChosesPayWaysActivity;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 工具类
 * 
 */
public class BaseHelper {

	/**
	 * 流转字符串方法
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

//	public static void showDialog(Activity context) {
//		final Dialog adialog = new Dialog(context, R.style.dialognotitle);
//		adialog.setContentView(R.layout.failedcharglayout);
//		Window dialogWindow = adialog.getWindow();
//		adialog.show();
//
//		Button button = (Button) adialog.findViewById(R.id.closedBtn);
//		button.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				adialog.dismiss();
//			}
//		});
//		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//		DisplayMetrics dm = new DisplayMetrics();
//		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
//		lp.width = (int) ((dm.widthPixels) * 0.65);
//		lp.height = (int) ((dm.heightPixels) * 0.60);
//		adialog.getWindow().setAttributes(lp);
//	}

	/**
	 * 打印信息
	 * 
	 * @param tag
	 *            标签
	 * @param xc_info
	 *            信息
	 */
	public static void log(String tag, String xc_info) {
		// Log.d(tag, xc_info);
	}

	/**
	 * 获取权限
	 * 
	 * @param permission
	 *            权限
	 * @param path
	 *            路径
	 */
	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//
	// show the progress bar.
	/**
	 * 显示进度条
	 * 
	 * @param context
	 *            环境
	 * @param title
	 *            标题
	 * @param message
	 *            信息
	 * @param indeterminate
	 *            确定性
	 * @param cancelable
	 *            可撤销
	 * @return
	 */
	public static ProgressDialog showProgress(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
		// dialog.setDefaultButton(false);
		dialog.setOnCancelListener(new ChosesPayWaysActivity.MainOnCancelListener(
				(Activity) context));

		dialog.show();
		return dialog;
	}

	
	
	
	/**
	 * 字符串转json对象
	 * 
	 * @param str
	 * @param split
	 * @return
	 */
	public static JSONObject string2JSON(String str, String split) {
		JSONObject json = new JSONObject();
		try {
			String[] arrStr = str.split(split);
			for (int i = 0; i < arrStr.length; i++) {
				String[] arrKeyValue = arrStr[i].split("=");
				json.put(arrKeyValue[0],
						arrStr[i].substring(arrKeyValue[0].length() + 1));
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

//	public static void showdialogSecuss(Activity context, String money,
//			String remoney) {
//		final Dialog adialog = new Dialog(context, R.style.dialognotitle);
//		adialog.setContentView(R.layout.sucssescharglayout);
//		Window dialogWindow = adialog.getWindow();
//		adialog.show();
//
//		Button button = (Button) adialog.findViewById(R.id.closedBtn);
//		// 充值金额
//		TextView textView = (TextView) adialog.findViewById(R.id.jinenumText);
//		// 返回金额
//		TextView textView2 = (TextView) adialog.findViewById(R.id.yuenumText);
//		textView.setText(money);
//		textView2.setText(remoney);
//		button.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				adialog.dismiss();
//			}
//		});
//		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//		DisplayMetrics dm = new DisplayMetrics();
//		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
//		lp.width = (int) ((dm.widthPixels) * 0.65);
//		lp.height = (int) ((dm.heightPixels) * 0.60);
//		adialog.getWindow().setAttributes(lp);
//
//	}

}