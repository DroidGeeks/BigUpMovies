package bankzworld.movies.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import bankzworld.movies.R;
import bankzworld.movies.adapter.ReviewAdapter;
import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.listeners.ReviewListener;
import bankzworld.movies.pojo.Results;
import bankzworld.movies.pojo.Review;
import bankzworld.movies.viewmodel.ReviewViewmodel;
import butterknife.BindView;
import butterknife.ButterKnife;

import static bankzworld.movies.util.Config.API_KEY;


public class ReviewActivity extends AppCompatActivity implements ReviewListener {

    private static final String TAG = "ReviewActivity";

    @Inject
    SharedPreferences preferences;

    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.review_rv)
    RecyclerView mRv;

    private ReviewViewmodel reviewViewmodel;

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
        setContentView(R.layout.activity_review);

        ButterKnife.bind(this);

        Results results = getIntent().getParcelableExtra("id");

        String id = results.getId().toString();

        this.setTitle(results.getTitle() + " (Review)");

        mRv.setHasFixedSize(true);
        mRv.setItemAnimator(new DefaultItemAnimator());
        mRv.setLayoutManager(new LinearLayoutManager(this));

        reviewViewmodel = ViewModelProviders.of(this).get(ReviewViewmodel.class);
        reviewViewmodel.setReviewListener(this);
        reviewViewmodel.getMovieReview(id, API_KEY);
    }

    @Override
    public void showProgress() {
        mPb.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mPb.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showList(List<Review> reviewList) {
        mRv.setAdapter(new ReviewAdapter(this, reviewList));
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mRv.setLayoutManager(new LinearLayoutManager(this));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRv.setLayoutManager(new LinearLayoutManager(this));
        }
    }

}

