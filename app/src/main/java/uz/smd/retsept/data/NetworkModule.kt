package uz.smd.retsept.data


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkModule {

    var baseUrl = "https://api.tgdev.io/tgscan/v1/"

    lateinit var utilService: APIInterface
        private set

    fun init() {
        utilService = trainerService(retrofit(okHttpClient(httpLoggingInterceptor())))
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private fun okHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val originalHttpUrl = original.url

                val url = originalHttpUrl.newBuilder()
                    .build()

                val requestBuilder = original.newBuilder()
                    .url(url)
                val request = requestBuilder
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(interceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    private fun trainerService(client: Retrofit): APIInterface {
        return client.create(APIInterface::class.java)
    }

    private fun retrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
