package de.thb.core.di

import android.content.Context
import androidx.room.Room
import de.thb.core.data.AppDatabase
import de.thb.core.data.places.local.PlacesLocalDataSource
import de.thb.core.data.places.remote.PlacesRemoteDataSource
import de.thb.core.data.places.remote.PlacesRemoteDataSourceImpl
import de.thb.core.data.places.remote.PlacesService
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

    fun providePlacesRemoteDataSource(service: PlacesService): PlacesRemoteDataSource {
        return PlacesRemoteDataSourceImpl(service)
    }

    fun providePlacesLocalDataSource(appDatabase: AppDatabase): PlacesLocalDataSource {
        return appDatabase.placesLocalDataSource()
    }

    single { provideRetrofit() }
    single { providePlacesService(get()) }
    single { providePlacesRemoteDataSource(get()) }
    single { providePlacesLocalDataSource(get()) }
    single { provideAppDatabase(get()) }
}
