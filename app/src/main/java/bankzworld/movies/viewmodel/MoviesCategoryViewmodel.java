package bankzworld.movies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import javax.inject.Inject;

import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.listeners.NetworkResponseListeners;
import bankzworld.movies.network.PaginationClient;
import bankzworld.movies.network.Server;
import bankzworld.movies.pojo.Responses;
import bankzworld.movies.util.Config;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static bankzworld.movies.util.Config.API_KEY;

public class MoviesCategoryViewmodel extends AndroidViewModel {
    private static final String TAG = "MoviesCategoryViewmodel";

    @Inject
    Retrofit retrofit;

    Server server;
    static NetworkResponseListeners networkResponseListeners;

    public MoviesCategoryViewmodel(@NonNull Application application) {
        super(application);
        ((DaggerApplication) getApplication()).getAppComponent().inject(this);
    }

    public static void setNetworkResponseListeners(NetworkResponseListeners networkResponseListeners) {
        MoviesCategoryViewmodel.networkResponseListeners = networkResponseListeners;
    }


    public void getMovies(String movie, int page) {
        networkResponseListeners.showProgress();
        server = retrofit.create(Server.class);
        server.getPaginationMovies(PaginationClient.getClient(movie, API_KEY, page)).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, Response<Responses> response) {
                Log.i(TAG, "onResponse: " + response.raw());
                if (response.isSuccessful()) {
                    networkResponseListeners.passData(response.body().getResults());
                    networkResponseListeners.hideProgress();
                } else {
                    networkResponseListeners.hideProgress();
                }
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                networkResponseListeners.hideProgress();
                networkResponseListeners.showErrorMessage(t.getMessage());
            }
        });
    }

    public void getSearchedMovie(String search) {
        networkResponseListeners.showProgress();
        server = retrofit.create(Server.class);
        server.getSearchedMovie(Config.API_KEY, search).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, Response<Responses> response) {
                Log.i(TAG, "onResponse: " + response.raw());
                if (response.isSuccessful()) {
                    networkResponseListeners.passData(response.body().getResults());
                    networkResponseListeners.hideProgress();
                } else {
                    networkResponseListeners.hideProgress();
                }
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                networkResponseListeners.hideProgress();
                networkResponseListeners.showErrorMessage(t.getMessage());
            }
        });
    }


}
