package bankzworld.movies.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Crew {

    @SerializedName("credit_id")
    @Expose
    public String creditId;
    @SerializedName("department")
    @Expose
    public String department;
    @SerializedName("gender")
    @Expose
    public Integer gender;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("job")
    @Expose
    public String job;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("profile_path")
    @Expose
    public Object profilePath;

    public String getCreditId() {
        return creditId;
    }

    public String getDepartment() {
        return department;
    }

    public Integer getGender() {
        return gender;
    }

    public Integer getId() {
        return id;
    }

    public String getJob() {
        return job;
    }

    public String getName() {
        return name;
    }

    public Object getProfilePath() {
        return profilePath;
    }
}