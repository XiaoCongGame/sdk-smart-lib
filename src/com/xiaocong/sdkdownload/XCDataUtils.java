package com.xiaocong.sdkdownload;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class XCDataUtils {

    /**
     * 获取是否已有下载信息
     * 
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean hasDownloadInfo(Context context, String pkgName) {
        Cursor c = null;
        String sRet = null;
        if (context != null) {
            try {
                c = context.getContentResolver().query(
                        ContentUris.withAppendedId(XCDataProvider.CONTENT_URI,
                                XCDataProvider.QUERY_DOWNLOAD_PROGRESS_IS_FIRST), null, pkgName,
                        null, null);
                c.moveToFirst();
                sRet = c.getString(c.getColumnIndex("isHasDownloadxc_info"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null && !c.isClosed()) {
                    c.close();
                }
            }
            return sRet.equals("TRUE");
        } else {
            return sRet.equals("FALSE");
        }
    }

    /**
     * 删除下载信息
     * 
     * @param context
     * @param pkgName
     */
    public static void deleteDownloadInfo(Context context, String pkgName) {
        if (context != null) {
            try {
                Log.e("删除数据库", pkgName + "");
                context.getContentResolver().delete(
                        ContentUris.withAppendedId(XCDataProvider.CONTENT_URI,
                                XCDataProvider.QUERY_DOWNLOAD_PROGRESS), "pkgName=?",
                        new String[] { pkgName });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新下载信息
     * 
     * @param context
     * @param pkgName
     * @param threadId
     * @param compeleteSize
     */
    public static void updataDownloadInfo(Context context, String pkgName, int compeleteSize) {
        ContentValues cv = new ContentValues();
        cv.put("compeleteSize", compeleteSize);
        if (context != null) {
            context.getContentResolver().update(
                    ContentUris.withAppendedId(XCDataProvider.CONTENT_URI,
                            XCDataProvider.QUERY_DOWNLOAD_PROGRESS), cv, "pkgName=?",
                    new String[] { pkgName });
        }
    }

    /**
     * 
     * @param context
     * @param pkgName
     * @param Test
     * @param isPaused
     *            1 等待 2下载 3 暂停
     * 
     * 
     */
    public static void setDownloadPauseStatus(Context context, String pkgName, int isPaused) {
        ContentValues cv = new ContentValues();
        cv.put("paused", isPaused);
        if (context != null) {
            context.getContentResolver().update(
                    ContentUris.withAppendedId(XCDataProvider.CONTENT_URI,
                            XCDataProvider.QUERY_DOWNLOAD_PAUSED), cv, "pkgName=?",
                    new String[] { pkgName });
        }
    }

    public static List<String> getDownloadingPackages(Context context) {
        List<String> list = new ArrayList<String>();
        Cursor c;
        if (context != null) {
            c = context.getContentResolver().query(
                    ContentUris.withAppendedId(XCDataProvider.CONTENT_URI,
                            XCDataProvider.QUERY_DOWNLOAD_PROGRESS_LIST), null, null, null, null);
            if (c != null) {
                try {
                    c.moveToFirst();
                    while (!c.isAfterLast()) {
                        list.add(c.getString(c.getColumnIndex("pkgName")));
                        c.moveToNext();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null && !c.isClosed()) {
                        c.close();
                    }
                }
            }
            return list;
        } else {
            return list;
        }
    }

}
