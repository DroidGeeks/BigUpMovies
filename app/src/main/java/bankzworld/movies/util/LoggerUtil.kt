package bankzworld.movies.util

import android.content.Context
import android.util.Log
import java.lang.Exception

class LoggerUtil {
    companion object {
        fun <T> log(context: Class<T>,msg: String){
            Log.e(context.javaClass.simpleName,msg)
        }
        fun <T> log(context: Class<T>,msg: Exception){
            Log.e(context.javaClass.simpleName,"ERROR >>>", msg)
        }
        fun <T> log(context: Class<T>,msg: Throwable){
            Log.e(context.javaClass.simpleName,"ERROR >>>",msg)
        }
    }
}