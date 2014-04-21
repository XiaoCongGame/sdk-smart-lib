package com.xiaocong.xcobject;


import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;



public class WayforPay {
	public String type;
	public String logourl;
	public String cardFilePath;
	public String cardFileUpgrade;
	public static WayforPay formJson(JSONObject json) throws Exception{
		
		WayforPay pay=new WayforPay();
		if (json.has("payType")) {
			StringBuffer buffer=new StringBuffer();
			JSONArray array=json.getJSONArray("payType");
			for (int i = 0; i < array.length(); i++) {
				buffer.append(array.get(i)+",");
			}
			pay.type=buffer.toString();
		}
		if (json.has("logo")) {
			pay.logourl=json.getString("logo");
		}
		if (json.has("cardFilePath")) {
			pay.cardFilePath=json.getString("cardFilePath");
		}
		if (json.has("cardFileUpgrade")) {
			pay.cardFileUpgrade=json.getString("cardFileUpgrade");
		}
		return pay;
	}
}
