package app.oxyjon.retrofit
import com.google.gson.Gson
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import app.oxyjon.BuildConfig
import app.oxyjon.MainApplication
import app.oxyjon.constant.Constant
import app.oxyjon.database.AppSharedPreferences
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object RestClient {
    val BASE_URL: String? = Constant.BASE_API
    private var apiRestInterfaces: APIService? = null
    val client: APIService?
        get() {
            val okHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer "+ AppSharedPreferences.getInstance(MainApplication.currentActivity)!!.token)
                        .build()
                    chain.proceed(newRequest) }
            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                okHttpClient.addInterceptor(interceptor)
            }
            val gson: Gson = GsonBuilder()
                .setLenient()
                .create()
            if (apiRestInterfaces == null) {
                val client: Retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient.build())
                    .build()
                apiRestInterfaces = client.create(APIService::class.java)
            }
            return apiRestInterfaces
        }
}