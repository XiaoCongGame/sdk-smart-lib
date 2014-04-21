package tv.xiaocong.sdk.security;

import java.lang.ref.WeakReference;

import org.json.JSONObject;

import tv.xiaocong.sdk.R;
import tv.xiaocong.sdk.UnauthorizedException;
import tv.xiaocong.sdk.XcAndroidUtils;
import tv.xiaocong.sdk.XcServiceClient;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 小葱用户登录
 * 
 * @author yaoyuan
 * 
 */
public class LoginActivity extends Activity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    /**
     * You could use this key to fetch the access_token
     */
    public static final String RESPONSE_ACESS_TOKEN = "access_token";

    private EditText usernameView;
    private EditText passwordView;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查缓存
        boolean usingCache = getIntent().getBooleanExtra("usingCache", true);
        if (usingCache) {
            String accessToken = getPreferences(MODE_PRIVATE).getString("access_token", null);
            if (accessToken != null && !accessToken.isEmpty()) {
                loginPass(accessToken);
            }
        }

        setContentView(R.layout.xcsdk_login);

        usernameView = (EditText) findViewById(R.id.xcsdk_login_username);
        String username = getPreferences(MODE_PRIVATE).getString("user_name", "");
        usernameView.setText(username);

        passwordView = (EditText) findViewById(R.id.xcsdk_login_password);

        loginBtn = (Button) findViewById(R.id.xcsdk_login_btn);

    }

    public void login(View view) {
        String username = usernameView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();

        if (username.isEmpty()) {
            XcAndroidUtils.loadAnimatorSet(R.animator.rock_horizontal, usernameView, this);
            usernameView.requestFocus();
            usernameView.requestFocusFromTouch();
            return;
        }

        if (password.isEmpty()) {
            XcAndroidUtils.loadAnimatorSet(R.animator.rock_horizontal, passwordView, this);
            passwordView.requestFocus();
            passwordView.requestFocusFromTouch();
            return;
        }

        String clientId = getIntent().getStringExtra("client_id");
        String clientSecret = getIntent().getStringExtra("client_secret");

        loginBtn.setText(R.string.xcsdk_login_logining);

        new LoginTask(this, username, password, clientId, clientSecret).execute();
    }

    private void loginPass(String accessToken) {
        Intent intent = new Intent();
        intent.putExtra("access_token", accessToken);
        setResult(RESULT_OK, intent);
        finish();
    }

    private class LoginTask extends AsyncTask<Void, Void, Object> {

        private WeakReference<LoginActivity> loginActivityRef;

        private String username;
        private String password;
        private String clientId;
        private String clientSecret;

        public LoginTask(LoginActivity activity, String username, String password, String clientId,
                String clientSecret) {
            super();
            this.loginActivityRef = new WeakReference<LoginActivity>(activity);
            this.username = username;
            this.password = password;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                JSONObject res = XcServiceClient.loginAsOauth2(clientId, clientSecret, username,
                        password);
                return res.get("access_token");
            } catch (Exception e) {
                Log.e(LOG_TAG, "login error/fail", e);
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            LoginActivity activity = loginActivityRef.get();
            if (activity == null) {
                return;
            }

            if (result instanceof UnauthorizedException) {
                activity.loginBtn.setText(R.string.xcsdk_login_btn);
                XcAndroidUtils.showInfoDialog(activity, R.string.xcsdk_title_error,
                        R.string.xcsdk_login_fail, R.string.xcsdk_btn_confirm);
            } else if (result instanceof RuntimeException) {
                activity.loginBtn.setText(R.string.xcsdk_login_btn);
                XcAndroidUtils.showInfoDialog(activity, R.string.xcsdk_title_error,
                        R.string.xcsdk_login_error, R.string.xcsdk_btn_confirm);
            } else {
                activity.loginPass((String) result);
            }

        }

    }

}
