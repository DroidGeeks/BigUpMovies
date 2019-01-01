package bankzworld.movies.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class TopRatedFragment extends Fragment implements NetworkResponseListeners, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "PopularFragment";

    @Inject
    Retrofit retrofit;

    private Server server;

    @BindView(R.id.rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.network_layout)
    ConstraintLayout networkLayout;

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

    public TopRatedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_rated, container, false);

        ButterKnife.bind(this, view);

        ((DaggerApplication) getContext().getApplicationContext()).getAppComponent().inject(this);

        getActivity().setTitle("Top Rated Movies");

        setHasOptionsMenu(true);

        setViews();

        refresh();

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
                        pagination();
                        isLoading = true;
                    }
                }

            }
        });

        return view;
    }

    private void refresh() {
        if (NetworkProvider.isConnected(getContext())) {
            networkLayout.setVisibility(View.INVISIBLE);
            moviesCategoryViewmodel.getMovies("top_rated", 1);
        } else {
            networkLayout.setVisibility(View.VISIBLE);
            spotsDialog.hide();
        }
    }

    private void refreshSearch(String movie) {
        if (NetworkProvider.isConnected(getContext())) {
            networkLayout.setVisibility(View.INVISIBLE);
            moviesCategoryViewmodel.getSearchedMovie(movie);
        } else {
            networkLayout.setVisibility(View.VISIBLE);
            spotsDialog.hide();
        }
    }


    private void setViews() {
        server = retrofit.create(Server.class);
        movieAdapter = new MovieAdapter(getActivity(), results);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(getContext(), 2);
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            layoutManager = new GridLayoutManager(getContext(), 4);
            mRecyclerView.setLayoutManager(layoutManager);
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorScheme(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        moviesCategoryViewmodel = ViewModelProviders.of(this).get(MoviesCategoryViewmodel.class);
        MoviesCategoryViewmodel.setNetworkResponseListeners(this);

        spotsDialog = new SpotsDialog(getContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getContext(), 4);
            mRecyclerView.setLayoutManager(layoutManager);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(getContext(), 2);
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
            movieAdapter = new MovieAdapter(getActivity(), results);
            movieAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(movieAdapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu, menu);

        final MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
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
        movieAdapter = new MovieAdapter(getActivity(), filteredList);
        movieAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(movieAdapter);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void pagination() {
        server.getPaginationMovies(PaginationClient.getClient("top_rated", API_KEY, pageNumber)).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, Response<Responses> response) {
                Log.i(TAG, "onResponse: Pages " + response.raw());
                if (response.isSuccessful()) {
                    results = response.body().getResults();
                    movieAdapter.addMovies(results);

                } else {
                    Toast.makeText(getContext(), "No more movies to display", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
