package com.xiaocong.xcapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import tv.xiaocong.sdk.XcServiceClient;
import tv.xiaocong.sdk.XcUtils;
import android.util.Log;

import com.xiaocong.xcobject.PaymentRequset;
import com.xiaocong.xcobject.XCRechargeData;

public class XCAPI {

    public static final String HOST_URL = XcServiceClient.SERVER_TVSTORE + "faces.do";

    private static final String PARAM_NAME = "$$FACES$$";

    public static PaymentRequset doPayment(String orderNo, int user, String goodsDes, int amount,
            String pkgName, String signType, String sign, int partnerId, String notifyUrl,
            String mark, String accessToken) throws Exception {
        List<BasicNameValuePair> pair = new ArrayList<BasicNameValuePair>();

        if (accessToken != null && !accessToken.isEmpty()) {
            pair.add(new BasicNameValuePair("accessToken", accessToken));
        }

        if (notifyUrl != null && !notifyUrl.isEmpty()) {
            pair.add(new BasicNameValuePair("notifyUrl", notifyUrl));
        }
        if (mark != null && !mark.isEmpty()) {
            pair.add(new BasicNameValuePair("mark", mark));
        }
        pair.add(new BasicNameValuePair("orderNo", orderNo));
        pair.add(new BasicNameValuePair("goodsDes", goodsDes));
        pair.add(new BasicNameValuePair("amount", String.valueOf(amount)));
        pair.add(new BasicNameValuePair("pkgName", pkgName));
        pair.add(new BasicNameValuePair("signType", signType));
        pair.add(new BasicNameValuePair("sign", sign));

        pair.add(new BasicNameValuePair("partnerId", String.valueOf(partnerId)));

        String ret = postData(buildRequestContent("payment", pair, user));
        Log.e("Payment", ret != null ? ret.toString() : "Payment is null");
        return PaymentRequset.formJson(new JSONObject(ret));
    }

    // http://127.0.0.1:8080/tvstore/faces.do?$$FACES$$={"head":{"method":"createOrder","hardware":"ubRDIdjLsX4xf2W5nwxdrg==","server":2,"version":"3.3"},"body":{"paymentType":9,"money":235,"payVersion":"Credit","outOrderNumber":"12365412365452"}}
    // 新的信用支付的接口
    // 技术部-冶卫军 14:56:19
    // {"message":"成功","status":200,"data":{"orderNumber":"2014011814375221648239","money":235,"notifyUrl":"http://data.xiaocong.tv/tvstore/paypalmNotify.action"}}
    // 返回值里有orderNumber，拿这个值调apk支付

    // -------------------------------------------------------

    /** 小葱币支付 */
    public static XCRechargeData payByXiaocongCoins(int user, String outOrderNumber)
            throws Exception {
        List<BasicNameValuePair> pair = new ArrayList<BasicNameValuePair>();
        pair.add(new BasicNameValuePair("type", "12001")); // 游戏内消费
        pair.add(new BasicNameValuePair("payType", "1"));
        pair.add(new BasicNameValuePair("outOrderNumber", outOrderNumber));

        String ret = postData(buildRequestContent("exchangeCentre", pair, user));

        Log.e("payByXiaocongCoins", ret != null ? ret : "payByXiaocongCoins is null");
        return XCRechargeData.fromJson(new JSONObject(ret));
    }

