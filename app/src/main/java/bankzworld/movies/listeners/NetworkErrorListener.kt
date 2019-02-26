package bankzworld.movies.listeners

interface NetworkErrorListener{
    fun onError(msg: String?)
    fun onBadInternet(msg: String?)
}