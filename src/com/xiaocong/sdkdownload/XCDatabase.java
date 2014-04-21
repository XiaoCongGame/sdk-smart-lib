package com.xiaocong.sdkdownload;

import java.io.File;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

class XCDatabase {

	private SQLiteDatabase database = null;

	public static final String TABLE_USER_APPS = "UserApps";
	public static final String TABLE_DOWNLOAD_PROGRESS = "DownloadProgress";
	public XCDatabase(String path) throws Exception {
		if (!new File(path).exists()) {
			createDatabase(path);
		} else {
			database = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READWRITE);
		}
	}
	
	private void createDatabase(String path) throws Exception {
		String sqlCreateTableUserApps = "create table UserApps (_id int primary key, name text not null, pkgName text not null, application text,smallPic text,bigPic text)";
		//paused 1 等待 2下载 3 暂停
		String sqlCreateTableDownloadProgress = "create table DownloadProgress(_id integer PRIMARY KEY AUTOINCREMENT, pkgName text,filesize integer,compeleteSize integer,url text, paused int)";
		database = SQLiteDatabase.openOrCreateDatabase(path, null);
		database.execSQL(sqlCreateTableUserApps);
		database.execSQL(sqlCreateTableDownloadProgress);
	}

	public void close() {
		if (database != null) {
			database.close();
		}
	}

	public void updateUserAppApplication(String pkgName, ContentValues cv) {
		if (database != null) {
			database.update(TABLE_USER_APPS, cv, "pkgName=?",
					new String[] { pkgName });
		}
	}

	public Cursor queryUserAppApplication(String pkgName) {
		Cursor c = null;
		if (database != null) {
			c = database.query(TABLE_USER_APPS, new String[] { "application" },
					"pkgName=?", new String[] { pkgName }, null, null, null);
		}
		return c;
	}

	public void addOrUpdateUserApp(int id, ContentValues cv) {
		// add or update user app
		if (database != null) {
			try {
				if (isRecordExists(TABLE_USER_APPS, "_id", id)) {
					database.update(TABLE_USER_APPS, cv, "_id=?",
							new String[] { String.valueOf(id) });
				} else {
					database.insert(TABLE_USER_APPS, null, cv);
				}
			} catch (Exception e) {
				Log.e("addOrUpdateUserApp", e.getMessage());
			}
		}
	}


	
	//添加下载数据
	public void addDownloadProgress(ContentValues cv) {
		if (database != null) {
			try {
				database.insert(TABLE_DOWNLOAD_PROGRESS, null, cv);
			} catch (Exception e) {
				Log.e("addOrUpdateDownloadProgress", e.getMessage());
			}
		}
	}

	public Cursor queryUserApp(String sort) {
		Cursor c = null;
		if (database != null) {
			c = database.query(TABLE_USER_APPS, null, null, null, null, null,
					sort + " desc");
		}
		return c;
	}

	private boolean isRecordExists(String table, String idField, int id) {
		Cursor c = database.query(table, new String[] { idField }, idField
				+ "=?", new String[] { String.valueOf(id) }, null, null, null);
		boolean ret = false;
		if (c != null) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				ret = true;
				c.moveToNext();
			}
			c.close();
		}
		return ret;
	}

	public boolean isHasDownloadxc_info(String pkgName) {
		int count = 0;
		if (database != null) {
			String sql = "select count(*) from DownloadProgress where pkgName=?";
			Cursor cursor = database.rawQuery(sql, new String[] { pkgName });
			cursor.moveToFirst();
			count = cursor.getInt(0);
			cursor.close();
		}
		return count != 0;
	}
	//视频下载信息判定
	public boolean isHasDownloadVideoxc_info(String videoName) {
		int count = 0;
		if (database != null) {
			String sql = "select count(*) from DownloadVideoProgress where videoName=?";
			Cursor cursor = database.rawQuery(sql, new String[] { videoName });
			cursor.moveToFirst();
			count = cursor.getInt(0);
			cursor.close();
		}
		return count != 0;
	}
	
	
	
	
	
	//删除下载数据
	public void deleteDownloadProgress(String where, String[] whereArgs) {
		if (database != null) {
			database.delete(TABLE_DOWNLOAD_PROGRESS, where, whereArgs);
		}
	}


	//更新下载信息
	public void updateDownloadProgress(String where, String[] whereArgs,
			ContentValues cv) {
		if (database != null) {
			database.update(TABLE_DOWNLOAD_PROGRESS, cv, where, whereArgs);
		}
	}
	
	//查询下载信息
	public Cursor queryDownloadProgress(String where, String[] whereArgs) {
		Cursor c = null;
		if (database != null) {
			c = database.query(TABLE_DOWNLOAD_PROGRESS, null, where, whereArgs,
					null, null, null);
		}
		return c;
	}
	
	//查询下载数据包名
	public Cursor queryDownloadingPackages() {
		Cursor c = null;
		if (database != null) {
			c = database.query(true, TABLE_DOWNLOAD_PROGRESS,
					new String[] { "pkgName" }, null, null, null, null, null,
					null);
		}
		return c;
	}
	
	//更新状态
	public void updateDownloadPauseStatus(String where, String[] whereArgs,
			ContentValues cv) {
		if (database != null) {
			database.update(TABLE_DOWNLOAD_PROGRESS, cv, where, whereArgs);
		}
	}
	
	
	//查询下载数据状态
	public Cursor queryDownloadPauseStatus(String where, String[] whereArgs) {
		Cursor c = null;
		if (database != null) {
			c = database.query(TABLE_DOWNLOAD_PROGRESS, new String[] {"paused", "compeleteSize" ,"filesize","pkgName"},
					where, whereArgs, null, null, null);
		}
		return c;
	}
}
