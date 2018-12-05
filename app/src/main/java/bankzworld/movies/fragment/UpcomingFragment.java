package bankzworld.movies.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import bankzworld.movies.R;
import bankzworld.movies.adapter.MovieAdapter;
import bankzworld.movies.listeners.NetworkResponseListeners;
import bankzworld.movies.pojo.Results;
import bankzworld.movies.util.NetworkProvider;
import bankzworld.movies.viewmodel.MoviesCategoryViewmodel;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingFragment extends Fragment implements NetworkResponseListeners, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "UpcomingFragment";

    @BindView(R.id.rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.network_layout)
    ConstraintLayout networkLayout;

    MoviesCategoryViewmodel moviesCategoryViewmodel;

    List<Results> resultsList = new ArrayList<>();

    private SpotsDialog spotsDialog;

    public UpcomingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_up_coming, container, false);

        ButterKnife.bind(this, view);

        getActivity().setTitle("Upcoming Movies");

        setHasOptionsMenu(true);

        setViews();

        refresh();

        return view;
    }

    private void refresh() {
        if (NetworkProvider.isConnected(getContext())) {
            networkLayout.setVisibility(View.INVISIBLE);
            moviesCategoryViewmodel.getMovies("upcoming");
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
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
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
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
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
            mRecyclerView.setAdapter(new MovieAdapter(getActivity(), results));
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
        mRecyclerView.setAdapter(new MovieAdapter(getActivity(), filteredList));
    }


    @Override
    public void onRefresh() {
        refresh();
    }
}
