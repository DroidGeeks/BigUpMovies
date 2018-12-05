package bankzworld.movies.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerResponse {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("results")
    @Expose
    public List<TrailerResult> results = null;

    public Integer getId() {
        return id;
    }

    public List<TrailerResult> getResults() {
        return results;
    }
}
