package de.thb.core.di

import de.thb.core.data.AppDatabase
import de.thb.core.data.sources.places.PlacesRepository
import de.thb.core.data.sources.places.PlacesRepositoryImpl
import de.thb.core.data.sources.places.local.PlacesLocalDataSource
import de.thb.core.data.sources.places.local.PlacesLocalDataSourceImpl
import de.thb.core.data.sources.places.remote.PlacesRemoteDataSource
import de.thb.core.data.sources.places.remote.PlacesRemoteDataSourceImpl
import de.thb.core.data.sources.places.remote.PlacesService
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module
import retrofit2.Retrofit

val placesModule = module {
    fun providePlacesService(retrofit: Retrofit.Builder): PlacesService {
        return retrofit
            .build()
            .create(PlacesService::class.java)
    }

    fun providePlacesRemoteDataSource(
        coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
        service: PlacesService,
    ): PlacesRemoteDataSource {
        return PlacesRemoteDataSourceImpl(coroutinesDispatcherProvider, service)
    }

    fun providePlacesLocalDataSource(
        appDatabase: AppDatabase,
        dispatcherProvider: CoroutinesDispatcherProvider,
        applicationScope: CoroutineScope,
    ): PlacesLocalDataSource {
        return PlacesLocalDataSourceImpl(
            placesRoomDao = appDatabase.placesDao(),
            dispatcherProvider = dispatcherProvider,
            applicationScope = applicationScope,
        )
    }

    single { providePlacesService(get()) }
    single { providePlacesRemoteDataSource(get(), get()) }
    single { providePlacesLocalDataSource(get(), get(), get()) }
    single<PlacesRepository> { PlacesRepositoryImpl(get(), get()) }
}
