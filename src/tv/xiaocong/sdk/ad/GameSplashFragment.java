package tv.xiaocong.sdk.ad;

import java.io.File;
import java.lang.ref.WeakReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tv.xiaocong.sdk.ImageViewAsyncLoader;
import tv.xiaocong.sdk.R;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameSplashFragment extends Fragment {

    private static final String LOG_TAG = GameSplashFragment.class.getSimpleName();

    private ImageView bottomImage;
    private LinearLayout pushGamesContainer;

    private TextView gameNameView;
    private ImageView gameIconView;
    private ImageButton enterGameBtn;
    private Button bottomAdBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.xcsdk_game_splash, null);

        bottomImage = (ImageView) root.findViewById(R.id.xcsdk_splash_bottom_image);
        pushGamesContainer = (LinearLayout) root.findViewById(R.id.xcsdk_pushed_games_layout);
        gameNameView = (TextView) root.findViewById(R.id.xcsdk_splash_game_name);
        gameIconView = (ImageView) root.findViewById(R.id.xcsdk_splash_game_icon);

        enterGameBtn = (ImageButton) root.findViewById(R.id.xcsdk_game_splash_enter_btn);

        bottomAdBtn = (Button) root.findViewById(R.id.xcsdk_splash_bottom_btn);

        enterGameBtn.requestFocus();
        enterGameBtn.requestFocusFromTouch();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new DataTask(this).execute();

        // 设置游戏名、图标
        gameNameView.setText(getArguments().getString(GameSplashActivity.INTENT_GAME_NAME));

        byte[] iconBytes = getArguments().getByteArray(GameSplashActivity.INTENT_GAME_ICON);
        if (iconBytes != null) {
            gameIconView.setImageBitmap(BitmapFactory.decodeByteArray(iconBytes, 0,
                    iconBytes.length));
        }
    }

    private static class DataTask extends AsyncTask<String, Void, Object> {

        private WeakReference<GameSplashFragment> fragmentReference;

        public DataTask(GameSplashFragment f) {
            fragmentReference = new WeakReference<GameSplashFragment>(f);
        }

        @Override
        protected Object doInBackground(String... params) {
            GameSplashFragment fragment = fragmentReference.get();
            if (fragment == null) {
                return null;
            }

            GameSplashActivity activity = (GameSplashActivity) fragment.getActivity();

            try {
                JSONObject data = activity.fetchCachableData();
                return data;
            } catch (RuntimeException e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result == null) {
                return;
            }

            if (result instanceof Exception) {
                Log.e(LOG_TAG, "fetch data", (Throwable) result);
                return;
            }

            GameSplashFragment fragment = fragmentReference.get();
            if (fragment == null) {
                return;
            }

            GameSplashActivity activity = ((GameSplashActivity) fragment.getActivity());
            if (result instanceof Exception) {
                Log.e(LOG_TAG, "fecht data", (Exception) result);

                // 结束活动
                if (activity != null) {
                    fragment.getActivity().finish();
                }
            } else {
                JSONObject response = (JSONObject) result;

                File imageCacheDir = activity.getImageCacheDir();
                try {
                    renderBottom(fragment, activity, imageCacheDir, response);
                    renderPushGames(fragment, activity, imageCacheDir, response);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "consume data", e);
                }

            }
        }

        private void renderPushGames(final GameSplashFragment fragment,
                final GameSplashActivity activity, File imageCacheDir, JSONObject response)
                throws JSONException {
            if (!response.has("push")) {
                return;
            }

            JSONArray pushGames = response.getJSONArray("push");
            int count = pushGames.length();
            LayoutInflater layoutInflater = LayoutInflater.from(fragment.getActivity());
            for (int i = 0; i < count; i++) {
                JSONObject game = pushGames.getJSONObject(i);
                View item = layoutInflater.inflate(R.layout.xcsdk_game_splash_game_item, null);
                item.setTag(game);
                fragment.pushGamesContainer.addView(item);

                item.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        JSONObject game = (JSONObject) v.getTag();
                        activity.showGame(game);
                    }
                });

                if (game.has(GameDataConsts.NAME)) {
                    ((TextView) item.findViewById(R.id.xcsdk_splash_game_name)).setText(game
                            .getString(GameDataConsts.NAME));
                }

                if (game.has(GameDataConsts.SMALL_IMAGE)) {
                    ImageView imageView = (ImageView) item
                            .findViewById(R.id.xcsdk_splash_game_icon);
                    ImageViewAsyncLoader.load(imageView, imageCacheDir,
                            game.getString(GameDataConsts.SMALL_IMAGE));
                }
            }
        }

        private void renderBottom(GameSplashFragment fragment, GameSplashActivity activity,
                File imageCacheDir, JSONObject response) throws JSONException {
            if (!response.has("ad")) {
                return;
            }

            JSONObject bottomAdData = response.getJSONObject("ad");
            if (bottomAdData.has("adPic")) {
                ImageViewAsyncLoader.load(fragment.bottomImage, imageCacheDir,
                        bottomAdData.getString("adPic"));
            }

            String link = bottomAdData.optString("link", null);
            if (link != null && !link.isEmpty()) {
                activity.setAdLink(link);
                fragment.bottomAdBtn.setVisibility(View.VISIBLE);
            } else {
                fragment.bottomAdBtn.setVisibility(View.GONE);
            }
        }
    }
}
