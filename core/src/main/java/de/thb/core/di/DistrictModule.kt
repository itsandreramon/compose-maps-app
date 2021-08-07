package de.thb.core.di

import de.thb.core.data.sources.district.DistrictRemoteDataSource
import de.thb.core.data.sources.district.DistrictRemoteDataSourceImpl
import de.thb.core.data.sources.district.DistrictService
import de.thb.core.util.CoroutinesDispatcherProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val districtModule = module {

    fun provideDistrictService(): DistrictService {
        return Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .client(
                OkHttpClient.Builder()
                    .addNetworkInterceptor { chain ->
                        chain.proceed(
                            chain.request()
                                .newBuilder()
                                .header("User-Agent", "Rulona Android Client")
                                .build()
                        )
                    }
                    .addInterceptor(
                        HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
                    )
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(DistrictService::class.java)
    }

    fun provideDistrictRemoteDataSource(
        coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
        service: DistrictService,
    ): DistrictRemoteDataSource {
        return DistrictRemoteDataSourceImpl(coroutinesDispatcherProvider, service)
    }

    single { provideDistrictService() }
    single { provideDistrictRemoteDataSource(get(), get()) }
}
