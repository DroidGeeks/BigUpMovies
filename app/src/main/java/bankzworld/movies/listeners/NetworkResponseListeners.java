package bankzworld.movies.listeners;

import java.util.List;

import bankzworld.movies.pojo.Results;


public interface NetworkResponseListeners {
    void showProgress();

    void hideProgress();

    void showErrorMessage(String err);

    void passData(List<Results> results);
}
