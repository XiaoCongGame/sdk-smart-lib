package com.xiaocong.xcobject;

import java.io.Serializable;

import org.json.JSONObject;
public class XCRechargeData implements Serializable {

	private static final long serialVersionUID = 1044069145565783961L;
	public String message;
	public int status;
	public XCRechargeInfomation data;

	public static XCRechargeData fromJson(JSONObject object) throws Exception {
		XCRechargeData commetData = new XCRechargeData();
		if (object.has("message")) {
			commetData.message = object.getString("message");
		}
		if (object.has("status")) {
			commetData.status = object.getInt("status");
		}
		if (object.has("data")) {
			commetData.data = XCRechargeInfomation.fromJson(object
					.getJSONObject("data"));
		}

		return commetData;
	}

}
