package bankzworld.movies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import javax.inject.Inject;

import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.listeners.ReviewListener;
import bankzworld.movies.network.Server;
import bankzworld.movies.pojo.ReviewResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReviewViewmodel extends AndroidViewModel {

    private static final String TAG = "ReviewViewmodel";

    @Inject
    Retrofit retrofit;

    Server server;

    static ReviewListener reviewListener;

    public ReviewViewmodel(@NonNull Application application) {
        super(application);
        ((DaggerApplication) getApplication()).getAppComponent().inject(this);
    }

    public static void setReviewListener(ReviewListener reviewListener) {
        ReviewViewmodel.reviewListener = reviewListener;
    }

    public void getMovieReview(String id, String key) {
        server = retrofit.create(Server.class);
        reviewListener.showProgress();
        server.getMovieReview(id, key).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {

                Log.i(TAG, "onResponse: " + response.raw());

                if (response.isSuccessful()) {
                    reviewListener.showList(response.body().getReviewList());
                }
                reviewListener.hideProgress();
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                reviewListener.hideProgress();
                reviewListener.showMessage(t.getMessage());
            }
        });
    }

}
