package de.thb.core.di

import de.thb.core.data.sources.route.RouteRemoteDataSource
import de.thb.core.data.sources.route.RouteRemoteDataSourceImpl
import de.thb.core.data.sources.route.RouteService
import de.thb.core.util.CoroutinesDispatcherProvider
import org.koin.dsl.module
import retrofit2.Retrofit

val routeModule = module {

    fun provideRouteService(retrofit: Retrofit.Builder): RouteService {
        return retrofit
            .build()
            .create(RouteService::class.java)
    }

    fun provideRouteRemoteDataSource(
        coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
        service: RouteService,
    ): RouteRemoteDataSource {
        return RouteRemoteDataSourceImpl(coroutinesDispatcherProvider, service)
    }

    single { provideRouteService(get()) }
    single { provideRouteRemoteDataSource(get(), get()) }
}
