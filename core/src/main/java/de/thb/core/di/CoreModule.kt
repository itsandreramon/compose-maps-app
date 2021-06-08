package de.thb.core.di

import android.content.Context
import androidx.room.Room
import de.thb.core.data.AppDatabase
import de.thb.core.data.filters.local.FiltersLocalDataSource
import de.thb.core.data.filters.local.FiltersLocalDataSourceImpl
import de.thb.core.data.places.local.PlacesLocalDataSource
import de.thb.core.data.places.local.PlacesLocalDataSourceImpl
import de.thb.core.data.places.remote.PlacesRemoteDataSource
import de.thb.core.data.places.remote.PlacesRemoteDataSourceImpl
import de.thb.core.data.places.remote.PlacesService
import de.thb.core.util.CoroutinesDispatcherProvider
import de.thb.core.util.DefaultDispatcherProvider
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val coreModule = module {

    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("localhost")
            .addConverterFactory(MoshiConverterFactory.create())
    }

    fun providePlacesService(retrofit: Retrofit.Builder): PlacesService {
        return retrofit
            .build()
            .create(PlacesService::class.java)
    }

    fun provideAppDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "db"
        ).build()
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
    ): PlacesLocalDataSource {
        return PlacesLocalDataSourceImpl(appDatabase.placesDao(), dispatcherProvider)
    }

    fun provideFiltersLocalDataSource(
        appDatabase: AppDatabase,
        dispatcherProvider: CoroutinesDispatcherProvider,
    ): FiltersLocalDataSource {
        return FiltersLocalDataSourceImpl(appDatabase.filtersDao(), dispatcherProvider)
    }

    single { provideRetrofit() }
    single { providePlacesService(get()) }
    single { providePlacesRemoteDataSource(get(), get()) }
    single { providePlacesLocalDataSource(get(), get()) }
    single { provideFiltersLocalDataSource(get(), get()) }
    single { provideAppDatabase(get()) }
    single<CoroutinesDispatcherProvider> { DefaultDispatcherProvider() }
}
