package bankzworld.movies.network

import bankzworld.movies.listeners.NetworkErrorListener
import bankzworld.movies.pojo.ErrorModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import java.net.SocketTimeoutException

class HTTPErrorHandler(private val networkErrorInterface: NetworkErrorListener?){


    private val _500 = "Internal Server Error. Please try again later"
    private val _401 = "Authorisation Failed: User not authorised"
    private val _502 = "Server Error: Bad Gateway"
    private val _503 = "Server Error: Service Unavailable"
    private val _504 = "Server Error: Gateway TimeOut"
    private val exError = "Error occurred trying to access the server. Please try again later"
    private val timeOut = "Connection timed out: Please make sure internet is stable and try again"
    private val internalError = "An error occurred trying to access the internet. " + "Make sure internet is enabled with good connectivity"

    //private val networkErrorInterface: NetworkErrorListener? = null
    private var errorModel: ErrorModel? = null
    private var gson: Gson? = null

    init {
        errorModel = ErrorModel()
        gson = Gson()
    }

    internal fun <T> httpError(response: Response<T>) {
        if (networkErrorInterface != null) {
            try {
                when (response.code()) {
                    401 -> {
                        networkErrorInterface.onError(_401)
                    }
                    413 -> {
                        networkErrorInterface.onError(response.errorBody()!!.string())
                    }
                    500 -> {
                        networkErrorInterface.onError(_500)
                    }
                    502 -> {
                        networkErrorInterface.onError(_502)
                    }
                    503 -> {
                        networkErrorInterface.onError(_503)
                    }
                    504 -> {
                        networkErrorInterface.onError(_504)
                    }
                    else -> {
                        errorModel = gson?.fromJson(response.errorBody()!!.string(), ErrorModel::class.java)
                        networkErrorInterface.onError(errorModel?.msg)
                    }
                }
            } catch (e: Exception) {
                networkErrorInterface.onError(exError)
                e.printStackTrace()
            }

        }

    }

    internal fun <T> httpFail(call: Call<T>, t: Throwable) {
        if (networkErrorInterface != null) {
            if (t is SocketTimeoutException) {
                networkErrorInterface.onError(timeOut)
            } else {
                networkErrorInterface.onError(internalError)
            }
        }
    }

}