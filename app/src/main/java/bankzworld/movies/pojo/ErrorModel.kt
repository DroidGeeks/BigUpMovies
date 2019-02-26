package bankzworld.movies.pojo

import com.google.gson.annotations.SerializedName

class ErrorModel(
        @SerializedName("status_message")
        var msg: String = "",
        @SerializedName("status_code")
        var status_code: Int = 0,
        @SerializedName("success")
        var success: Boolean = false
)