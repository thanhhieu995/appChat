package vnd.hieuUpdate.chitChat.notificationTest

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vnd.hieuUpdate.chitChat.notificationTest.Constants.Companion.BASE_URL


class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging) // <-- this is the important line!


            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI:: class.java)
        }
    }
}