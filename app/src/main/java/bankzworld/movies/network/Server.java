package bankzworld.movies.network;


import bankzworld.movies.pojo.CastDetails;
import bankzworld.movies.pojo.Casts;
import bankzworld.movies.pojo.Responses;
import bankzworld.movies.pojo.ReviewResponse;
import bankzworld.movies.pojo.TrailerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Server {

    @GET("movie/{movie}")
    Call<Responses> getMovies(@Path("movie") String movie, @Query("api_key") String api_key);

    @GET("search/movie")
    Call<Responses> getSearchedMovie(@Query("api_key") String api_key, @Query("query") String name);

    @GET("movie/{id}/videos?")
    Call<TrailerResponse> getTrailerKey(@Path("id") String id, @Query("api_key") String api_key);

    @GET("movie/{id}/reviews?")
    Call<ReviewResponse> getMovieReview(@Path("id") String id, @Query("api_key") String api_key);

    @GET("movie/{id}/credits?")
    Call<Casts> getCasts(@Path("id") String id, @Query("api_key") String api_key);

    @GET("movie/{movie_id}/similar?")
    Call<Responses> getSimilarMovies(@Path("movie_id") String id, @Query("api_key") String api_key);

    @GET("movie/{movie_id}/recommendations?")
    Call<Responses> getRecommendedMovies(@Path("movie_id") String id, @Query("api_key") String api_key);

    @GET("person/{person_id}")
    Call<CastDetails> getCharacterId(@Path("person_id") int id, @Query("api_key") String api_key);

}
