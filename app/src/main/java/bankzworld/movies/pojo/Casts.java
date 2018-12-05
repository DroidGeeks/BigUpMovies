package bankzworld.movies.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Casts {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("cast")
    @Expose
    public List<Cast> cast = null;
    @SerializedName("crew")
    @Expose
    public List<Crew> crew = null;

    public Integer getId() {
        return id;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public List<Crew> getCrew() {
        return crew;
    }
}