    /**
     * 充值 paymentType 1电信.2联.3支付宝.4移动 5 易宝　phone CMCC==>productCode 010.020.050.100 9.PP 支付　8小葱　18微信
     * "body" :{"paymentType":5,"money":50,"cardNo":"35688XXXXX175688","cvv2":"536"
     * ,"phone":"13401003065","validthru":"0315"}}
     * 
     * @param
     * @return
     * @throws Exception
     */
    public static XCRechargeData AllMobilecharge(int userId, String phone, String productCode,
            int paymentType, String outOrderNumber) throws Exception {

        List<BasicNameValuePair> pair = new ArrayList<BasicNameValuePair>();
        if (paymentType == 1) {
            pair.add(new BasicNameValuePair("payTypeId", "2"));
            pair.add(new BasicNameValuePair("phone", phone));
            pair.add(new BasicNameValuePair("productCode", productCode));
        } else if (paymentType == 2) {
            pair.add(new BasicNameValuePair("phone", phone));
            pair.add(new BasicNameValuePair("productCode", productCode));
        } else if (paymentType == 3) {
            pair.add(new BasicNameValuePair("money", Integer.parseInt(productCode) + ""));
        } else if (paymentType == 4) {
            pair.add(new BasicNameValuePair("phone", phone));
            pair.add(new BasicNameValuePair("productCode", productCode));
        } else if (paymentType == 5) {
            pair.add(new BasicNameValuePair("bindId", phone));
            pair.add(new BasicNameValuePair("money", productCode));
        } else if (paymentType == 7) {
            pair.add(new BasicNameValuePair("money", productCode));
        } else if (paymentType == 9) {
            // 商户编号：30400010XCKJ
            // "money":235,"payVersion":"Credit","outOrderNumber":"12365412365452"
            pair.add(new BasicNameValuePair("payVersion", "Credit"));
            pair.add(new BasicNameValuePair("money", productCode));

        }
        pair.add(new BasicNameValuePair("outOrderNumber", outOrderNumber));

        pair.add(new BasicNameValuePair("paymentType", String.valueOf(paymentType)));
        String ret = null;
        ret = postData(buildRequestContent("createOrder", pair, userId));
        Log.e("AllMobilecharge", ret != null ? ret.toString() : "AllMobilecharge is null");
        return XCRechargeData.fromJson(new JSONObject(ret));
    }

    /**
     * 
     * @param money
     *            *100
     * @return
     * @throws Exception
     */
    public static XCRechargeData AllYeePaycharge(String phone, int paymentType, String cardNo,
            String cvv2, int money, String validthru, String outOrderNumber) throws Exception {

        List<BasicNameValuePair> pair = new ArrayList<BasicNameValuePair>();
        pair.add(new BasicNameValuePair("phone", phone));
        pair.add(new BasicNameValuePair("cardNo", cardNo));
        pair.add(new BasicNameValuePair("cvv2", cvv2));
        pair.add(new BasicNameValuePair("money", String.valueOf(money)));
        pair.add(new BasicNameValuePair("validthru", validthru));
        pair.add(new BasicNameValuePair("paymentType", String.valueOf(paymentType)));
        pair.add(new BasicNameValuePair("outOrderNumber", outOrderNumber));
        String ret = null;
        ret = postData(buildRequestContent("createOrder", pair, -1));

        Log.e("AllYeePaycharge", ret != null ? ret.toString() : "AllMobilecharge is null");
        return XCRechargeData.fromJson(new JSONObject(ret));
    }

    public static XCRechargeData AllYeePaychargeSEC(int paymentType, int money, String bindId,
            String outOrderNumber) throws Exception {
        List<BasicNameValuePair> pair = new ArrayList<BasicNameValuePair>();
        pair.add(new BasicNameValuePair("bindId", bindId));
        pair.add(new BasicNameValuePair("money", String.valueOf(money)));
        pair.add(new BasicNameValuePair("paymentType", String.valueOf(paymentType)));
        pair.add(new BasicNameValuePair("outOrderNumber", outOrderNumber));
        String ret = null;
        ret = postData(buildRequestContent("createOrder", pair, -1));
        Log.e("AllYeePaychargeSEC", ret != null ? ret.toString() : "AllYeePaychargeSECis null");
        return XCRechargeData.fromJson(new JSONObject(ret));
    }

    private static String buildRequestContent(String method, List<BasicNameValuePair> pair,
            int userId) throws Exception {
        JSONObject jHead = buildHead(method, userId);
        JSONObject jBody = new JSONObject();
        if (pair != null && pair.size() != 0) {
            for (int i = 0; i < pair.size(); i++) {
                jBody.put(pair.get(i).getName(), pair.get(i).getValue());
            }
        }
        JSONObject jRequest = new JSONObject();
        jRequest.put("head", jHead);
        jRequest.put("body", jBody);
        Log.e("接口", jRequest + "");
        return jRequest.toString();
    }

    private static JSONObject buildHead(String method, int userId) throws Exception {
        JSONObject jHead = new JSONObject();
        jHead.put("method", method);
        jHead.put("server", XcUtils.getServer());
        jHead.put("hardware", XcUtils.getHardware());
        jHead.put("version", "3.3");

        if (userId > 0) {
            jHead.put("user", userId);
        }

        return jHead;
    }

    private static String postData(String param) {
        List<BasicNameValuePair> pair = new ArrayList<BasicNameValuePair>();
        pair.add(new BasicNameValuePair(PARAM_NAME, param));
        return HttpRequest.post(HOST_URL, pair, HTTP.UTF_8);
    }

}
