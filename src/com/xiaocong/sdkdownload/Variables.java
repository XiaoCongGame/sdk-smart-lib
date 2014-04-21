package com.xiaocong.sdkdownload;

import java.util.ArrayList;
import java.util.List;

public class Variables {
    public static String pkgName;
    public static String TThirdDataPath;

    public static List<String> downLoadPkgs = null;// 断点续传包名数组
    public final static String BASE_URL = "http://data.xiaocong.tv/tvstore/";
    public final static String XCPATH = "/xc";
    public final static String XCLAUNCHER = "/xc/launcher";
    public final static String XCLAUNCHERAPK = "/xc/launcher/apk/";
    public static ArrayList<String> installingApps = new ArrayList<String>();
}
