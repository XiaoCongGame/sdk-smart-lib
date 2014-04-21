package tv.xiaocong.sdk.ad;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import tv.xiaocong.sdk.R;
import tv.xiaocong.sdk.XcAndroidUtils;
import tv.xiaocong.sdk.XcServiceClient;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * 游戏启动页
 * 
 * @author yaoyuan
 * 
 */
public class GameSplashActivity extends Activity {

    public static final String INTENT_GAME_NAME = "game_name";

    public static final String INTENT_GAME_PKG_NAME = "game_pkgName";

    public static final String INTENT_GAME_ICON = "game_icon";

    public static final String INTENT_GAME_IMAGE = "game_image";

    private static final String LOG_TAG = GameSplashActivity.class.getSimpleName();

    private static final String PREFERENCES_NAME = "xcsdk_splash";

    // 图片缓存目录位于：外部缓存区的此目录下
    private static final String IMAGE_CACHE_DIR_NAME = "splash_images";

    // 详情页正在显示的游戏的数据
    private JSONObject showingGameData = null;

    // 底部广告链接
    private String adLink = null;

    public void setAdLink(String adLink) {
        this.adLink = adLink;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.xcsdk_game_splash_activity);

        initSplashFragment();
    }

    public JSONObject fetchCachableData() {
        SharedPreferences ps = getSharedPreferences(GameSplashActivity.PREFERENCES_NAME,
                Activity.MODE_PRIVATE);

        return XcServiceClient.fetchSplashScreenData(
                getIntent().getExtras().getString(INTENT_GAME_PKG_NAME), ps);
    }

    private void initSplashFragment() {
        GameSplashFragment fragment = new GameSplashFragment();
        Bundle arguments = new Bundle(3);
        // 游戏名称
        arguments.putString(INTENT_GAME_NAME, getIntent().getExtras().getString(INTENT_GAME_NAME));
        // 游戏图标
        arguments.putByteArray(INTENT_GAME_ICON, getIntent().getByteArrayExtra(INTENT_GAME_ICON));

        fragment.setArguments(arguments);

        getFragmentManager().beginTransaction().add(R.id.xcsdk_game_splash_cotainer, fragment)
                .commit();
    }

    /** 查看游戏详情 **/
    public void showGame(JSONObject game) {
        showingGameData = game;

        PushGameFragment fragment = new PushGameFragment();

        try {
            Bundle args = new Bundle();
            args.putString(INTENT_GAME_IMAGE, game.getString(GameDataConsts.BIG_IMAGE));
            fragment.setArguments(args);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "big pic", e);
        }

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_throw_in, R.animator.fragment_throw_out)
                .replace(R.id.xcsdk_game_splash_cotainer, fragment).addToBackStack(null).commit();
    }

    public File getImageCacheDir() {
        if (!XcAndroidUtils.isExternalStorageReadable()
                || !XcAndroidUtils.isExternalStorageWritable()) {
            return null;
        }

        File file = new File(getExternalCacheDir(), IMAGE_CACHE_DIR_NAME);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(LOG_TAG, "Directory not created");
                return null;
            }
        }

        return file;
    }

    /** 下载并安装游戏 */
    public void downloadGame(View view) {
        getFragmentManager().popBackStack();

        if (showingGameData == null) {
            Log.e(LOG_TAG, "showingGameData为null，这是一个BUG！");
            return;
        }

        Intent service = new Intent(this, ApkDownloadSetupService.class);
        try {
            service.putExtra(ApkDownloadSetupService.INTENT_GAME_NAME,
                    showingGameData.getString(GameDataConsts.NAME));
            service.putExtra(ApkDownloadSetupService.INTENT_DOWNLOAD_PATH,
                    showingGameData.getString(GameDataConsts.DOWNLOAD_LINK));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "无法下载，游戏数据有错误", e);

            XcAndroidUtils.showInfoDialog(this, R.string.xcsdk_title_error,
                    R.string.xcsdk_download_game_bad_data, R.string.xcsdk_btn_confirm);

            return;
        }

        // 启动下载服务
        startService(service);

        // 通知用户正在下载
        XcAndroidUtils.showInfoDialog(this, R.string.xcsdk_downloading,
                R.string.xcsdk_download_game_async, R.string.xcsdk_btn_ok);
    }

    public void enterGame(View view) {
        setResult(RESULT_OK);
        finish();
    }

    public void showAd(View view) {
        Uri uri = Uri.parse(this.adLink);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}
