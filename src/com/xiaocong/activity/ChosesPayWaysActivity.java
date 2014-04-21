package com.xiaocong.activity;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tv.xiaocong.sdk.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.pp.creditpayment.global.PPCreditInterface_v1_1;

import com.coocaa.ccapi.CcApi;
import com.coocaa.ccapi.CcApi.PurchaseCallBack;
import com.coocaa.ccapi.OrderData;
import com.xiaocong.alipayjo.AlixId;
import com.xiaocong.alipayjo.BaseHelper;
import com.xiaocong.alipayjo.MobileSecurePayer;
import com.xiaocong.alipayjo.PartnerConfig;
import com.xiaocong.alipayjo.ResultChecker;
import com.xiaocong.alipayjo.Rsa;
import com.xiaocong.loader.TopayforGameLoader;
import com.xiaocong.unitil.MySimpleAdapter;
import com.xiaocong.unitil.UIUtils;
import com.xiaocong.unitil.UntilTools;
import com.xiaocong.unitil.XCPayuntil;
import com.xiaocong.xcobject.XCRechargeData;

/**
 * 选择支付途径
 * 
 * @author Maximus
 * 
 */
public class ChosesPayWaysActivity extends Activity implements OnClickListener,
        OnItemClickListener, OnLoadCompleteListener<XCRechargeData>, PurchaseCallBack {

    private GridView paywaysGridview;

    private MySimpleAdapter mySimpleAdapter, mySimpleAdapterCards;
    // private GetPayWaysLoader getPayWaysLoader;
    private String typeforpayways, productCode, outOrderNumber, pakegename;
    private int screenwight, screenheight;
    private RelativeLayout.LayoutParams gridviewlp;
    private TopayforGameLoader loader;
    private int payType;

    private ProgressDialog mProgress, xcPayProgress, ppPayProgress;
    private ArrayList<Map<String, Object>> imglist = new ArrayList<Map<String, Object>>();
    private ArrayList<Map<String, Object>> listforucard = new ArrayList<Map<String, Object>>();
    private RelativeLayout cmccLayoutFather, yeepayLayoutFather, cmccChossemoney, paysueessLayout,
            yeepayChossemoney, cardXcInfoCvv2layout;
    private TextView toptile, xieyi_tv, yeepaymoney_cardtv, yeepaymoney_tv;
    private EditText get_phonenum;
    private Button yeepay_steps, get_more_card;
    private String CMBCNUM, CMBCNUM2;
    private boolean clikforlist = false, hasmore_card = false, hasCardnum = false;
    // MyAutoCompleteTextView autoCompleteTextView1;
    //
    private LinearLayout yeepaymoney_layout_card;
    private InputMethodManager imm;
    private EditText autoCompleteTextView1, valdity_edit, ccv2_edit, card_phone_edit;
    private SharedPreferences cardshare;
    private Editor editcard;
    private ListView thiscard_show;
    private boolean insidecharge = false, completepay = false, isthird_pay = false,
            select_xieyi = false;
    private Button xieyi_box;
    private ChosesPayWaysActivity intece;
    private static final int REQUEST_PPAPPCODE = 0x100001;
    private CcApi ccApi;
    private OrderData orderData;
    private static final Integer MER_ID = 2010;

    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.xc_allpayways_chooses);
        intece = this;

        // 获取Intent数据
        Intent getxc_infoIntent = getIntent();
        typeforpayways = getxc_infoIntent.getStringExtra("payType");
        // typeforpayways = "3,4,5,8,18";
        outOrderNumber = getxc_infoIntent.getStringExtra("outOrderNumber");
        productCode = getxc_infoIntent.getStringExtra("productCode");
        pakegename = getxc_infoIntent.getStringExtra("pakegename");
        accessToken = getxc_infoIntent.getStringExtra("accessToken");

        setupView();
    }

    private void setupView() {
        screenwight = UntilTools.getScreenPoint(this).x;
        screenheight = UntilTools.getScreenPoint(this).y;
        loader = new TopayforGameLoader(this);
        loader.registerListener(0, this);
        gridviewlp = new RelativeLayout.LayoutParams((int) (screenwight * 0.875),
                (int) (screenheight * 0.5) + 10);

        Log.e("Choses_payWays_Activity", typeforpayways + "////" + outOrderNumber);
        paywaysGridview = (GridView) findViewById(R.id.payways_gridview);
        UIUtils.setViewSizeX(paywaysGridview, gridviewlp.width);
        UIUtils.setViewSizeY(paywaysGridview, gridviewlp.height);

        paywaysGridview.setCacheColorHint(android.R.color.transparent);
        paywaysGridview.setSelector(android.R.color.transparent);
        paywaysGridview.setVerticalSpacing(10);
        paywaysGridview.setGravity(Gravity.CENTER);
        paywaysGridview.setOnItemClickListener(this);
        mySimpleAdapter = new MySimpleAdapter(this, imglist, R.layout.xc_itemfor_grid,
                new String[] { "keyname", "img" }, new int[] { R.id.itemname, R.id.payway_img },
                gridviewlp, 1);
        paywaysGridview.setAdapter(mySimpleAdapter);

        renderPayWays(typeforpayways);

        yeepaymoney_layout_card = (LinearLayout) findViewById(R.id.yeepaymoney_layout_card);

        yeepaymoney_cardtv = (TextView) findViewById(R.id.yeepaymoney_cardtv);
        yeepaymoney_tv = (TextView) findViewById(R.id.yeepaymoney_tv);
        yeepaymoney_tv.setText(Integer.parseInt(productCode) * 0.01 + " RMB");
        cardXcInfoCvv2layout = (RelativeLayout) findViewById(R.id.card_xc_info_cvv2layout);
        xieyi_tv = (TextView) findViewById(R.id.xieyi_tv);
        xieyi_box = (Button) findViewById(R.id.xieyi_box);
        xieyi_box.setSelected(true);
        yeepayChossemoney = (RelativeLayout) findViewById(R.id.yeepay_chossemoney);
        cmccLayoutFather = (RelativeLayout) findViewById(R.id.cmcc_layout_father);
        yeepayLayoutFather = (RelativeLayout) findViewById(R.id.yeepay_layout_father);
        valdity_edit = (EditText) findViewById(R.id.valdity_edit);
        ccv2_edit = (EditText) findViewById(R.id.ccv2_edit);
        yeepay_steps = (Button) findViewById(R.id.yeepay_steps);
        card_phone_edit = (EditText) findViewById(R.id.card_phone_edit);
        thiscard_show = (ListView) findViewById(R.id.thiscard_show);
        cardshare = getSharedPreferences("AllCardNums", MODE_PRIVATE);
        get_more_card = (Button) findViewById(R.id.get_more_card);
        autoCompleteTextView1 = (EditText) findViewById(R.id.autoCompleteTextView1);
        toptile = (TextView) findViewById(R.id.toptile);
        get_more_card.setOnClickListener(this);
        yeepay_steps.setOnClickListener(this);

        final double factor = 0.13;
        UIUtils.setViewSizeY(toptile, (int) (screenheight * factor));
        UIUtils.setViewSizeX(autoCompleteTextView1, (int) (screenwight * 0.5));
        UIUtils.setViewSizeY(autoCompleteTextView1, (int) (screenheight * 0.1));

        UIUtils.setViewSizeX(get_more_card, (int) (screenwight * 0.05) - 3);
        UIUtils.setViewSizeY(get_more_card, (int) (screenwight * 0.05) - 3);

        UIUtils.setViewSizeX(valdity_edit, (int) (screenwight * 0.5));
        UIUtils.setViewSizeY(valdity_edit, (int) (screenheight * 0.1));
        UIUtils.setViewSizeX(ccv2_edit, (int) (screenwight * 0.5));
        UIUtils.setViewSizeY(ccv2_edit, (int) (screenheight * 0.1));
        UIUtils.setViewSizeX(card_phone_edit, (int) (screenwight * 0.5));
        UIUtils.setViewSizeY(card_phone_edit, (int) (screenheight * 0.1));
        UIUtils.setViewSizeX(yeepay_steps, (int) (screenwight * 0.16));
        UIUtils.setViewSizeY(yeepay_steps, (int) (screenheight * 0.11));
        UIUtils.setViewSizeX(xieyi_box, (int) (screenheight * 0.07));
        UIUtils.setViewSizeY(xieyi_box, (int) (screenheight * 0.07));

        xieyi_tv.setText(R.string.xcsdk_agree_charge);

        UIUtils.setViewSizeY(xieyi_tv, (int) (screenheight * 0.11));
        xieyi_tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    xieyi_tv.setBackgroundResource(R.drawable.xcxyprotocol_focus);
                } else {
                    xieyi_tv.setBackgroundDrawable(null);
                }

            }
        });
        xieyi_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ChosesPayWaysActivity.this, R.style.dialognotitle);

                dialog.setContentView(R.layout.xc_pay_xieyi);
                TextView contexttv = (TextView) dialog.findViewById(R.id.context_xiyi);
                Button buttoncloles = (Button) dialog.findViewById(R.id.colse_xiyi);
                UIUtils.setViewSizeX(buttoncloles, (int) (screenwight * 0.16));
                UIUtils.setViewSizeY(buttoncloles, (int) (screenwight * 0.06));
                buttoncloles.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                contexttv.setText(R.string.xiaocongxiyi_text);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                dialog.getWindow().getAttributes().width = (int) ((dm.widthPixels) * 0.75);
                dialog.getWindow().getAttributes().height = (int) ((dm.heightPixels) * 0.80);
                dialog.show();
            }
        });
        try {
            UIUtils.setViewMarginSize(yeepayChossemoney, 0, (int) (screenheight * 0.06), 0, 0);

            UIUtils.setViewMarginSize(paywaysGridview,
                    (int) ((screenwight - screenwight * 0.8) * 0.2), (int) (screenheight * 0.06),
                    (int) ((screenwight - screenwight * 0.8) * 0.2), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        xieyi_box.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!select_xieyi) {
                    select_xieyi = true;
                    xieyi_box.setSelected(true);
                } else {
                    select_xieyi = false;
                    xieyi_box.setSelected(false);
                }
            }
        });

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // 　　boolean isOpen=imm.isActive();
        // 　　isOpen若返回true，则表示输入法打开
        autoCompleteTextView1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                imm.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });
        ccv2_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                imm.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });
        valdity_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                imm.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });
        card_phone_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                imm.showSoftInput(v, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

    }

    /** 获取支持的支付类型的图标 */
    private void loadPayTypeIcon(String types) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("img", findPayTypeFocusIcon(Integer.parseInt(types)));
        map.put("keyname", types + "");
        imglist.add(map);
    }

    public int findPayTypeFocusIcon(int type) {
        int types = 0;
        if (type == 3) {

            types = R.drawable.xc_btn_alipay;
        } else if (type == 4) {
            types = R.drawable.xc_btn_china_mobile;
        } else if (type == 5) {
            types = R.drawable.xc_btn_yeepay;
        } else if (type == 8) {
            types = R.drawable.xc_btn_xiaocongpay;
        } else if (type == 18) {
            types = R.drawable.xc_btn_weixinpay;
        } else if (type == 19) {
            types = R.drawable.xc_btn_payofpp;
        } else if (type == 17) {
            types = R.drawable.xc_btn_coopay;
        }
        return types;
    }

    /** 显示支持的支付方式 */
    private void renderPayWays(String typeStr) {
        String[] types = null;
        if (typeStr != null) {
            types = typeStr.split(",");
        }
        Log.e("types.length", types.length + "");
        for (int i = 0; i < types.length; i++) {
            loadPayTypeIcon(types[i]);
        }
        mySimpleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        TextView textView = (TextView) arg1.findViewById(R.id.itemname);
        int way = Integer.parseInt(textView.getText().toString());
        whichway(way);
    }

    /**
     * 选择了一种支付方式<br>
     * 1 电信手机支付 + 2 联通手机支付 + 3 支付宝无线 + 4 移动手机支付 + 5 易宝信用卡支付 + 6 易联语音支付（银行卡） ? 7 阿里巴巴 +
     * 
     * 10 支付宝快捷支付 11 百视通 12 掌中付 + 13 海信支付 14 mo9支付 ? 8 微信支付　18　小葱支付 19ＰＰ支付
     * 
     * 
     */

    private void whichway(int i) {
        payType = i;
        switch (i) {

        case 3:
            // ｛｛支付宝无线｝｝
            boolean b = UntilTools.thepaytool(ChosesPayWaysActivity.this, pakegename);
            Log.e("pakegename", pakegename + "");
            if (!b) {
                return;
            }
            // TODO ｛｛判断外部订单号是否为空应该在外面一次性做掉｝｝
            if (outOrderNumber != null) {
                loader.setParams("", (Integer.parseInt(productCode)) + "", payType, outOrderNumber,
                        accessToken);
                loader.startLoading();

            } else {
                Toast.makeText(ChosesPayWaysActivity.this, R.string.getordernum_failed,
                        Toast.LENGTH_LONG).show();
            }

            break;
        case 5:
            insidecharge = true;
            /**
             * 界面一次选定执行信用卡选取
             */
            paywaysGridview.setVisibility(View.GONE);
            yeepayLayoutFather.setVisibility(View.VISIBLE);
            toptile.setText("易宝支付");
            // ｛｛下面是加载之前输入过的信用卡吗｝｝
            editcard = cardshare.edit();
            Map<String, ?> cardmap = cardshare.getAll();
            Iterator<?> iter = cardmap.entrySet().iterator();
            Log.e("Choses_payWays_Activity", cardmap.size() + "");
            while (iter.hasNext()) {
                Map<String, Object> cardnum = new HashMap<String, Object>();
                @SuppressWarnings("rawtypes")
                Map.Entry entry = (Map.Entry) iter.next();
                Object val = entry.getValue();
                cardnum.put("shownum", UntilTools.replaceSubString(val.toString().trim()));
                cardnum.put("shownum2", val.toString().trim());
                listforucard.add(cardnum);
            }
            mySimpleAdapterCards = new MySimpleAdapter(ChosesPayWaysActivity.this, listforucard,
                    R.layout.xc_itemfor_cardlist, new String[] { "shownum", "shownum2" },
                    new int[] { R.id.shownum, R.id.shownum2 }, gridviewlp, 0);
            thiscard_show.setAdapter(mySimpleAdapterCards);
            mySimpleAdapterCards.notifyDataSetChanged();
            if (listforucard != null && listforucard.size() > 0) {
                autoCompleteTextView1.setText(listforucard.get(0).get("shownum").toString());
                hasmore_card = true;
                get_more_card.setVisibility(View.VISIBLE);
            } else {
                get_more_card.setVisibility(View.GONE);
                hasmore_card = false;
            }

            thiscard_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    TextView textView = (TextView) arg1.findViewById(R.id.shownum);
                    TextView textView1 = (TextView) arg1.findViewById(R.id.shownum2);
                    CMBCNUM = textView.getText().toString().trim();
                    CMBCNUM2 = textView1.getText().toString().trim();
                    if (CMBCNUM != null) {
                        autoCompleteTextView1.setText(CMBCNUM);
                    }
                    thiscard_show.setVisibility(View.GONE);
                    get_more_card.setBackgroundResource(R.drawable.xc_btn_dropdown_bg);// 换向下的图
                }
            });
            break;
        case 17:
            // 酷开
            xcPayProgress = UntilTools.showProgress(ChosesPayWaysActivity.this, null,
                    getString(R.string.push_ordernum), false, true);
            loader.setParams("", productCode, 7, outOrderNumber, accessToken);
            loader.startLoading();
            break;

        case 19:
            // 测试用的PP支付接口
            if (outOrderNumber != null) {
                loader.setParams("", productCode, 9, outOrderNumber, accessToken);
                loader.startLoading();

            } else {
                Toast.makeText(ChosesPayWaysActivity.this, R.string.getordernum_failed,
                        Toast.LENGTH_LONG).show();
            }

            break;
        case 8:
            loader.setParams("", productCode, 1, outOrderNumber, accessToken);
            loader.startLoading();
            break;

        // 以下支付方式不支持
        case 4:
        case 18:
            Toast.makeText(ChosesPayWaysActivity.this, R.string.waiting_min, Toast.LENGTH_LONG)
                    .show();

        }
    }

    @Override
    public void onClick(View v) {
        int a, b, c, d;
        a = R.id.cmcc_enter;
        b = R.id.close_Btn;
        c = R.id.get_more_card;
        d = R.id.yeepay_steps;

        if (v.getId() == a) {
            if (get_phonenum.getText() != null) {
                if (UntilTools.isCMCCmoblie(get_phonenum.getText().toString())) {
                    xcPayProgress = UntilTools.showProgress(ChosesPayWaysActivity.this, null,
                            getString(R.string.push_ordernum), false, true);
                    String phone = get_phonenum.getText().toString();
                    loader.setParams(phone, productCode, payType, outOrderNumber, accessToken);
                    loader.startLoading();
                } else {
                    Toast.makeText(ChosesPayWaysActivity.this, R.string.notice_relphone,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ChosesPayWaysActivity.this, R.string.notice_nullphone,
                        Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == b) {
            GetGameinfomation.intenct.OutPayment(92);
            ChosesPayWaysActivity.this.finish();
        } else if (v.getId() == c) {

            if (hasmore_card) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (!clikforlist) {
                    clikforlist = true;
                    get_more_card.setBackgroundResource(R.drawable.xc_btn_dropdown_bg); // 换向下的图
                    thiscard_show.setVisibility(View.VISIBLE);
                } else {
                    get_more_card.setBackgroundResource(R.drawable.xc_btn_dropdown_bg); // 换向下的图
                    clikforlist = false;
                    thiscard_show.setVisibility(View.GONE);
                }
            }

        } else if (v.getId() == d) {
            String str, str1, str2, str3;
            if (CMBCNUM2 != null) {
                str = CMBCNUM2;
            } else {
                str = autoCompleteTextView1.getText().toString();
            }

            str1 = valdity_edit.getText().toString();
            str2 = ccv2_edit.getText().toString();
            str3 = card_phone_edit.getText().toString();
            if (hasCardnum) {
                if (hasCardnum) {

                    if (str1 != null) {
                        if (str1.length() == 4) {
                            if (str2 != null) {
                                if (str2.length() == 3) {
                                    // to pay
                                    if (UntilTools.isMobile(str3)) {

                                        if (xieyi_box.isSelected()) {
                                            loader.setParamsYeepay(str3, payType, str, str2,
                                                    Integer.parseInt(productCode), str1,
                                                    outOrderNumber);
                                            loader.startLoading();
                                        } else {
                                            Toast.makeText(ChosesPayWaysActivity.this,
                                                    R.string.notice_xiyigree, Toast.LENGTH_LONG)
                                                    .show();
                                        }

                                    } else {
                                        Toast.makeText(ChosesPayWaysActivity.this,
                                                R.string.notice_cardphone, Toast.LENGTH_LONG)
                                                .show();
                                    }
                                } else {
                                    Toast.makeText(ChosesPayWaysActivity.this,
                                            R.string.notice_card_cvv2, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(ChosesPayWaysActivity.this,
                                        R.string.notice_card_cvv2null, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ChosesPayWaysActivity.this,
                                    R.string.notice_card_vakdity, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ChosesPayWaysActivity.this,
                                R.string.notice_card_vakditynull, Toast.LENGTH_LONG).show();
                    }

                }
            } else if (str != null) {
                if (str.length() >= 16) {
                    yeepay_steps.setText("确定充值");
                    hasCardnum = true;
                    if (CMBCNUM2 != null && CMBCNUM != null) {
                        yeepaymoney_cardtv.setText(CMBCNUM);
                    } else {
                        yeepaymoney_cardtv.setText(str);
                    }
                    autoCompleteTextView1.setVisibility(View.GONE);
                    yeepaymoney_layout_card.setVisibility(View.VISIBLE);
                    cardXcInfoCvv2layout.setVisibility(View.VISIBLE);

                } else {
                    hasCardnum = false;
                    Toast.makeText(ChosesPayWaysActivity.this, R.string.notice_cardwrong,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                hasCardnum = false;
                Toast.makeText(ChosesPayWaysActivity.this, R.string.notice_cardnull,
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onLoadComplete(Loader<XCRechargeData> arg0, XCRechargeData data) {
        try {
            if (xcPayProgress != null) {
                xcPayProgress.dismiss();
                xcPayProgress = null;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (data != null) {
            completepay = true;
            if (data.message != null) {
                XCPayuntil.MESSAGE = data.message;
            }
            if (data.status == 200) {
                if (data.data != null) {
                    XCPayuntil.AOUMONT = data.data.money;
                }

                switch (payType) {
                case 3:
                    // 支付宝
                    // needs String ,sign
                    try {
                        String orderxc_info = getOrderxcInfo(XCPayuntil.GOODEECC,
                                XCPayuntil.GOODEECC, data.data.orderNumber,
                                Float.valueOf(data.data.money * 0.01 + ""), data.data.notifyUrl);
                        String signType = getSignType();
                        String strsign = sign(signType, orderxc_info);
                        // 对签名进行编码
                        strsign = URLEncoder.encode(strsign);
                        // 组装好参数
                        String xc_info = orderxc_info + "&sign=" + "\"" + strsign + "\"" + "&"
                                + getSignType();
                        // 调用pay方法进行支付
                        MobileSecurePayer msp = new MobileSecurePayer();
                        boolean bRet = msp.pay(xc_info, mHandler, AlixId.RQF_PAY,
                                ChosesPayWaysActivity.this);
                        if (bRet) {
                            // show the progress bar to indicate that we have started paying.
                            // 显示“正在支付”进度条
                            isthird_pay = true;
                            closeProgress();
                            mProgress = BaseHelper
                                    .showProgress(ChosesPayWaysActivity.this, null, getResources()
                                            .getString(R.string.notice_toalipay), false, true);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(ChosesPayWaysActivity.this,
                                "Failure calling remote service", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 4:
                    cmccChossemoney.setVisibility(View.GONE);
                    paysueessLayout.setVisibility(View.VISIBLE);

                    break;
                case 5:
                    boolean ishased = false;
                    for (int i = 0; i < listforucard.size(); i++) {
                        String s = (String) listforucard.get(i).get("shownum2");
                        if (s.equals(autoCompleteTextView1.getText().toString())) {
                            ishased = true;
                        }
                    }
                    if (ishased) {
                        int temp = cardshare.getInt("count", 0);
                        editcard.putString(String.valueOf(temp + 1), autoCompleteTextView1
                                .getText().toString().trim());
                        editcard.putInt("count", temp + 1);
                        editcard.commit();
                        cardshare = null;
                        showDialog(ChosesPayWaysActivity.this, true, "");
                    }
                    if (data.status == 200 && data.message.equals("成功")) {
                        showDialog(ChosesPayWaysActivity.this, true, "");
                    }
                    break;

                case 8:
                    // 小葱币
                    if (data.status == 200) {
                        showDialog(ChosesPayWaysActivity.this, true, "");
                    } else {
                        showDialog(ChosesPayWaysActivity.this, false, XCPayuntil.MESSAGE);
                    }
                    break;
                case 17:
                    // 酷开支付
                    orderData = new OrderData(MER_ID + "", "小葱币", "虚拟", data.data.orderNumber, "1",
                            Integer.parseInt(productCode) * 0.01);

                    new Thread() {
                        public void run() {
                            mHandler.sendEmptyMessage(99);
                        };
                    }.start();
                    break;
                case 18:
                    break;
                // PP支付动作
                case 19:
                    startPP_loading(intece, data.data.orderNumber, "", "30400010XCKJ", "",
                            data.data.notifyUrl, REQUEST_PPAPPCODE);
                    break;

                }
            } else {
                showDialog(ChosesPayWaysActivity.this, false, XCPayuntil.MESSAGE);
            }
        }
    }

    /**
     * 支付宝
     */
    protected String getOrderxcInfo(String body, String subject, String orderNumber, Float money,
            String notifyUrl) {
        String strOrderxc_info = "partner=" + "\"" + PartnerConfig.PARTNER + "\"";
        strOrderxc_info += "&";
        strOrderxc_info += "seller=" + "\"" + PartnerConfig.PARTNER + "\"";
        strOrderxc_info += "&";
        strOrderxc_info += "out_trade_no=" + "\"" + orderNumber + "\"";// 订单号
        strOrderxc_info += "&";
        strOrderxc_info += "subject=" + "\"" + subject + "\"";
        strOrderxc_info += "&";
        strOrderxc_info += "body=" + "\"" + body + "\"";
        strOrderxc_info += "&";
        strOrderxc_info += "total_fee=" + "\"" + money + "\"";
        strOrderxc_info += "&";
        strOrderxc_info += "notify_url=" + "\"" + notifyUrl + "\"";
        return strOrderxc_info;
    }

    protected String getSignType() {
        String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
        return getSignType;
    }

    /**
     * sign the order xc_info. 对订单信息进行签名
     * 
     * @param signType
     *            签名方式
     * @param content
     *            待签名订单信息
     * @return
     */
    protected String sign(String signType, String content) {
        return Rsa.sign(content, PartnerConfig.RSA_PRIVATE);
    }

    // the handler use to receive the pay result.
    // 这里接收支付结果，支付宝手机端同步通知
    // 9000 操作成功 4000 系统异常 4001 数据格式不正确 
    // 4003 该用户绑定的支付宝账户被冻结或不允许支付 4004  该用户已解除绑定 4005  绑定失败或没有绑定 4006  订单支付失败 4010  重新绑定账户。 
    // 6000 支付服务正在进行升级操作。 6001 用户中途取消支付操作。 6002 
    // 网络连接异常
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                String strRet = (String) msg.obj;
                Log.e("Choses_payWays_Activity", strRet + ""); // strRet范例：resultStatus={9000};memo={};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010新款NIKE 耐克902第三代板鞋 耐克男女鞋 386201 白红"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
                switch (msg.what) {
                case AlixId.RQF_PAY: {
                    //
                    closeProgress();
                    BaseHelper.log("Choses_payWays_Activity", strRet);
                    // 处理交易结果
                    try {
                        // 获取交易状态码，具体状态代码请参看文档
                        String tradeStatus = "resultStatus={";
                        int imemoStart = strRet.indexOf("resultStatus=");
                        imemoStart += tradeStatus.length();
                        int imemoEnd = strRet.indexOf("};memo=");
                        tradeStatus = strRet.substring(imemoStart, imemoEnd);

                        // 先验签通知
                        ResultChecker resultChecker = new ResultChecker(strRet);
                        int retVal = resultChecker.checkSign();
                        // 验签失败
                        if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
                            // 失败
                            showDialog(ChosesPayWaysActivity.this, false, "");
                        } else {// 验签成功。验签成功后再判断交易状态码
                            if (tradeStatus.equals("9000")) {
                                showDialog(ChosesPayWaysActivity.this, true, "");
                            } else {
                                // false
                                showDialog(ChosesPayWaysActivity.this, false, "");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showDialog(ChosesPayWaysActivity.this, false, "");
                    }
                }

                    break;
                case 99: {
                    if (orderData != null) {
                        ccApi = new CcApi(intece);
                        ccApi.purchase(orderData, intece);
                    }
                }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // close the progress bar
    // 关闭进度框
    protected void closeProgress() {
        try {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialog(Activity context, final boolean yesorno, String msg) {
        Dialog dialog = new Dialog(intece, R.style.PayDialog);
        dialog.setContentView(R.layout.xc_dialog_notice);

        TextView textView = (TextView) dialog.findViewById(R.id.xc_msgtv);
        if (yesorno) {
            String successMessage = getResources().getString(R.string.order_push_suc);
            if (payType == 5) {
                textView.setText(successMessage);
            } else {
                textView.setText(successMessage);
            }

        } else {
            if (!msg.endsWith("")) {
                textView.setText(msg);
            } else {
                String failMsg = getResources().getString(R.string.order_push_fail);
                Log.e("string_tag", "" + failMsg);
                textView.setText(failMsg);
            }

        }

        Button button = (Button) dialog.findViewById(R.id.xc_close_dailog);

        UIUtils.setViewSizeX(button, (int) (screenwight * 0.16));
        UIUtils.setViewSizeY(button, (int) (screenheight * 0.11));
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (yesorno) {
                    GetGameinfomation.intenct.OutPayment(99);
                    ChosesPayWaysActivity.this.finish();
                } else {
                    GetGameinfomation.intenct.OutPayment(98);
                    ChosesPayWaysActivity.this.finish();
                }
            }
        });

        Window dialogWindow = dialog.getWindow();
        dialog.show();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = (int) ((dm.widthPixels) * 0.5);
        lp.height = (int) ((dm.heightPixels) * 0.51);
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (payType != 3 && insidecharge && !completepay) {
                // 进入．但没有支付
                toptile.setText(R.string.xcsdk_xczhifupingtai);
                insidecharge = false;
                cmccLayoutFather.setVisibility(View.GONE);
                yeepayLayoutFather.setVisibility(View.GONE);
                paywaysGridview.setVisibility(View.VISIBLE);
                return false;
            } else if (!insidecharge && !isthird_pay) {
                // 选择界面　没有进行操作直接退出

                Dialog dialog = new Dialog(intece, R.style.PayDialog);
                dialog.setContentView(R.layout.xc_dialog_notice);
                TextView textView = (TextView) dialog.findViewById(R.id.xc_msgtv);

                textView.setText(R.string.notice_sumitcancel_controul);

                Button button = (Button) dialog.findViewById(R.id.xc_close_dailog);

                UIUtils.setViewSizeX(button, (int) (screenwight * 0.16));
                UIUtils.setViewSizeY(button, (int) (screenheight * 0.11));
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GetGameinfomation.intenct.OutPayment(93);
                        ChosesPayWaysActivity.this.finish();
                    }

                });
                Window dialogWindow = dialog.getWindow();
                dialog.show();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                lp.width = (int) ((dm.widthPixels) * 0.5);
                lp.height = (int) ((dm.heightPixels) * 0.51);
                dialog.getWindow().setAttributes(lp);
            } else if (insidecharge && completepay) {
                // 进入，支付完成直接退出
                GetGameinfomation.intenct.OutPayment(97);
                ChosesPayWaysActivity.this.finish();
            } else if (!insidecharge && !completepay) {
                GetGameinfomation.intenct.OutPayment(96);
                ChosesPayWaysActivity.this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /** 监听 */
    public static class MainOnCancelListener implements DialogInterface.OnCancelListener {
        private Activity mcontext;

        public MainOnCancelListener(Activity context) {
            mcontext = context;
        }

        public void onCancel(DialogInterface dialog) {
            mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_BACK));
        }
    }

    // @Override
    // public void onFocusChange(View v, boolean hasFocus) {
    // InputMethodManager imm = (InputMethodManager) v.getContext()
    // .getSystemService(INPUT_METHOD_SERVICE);
    // if (hasFocus) {
    // if (v == autoCompleteTextView1 || v == valdity_edit
    // || v == ccv2_edit || v == card_phone_edit) {
    // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    // } else {
    // imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    // }
    //
    // }
    // }
    // private void setresultCode(int resultCode){
    // //1 提交订单成功　２提交订单失败　3.支付成功,　4.支付失败
    // Intent intent=new Intent();
    // // intent.putExtras(bundle);
    // setResult(resultCode, intent);
    // }
    private void startPP_loading(final Context context, final String order, final String phone,
            final String merchantId, final String subMerId, final String reserve,
            final int requestCode) {
        ppPayProgress = new ProgressDialog(intece);
        ppPayProgress.setMessage("正在请求订单请稍后...");
        ppPayProgress.setCancelable(false);
        ppPayProgress.setCanceledOnTouchOutside(false);
        ppPayProgress.show();

        // 30400010XCKJ
        if (order != null && order.length() > 2) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("pploading", "************");
                    startPP(context, order, phone, merchantId, subMerId, reserve, requestCode);
                    // // 02-13 16:49:34.699: V/(20236):
                    // strUrl=http://124.193.184.92/payDemo/wap/orderSaveRequest.jsp?,
                    // phone=, amt=1000, merUserId=,
                    // orderDesc=彩票充值,merId=30400010TEST,subMerId=,payVersion=Credit
                    // // 02-13 16:49:34.749: V/httpURLConnection_post(20236):
                    // content：userId=&payAmt=1000&merUserId=&orderDesc=%E5%BD%A9%E7%A5%A8%E5%85%85%E5%80%BC&merId=30400010TEST&subMerId=&payVersion=Credit
                    // // 02-13 16:49:35.029: V/httpURLConnection_post(20236):
                    // 订单入库应答：2014021316464921730918
                    // // 02-13 16:49:35.029: V/(20236):
                    // strOrders=2014021316464921730918
                    //
                    // startPP(context, "2014021218043421727490", phone,
                    // merchantId, subMerId,
                    // reserve, requestCode);

                    ppPayProgress.dismiss();
                }
            }).start();
        } else {
            Toast.makeText(intece, R.string.xcsdk_order_null, Toast.LENGTH_LONG).show();
            return;
        }

    };

    /**
     * 
     * @param context
     * @param order
     *            订单号
     * @param phone
     *            　电话
     * @param merchantId
     *            　 商户编号
     * @param subMerId
     *            　二级商户编号
     * @param reserve
     *            　保留字段
     * @param requestCode
     *            　REQUEST_PPAPPCODE;
     */

    private void startPP(Context context, String order, String phone, String merchantId,
            String subMerId, String reserve, int requestCode) {
        Log.v("", "order=" + order + ", phone=" + phone + ", merchantId=" + merchantId
                + ", subMerId=" + subMerId + ", reserve=" + reserve + ", requestCode="
                + requestCode);
        // 调用PP支付
        PPCreditInterface_v1_1.startPPCreditPayment_v1_1(context, order, phone, merchantId,
                subMerId, reserve, requestCode);

    }

    /**
     * 检测ＰＰ支付回执信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("onActivityResult", "data=" + data + ", resultCode=" + resultCode);
        String strPayResult = "";
        if (data != null) {
            switch (requestCode) {
            case REQUEST_PPAPPCODE:// PP插件支付同步返回结果

                Bundle bundle = data.getExtras();
                strPayResult = bundle.getString(PPCreditInterface_v1_1.PP_RESULT_BUNDLE_KEY);
                Log.v("onActivityResult", "strPayResult=" + strPayResult);

                switch (resultCode) {
                case 0:
                    // 0：未获取到返回值，通过服务端异步回调获取
                    break;
                case PPCreditInterface_v1_1.PP_RESULT_PAYCODE_OK:
                    // 1：支付成功
                    break;
                case PPCreditInterface_v1_1.PP_RESULT_PAYCODE_ERROR:
                    // -2：支付失败
                    break;
                case PPCreditInterface_v1_1.PP_RESULT_PAYCODE_UNKNOWN:
                    // -3：结果未知，通过服务端异步回调获取
                    break;
                case PPCreditInterface_v1_1.PP_RESULT_PAYCODE_CANCEL:
                    // -4：用户取消支付
                    break;
                }

                break;
            }
            new AlertDialog.Builder(this).setTitle(R.string.xcsdk_pay_result)
                    .setMessage(strPayResult).setNeutralButton(R.string.xcsdk_confirm, null)
                    .create().show();
        }
    }

    @Override
    public void pBack(int resultstatus, String tradeid, String uslever, String resultmsg,
            double balance, String purchWay) {
        if (resultstatus == 0) {
            Toast.makeText(intece, R.string.xcsdk_success, Toast.LENGTH_LONG).show();
        } else if (resultstatus == 1) {
            Toast.makeText(intece, R.string.xcsdk_fail, Toast.LENGTH_LONG).show();
        } else if (resultstatus == 2) {
            Toast.makeText(intece, resultmsg, Toast.LENGTH_LONG).show();
        }

    }
}
