package de.thb.core.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.thb.core.data.example.remote.ExampleService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("localhost")
            .addConverterFactory(MoshiConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideExampleService(retrofit: Retrofit.Builder): ExampleService {
        return retrofit
            .build()
            .create(ExampleService::class.java)
    }
}
