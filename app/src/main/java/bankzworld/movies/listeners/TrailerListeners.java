package bankzworld.movies.listeners;

import java.util.List;

import bankzworld.movies.pojo.Cast;
import bankzworld.movies.pojo.Results;
import bankzworld.movies.pojo.TrailerResult;


public interface TrailerListeners {
    void showTrailers(List<TrailerResult> trailerResults);

    void showMessage(String message);

    void showCastsList(List<Cast> cast);

    void makeOtherQueries();

    void showSimilarList(List<Results> results);

    void showRecommendedMovies(List<Results> results);

    void showProgress();

    void hideProgress();}
