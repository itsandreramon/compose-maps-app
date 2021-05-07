package de.thb.core.di

import de.thb.core.data.example.remote.ExampleRemoteDataSource
import de.thb.core.data.example.remote.ExampleRemoteDataSourceImpl
import de.thb.core.data.example.remote.ExampleService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val coreModule = module {

    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("localhost")
            .addConverterFactory(MoshiConverterFactory.create())
    }

    fun provideExampleService(retrofit: Retrofit.Builder): ExampleService {
        return retrofit
            .build()
            .create(ExampleService::class.java)
    }

    fun provideExampleRemoteDataSource(service: ExampleService): ExampleRemoteDataSource {
        return ExampleRemoteDataSourceImpl(service)
    }

    single { provideRetrofit() }
    single { provideExampleService(get()) }
    single { provideExampleRemoteDataSource(get()) }
}
