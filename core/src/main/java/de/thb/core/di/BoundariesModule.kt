package de.thb.core.di

import com.squareup.moshi.Moshi
import de.thb.core.data.sources.boundaries.remote.BoundariesRemoteDataSource
import de.thb.core.data.sources.boundaries.remote.BoundariesRemoteDataSourceImpl
import de.thb.core.data.sources.boundaries.remote.BoundariesService
import de.thb.core.domain.boundary.types.GeojsonAdapter
import de.thb.core.util.CoroutinesDispatcherProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val boundariesModule = module {

    fun provideBoundariesService(): BoundariesService {
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
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(GeojsonAdapter())
                        .build()
                )
            )
            .build()
            .create(BoundariesService::class.java)
    }

    fun provideBoundariesRemoteDataSource(
        coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
        service: BoundariesService,
    ): BoundariesRemoteDataSource {
        return BoundariesRemoteDataSourceImpl(coroutinesDispatcherProvider, service)
    }

    single { provideBoundariesService() }
    single { provideBoundariesRemoteDataSource(get(), get()) }
}
