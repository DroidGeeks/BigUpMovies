package bankzworld.movies.network

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import bankzworld.movies.injection.DaggerApplication
import bankzworld.movies.listeners.NetworkErrorListener
import bankzworld.movies.pojo.Responses
import bankzworld.movies.pojo.Results
import bankzworld.movies.util.Config
import bankzworld.movies.util.LoggerUtil
import bankzworld.movies.util.NetworkProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit

//Created by arinzedroid at

class AppRepo: Application.ActivityLifecycleCallbacks {


    private val baseUrl = "https://api.themoviedb.org/3/"
    private var server: Server
    private var networkErrorListener: NetworkErrorListener? = null
    private val tag = this.javaClass.simpleName
    private var httpErrorHandler: HTTPErrorHandler? = null

    init {

        DaggerApplication.getDaggerApplication().registerActivityLifecycleCallbacks(this)

        httpErrorHandler = HTTPErrorHandler(networkErrorListener)

        val client: OkHttpClient.Builder = OkHttpClient.Builder()

                .followRedirects(true)
                .connectTimeout(20,TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
        val interceptor = HttpLoggingInterceptor()

        interceptor.level = HttpLoggingInterceptor.Level.BODY

        client.addInterceptor(interceptor)

        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        server = retrofit.create(Server::class.java)
    }

    fun getPaginationMovies(url: String): LiveData<List<Results>>{
        val data = MutableLiveData<List<Results>>()
        server.getPaginationMovies(url).enqueue(object: Callback<Responses>{
            override fun onResponse(call: Call<Responses>, response: Response<Responses>) {
                if(response.isSuccessful){
                    data.postValue(response.body()?.results)
                }else{
                    data.postValue(null)
                    //networkErrorListener?.onError(response.errorBody())
                }
            }
            override fun onFailure(call: Call<Responses>, t: Throwable) {
                data.postValue(null)
                LoggerUtil.log(this@AppRepo::class.java,t)
            }
        })
        return data
    }

    fun getSearchedMovie(apiKey: String, query: String): LiveData<List<Results>>{
        val data = MutableLiveData<List<Results>>()
        if(NetworkProvider.isConnected(DaggerApplication.getAppContext())){
            server.getSearchedMovie(apiKey,query).enqueue(object: Callback<Responses>{
                override fun onResponse(call: Call<Responses>, response: Response<Responses>) {
                    if(response.isSuccessful){
                        data.postValue(response.body()?.results)
                        LoggerUtil.log(this@AppRepo::class.java,"response successful")
                    }else{
                        data.postValue(null)
                        httpErrorHandler?.httpError(response)
                        //networkErrorListener?.onError("Error >>> ${response.errorBody()?.string()}")
                    }
                }
                override fun onFailure(call: Call<Responses>, t: Throwable) {
                    data.postValue(null)
                    LoggerUtil.log(this@AppRepo::class.java,t)
                    httpErrorHandler?.httpFail(call,t)
                    //networkErrorListener?.onError("Error >>> ${t.message}")
                }
            })
        }else{
            data.postValue(null)
            networkErrorListener?.onBadInternet("Bad Internet")
        }

        return data
    }

    fun getMoviesByCategory(category: String, page: Int): LiveData<List<Results>>{
        val data = MutableLiveData<List<Results>>()
        if(NetworkProvider.isConnected(DaggerApplication.getAppContext())){
            server.getMovieByCategory(category,page,"en-Us",Config.API_KEY).enqueue(object: Callback<Responses>{
                override fun onResponse(call: Call<Responses>, response: Response<Responses>) {
                    if(response.isSuccessful){
                        data.postValue(response.body()?.results)
                    }else{
                        data.postValue(null)
                        httpErrorHandler?.httpError(response)
                    }
                }

                override fun onFailure(call: Call<Responses>, t: Throwable) {
                    data.postValue(null)
                    httpErrorHandler?.httpFail(call,t)
                    //networkErrorListener?.onError(t.message)
                }
            })
        }else{
            data.postValue(null)
            networkErrorListener?.onBadInternet("Bad Network")
        }
        return data
    }



    private fun attachToActivity(activity: Activity){
        try {
            if(activity is NetworkErrorListener){
                networkErrorListener = activity
                httpErrorHandler = HTTPErrorHandler(networkErrorListener)
            }
        }catch (ex: Exception){

        }

    }

    override fun onActivityPaused(activity: Activity?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResumed(activity: Activity?) {
        activity?.let {
            attachToActivity(it)
        }
    }

    override fun onActivityStarted(activity: Activity?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityDestroyed(activity: Activity?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityStopped(activity: Activity?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}