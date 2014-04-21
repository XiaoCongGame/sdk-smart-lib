package tv.xiaocong.sdk.ad;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tv.xiaocong.sdk.R;
import tv.xiaocong.sdk.XcAndroidUtils;
import tv.xiaocong.sdk.XcServiceClient;
import tv.xiaocong.sdk.XcUtils;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 下载并安装游戏。<br>
 * 不支持绑定。 <br>
 * 启动此服务的Intent需要提供游戏名称（ {@link ApkDownloadSetupService#INTENT_GAME_NAME}）和下载地址（
 * {@link ApkDownloadSetupService#INTENT_DOWNLOAD_PATH}）两个参数
 * 
 * @author yaoyuan
 * 
 */
public class ApkDownloadSetupService extends Service {

    public static final String INTENT_GAME_NAME = "game_name";

    public static final String INTENT_DOWNLOAD_PATH = "download_path";

    private static final String LOG_TAG = ApkDownloadSetupService.class.getSimpleName();

    private ExecutorService executorService;

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        // 最大并行三个线程
        executorService = Executors.newFixedThreadPool(3);

        // 为的是在UI线程中执行某些代码。
        // 注意如果不在这里初始化可能造成空指针
        handler = new Handler(this.getMainLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 强杀线程池
        executorService.shutdownNow();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        File targetDir = getApkDownloadDir();
        if (targetDir == null) {
            Log.e(LOG_TAG, "下载失败：无法访问目录");
            stopSelf(startId);
        } else {
            final String srcPath = intent.getExtras().getString(INTENT_DOWNLOAD_PATH);
            final String gameName = intent.getExtras().getString(INTENT_GAME_NAME);

            // 随机生成一个文件名
            final File targetFile = new File(targetDir, XcUtils.getTimestampString() + ".apk");

            // 新线程执行
            executorService.execute(new Runnable() {

                @Override
                public void run() {
                    downloadThenInstall(srcPath, targetFile, gameName);

                    stopSelf(startId);
                }
            });
        }

        return START_NOT_STICKY;
    }

    /**
     * 下载并安装
     * 
     * @param srcPath
     *            需要是以协议开头的完整路径。如http://x.x.c/a.apk
     * @param targetFile
     *            下载文件的保存位置
     * @param gameName
     *            游戏名称
     */
    private void downloadThenInstall(final String srcPath, final File targetFile,
            final String gameName) {
        try {
            XcServiceClient.downloadFile(srcPath, targetFile);
        } catch (RuntimeException e) {
            showToastInUiThread(gameName
                    + getResources().getString(R.string.xcsdk_download_game_fail));
            Log.e(LOG_TAG, "下载失败", e);
            return;
        }

        XcAndroidUtils.installApk(ApkDownloadSetupService.this, targetFile.getAbsolutePath());
    }

    private void showToastInUiThread(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ApkDownloadSetupService.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public File getApkDownloadDir() {
        if (!XcAndroidUtils.isExternalStorageReadable()
                || !XcAndroidUtils.isExternalStorageWritable()) {
            return null;
        }

        File file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        return file;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
