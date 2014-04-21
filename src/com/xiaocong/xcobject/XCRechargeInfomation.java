package com.xiaocong.xcobject;

import org.json.JSONObject;

public class XCRechargeInfomation {

	public String message;
	public String channel;
	public String sign,body,subject,orderNumber,notifyUrl;
	public long time;
	public int money;
	public boolean mobileValidate=false;
	
	public static XCRechargeInfomation fromJson(JSONObject json) throws Exception {
		XCRechargeInfomation xc_info = new XCRechargeInfomation();
		if (json.has("message")) {
			xc_info.message = json.getString("message");
		}
		
		if (json.has("channel")) {
			xc_info.channel = json.getString("channel");
		}
		if (json.has("sign")) {
			xc_info.sign = json.getString("sign");
		}
		if (json.has("body")) {
			xc_info.body = json.getString("body");
		}
		if (json.has("subject")) {
			xc_info.subject = json.getString("subject");
		}
		if (json.has("orderNumber")) {
			xc_info.orderNumber = json.getString("orderNumber");
		}
		if (json.has("notifyUrl")) {
			xc_info.notifyUrl = json.getString("notifyUrl");
		}
		if (json.has("time")) {
			xc_info.time = json.getLong("time");
		}
		if (json.has("money")) {
			xc_info.money = json.getInt("money");
		}
		if (json.has("mobileValidate")) {
			xc_info.mobileValidate=json.getBoolean("mobileValidate");
		}
		return xc_info;
	}

}
