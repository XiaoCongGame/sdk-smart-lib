package tv.xiaocong.sdk.ad;

import java.io.File;

import tv.xiaocong.sdk.ImageViewAsyncLoader;
import tv.xiaocong.sdk.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/** 推送游戏详情页 */
public class PushGameFragment extends Fragment {

    private static final String LOG_TAG = PushGameFragment.class.getSimpleName();

    private GameSplashActivity activity;

    private ImageView gameImageView;
    private Button gameDownloadBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.xcsdk_push_game, null);

        gameImageView = (ImageView) root.findViewById(R.id.xcsdk_push_game_image);
        gameDownloadBtn = (Button) root.findViewById(R.id.xcsdk_push_game_download_btn);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = (GameSplashActivity) getActivity();

        String imagePath = getArguments().getString(GameSplashActivity.INTENT_GAME_IMAGE);
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageCacheDir = activity.getImageCacheDir();
            ImageViewAsyncLoader.load(gameImageView, imageCacheDir, imagePath);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        gameDownloadBtn.requestFocus();
        gameDownloadBtn.requestFocusFromTouch();
    }
}
