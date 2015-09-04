package me.relex.okhttpmanager;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.concurrent.ConcurrentHashMap;

public class OkHttpClientManager {
    private static final BaseCallback sDefaultCallback = new BaseCallback();
    private static OkHttpClientManager mInstance;
    private final OkHttpClient mOkHttpClient;
    private final Handler mUIHandler;

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    public OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient();
        mUIHandler = new Handler(Looper.getMainLooper());
    }

    public static void setCookieHandler(CookieStore cookieStore) {
        getInstance().getOkHttpClient()
                .setCookieHandler(
                        new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    private Handler getUIHandler() {
        return mUIHandler;
    }

    // Send Request
    public static void syncRequest(Request request, BaseCallback callback) {
        callback.onRequestStart();
        Call call = getInstance().getOkHttpClient().newCall(request);
        try {
            Response response = call.execute();
            callback.onResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    public static void asyncRequest(Request request, BaseCallback callback) {
        if (callback == null) {
            callback = sDefaultCallback;
        }
        callback.setUIHandler(getInstance().getUIHandler());
        callback.onRequestStart();
        getInstance().getOkHttpClient().newCall(request).enqueue(callback);
    }

    /////////// GET ///////////
    public static void getSync(String url, final BaseCallback callback) {
        getSync(url, null, callback);
    }

    public static void getSync(String url, RequestParams requestParams,
            final BaseCallback callback) {
        final Request request =
                new Request.Builder().url(getUrlWithQueryString(url, requestParams)).build();
        syncRequest(request, callback);
    }

    public static void get(String url, final BaseCallback callback) {
        get(url, null, callback);
    }

    public static void get(String url, RequestParams requestParams, final BaseCallback callback) {
        final Request request =
                new Request.Builder().url(getUrlWithQueryString(url, requestParams)).build();
        asyncRequest(request, callback);
    }

    public static String getUrlWithQueryString(String url, RequestParams requestParams) {
        if (requestParams == null || requestParams.getUrlParams().isEmpty()) {
            return url;
        }
        if (url == null) throw new IllegalArgumentException("url == null");
        String paramUrl = requestParams.toString();

        if (url.indexOf('?') == -1) {
            url += "?" + paramUrl;
        } else {
            url += "&" + paramUrl;
        }
        return url;
    }

    /////////// POST ///////////
    public static void postSync(String url, final BaseCallback callback) {
        postSync(url, null, callback);
    }

    public static void postSync(String url, RequestParams requestParams,
            final BaseCallback callback) {
        Request request =
                new Request.Builder().url(url).post(createPostRequest(requestParams)).build();
        syncRequest(request, callback);
    }

    public static void post(String url, final BaseCallback callback) {
        post(url, null, callback);
    }

    public static void post(String url, RequestParams requestParams, final BaseCallback callback) {
        Request request =
                new Request.Builder().url(url).post(createPostRequest(requestParams)).build();
        asyncRequest(request, callback);
    }

    public static RequestBody createPostRequest(RequestParams requestParams) {
        RequestBody requestBody;
        if (requestParams == null || (requestParams.getFileParams().isEmpty()
                && requestParams.getStreamParams().isEmpty())) {
            requestBody = buildFormRequestBody(requestParams);
        } else {
            requestBody = buildMultipartRequestBody(requestParams);
        }

        return requestBody;
    }

    private static RequestBody buildFormRequestBody(@Nullable RequestParams requestParams) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (requestParams != null) {
            ConcurrentHashMap<String, String> urlParams = requestParams.getUrlParams();
            for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    private static RequestBody buildMultipartRequestBody(@Nullable RequestParams requestParams) {

        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

        if (requestParams != null) {
            ConcurrentHashMap<String, String> urlParams = requestParams.getUrlParams();
            for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }

            ConcurrentHashMap<String, RequestParams.FileWrapper> fileParams =
                    requestParams.getFileParams();
            for (ConcurrentHashMap.Entry<String, RequestParams.FileWrapper> entry : fileParams.entrySet()) {
                RequestParams.FileWrapper fileWrapper = entry.getValue();
                RequestBody fileBody = RequestBody.create(MediaType.parse(fileWrapper.contentType),
                        fileWrapper.file);
                builder.addFormDataPart(entry.getKey(), fileWrapper.customFileName, fileBody);
            }

            ConcurrentHashMap<String, RequestParams.StreamWrapper> streamParams =
                    requestParams.getStreamParams();
            for (ConcurrentHashMap.Entry<String, RequestParams.StreamWrapper> entry : streamParams.entrySet()) {
                final RequestParams.StreamWrapper streamWrapper = entry.getValue();
                RequestBody streamBody =
                        StreamRequestBody.create(MediaType.parse(streamWrapper.contentType),
                                streamWrapper.inputStream);
                builder.addFormDataPart(entry.getKey(), streamWrapper.name, streamBody);
            }
        }

        return builder.build();
    }
}
