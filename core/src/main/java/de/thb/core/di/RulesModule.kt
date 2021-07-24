package de.thb.core.di

import de.thb.core.data.AppDatabase
import de.thb.core.data.sources.rules.RulesRepository
import de.thb.core.data.sources.rules.RulesRepositoryImpl
import de.thb.core.data.sources.rules.local.RulesLocalDataSource
import de.thb.core.data.sources.rules.local.RulesLocalDataSourceImpl
import de.thb.core.data.sources.rules.remote.RulesRemoteDataSource
import de.thb.core.data.sources.rules.remote.RulesRemoteDataSourceImpl
import de.thb.core.data.sources.rules.remote.RulesService
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module
import retrofit2.Retrofit

val rulesModule = module {
    fun provideRulesService(retrofit: Retrofit.Builder): RulesService {
        return retrofit
            .build()
            .create(RulesService::class.java)
    }

    fun provideRulesRemoteDataSource(
        coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
        service: RulesService,
    ): RulesRemoteDataSource {
        return RulesRemoteDataSourceImpl(service, coroutinesDispatcherProvider)
    }

    fun provideRulesLocalDataSource(
        appDatabase: AppDatabase,
        dispatcherProvider: CoroutinesDispatcherProvider,
        applicationScope: CoroutineScope,
    ): RulesLocalDataSource {
        return RulesLocalDataSourceImpl(
            rulesRoomDao = appDatabase.rulesDao(),
            dispatcherProvider = dispatcherProvider,
            applicationScope = applicationScope,
        )
    }

    single { provideRulesService(get()) }
    single { provideRulesRemoteDataSource(get(), get()) }
    single { provideRulesLocalDataSource(get(), get(), get()) }
    single<RulesRepository> { RulesRepositoryImpl(get(), get()) }
}
