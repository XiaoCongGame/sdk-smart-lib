package com.xiaocong.xcapi;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

public class HttpRequest {

    public static String post(String host, List<BasicNameValuePair> params, String encoding) {
        HttpPost httpPost = new HttpPost(host);
        try {
            UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(params, encoding);
            httpPost.setEntity(p_entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return executeForResult(httpPost, encoding);
    }

    private static BasicHttpParams buildHttpParams() {
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
        return httpParams;
    }

    private static String executeForResult(HttpRequestBase request, String encoding) {
        try {
            DefaultHttpClient client = new DefaultHttpClient(buildHttpParams());
            HttpResponse response = client.execute(request);
            String result = "";
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                result = EntityUtils.toString(response.getEntity(), encoding);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
