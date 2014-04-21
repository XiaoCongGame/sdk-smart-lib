package com.xiaocong.sdkdownload;

import java.io.File;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

public class XCDataProvider extends ContentProvider {

//	public static final Uri CONTENT_URI = Uri
//			.parse("content://com.example.xiaocongpay.data");
	public static  Uri CONTENT_URI ;
	public static final int QUERY_USER_APPS = 1;
	public static final int QUERY_DOWNLOAD_PROGRESS = 3;
	public static final int QUERY_DOWNLOAD_PROGRESS_IS_FIRST = 31;
	public static final int QUERY_DOWNLOAD_PROGRESS_LIST = 32;
	public static final int QUERY_DOWNLOAD_PAUSED = 33;
	
	XCDatabase database = null;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int action = (int) ContentUris.parseId(uri);
		switch (action) {
		case QUERY_DOWNLOAD_PROGRESS:
			database.deleteDownloadProgress(selection, selectionArgs);
			break;
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (database != null) {
			int action = (int) ContentUris.parseId(uri);
			switch (action) {
			case QUERY_USER_APPS:
				database.addOrUpdateUserApp(values.getAsInteger("_id"), values);
				break;
			case QUERY_DOWNLOAD_PROGRESS:
				database.addDownloadProgress(values);
				break;
			}

		}
		return null;
	}

	@Override
	public boolean onCreate() {
		String path = "/data/data/" + getContext().getPackageName()
				+ "/databases/";
		File dbPath = new File(path);
		if (!dbPath.exists()) {
			dbPath.mkdirs();
		}
		path += "xcdata.db";
		CONTENT_URI = Uri
				.parse("content://"+getContext().getPackageName());
		try {
			database = new XCDatabase(path);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int id = (int) ContentUris.parseId(uri);
		Cursor c = null;
		if (database != null) {
			switch (id) {
			case QUERY_USER_APPS:
				c = database.queryUserApp(sortOrder);
				break;
			case QUERY_DOWNLOAD_PROGRESS:
				c = database.queryDownloadProgress(selection, selectionArgs);
				break;
			case QUERY_DOWNLOAD_PROGRESS_IS_FIRST:
				boolean bRet = database.isHasDownloadxc_info(selection);
				c = new MatrixCursor(new String[] { "isHasDownloadxc_info" });
				((MatrixCursor) c).addRow(new String[] { (bRet ? "TRUE"
						: "FALSE") });
				break;
			case QUERY_DOWNLOAD_PROGRESS_LIST:
				c = database.queryDownloadingPackages();
				break;
			case QUERY_DOWNLOAD_PAUSED:
				c = database.queryDownloadPauseStatus(selection, selectionArgs);
				break;
			}
		}
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int action = (int) ContentUris.parseId(uri);
		switch (action) {
		case QUERY_USER_APPS:
			database.updateUserAppApplication(selectionArgs[0], values);
			break;
		case QUERY_DOWNLOAD_PROGRESS:
			database.updateDownloadProgress(selection, selectionArgs, values);
			break;
		case QUERY_DOWNLOAD_PAUSED:
			database.updateDownloadPauseStatus(selection, selectionArgs, values);
			break;
		}
		return 0;
	}

}
