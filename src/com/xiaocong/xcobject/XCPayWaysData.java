package com.xiaocong.xcobject;

import java.io.Serializable;

import org.json.JSONObject;

public class XCPayWaysData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 338044913903563177L;
	public String message;
	public int status;
	public WayforPay wp;
	public static XCPayWaysData fromJson(JSONObject object) throws Exception {
		XCPayWaysData commetData = new XCPayWaysData();
		if (object.has("message")) {
			commetData.message = object.getString("message");
		}
		if (object.has("status")) {
			commetData.status = object.getInt("status");
		}
		if (object.has("data")) {
			commetData.wp=WayforPay.formJson(object.getJSONObject("data"));
		}
		return commetData;
	}

}
