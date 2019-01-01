package bankzworld.movies.network;

public class PaginationClient {
   public static String getClient(String movie, String apiKey, int page) {
        String apiURL = new StringBuilder("https://api.themoviedb.org/3/movie/")
                .append(movie)
                .append("?api_key=")
                .append(apiKey)
                .append("&page=")
                .append(page)
                .toString();
        return apiURL;
    }
}