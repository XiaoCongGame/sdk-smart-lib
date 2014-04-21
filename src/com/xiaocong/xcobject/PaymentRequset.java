package com.xiaocong.xcobject;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class PaymentRequset {
	public String type, message;
	public int status;
	public GameOrderData data;
//	01-11 14:48:31.145: E/Payment(5300): {"message":"成功","status":200,"data":{"outOrderNumber":"2014011114482112","payType":"1,2,3,5,4,15,16"}}

	public static PaymentRequset formJson(JSONObject json) throws Exception {

		PaymentRequset pay = new PaymentRequset();
		if (json.has("status")) {
			pay.status = json.getInt("status");
		}
		if (json.has("message")) {
			pay.message = json.getString("message");
		}
		pay.data =GameOrderData.formJson(json.getJSONObject("data"));
		return pay;
	}

}
