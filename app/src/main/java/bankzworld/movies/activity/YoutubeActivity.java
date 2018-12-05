package bankzworld.movies.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import javax.inject.Inject;

import bankzworld.movies.R;
import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.pojo.TrailerResult;

import static bankzworld.movies.util.Config.GOOGLE_API_KEY;


public class YoutubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String TAG = "YoutubeActivity";
    private String YOUTUBE_VIDEO_KEY;

    @Inject
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((DaggerApplication) getApplication()).getAppComponent().inject(this);

        boolean getMode = preferences.getBoolean(getString(R.string.get_theme_mode), false);
        if (getMode) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);

        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_youtube, null);
        setContentView(constraintLayout);

        YouTubePlayerView youTubePlayerView = new YouTubePlayerView(this);
        youTubePlayerView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        constraintLayout.addView(youTubePlayerView);
        youTubePlayerView.initialize(GOOGLE_API_KEY, this);

        TrailerResult trailerResult = getIntent().getParcelableExtra("key");
        YOUTUBE_VIDEO_KEY = trailerResult.getKey();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        Log.d(TAG, "onInitializationSuccess: provider is " + provider.getClass().toString());

        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);

        if (!wasRestored) {
            youTubePlayer.cueVideo(YOUTUBE_VIDEO_KEY);
        }
    }

    @Override
    protected void onResume() {
//        
        super.onResume();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        final int REQUEST_CODE = 1;

        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, REQUEST_CODE).show();
        } else {
            String errorMessage = String.format("There was an error initializing the youtube player (%1$s)", youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {
            Log.i(TAG, "onPlaying: ");
        }

        @Override
        public void onPaused() {
            Log.i(TAG, "onPaused: ");
        }

        @Override
        public void onStopped() {
            Log.i(TAG, "onStopped: ");
        }

        @Override
        public void onBuffering(boolean b) {
            Log.i(TAG, "onBuffering: ");
        }

        @Override
        public void onSeekTo(int i) {
            Log.i(TAG, "onSeekTo: ");
        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {
            Log.i(TAG, "onLoading: ");
        }

        @Override
        public void onLoaded(String s) {
            Log.i(TAG, "onLoaded: ");
        }

        @Override
        public void onAdStarted() {
            Log.i(TAG, "onAdStarted: ");
        }

        @Override
        public void onVideoStarted() {
            Log.i(TAG, "onVideoStarted: ");
        }

        @Override
        public void onVideoEnded() {
            Log.i(TAG, "onVideoEnded: ");
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            Log.i(TAG, "onError: ");
        }
    };
}