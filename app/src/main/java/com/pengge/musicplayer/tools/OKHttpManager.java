package com.pengge.musicplayer.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pengge on 16/8/11.
 */
public class OKHttpManager {
    public static boolean IS_ONLY_RETURN_RES   = true;

    private static OKHttpManager instance = null;
    private OkHttpClient client = null;
    private String secret       = "";

    public synchronized static OKHttpManager getInstance() {
        if (instance == null) {
            instance    = new OKHttpManager();
        }

        return instance;
    }

    private OKHttpManager() {
        client  = new OkHttpClient();
    }

    public JSONObject get(String url) {
        HashMap params = new HashMap();

        return get(url, params);
    }

    public JSONObject get(String url, HashMap<String, String> params) {
        url = buildParameters(url, params);
        Request.Builder builder   = new Request.Builder();
        Request request = builder.url(url).get().build();
        Call newCall            = client.newCall(request);
        Response response       = null;
        JSONObject jsonObject   = new JSONObject();
        try {
            response = newCall.execute();
            String body     = response.body().string();
            Log.d("miller", "get data for url " + url + " => " + body);
            jsonObject      = new JSONObject(body);

            response.close();
            response    = null;

            return jsonObject;
        } catch (IOException e) {
            Log.d("error", "请求" + url + "失败: " + e.getMessage());
        } catch (JSONException e1) {
            Log.d("error", "解析" + url + "响应数据失败: " + e1.getMessage());
        }

        if (response != null) {
            response.close();
        }

        return jsonObject;
    }

    public JSONObject post(String url, HashMap<String, String> params,Boolean isContainHeader) {
        Log.d("miller", "post request = " + url + " \n" + params.toString());
        Response response       = null;
        JSONObject jsonObject   = new JSONObject();
        Set<Map.Entry<String, String>> entries  = params.entrySet();
        Request.Builder builder = new Request.Builder();
        FormBody.Builder fbBuilder   = new FormBody.Builder();
        try {
            for (Map.Entry<String, String> entry : entries) {
                fbBuilder.add(entry.getKey(), entry.getValue());
            }
            if(isContainHeader) {
                builder.header("Referer","http://www.music.163.com");
                builder.addHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
                builder.addHeader("Cookie","appver=2.0.2");
            }
            Request request = builder.url(url).post(fbBuilder.build()).build();
            Call newCall    = client.newCall(request);

            response    = newCall.execute();
            String body = response.body().string();
            Log.d("miller", "post response body = " + url + "\n" + body);
            jsonObject  = new JSONObject(body);

            response.close();
            response    = null;

            return jsonObject;
        } catch (UnsupportedEncodingException e2) {
            //nothing to do
        } catch (IOException e) {
            Log.d("error", "请求" + url + "失败: " + e.getMessage());
        } catch (JSONException e1) {
            Log.d("error", "解析" + url + "响应数据失败: " + e1.getMessage());
        }

        if (response != null) {
            response.close();
        }

        return jsonObject;
    }
    public static OkHttpClient getUnsafeOkHttpClient() {

        try {

            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    java.security.cert.X509Certificate[] x509Certificates = new java.security.cert.X509Certificate[0];
                    return x509Certificates;
                }

            }};


            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());


            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                                          @Override
                                          public boolean verify(String hostname, SSLSession session) {
                                              return true;
                                          }
                                      }
                    ).build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject addHeaderPost(String url, HashMap<String, String> params) {
        Log.d("miller", "post request = " + url + " \n" + params.toString());
        Response response       = null;
        JSONObject jsonObject   = new JSONObject();
        Set<Map.Entry<String, String>> entries  = params.entrySet();
        Request.Builder builder = new Request.Builder();
        FormBody.Builder fbBuilder   = new FormBody.Builder();
        try {
            for (Map.Entry<String, String> entry : entries) {
                fbBuilder.add(entry.getKey(), entry.getValue());
            }
            builder.header("Referer","http://www.music.163.com");
            Request request = builder.url(url).post(fbBuilder.build()).build();
            Call newCall    = client.newCall(request);

            response    = newCall.execute();
            String body = response.body().string();
            Log.d("miller", "post response body = " + url + "\n" + body);
            jsonObject  = new JSONObject(body);

            response.close();
            response    = null;

            return jsonObject;
        } catch (UnsupportedEncodingException e2) {
            //nothing to do
        } catch (IOException e) {
            Log.d("error", "请求" + url + "失败: " + e.getMessage());
        } catch (JSONException e1) {
            Log.d("error", "解析" + url + "响应数据失败: " + e1.getMessage());
        }

        if (response != null) {
            response.close();
        }

        return jsonObject;
    }

    public void download(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        Call newCall    = client.newCall(request);
        newCall.enqueue(callback);
    }

    private String buildParameters(String url, HashMap<String, String> params) {
        StringBuffer buffer = new StringBuffer(url);
        char sep    = '?';
        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        try {
            for (Map.Entry<String, String> entry : entrySet) {
                if ((sep == '?') && (url.indexOf('?') > 0)) {
                    sep = '&';
                }

                buffer.append(sep).append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                sep = '&';
            }
        } catch (Exception e) {
            //nothing to do
        }

        return buffer.toString();
    }

    public static class Callback implements okhttp3.Callback {
        private String apkPath;
        private int readLen = 0;
        private Handler handler;
        private boolean isCanceled   = false;

        public static final int DOWNLOAD_SUCC   = 1;
        public static final int DOWNLOAD_FAIL   = 2;
        public static final int IN_PROGRESS     = 4;

        public Callback(String dir, String apkName, Handler handler) {
            File filePath    = new File(dir);
            if (!filePath.exists()) {
                filePath.mkdir();
            }

            apkPath      = dir + apkName;
            this.handler = handler;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            handler.sendEmptyMessage(DOWNLOAD_FAIL);
        }

        @Override
        public void onResponse(Call call, Response response) {
            InputStream in  = null;
            byte[] buf      = new byte[2048];
            int len         = 0;
            FileOutputStream fos    = null;

            //获取下载文件总长度
            String strContentLen= response.header("Content-Length");
            //Log.e("tag---response.header",response.header("Content-Length"));
            int contentLen = 0;
            if(strContentLen != null) {
                contentLen   = Integer.parseInt(strContentLen);
            }


            try {
                int percent = 0;
                in = response.body().byteStream();
                File file = new File(apkPath);
                fos = new FileOutputStream(file);
                while ((len = in.read(buf)) != -1) {
                    if (isCanceled) {
                        break;
                    }

                    fos.write(buf, 0, len);
                    readLen += len;

                    percent = (int)(((float)readLen / contentLen) * 100);

                    //更新下载状态
                    Message msg = new Message();
                    msg.what    = IN_PROGRESS;
                    msg.arg1    = percent;
                    handler.sendMessage(msg);
                    if (readLen >= contentLen) {
                        handler.sendEmptyMessage(DOWNLOAD_SUCC);
                    }
                }
                fos.flush();
            } catch (Exception e) {
                handler.sendEmptyMessage(DOWNLOAD_FAIL);
            } finally {
                try {
                    if (in != null) in.close();
                    if (fos != null) fos.close();
                } catch (Exception e1) {
                    handler.sendEmptyMessage(DOWNLOAD_FAIL);
                }
            }
        }

    }
}
