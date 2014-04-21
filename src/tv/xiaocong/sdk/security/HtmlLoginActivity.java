package tv.xiaocong.sdk.security;

import tv.xiaocong.sdk.R;
import tv.xiaocong.sdk.XcServiceClient;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * 小葱用户登录
 * 
 * @author yaoyuan
 * 
 */
public class HtmlLoginActivity extends Activity {

    private static final String LOG_TAG = HtmlLoginActivity.class.getSimpleName();

    /**
     * 从Intent的数据中，利用下面的key获取access_token。
     */
    public static final String RESPONSE_ACESS_TOKEN = "access_token";

    private WebView webView;

    private static final String INDEX = XcServiceClient.SERVER_ROOT + "gamecenter/login.html";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.xcsdk_login_web);
        webView = (WebView) findViewById(R.id.xcsdk_login_web);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setDomStorageEnabled(true);

        JsObj jo = new JsObj(this);
        webView.addJavascriptInterface(jo, "JJ");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                    String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(HtmlLoginActivity.this, errorCode + ":" + description,
                        duration);
                toast.show();
            }

        });

        webView.loadUrl(INDEX);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        webView.loadUrl(INDEX);
    }

    /**
     * 与Javascript的接口层。
     * 
     */
    class JsObj {
        private Context con;

        public JsObj(Context con) {
            this.con = con;
        }

        /**
         * 用于向Javascript提供client_id。
         */
        @android.webkit.JavascriptInterface
        public String getClientId() {
            return getIntent().getStringExtra("client_id");
        }

        /**
         * 用于向Javascript提供client_secret。
         */
        @android.webkit.JavascriptInterface
        public String getClientSecret() {
            return getIntent().getStringExtra("client_secret");
        }

        /**
         * 用于向Javascript提供client_secret。
         */
        @android.webkit.JavascriptInterface
        public boolean isUsingCache() {
            return getIntent().getBooleanExtra("usingCache", true);
        }

        @android.webkit.JavascriptInterface
        public void loginSuccessfully(String accessToken) {
            Intent intent = new Intent();
            intent.putExtra("access_token", accessToken);
            setResult(RESULT_OK, intent);
            finish();
        }

        @android.webkit.JavascriptInterface
        public void loginFail(String message) {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(con, message, duration);
            toast.show();
        }

        @android.webkit.JavascriptInterface
        public void debug(String message) {
            Log.d(LOG_TAG, message);
        }
    }

}
