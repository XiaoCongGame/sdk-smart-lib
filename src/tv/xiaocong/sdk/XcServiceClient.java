package tv.xiaocong.sdk;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

/**
 * 服务器接口客户端
 * 
 * @author yaoyuan
 * 
 */
public final class XcServiceClient {

    public static final String SERVER_ROOT = "http://data.xiaocong.tv/";
    // public static final String SERVER_ROOT = "http://172.16.32.10:8080/";

    public static final String SERVER_TVSTORE = SERVER_ROOT + "tvstore/";

    public static final String SERVER_GAMECENTER = SERVER_ROOT + "gamecenter/";

    private static final String LOG_TAG = XcServiceClient.class.getSimpleName();

    private static final int SEND_MOBILE_CODE_OPTION_TYPE = 5;

    private static final String API_ROOT = SERVER_TVSTORE + "faces.do?";

    private static final String JSON_DATE_FORMAT = "yyyy-MM-dd";

    private XcServiceClient() {
    }

    private static JSONObject buildRequestHead(String method, String version) throws JSONException {
        JSONObject head = new JSONObject();
        head.put("method", method);
        head.put("version", version);
        head.put("hardware", XcUtils.getHardware());
        head.put("server", XcUtils.getServer());

        return head;
    }

    private static JSONObject buildRequest(JSONObject head, JSONObject body) throws JSONException {
        JSONObject req = new JSONObject();
        req.put("head", head);
        req.put("body", body);

        return req;
    }

