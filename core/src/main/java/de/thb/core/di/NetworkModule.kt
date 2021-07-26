package de.thb.core.di

import de.thb.core.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {

    fun provideRetrofit(client: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://dev-backend-rulona.ci.beilich.de")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
    }

    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
        }

        return builder.build()
    }

    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
}
