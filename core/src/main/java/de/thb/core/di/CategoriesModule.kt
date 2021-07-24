package de.thb.core.di

import de.thb.core.data.AppDatabase
import de.thb.core.data.sources.categories.CategoriesRepositoryImpl
import de.thb.core.data.sources.categories.CategoriesRepsitory
import de.thb.core.data.sources.categories.local.CategoriesLocalDataSource
import de.thb.core.data.sources.categories.local.CategoriesLocalDataSourceImpl
import de.thb.core.data.sources.categories.remote.CategoriesRemoteDataSource
import de.thb.core.data.sources.categories.remote.CategoriesRemoteDataSourceImpl
import de.thb.core.data.sources.categories.remote.CategoriesService
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module
import retrofit2.Retrofit

val categoriesModule = module {

    fun provideCategoriesService(retrofit: Retrofit.Builder): CategoriesService {
        return retrofit
            .build()
            .create(CategoriesService::class.java)
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

    single { provideCategoriesService(get()) }
    single { provideCategoriesRemoteDataSource(get(), get()) }
    single { provideCategoriesLocalDataSource(get(), get(), get()) }
    single<CategoriesRepsitory> { CategoriesRepositoryImpl(get(), get()) }
}
