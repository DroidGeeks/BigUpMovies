package bankzworld.movies.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bankzworld.com.AppExecutor;
import bankzworld.movies.R;
import bankzworld.movies.adapter.CastAdapter;
import bankzworld.movies.adapter.RecommendedAdapter;
import bankzworld.movies.adapter.SimilarAdapter;
import bankzworld.movies.adapter.TrailerAdapter;
import bankzworld.movies.database.MovieDatabase;
import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.listeners.TrailerListeners;
import bankzworld.movies.pojo.Cast;
import bankzworld.movies.pojo.Results;
import bankzworld.movies.pojo.TrailerResult;
import bankzworld.movies.viewmodel.TrailerViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;

import static bankzworld.movies.util.Config.API_KEY;
import static bankzworld.movies.util.Config.BACKDROP_PATH;


public class DetailsActivity extends AppCompatActivity implements TrailerListeners, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "DetailsActivity";
    @BindView(R.id.back_drop)
    KenBurnsView mBackDrop;
    @BindView(R.id.text_show_rating)
    TextView mRating;
    @BindView(R.id.text_show_date)
    TextView mReleasedDate;
    @BindView(R.id.trailers_rv)
    RecyclerView mTrailerList;
    @BindView(R.id.casts_rv)
    RecyclerView mCastsList;
    @BindView(R.id.similar_rv)
    RecyclerView mSimilarRv;
    @BindView(R.id.recommendation_rv)
    RecyclerView mRecommendedRv;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.overview_button)
    TextView mOverView;
    @BindView(R.id.swipe_details)
    SwipeRefreshLayout mRefresh;

    private TrailerViewModel trailerViewModel;
    private Results results;
    private Animation animation, animation2;
    private boolean ifFound = false;

    @Inject
    MovieDatabase movieDatabase;
    @Inject
    SharedPreferences preferences;

    private List<Results> resultsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialise dagger with this dependency
        ((DaggerApplication) getApplication()).getAppComponent().inject(this);
        boolean getMode = preferences.getBoolean("mode", false);
        if (getMode) {
            setTheme(R.style.ScrollDarkTheme);
        } else {
            setTheme(R.style.AppTheme_NoActionBar);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        mRefresh.setColorScheme(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent);
        mRefresh.setOnRefreshListener(this);

        // initialise views
        initRecyclerViews();

        // init get Intent
        results = getIntent().getParcelableExtra("data");

        // add all retrieved intents to the list
        resultsArrayList.add(results);
        // loop over retrieved items
        for (int i = 0; i < resultsArrayList.size(); i++) {
            results = resultsArrayList.get(i);
        }

        mOverView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOverViewDialog(R.layout.overview_layout, results.getOverview());
            }
        });

        // get other details of the movie
        getOtherDetails();

        this.setTitle(results.getOriginalTitle());
        mRating.setText(results.getVoteAverage().toString());
        mReleasedDate.setText(results.getReleaseDate());

        Picasso.get()
                .load(BACKDROP_PATH + results.getBackdropPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image_placeholder)
                .into(mBackDrop);


        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anti_clockwise);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFavouritism();
            }
        });
    }


    private void checkFavouritism() {
        if (ifFound) {
            removeFromFavourite();
        } else {
            insertToFavourite();
        }
    }

    private void getOtherDetails() {
        trailerViewModel = ViewModelProviders.of(this).get(TrailerViewModel.class);
        AppExecutor.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        trailerViewModel.setTrailerListeners(DetailsActivity.this);
                        trailerViewModel.getTrailers(results.getId().toString(), API_KEY);
                    }
                });
            }
        });
    }

    private void isExist() {
        AppExecutor.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final LiveData<List<Results>> retrieveMovies = movieDatabase.movieDao().retrieveMovies();
                retrieveMovies.observe(DetailsActivity.this, new Observer<List<Results>>() {
                    @Override
                    public void onChanged(@Nullable List<Results> resultsList) {
                        for (int i = 0; i < resultsList.size(); i++) {
                            Results res = resultsList.get(i);
                            if (res.getId().equals(results.getId())) {
                                mFab.setImageResource(R.drawable.ic_favorite_full_white_24dp);
                                ifFound = true;
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            shareIntent();
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.action_review) {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("id", results);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showOverViewDialog(int layout, String overview) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(layout, null);
        TextView mOver = view.findViewById(R.id.message);
        mOver.setText(overview);
        builder.setCancelable(false);
        builder.setView(view)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
        builder.show();
    }

    private void initRecyclerViews() {
        mTrailerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTrailerList.setItemAnimator(new DefaultItemAnimator());
        mTrailerList.setHasFixedSize(true);

        mCastsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCastsList.setItemAnimator(new DefaultItemAnimator());
        mCastsList.setHasFixedSize(true);

        mSimilarRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSimilarRv.setItemAnimator(new DefaultItemAnimator());
        mSimilarRv.setHasFixedSize(true);

        mRecommendedRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecommendedRv.setItemAnimator(new DefaultItemAnimator());
        mRecommendedRv.setHasFixedSize(true);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTrailerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mCastsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mSimilarRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mRecommendedRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mTrailerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mCastsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mSimilarRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mRecommendedRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
    }


    private void removeFromFavourite() {
        AppExecutor.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDatabase.movieDao().removeMovie(results);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFab.startAnimation(animation2);
                        mFab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                        Toast.makeText(DetailsActivity.this, "removed from favourite", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void insertToFavourite() {
        AppExecutor.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDatabase.movieDao().insertMovie(results);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFab.startAnimation(animation);
                        mFab.setImageResource(R.drawable.ic_favorite_full_white_24dp);
                        Toast.makeText(DetailsActivity.this, "added to favourite", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void shareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey there. Check out this movie \n" +
                results.getTitle());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }

    @Override
    public void showTrailers(List<TrailerResult> trailerResults) {
        mTrailerList.setAdapter(new TrailerAdapter(this, trailerResults));
    }

    @Override
    public void makeOtherQueries() {
        trailerViewModel.getCasts(results.getId().toString(), API_KEY);
        trailerViewModel.getSimilarMovies(results.getId().toString(), API_KEY);
        trailerViewModel.getRecommendedMovies(results.getId().toString(), API_KEY);
    }

    @Override
    public void showSimilarList(List<Results> results) {
        mSimilarRv.setAdapter(new SimilarAdapter(this, results));
    }

    @Override
    public void showRecommendedMovies(List<Results> results) {
        mRecommendedRv.setAdapter(new RecommendedAdapter(this, results));
    }

    @Override
    public void showProgress() {
        mRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        mRefresh.setRefreshing(false);
    }

    @Override
    public void showCastsList(List<Cast> cast) {
        mCastsList.setAdapter(new CastAdapter(this, cast));
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // checks if the item already exists int the database
        isExist();
    }

    @Override
    public void onRefresh() {
        getOtherDetails();
    }
}