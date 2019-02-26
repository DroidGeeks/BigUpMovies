package bankzworld.movies.network;

public class PaginationClient {
   public static String getClient(String movie, String apiKey, int page) {
       return "https://api.themoviedb.org/3/movie/" + movie + "?api_key=" + apiKey  + "&page=" + page;
    }
}