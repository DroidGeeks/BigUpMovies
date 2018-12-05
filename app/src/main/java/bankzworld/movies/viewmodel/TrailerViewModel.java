package bankzworld.movies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import javax.inject.Inject;

import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.listeners.TrailerListeners;
import bankzworld.movies.network.Server;
import bankzworld.movies.pojo.Casts;
import bankzworld.movies.pojo.Responses;
import bankzworld.movies.pojo.TrailerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TrailerViewModel extends AndroidViewModel {

    private static final String TAG = "TrailerViewModel";

    @Inject
    Retrofit retrofit;

    Server server;

    static TrailerListeners trailerListeners;

    public TrailerViewModel(@NonNull Application application) {
        super(application);
        ((DaggerApplication) getApplication()).getAppComponent().inject(this);
    }

    public void setTrailerListeners(TrailerListeners trailerListeners) {
        TrailerViewModel.trailerListeners = trailerListeners;
    }

    public void getTrailers(String id, String key) {
        server = retrofit.create(Server.class);
        trailerListeners.showProgress();
        server.getTrailerKey(id, key).enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                Log.i(TAG, "onResponse: " + response.raw());
                if (response.isSuccessful()){
                    trailerListeners.showTrailers(response.body().getResults());
                    trailerListeners.makeOtherQueries();
                }
                trailerListeners.hideProgress();
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                trailerListeners.showMessage(t.getMessage());
                trailerListeners.hideProgress();
            }
        });
    }

    public void getCasts(String id, String key) {
        server = retrofit.create(Server.class);
        trailerListeners.showProgress();
        server.getCasts(id, key).enqueue(new Callback<Casts>() {
            @Override
            public void onResponse(Call<Casts> call, Response<Casts> response) {
                Log.i(TAG, "onResponse: " + response.raw());
                if (response.isSuccessful()){
                    trailerListeners.showCastsList(response.body().getCast());
                }else {
                    trailerListeners.hideProgress();
                }

            }

            @Override
            public void onFailure(Call<Casts> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                trailerListeners.showMessage(t.getMessage());
                trailerListeners.hideProgress();
            }
        });
    }

    public void getSimilarMovies(String id, String key) {
        server = retrofit.create(Server.class);
        trailerListeners.showProgress();
        server.getSimilarMovies(id, key).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, Response<Responses> response) {
                Log.i(TAG, "onResponse: " + response.raw());
                trailerListeners.hideProgress();
                if (response.isSuccessful()) {
                    trailerListeners.showSimilarList(response.body().getResults());
                }
                    trailerListeners.hideProgress();
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                trailerListeners.showMessage(t.getMessage());
                trailerListeners.hideProgress();
            }
        });
    }

    public void getRecommendedMovies(String id, String key) {
        server = retrofit.create(Server.class);
        trailerListeners.showProgress();
        server.getRecommendedMovies(id, key).enqueue(new Callback<Responses>() {
            @Override
            public void onResponse(Call<Responses> call, Response<Responses> response) {
                Log.i(TAG, "onResponse: " + response.raw());
                if (response.isSuccessful()) {
                    trailerListeners.showRecommendedMovies(response.body().getResults());
                }
                    trailerListeners.hideProgress();
            }

            @Override
            public void onFailure(Call<Responses> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                trailerListeners.showMessage(t.getMessage());
                trailerListeners.hideProgress();
            }
        });
    }

}
