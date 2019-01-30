package bankzworld.movies.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bankzworld.movies.R;
import bankzworld.movies.adapter.MovieAdapter;
import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.listeners.NetworkResponseListeners;
import bankzworld.movies.network.PaginationClient;
import bankzworld.movies.network.Server;
import bankzworld.movies.pojo.Responses;
import bankzworld.movies.pojo.Results;
import bankzworld.movies.util.NetworkProvider;
import bankzworld.movies.viewmodel.MoviesCategoryViewmodel;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static bankzworld.movies.util.Config.API_KEY;

public class MainActivity extends AppCompatActivity implements NetworkResponseListeners, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    SharedPreferences preferences;
    @Inject
    Retrofit retrofit;

    private static String FRAGMENT_KEY = "KEY";
    private static String FRAGMENT = "FRAGMENT";
    private Fragment fragment;

    private Server server;

    @BindView(R.id.rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.network_layout)
    ConstraintLayout networkLayout;
    @BindView(R.id.spin_kit)
    SpinKitView mSpinKitView;

    MoviesCategoryViewmodel moviesCategoryViewmodel;
    List<Results> resultsList = new ArrayList<>();
    private SpotsDialog spotsDialog;

    private int pageNumber = 1;
    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshHold = 20;

    private MovieAdapter movieAdapter;
    private List<Results> results = new ArrayList<>();
    private GridLayoutManager layoutManager;
    private String movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set dagger
        ((DaggerApplication) getApplication()).getAppComponent().inject(this);
        boolean getMode = preferences.getBoolean(getString(R.string.get_theme_mode), false);
        if (getMode) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setViews();

        getMoviesOnline();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if (dy > 0) {
                    if (isLoading) {
                        if (totalItemCount > previousTotal) {
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!isLoading && (totalItemCount - visibleItemCount) <= (pastVisibleItems + viewThreshHold)) {
                        pageNumber++;
                        pagination(movie);
                        isLoading = true;
                    }
                }

            }
        });
        
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

    }

    private void refreshSearch(String movie) {
        if (NetworkProvider.isConnected(this)) {
            networkLayout.setVisibility(View.INVISIBLE);
            moviesCategoryViewmodel.getSearchedMovie(movie);
        } else {
            networkLayout.setVisibility(View.VISIBLE);
            spotsDialog.hide();
        }
    }

    private void refresh(String movie) {
        if (NetworkProvider.isConnected(this)) {
            networkLayout.setVisibility(View.INVISIBLE);
            moviesCategoryViewmodel.getMovies(movie, 1);
        } else {
            networkLayout.setVisibility(View.VISIBLE);
            spotsDialog.hide();
        }
    }

    private void setViews() {
        server = retrofit.create(Server.class);
        movieAdapter = new MovieAdapter(this, results);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            layoutManager = new GridLayoutManager(this, 4);
            mRecyclerView.setLayoutManager(layoutManager);
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorScheme(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        moviesCategoryViewmodel = ViewModelProviders.of(this).get(MoviesCategoryViewmodel.class);
        MoviesCategoryViewmodel.setNetworkResponseListeners(this);

        spotsDialog = new SpotsDialog(this);
    }

    private boolean getMoviesOnline() {
        // get Item from preference
        int key = preferences.getInt(getString(R.string.sort_key), 0);
        switch (key) {
            case 0:
                movie = "popular";
                this.setTitle("Popular");
                refresh("popular");
                break;
            case 1:
                movie = "top_rated";
                this.setTitle("Top Rated");
                refresh("top_rated");
                break;
            case 2:
                movie = "now_playing";
                this.setTitle("Now Playing");
                refresh("now_playing");
                break;
            case 3:
                movie = "coming_soon";
                this.setTitle("Coming Soon");
                refresh("coming_soon");
                break;
            default:
                Log.e(TAG, "onCreate: error,");
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FRAGMENT_KEY, FRAGMENT);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            getMoviesOnline();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_favourite) {
            startActivity(new Intent(this, FavouriteActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        if (i == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 4);
            mRecyclerView.setLayoutManager(layoutManager);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public void showProgress() {
        spotsDialog.show();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        spotsDialog.dismiss();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showErrorMessage(String err) {
        networkLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void passData(List<Results> results) {
        if (results != null) {
            resultsList = results;
            movieAdapter = new MovieAdapter(this, results);
            movieAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(movieAdapter);
        }
    }

    private void search(SearchView searchView) {
        searchView.setQueryHint(getString(R.string.search_query));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                refreshSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "onQueryTextChange: " + newText);
                filterQuery(resultsList, newText);
                return false;
            }
        });
    }

    // performs a search query
    private void filterQuery(List<Results> p, String query) {
        query = query.toLowerCase();
        final List<Results> filteredList = new ArrayList<>();
        for (Results results : p) {
            final String text = results.getTitle().toLowerCase();
            if (text.startsWith(query)) {
                filteredList.add(results);
            }
        }
        movieAdapter = new MovieAdapter(this, filteredList);
        movieAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(movieAdapter);
    }

    @Override
    public void onRefresh() {
        getMoviesOnline();
    }

    private void pagination(String movie) {
        mSpinKitView.setVisibility(View.VISIBLE);
        server.getPaginationMovies(PaginationClient.getClient(movie, API_KEY, pageNumber)).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, Response<Responses> response) {
                Log.i(TAG, "onResponse: Pages " + response.raw());
                if (response.isSuccessful()) {
                    results = response.body().getResults();
                    movieAdapter.addMovies(results);
                } else {
                    Toast.makeText(MainActivity.this, "No more movies to display", Toast.LENGTH_SHORT).show();
                }
                mSpinKitView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                mSpinKitView.setVisibility(View.INVISIBLE);
            }
        });
    }
}
