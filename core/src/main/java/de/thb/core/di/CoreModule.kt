package de.thb.core.di

import android.content.Context
import androidx.room.Room
import de.thb.core.data.AppDatabase
import de.thb.core.data.sources.categories.CategoriesRepositoryImpl
import de.thb.core.data.sources.categories.CategoriesRepsitory
import de.thb.core.data.sources.categories.local.CategoriesLocalDataSource
import de.thb.core.data.sources.categories.local.CategoriesLocalDataSourceImpl
import de.thb.core.data.sources.categories.remote.CategoriesRemoteDataSource
import de.thb.core.data.sources.categories.remote.CategoriesRemoteDataSourceImpl
import de.thb.core.data.sources.categories.remote.CategoriesService
import de.thb.core.data.sources.places.PlacesRepository
import de.thb.core.data.sources.places.PlacesRepositoryImpl
import de.thb.core.data.sources.places.local.PlacesLocalDataSource
import de.thb.core.data.sources.places.local.PlacesLocalDataSourceImpl
import de.thb.core.data.sources.places.remote.PlacesRemoteDataSource
import de.thb.core.data.sources.places.remote.PlacesRemoteDataSourceImpl
import de.thb.core.data.sources.places.remote.PlacesService
import de.thb.core.util.CoroutinesDispatcherProvider
import de.thb.core.util.DefaultDispatcherProvider
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val coreModule = module {

    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://dev-backend-rulona.ci.beilich.de")
            .addConverterFactory(MoshiConverterFactory.create())
    }

    fun providePlacesService(retrofit: Retrofit.Builder): PlacesService {
        return retrofit
            .build()
            .create(PlacesService::class.java)
    }

    fun provideCategoriesService(retrofit: Retrofit.Builder): CategoriesService {
        return retrofit
            .build()
            .create(CategoriesService::class.java)
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
        applicationScope: CoroutineScope,
    ): PlacesLocalDataSource {
        return PlacesLocalDataSourceImpl(
            placesRoomDao = appDatabase.placesDao(),
            dispatcherProvider = dispatcherProvider,
            applicationScope = applicationScope,
        )
    }

    fun provideCategoriesLocalDataSource(
        appDatabase: AppDatabase,
        dispatcherProvider: CoroutinesDispatcherProvider,
        applicationScope: CoroutineScope,
    ): CategoriesLocalDataSource {
        return CategoriesLocalDataSourceImpl(
            categoriesRoomDao = appDatabase.categoriesDao(),
            dispatcherProvider = dispatcherProvider,
            applicationScope = applicationScope
        )
    }

    fun provideCategoriesRemoteDataSource(
        coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
        service: CategoriesService,
    ): CategoriesRemoteDataSource {
        return CategoriesRemoteDataSourceImpl(coroutinesDispatcherProvider, service)
    }

    single { provideRetrofit() }

    single { providePlacesService(get()) }
    single { providePlacesRemoteDataSource(get(), get()) }
    single { providePlacesLocalDataSource(get(), get(), get()) }
    single<PlacesRepository> { PlacesRepositoryImpl(get(), get()) }

    single { provideCategoriesService(get()) }
    single { provideCategoriesRemoteDataSource(get(), get()) }
    single { provideCategoriesLocalDataSource(get(), get(), get()) }
    single<CategoriesRepsitory> { CategoriesRepositoryImpl(get(), get()) }

    single { provideAppDatabase(get()) }
    single<CoroutinesDispatcherProvider> { DefaultDispatcherProvider() }
}
