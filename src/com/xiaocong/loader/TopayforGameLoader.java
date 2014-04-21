package com.xiaocong.loader;

import tv.xiaocong.sdk.UserBasicInfo;
import tv.xiaocong.sdk.XcServiceClient;
import android.content.Context;
import android.util.Log;

import com.xiaocong.base.BaseAPILoader;
import com.xiaocong.xcapi.XCAPI;
import com.xiaocong.xcobject.XCRechargeData;

public class TopayforGameLoader extends BaseAPILoader<XCRechargeData> {

    private static final String LOG_TAG = TopayforGameLoader.class.getSimpleName();

    private String phone, productCode, cardNo, cvv2, validthru, bindId, outOrderNumber,
            accessToken;
    private int paymentType, money;

    public TopayforGameLoader(Context context) {
        super(context);
    }

    public void setParams(String phone, String productCode, int paymentType, String outOrderNumber,
            String accessToken) {
        this.paymentType = paymentType;
        this.productCode = productCode;
        this.phone = phone;
        this.outOrderNumber = outOrderNumber;
        this.accessToken = accessToken;
    }

    public void setParamsYeepay(String phone, int paymentType, String cardNo, String cvv2,
            int money, String validthru, String outOrderNumber) {
        this.paymentType = paymentType;
        this.cardNo = cardNo;
        this.cvv2 = cvv2;
        this.money = money;
        this.validthru = validthru;
        this.phone = phone;
        this.outOrderNumber = outOrderNumber;
    }

    public void setParamsYeepaytwo(int paymentType, int money, String bindId, String outOrderNumber) {
        this.paymentType = paymentType;
        this.bindId = bindId;
        this.money = money;
        this.outOrderNumber = outOrderNumber;
    }

    @Override
    public XCRechargeData loadInBackground() {
        XCRechargeData data = null;
        // 执行请求操作．

        int userId = -1;
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                UserBasicInfo ui = XcServiceClient.getUserBasicInfo(accessToken);
                userId = Integer.parseInt(ui.getId());
            } catch (RuntimeException e) {
                Log.e(LOG_TAG, "failed to get user id", e);
            }
        }

        try {
            if (paymentType == 1) {
                data = XCAPI.payByXiaocongCoins(userId, outOrderNumber);
            } else if (paymentType == 5 && cvv2 != null) {
                data = XCAPI.AllYeePaycharge(phone, paymentType, cardNo, cvv2, money, validthru,
                        outOrderNumber);
            } else if (paymentType == 5 && bindId.length() > 2) {
                data = XCAPI.AllYeePaychargeSEC(paymentType, money, bindId, outOrderNumber);
            } else {
                data = XCAPI.AllMobilecharge(userId, phone, productCode, paymentType,
                        outOrderNumber);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "do final pay", e);
        }

        return data;
    }
}
