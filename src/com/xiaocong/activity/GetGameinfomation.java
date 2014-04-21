package com.xiaocong.activity;

import tv.xiaocong.sdk.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.xiaocong.alipayjo.BaseHelper;
import com.xiaocong.loader.GetbuyGameOrderLoader;
import com.xiaocong.unitil.UntilTools;
import com.xiaocong.unitil.XCPayuntil;
import com.xiaocong.xcobject.PaymentParameters;
import com.xiaocong.xcobject.PaymentRequset;

/**
 * first get game xc_info
 * 
 * @author yg
 * 
 */
public class GetGameinfomation extends Activity implements OnLoadCompleteListener<PaymentRequset> {

    private PaymentParameters paymentParameters = new PaymentParameters();

    private GetbuyGameOrderLoader getbuyGameOrderLoader;

    private ProgressDialog loading;

    public static GetGameinfomation intenct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        intenct = this;
        loading = BaseHelper.showProgress(this, null,
                getResources().getString(R.string.notice_loading), false, true);

        startGetbuyGameOrderLoader();
    }

    private void startGetbuyGameOrderLoader() {
        initPaymentParameters();

        // 请求接口获取购买内部订单
        getbuyGameOrderLoader = new GetbuyGameOrderLoader(this);

        getbuyGameOrderLoader.setParams(paymentParameters.getOrderNo(),
                paymentParameters.getGoodsDes(), paymentParameters.getPackageName(),
                Integer.parseInt(paymentParameters.getAmount()),
                Integer.parseInt(paymentParameters.getPartnerId()),
                paymentParameters.getAccessToken(), paymentParameters.getNotifyUrl(),
                paymentParameters.getMark(), paymentParameters.getSignType(),
                paymentParameters.getSign());

        getbuyGameOrderLoader.registerListener(0, this);
        getbuyGameOrderLoader.startLoading();
    }

    private void initPaymentParameters() {
        // amount
        setAmountFromIntent();

        // access_token
        String accessToken = getIntent().getStringExtra("accessToken");
        if (accessToken != null && !accessToken.isEmpty()) {
            paymentParameters.setAccessToken(accessToken);
        }

        // signType
        String signType = getRequiredIntentString("signType");
        if ("RSA".equalsIgnoreCase(signType)) {
            paymentParameters.setSignType("RSA");
        } else if ("md5".equalsIgnoreCase(signType)) {
            paymentParameters.setSignType("md5");
        } else {
            throw new RuntimeException("Invalid signType: " + signType);
        }

        // goodsDes
        String goodsDes = getRequiredIntentString("goodsDes");
        paymentParameters.setGoodsDes(goodsDes);
        XCPayuntil.GOODEECC = goodsDes;

        // nofityUrl
        String nofityUrl = getIntent().getStringExtra("notifyUrl");
        if (nofityUrl != null && !nofityUrl.isEmpty()) {
            paymentParameters.setNotifyUrl(nofityUrl);
        }

        paymentParameters.setOrderNo(getRequiredIntentString("orderNo"));
        paymentParameters.setPackageName(getRequiredIntentString("PackageName"));
        paymentParameters.setPartnerId(getRequiredIntentString("partnerId"));
        paymentParameters.setSign(getRequiredIntentString("sign"));

        // mark
        String remark = getIntent().getStringExtra("mark");
        if (remark != null && !remark.isEmpty()) {
            paymentParameters.setMark(remark);
        }
    }

    public String getRequiredIntentString(String name) {
        String v = getIntent().getStringExtra(name);
        if (v == null) {
            throw new IllegalArgumentException(name + " is required!");
        }
        v = v.trim();
        if (v.isEmpty()) {
            throw new IllegalArgumentException(name + " is required!");
        }
        return v;
    }

    private void setAmountFromIntent() {
        String amount = getRequiredIntentString("amount");
        try {
            long value = Long.parseLong(amount);
            if (value < 0) {
                throw new IllegalArgumentException("amount is invalid: " + amount);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("amount is invalid: " + amount);
        }
        paymentParameters.setAmount(amount);
    }

    @Override
    public void onLoadComplete(Loader<PaymentRequset> arg0, PaymentRequset data) {
        Log.e("getGameinfomation", data + "");
        // 01-11 14:48:31.145: E/Payment(5300):
        // {"message":"成功","status":200,"data":{"outOrderNumber":"2014011114482112","payType":"1,2,3,5,4,15,16"}}

        if (data != null) {
            if (data.message != null) {
                XCPayuntil.MESSAGE = data.message;
            }
            if (data.status == 200) {
                if (data.data.outOrderNumber != null) {
                    // 起跳下一个界面选择
                    Log.e("outOrderNumber", data.data.outOrderNumber + "");
                    Intent intent = new Intent(GetGameinfomation.this, ChosesPayWaysActivity.class);
                    intent.putExtra("outOrderNumber", data.data.outOrderNumber);
                    intent.putExtra("payType", data.data.payType);
                    intent.putExtra("productCode", paymentParameters.getAmount());
                    intent.putExtra("pakegename", paymentParameters.getPackageName());
                    intent.putExtra("accessToken", paymentParameters.getAccessToken());
                    GetGameinfomation.this.startActivity(intent);
                } else {
                    Toast.makeText(GetGameinfomation.this,
                            getString(R.string.order_createfail_null), Toast.LENGTH_LONG).show();
                    OutPayment(94);
                }
            } else {
                Toast.makeText(GetGameinfomation.this, data.message, Toast.LENGTH_LONG).show();
                // getGameinfomation.this.finish();
                OutPayment(95);

            }
        } else {
            Toast.makeText(GetGameinfomation.this, R.string.order_createfail, Toast.LENGTH_LONG)
                    .show();
            OutPayment(95);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UntilTools.WarnDialog(GetGameinfomation.this, getString(R.string.cancel_buythis));
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onDestroy() {
        if (loading != null) {
            loading.dismiss();
            loading = null;
        }

        super.onDestroy();
    }

    /**
     * 
     * @param resultCode
     *            99 支付成功 98　支付失败 97订单提交成功 96订单提交失败 95订单创建失败 94订单返回为空 93取消购买　
     */
    public void OutPayment(int resultCod) {
        XCPayuntil.resultCodeforbuy = resultCod;
        GetGameinfomation.this.finish();
    }
}