    private static URL buildRequestUrl(JSONObject req) throws JSONException {
        try {
            String uri = API_ROOT + Uri.encode("$$FACES$$") + "=" + Uri.encode(req.toString());
            return new URL(uri);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Get a JSON response from the <code>url</code>.
     * 
     * @param url
     *            the request URL
     * @param post
     * @return the response as a JSON object
     * @throws RemoteServiceException
     * @throws UnauthorizedException
     *             401/403 error
     */
    private static JSONObject doRequest(URL url, boolean post) {
        return doRequest(url, post, null);
    }

    /**
     * Get a JSON response from the <code>url</code>.
     * 
     * @param url
     *            the request URL
     * @return the response as a JSON object
     * @throws RemoteServiceException
     * @throws UnauthorizedException
     *             401/403 error
     */
    private static JSONObject doRequest(URL url, boolean post, HttpErrorHandler handle400) {
        Log.d(LOG_TAG, url.toString());

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            if (post) {
                conn.setRequestMethod("POST");
            }

            if (conn.getResponseCode() == 400 && handle400 != null) {
                handle400.process(conn);
            }

            if (conn.getResponseCode() == 401 || conn.getResponseCode() == 403) {
                throw new UnauthorizedException(conn.getResponseCode());
            }

            return inputStreamToJSON(conn.getInputStream());
        } catch (JSONException e) {
            throw new RemoteServiceException(e);
        } catch (IOException e) {
            throw new RemoteServiceException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static JSONObject inputStreamToJSON(InputStream connectionInputStream)
            throws IOException, JSONException {
        InputStream in = new BufferedInputStream(connectionInputStream);
        try {
            int i = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                while ((i = in.read()) != -1) {
                    baos.write(i);
                }
                String content = baos.toString("utf-8");

                if (content == null || content.isEmpty()) {
                    Log.e(LOG_TAG, "接口无响应！");
                    throw new RemoteServiceException("No response!");
                } else {
                    return new JSONObject(content);
                }
            } finally {
                if (baos != null) {
                    baos.close();
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Send a code to user's mobile.
     * 
     * @param mobileNum
     *            user's mobile
     * @return the code sent to user
     * @throws RemoteServiceException
     * @throws UnauthorizedException
     *             401/403 error
     */
    public static String sendMobileCode(String mobileNum) {
        try {
            JSONObject reqBody = new JSONObject();
            reqBody.put("optionType", SEND_MOBILE_CODE_OPTION_TYPE);
            reqBody.put("mobile", mobileNum);
            reqBody.put("msgModel", "from sdk");

            URL url = buildRequestUrl(buildRequest(
                    buildRequestHead("retrievalValidateNumber", "3.0"), reqBody));
            JSONObject res = doRequest(url, true);

            return res.getString("validateNo");
        } catch (JSONException e) {
            throw new RemoteServiceException(e);
        }
    }

    /**
     * Register a new user in XIAOCONG user system.
     * 
     * @param mobileNum
     *            the user's mobile number
     * @param password
     * @throws RemoteServiceException
     * @throws UnauthorizedException
     *             401/403 error
     */
    public static void registerUser(String mobileNum, String password) {
        try {
            JSONObject reqHead = buildRequestHead("register", "3.3");

            JSONObject reqBody = new JSONObject();
            reqBody.put("mobile", mobileNum);
            reqBody.put("password", password);

            URL url = buildRequestUrl(buildRequest(reqHead, reqBody));
            JSONObject res = doRequest(url, true);

            if (!"200".equals(res.getString("status"))) {
                throw new RuntimeException(res.getString("message"));
            }
        } catch (JSONException e) {
            throw new RemoteServiceException(e);
        }
    }

    /**
     * Fetch the data for the game splash.
     * 
     * @param gamePkgName
     *            the package name of the game to be started.
     * @return
     * @throws RemoteServiceException
     * @throws UnauthorizedException
     *             401/403 error
     */
    public static JSONObject fetchSplashScreenData(String gamePkgName,
            SharedPreferences sharedPreferences) {
        final String PREFERENCES_DATA_CACHE_KEY = "xcsdk_splash_data";

        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT, Locale.getDefault());
        String todayString = df.format(new Date());

        String dataString = sharedPreferences.getString(PREFERENCES_DATA_CACHE_KEY, null);
        try {
            if (dataString != null && !dataString.isEmpty()) {
                JSONObject data = new JSONObject(dataString);
                if (todayString.equals(data.get("time"))) { // 数据不能过期
                    Log.d(LOG_TAG, "fetch splash data from cache");
                    return data;
                }
            }

            JSONObject data = fetchSplashScreenDataRemotely(gamePkgName, todayString);
            data.put("time", todayString); // 增加时间戳

            // TODO 检查数据，移除数据不完整的推荐游戏

            Editor sharedPreferencesEdit = sharedPreferences.edit();
            sharedPreferencesEdit.putString(PREFERENCES_DATA_CACHE_KEY, data.toString());
            sharedPreferencesEdit.commit();

            return data;
        } catch (JSONException e) {
            throw new RemoteServiceException(e);
        }
    }

    /** 从服务器获取启动屏数据 */
    private static JSONObject fetchSplashScreenDataRemotely(String gamePkgName, String todayString)
            throws JSONException {
        JSONObject reqHead = buildRequestHead("sdkads", "3.3");

        JSONObject reqBody = new JSONObject();
        reqBody.put("startDate", todayString);
        reqBody.put("endDate", todayString);
        reqBody.put("pkgName", gamePkgName);

        JSONObject req = buildRequest(reqHead, reqBody);

        Log.d(LOG_TAG, "fetchSplashScreenData.request=" + req.toString());

        URL url = buildRequestUrl(req);
        JSONObject res = doRequest(url, true);

        Log.d(LOG_TAG, "fetchSplashScreenData.response=" + res.toString());

        if (!"200".equals(res.getString("status"))) {
            throw new RemoteServiceException(res.getString("message"));
        }

        return res.getJSONObject("data");
    }

    /**
     * Download a file into the target file.
     * 
     * @param srcPath
     *            a full source path of the file。e.g. http://x.x.c/a.apk
     * @param targetFile
     *            the target file to be inserted into
     * @throws RemoteServiceException
     * @throws UnauthorizedException
     *             401/403 error
     */
    public static void downloadFile(String srcPath, File targetFile) {
        try {
            URL url = new URL(srcPath);

            InputStream is = null;
            try {
                is = url.openConnection().getInputStream();

                // 缓存
                if (targetFile != null) {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(targetFile);
                        XcUtils.copyStream(is, fos);
                    } catch (IOException e) {
                        throw new RemoteServiceException(e);
                    } finally {
                        fos.close();
                    }
                }
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (MalformedURLException e) {
            throw new RemoteServiceException(e);
        } catch (IOException e) {
            throw new RemoteServiceException(e);
        }
    }

    /**
     * Get <code>access_token</code> and other Oauth2 parameters from XIAOCONG Oauth2 service.
     * 
     * @return Oauth2 parameters. Including: <code>access_token</code>, <code>refresh_token</code>,
     *         etc.
     * @throws RemoteServiceException
     * @throws UnauthorizedException
     *             401/403 error
     */
    public static JSONObject loginAsOauth2(String clientId, String clientSecret, String username,
            String password) {
        String url = SERVER_GAMECENTER + "oauth/token" + "?grant_type=password&client_id="
                + clientId + "&client_secret=" + clientSecret + "&username=" + username
                + "&password=" + password + "&scope=basic";

        try {
            JSONObject res = doRequest(new URL(url), false, new HttpErrorHandler() {

                @Override
                public void process(HttpURLConnection conn) {
                    Log.e(LOG_TAG, "loginAsOauth2-400");
                    throw new UnauthorizedException(400);
                }
            });

            if (res.has("acess_token")) {
                throw new UnauthorizedException(403);
            }
            return res;
        } catch (MalformedURLException e) {
            throw new RemoteServiceException(e);
        }
    }

    /**
     * Get basic information of user.
     * 
     * @param accessToken
     * @throws UnauthorizedException
     *             if access_token is invalid.
     */
    public static UserBasicInfo getUserBasicInfo(String accessToken) {
        try {
            URL url = new URL(SERVER_GAMECENTER + "me.json?access_token=" + accessToken
                    + "&method=me");
            JSONObject jsonInfo = doRequest(url, false);

            Log.d(LOG_TAG, jsonInfo == null ? "" : jsonInfo.toString());

            UserBasicInfo info = new UserBasicInfo(jsonInfo.getString("userId"),
                    jsonInfo.getString("userName"));
            return info;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private static interface HttpErrorHandler {
        void process(HttpURLConnection conn);
    }
}
