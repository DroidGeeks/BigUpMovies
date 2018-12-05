package bankzworld.movies.listeners;

import java.util.List;

import bankzworld.movies.pojo.Review;

public interface ReviewListener {
    void showProgress();

    void hideProgress();

    void showList(List<Review> reviewList);

    void showMessage(String message);
}
