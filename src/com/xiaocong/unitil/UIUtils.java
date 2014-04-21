package com.xiaocong.unitil;

import android.view.View;
import android.view.ViewGroup;

public class UIUtils {

    public static void setViewSizeX(View v, int size) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width = size;
        v.setLayoutParams(lp);
    }

    public static void setViewSizeY(View v, int size) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.height = size;
        v.setLayoutParams(lp);
    }

    public static void setViewMarginSize(View v, int marginLeft, int marginTop, int marginRight,
            int marginBottom) throws Exception {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            mlp.leftMargin = marginLeft;
            mlp.topMargin = marginTop;
            mlp.rightMargin = marginRight;
            mlp.bottomMargin = marginBottom;
            v.setLayoutParams(mlp);
        } else {
            throw new Exception("ＥＲＲＯＲ");
        }
    }

}
