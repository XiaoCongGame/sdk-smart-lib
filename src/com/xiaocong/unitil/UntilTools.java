package com.xiaocong.unitil;

import tv.xiaocong.sdk.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.xiaocong.activity.ChosesPayWaysActivity;
import com.xiaocong.alipayjo.MobileSecurePayHelper;

public class UntilTools {

    public static boolean thepaytool(Context context, String pakegename) {
        MobileSecurePayHelper mobileSecurePayHelper = new MobileSecurePayHelper(context, pakegename);
        return mobileSecurePayHelper.detectMobile_sp();
    }

    // 获得屏幕高宽
    public static Point getScreenPoint(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point(display.getWidth(), display.getHeight());
        return point;
    }

    // ISMOBILENUM
    public static boolean isMobile(String mobile) {
        if (mobile.matches("^1\\d{10}$")) {
            return true;
        }
        return false;
    }

    // IS31
    public static boolean isThrtyone(String mouth) {
        if (mouth.matches("^[1]|[3]|[5]|[7]|[8]|[10]|[12]$")) {
            return true;
        }
        return false;
    }

    // IS ctmobile Num
    public static boolean isCTmoblie(String mobile) {
        // 133、153、180、189。
        // if (mobile.matches("^18[0-9]{9}$")) {
        if (mobile.matches("^1(33|53|80|89)\\d{8}$")) {
            return true;
        }
        return false;

    }

    // IS CMCC mobile Num
    public static boolean isCMCCmoblie(String mobile) {

        if (mobile.matches("^^1(3[4-9]|5[012789]|8[278])\\d{8}$")) {
            return true;
        }
        return false;
    }

    // IS cuccmobile Num
    public static boolean isCUCCmoblie(String mobile) {

        if (mobile.matches("^1(3[0-2]|5[56]|8[56])\\d{8}$")) {
            return true;
        }
        return false;
    }

    public void toPay(Context context, String parms) {
        Intent intent = new Intent(context, ChosesPayWaysActivity.class);
        context.startActivity(intent);
    }

    public static void WarnDialog(final Activity context, String msg) {
        AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
        tDialog.setIcon(R.drawable.xc_info);
        tDialog.setTitle(R.string.confirm_xc);
        tDialog.setMessage(msg);
        tDialog.setPositiveButton(R.string.xcsdk_confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                context.finish();
            }
        });
        tDialog.setNegativeButton(R.string.xcsdk_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        tDialog.show();

    }

    // progressbar
    public static ProgressDialog showProgress(Context context, CharSequence title,
            CharSequence message, boolean indeterminate, boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        // dialog.setDefaultButton(false);
        dialog.setOnCancelListener(new ChosesPayWaysActivity.MainOnCancelListener(
                (Activity) context));

        dialog.show();
        return dialog;
    }

    /**
     * 
     * @param str
     *            　 替换字符串
     * @return
     */
    public static String replaceSubString(String str) {
        String sub = "";
        String sub2 = "";
        StringBuffer sb = null;
        try {
            sub = str.substring(0, 4);
            sub2 = str.substring(str.length() - 4, str.length());
            sb = new StringBuffer();
            for (int i = 0; i < (str.length() - 8); i++) {
                sb = sb.append("*");
            }
            sub += sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sub + sub2;
    }

}
