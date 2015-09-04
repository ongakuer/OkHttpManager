package me.relex.okhttpmanager.sample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import me.relex.okhttpmanager.OkHttpClientManager;
import me.relex.okhttpmanager.RequestParams;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mResultTextView;
    private ProgressDialog mProgressDialog;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.get_button).setOnClickListener(this);
        findViewById(R.id.post_button).setOnClickListener(this);

        mResultTextView = (TextView) findViewById(R.id.result_text);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading…");

        // Set CookieStore
        //OkHttpClientManager.setCookieHandler(new PersistentCookieStore(this));
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_button: {
                mResultTextView.setText("");

                RequestParams params = new RequestParams();
                params.put("q", "fly me to the moon");
                OkHttpClientManager.get("http://api.douban.com/v2/music/search", params,
                        new StringCallback() {

                            @Override public void onRequestStart() {
                                mProgressDialog.show();
                            }

                            @Override public void onRequestFinish() {
                                mProgressDialog.dismiss();
                            }

                            @Override public void onFailure(int statusCode) {
                                Toast.makeText(MainActivity.this, "Error code =" + statusCode,
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override public void onSuccess(String response) {
                                mResultTextView.setText(response);
                            }
                        });

                break;
            }
            case R.id.post_button:
                mResultTextView.setText("");

                //Use Like Get Method
                //OkHttpClientManager.post();

                break;
        }
    }
}
