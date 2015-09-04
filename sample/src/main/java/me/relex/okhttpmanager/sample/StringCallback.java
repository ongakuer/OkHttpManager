package me.relex.okhttpmanager.sample;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import me.relex.okhttpmanager.BaseCallback;

public abstract class StringCallback extends BaseCallback {

    public abstract void onFailure(int statusCode);

    public abstract void onSuccess(String response);

    @Override public void onFailure(Request request, IOException e) {
        requestFinish();

        requestFailure(-1);
    }

    @Override public void onResponse(Response response) throws IOException {
        requestFinish();

        if (response.isSuccessful()) {
            requestSuccess(response);
        } else {
            requestFailure(response.code());
        }
    }

    private void requestSuccess(Response response) {
        try {
            final String responseString = response.body().string();

            Runnable successRunnable = new Runnable() {
                @Override public void run() {
                    onSuccess(responseString);
                }
            };

            if (mUIHandler != null) {
                mUIHandler.post(successRunnable);
            } else {
                successRunnable.run();
            }
        } catch (IOException e) {
            e.printStackTrace();

            requestFailure(-1);
        }
    }

    private void requestFailure(final int statusCode) {
        Runnable failureRunnable = new Runnable() {
            @Override public void run() {
                onFailure(statusCode);
            }
        };

        if (mUIHandler != null) {
            mUIHandler.post(failureRunnable);
        } else {
            failureRunnable.run();
        }
    }
}
