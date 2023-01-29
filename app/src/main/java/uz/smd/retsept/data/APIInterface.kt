package uz.smd.retsept.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

const val BASE_URL_TG_SCAN = "http://maklas.info/"

interface APIInterface {
    @GET("search/{searchId}")
    suspend fun search(@Path("searchId") searchId: String): Results
}