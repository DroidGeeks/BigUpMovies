package bankzworld.movies.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import javax.inject.Inject;

import bankzworld.movies.R;
import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.pojo.TrailerResult;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerDownloader extends AppCompatActivity {

    @Inject
    SharedPreferences preferences;

    @BindView(R.id.webview)
    WebView mWebView;

    String YOUTUBE_VIDEO_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((DaggerApplication) getApplication()).getAppComponent().inject(this);

        boolean getMode = preferences.getBoolean(getString(R.string.get_theme_mode), false);
        if (getMode) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_trailer_downloader);

        ButterKnife.bind(this);

        this.setTitle("Big-Up Downloader");

        TrailerResult trailerResult = getIntent().getParcelableExtra("key");
        YOUTUBE_VIDEO_KEY = trailerResult.getKey();

        String url = "https://en.savefrom.net/#url=http://youtube.com/watch?v=" + YOUTUBE_VIDEO_KEY + "&utm_source=youtube.com&utm_medium=short_domains&utm_campaign=www.ssyoutube.com";

        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.loadUrl(url);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
