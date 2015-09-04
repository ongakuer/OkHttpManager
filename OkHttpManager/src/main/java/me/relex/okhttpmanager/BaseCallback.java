package me.relex.okhttpmanager;

import android.os.Handler;
import android.support.annotation.Nullable;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

public class BaseCallback implements Callback {

    @Nullable protected Handler mUIHandler;

    public final void setUIHandler(@Nullable Handler uiHandler) {
        mUIHandler = uiHandler;
    }

    public void onRequestStart() {

    }

    public void onRequestFinish() {

    }

    @Override public void onFailure(Request request, IOException e) {

    }

    @Override public void onResponse(Response response) throws IOException {

    }


    protected void requestFinish() {
        Runnable finishRunnable = new Runnable() {
            @Override public void run() {
                onRequestFinish();
            }
        };

        if (mUIHandler != null) {
            mUIHandler.post(finishRunnable);
        } else {
            finishRunnable.run();
        }
    }

}
