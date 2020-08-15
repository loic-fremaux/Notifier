package fr.foxy.notifier;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebviewActivity extends AppCompatActivity {

    private boolean next = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final WebView myWebView = new WebView(getApplicationContext());
        setContentView(myWebView);

        myWebView.setWebViewClient(new WebViewClient() {



            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override

            public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {

                if (url.endsWith("2fa")) {
                    next = true;
                } else if (next) {
                    next = false;
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.w("getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                myWebView.postUrl("http://api.lafermedes3chataigniers.com/user/add-token", ("token=" + token).getBytes());
                            });
                }

                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        myWebView.loadUrl("https://api.lafermedes3chataigniers.com/login");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        setContentView(myWebView);
    }
}
