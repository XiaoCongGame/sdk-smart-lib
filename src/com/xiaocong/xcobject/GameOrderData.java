package com.xiaocong.xcobject;

import org.json.JSONObject;

public class GameOrderData {
    public int userMoney, money;
    public String serverId;
    public String cost = null;
    public String appName = null;
    public String properNe = null;
    public String msg = null, goodsDes;
    public String outOrderNumber;
    public String payType;

    // 01-11 14:48:31.145: E/Payment(5300):
    // {"message":"成功","status":200,"data":{"outOrderNumber":"2014011114482112","payType":"1,2,3,5,4,15,16"}}

    public static GameOrderData formJson(JSONObject jsonObject) throws Exception {

        GameOrderData data = new GameOrderData();

        if (jsonObject.has("outOrderNumber")) {
            data.outOrderNumber = jsonObject.getString("outOrderNumber");
        }
        if (jsonObject.has("payType")) {
            data.payType = jsonObject.getString("payType");
        }

        if (jsonObject.has("appName")) {
            data.appName = jsonObject.getString("appName");
        }
        if (jsonObject.has("serverId")) {
            data.serverId = jsonObject.getString("serverId");
        }
        if (jsonObject.has("userMoney")) {
            data.userMoney = jsonObject.getInt("userMoney");
        }
        if (jsonObject.has("money")) {
            data.money = jsonObject.getInt("money");
        }
        if (jsonObject.has("goodsDes")) {
            data.goodsDes = jsonObject.getString("goodsDes");
        }
        return data;
    }
}
