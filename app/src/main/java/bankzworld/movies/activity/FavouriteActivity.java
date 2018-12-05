package bankzworld.movies.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import bankzworld.com.AppExecutor;
import bankzworld.movies.R;
import bankzworld.movies.adapter.MovieAdapter;
import bankzworld.movies.database.MovieDatabase;
import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.pojo.Results;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteActivity extends AppCompatActivity {

    private static final String TAG = "FavouriteActivity";

    @Inject
    SharedPreferences preferences;
    @Inject
    MovieDatabase movieDatabase;

    @BindView(R.id.fav_rv)
    RecyclerView mFavRv;
    @BindView(R.id.fav_layout)
    CoordinatorLayout mNoFav;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((DaggerApplication) getApplication()).getAppComponent().inject(this);
        boolean getMode = preferences.getBoolean(getString(R.string.get_theme_mode), false);
        if (getMode) {
            setTheme(R.style.DarkTheme_NoActionBar);
        } else {
            setTheme(R.style.AppTheme_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mFavRv.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mFavRv.setLayoutManager(new GridLayoutManager(this, 4));
        }
        mFavRv.setItemAnimator(new DefaultItemAnimator());
        mFavRv.setHasFixedSize(true);

        getItems();

    }

    private void getItems() {
        AppExecutor.getsInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final LiveData<List<Results>> resultsList = movieDatabase.movieDao().retrieveMovies();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultsList.observe(FavouriteActivity.this, new Observer<List<Results>>() {
                            @Override
                            public void onChanged(@Nullable List<Results> resultsList) {
                                if (!resultsList.isEmpty()) {
                                    mFavRv.setAdapter(new MovieAdapter(FavouriteActivity.this, resultsList));
                                } else {
                                    mNoFav.setVisibility(View.VISIBLE);
                                    mFavRv.setAdapter(new MovieAdapter(FavouriteActivity.this, resultsList));
                                }
                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getItems();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
