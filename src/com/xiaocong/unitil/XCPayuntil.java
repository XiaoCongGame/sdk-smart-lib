package com.xiaocong.unitil;

import com.xiaocong.activity.GetGameinfomation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class XCPayuntil {

//	String mspid = "100005";
//	String msamount = "5";
//	String msgtype = "md5";
//	String msorderNo = null;
//	String mspkgName = "com.sg.android.fish";
//	String msign = null;
//	String msproperty = "小葱币";
	public static int resultCodeforbuy=0;
	public final static int PAYRESULT_OK=99;
	public final static int PAYRESULT_FAIL=98;
	public final static int PUSHORDER_OK=97;
	public final static int PUSHORDER_FAIL=96;
	public final static int CREAT_ORDER_FAIL=95;
	public final static int RESULT_ORDER_NULL=94;
	public final static int CANCEL_BUY=93;
	public final static int AsyncTask_result=92;
	public static int AOUMONT;
	public static String MESSAGE;
	public static String GOODEECC;
	/**
	 * 游戏支付方法１　　无ＯＡＵＴＨ2.0认证
	 * @param context 
	 * @param partnerId
	 * @param amount
	 * @param signType
	 * @param orderNo
	 * @param PackageName
	 * @param goodsDes
	 * @param sign 
	 */
	public static void ToGame_infomation(Context context,String partnerId,String amount,String signType,String orderNo,String PackageName,String goodsDes,String sign,String notifyUrl,String mark){
//		Intent payIntent = new Intent();
//		payIntent.setAction("com.xiaocong.android.app.XCpayGetgame");
		Intent payIntent = new Intent(context,GetGameinfomation.class);
		Bundle bundle = new Bundle();
		bundle.putString("partnerId", partnerId+"");
		bundle.putString("amount", amount+"");
		bundle.putString("signType", signType+"");
		bundle.putString("orderNo", orderNo+"");
		bundle.putString("PackageName", PackageName+"");
		bundle.putString("goodsDes", goodsDes+"");
		bundle.putString("sign", sign+"");
		if (notifyUrl != null && !"".equals(notifyUrl)) {
			bundle.putString("notifyUrl", notifyUrl+"");
		}
		if (mark!=null&&!mark.equals("")) {
			bundle.putString("mark", mark+"");
		}
		
//		bundle.putString("accessToken", accessToken+"");
		payIntent.putExtras(bundle);
		context.startActivity(payIntent);
	}
	/**
	 * 游戏支付方法２　有ＯＡＵＴＨ2.0认证
	 * @param context 
	 * @param partnerId
	 * @param amount
	 * @param orderNo
	 * @param PackageName
	 * @param goodsDes
	 * @param accessToken 
	 */
	public static void ToGame_infomation(Context context,String partnerId,String amount,String orderNo,String PackageName,String goodsDes,String accessToken,String notifyUrl,String mark){
//		Intent payIntent = new Intent();
//		payIntent.setAction("com.xiaocong.android.app.XCpayGetgame");
		Intent payIntent = new Intent(context,GetGameinfomation.class);
		Bundle bundle = new Bundle();
		bundle.putString("partnerId", partnerId+"");
		bundle.putString("amount", amount+"");
//		bundle.putString("signType", signType+"");
		bundle.putString("orderNo", orderNo+"");
		bundle.putString("PackageName", PackageName+"");
		bundle.putString("goodsDes", goodsDes+"");
//		bundle.putString("sign", sign+"");
		bundle.putString("accessToken", accessToken+"");
		
		if (notifyUrl != null && !"".equals(notifyUrl)) {
			bundle.putString("notifyUrl", notifyUrl+"");
		}
		if (mark!=null&&!mark.equals("")) {
			bundle.putString("mark", mark+"");
		}
		
		
		payIntent.putExtras(bundle);
		context.startActivity(payIntent);
	}
}
