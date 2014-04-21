package com.xiaocong.loader;

import tv.xiaocong.sdk.UserBasicInfo;
import tv.xiaocong.sdk.XcServiceClient;
import android.content.Context;
import android.util.Log;

import com.xiaocong.base.BaseAPILoader;
import com.xiaocong.xcapi.XCAPI;
import com.xiaocong.xcobject.PaymentRequset;

public class GetbuyGameOrderLoader extends BaseAPILoader<PaymentRequset> {

    private static final String LOG_TAG = GetbuyGameOrderLoader.class.getSimpleName();

    private String orderNo, goodsDes, pkgName, signType, sign, accessToken, notifyUrl, mark;
    private int amount, partnerId, userId;

    public GetbuyGameOrderLoader(Context context) {
        super(context);
    }

    public void setParams(String orderNo, String goodsDes, String pkgName, int amount,
            int partnerId, String accessToken, String notifyUrl, String mark, String signType,
            String sign) {
        this.orderNo = orderNo;
        this.goodsDes = goodsDes;
        this.pkgName = pkgName;
        this.signType = signType;
        this.sign = sign;
        this.amount = amount;
        this.notifyUrl = notifyUrl;
        this.mark = mark;
        this.partnerId = partnerId;
        this.userId = 0;
        this.accessToken = accessToken;
    }

    @Override
    public PaymentRequset loadInBackground() {
        PaymentRequset data = null;

        userId = -1;
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                UserBasicInfo ui = XcServiceClient.getUserBasicInfo(accessToken);
                userId = Integer.parseInt(ui.getId());
            } catch (RuntimeException e) {
                Log.e(LOG_TAG, "failed to get user id", e);
            }
        }

        try {
            data = XCAPI.doPayment(orderNo, userId, goodsDes, amount, pkgName, signType, sign, partnerId,
                    notifyUrl, mark, accessToken);
        } catch (Exception e) {
        }

        return data;
    }

}
